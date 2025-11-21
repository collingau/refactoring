package theater;

import java.text.NumberFormat;
import java.util.Locale;
import java.util.Map;

/**
 * This class generates a statement for a given invoice of performances.
 */
public class StatementPrinter {
    private final Invoice invoice;
    private final Map<String, Play> plays;

    /**
     * Creates a new printer for the given invoice and play map.
     *
     * @param invoice the invoice to print
     * @param plays   map from play ID to play data
     */
    public StatementPrinter(final Invoice invoice, final Map<String, Play> plays) {
        this.invoice = invoice;
        this.plays = plays;
    }

    /**
     * Returns a formatted statement of the invoice associated with this printer.
     *
     * @return a formatted text statement of the invoice
     * @throws RuntimeException if one of the play types is not known
     */
    public String statement() {
        final StringBuilder result = new StringBuilder(
                "Statement for " + invoice.getCustomer() + System.lineSeparator()
        );

        // print line for each order
        for (final Performance p : invoice.getPerformances()) {
            result.append(String.format(
                    "  %s: %s (%s seats)%n",
                    getPlay(p).getName(),
                    usd(getAmount(p)),
                    p.getAudience()
            ));
        }

        result.append(String.format("Amount owed is %s%n", usd(getTotalAmount())));
        result.append(String.format("You earned %s credits%n", getTotalVolumeCredits()));
        return result.toString();
    }

    /**
     * Calculates the cost for a given performance based on its play type.
     *
     * @param performance the performance to calculate the amount for
     * @return the total amount owed for that performance in cents
     * @throws RuntimeException if the play type is unknown
     */
    private int getAmount(final Performance performance) {
        final String type = getPlay(performance).getType();
        int result;
        switch (type) {
            case "tragedy":
                result = Constants.TRAGEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.TRAGEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.TRAGEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience()
                            - Constants.TRAGEDY_AUDIENCE_THRESHOLD);
                }
                break;
            case "comedy":
                result = Constants.COMEDY_BASE_AMOUNT;
                if (performance.getAudience() > Constants.COMEDY_AUDIENCE_THRESHOLD) {
                    result += Constants.COMEDY_OVER_BASE_CAPACITY_AMOUNT
                            + Constants.COMEDY_OVER_BASE_CAPACITY_PER_PERSON
                            * (performance.getAudience()
                            - Constants.COMEDY_AUDIENCE_THRESHOLD);
                }
                result += Constants.COMEDY_AMOUNT_PER_AUDIENCE * performance.getAudience();
                break;
            default:
                throw new RuntimeException(String.format("unknown type: %s", type));
        }
        return result;
    }

    /**
     * Calculates the total amount owed for all performances.
     *
     * @return the total amount owed for the invoice in cents
     */
    private int getTotalAmount() {
        int result = 0;
        for (final Performance p : invoice.getPerformances()) {
            result += getAmount(p);
        }
        return result;
    }

    /**
     * Calculates the total volume credits earned for all performances.
     *
     * @return the total volume credits for the invoice
     */
    private int getTotalVolumeCredits() {
        int result = 0;
        for (final Performance p : invoice.getPerformances()) {
            result += getVolumeCredits(p);
        }
        return result;
    }

    /**
     * Calculates the volume credits contributed by a single performance.
     *
     * @param performance the performance to calculate credits for
     * @return the number of volume credits earned for that performance
     */
    private int getVolumeCredits(final Performance performance) {
        int result = Math.max(
                performance.getAudience() - Constants.BASE_VOLUME_CREDIT_THRESHOLD, 0
        );
        if ("comedy".equals(getPlay(performance).getType())) {
            result += performance.getAudience() / Constants.COMEDY_EXTRA_VOLUME_FACTOR;
        }
        return result;
    }

    /**
     * Retrieves the {@link Play} object for a given {@link Performance}.
     *
     * @param performance the performance whose play is being retrieved
     * @return the Play object corresponding to the performance
     */
    private Play getPlay(final Performance performance) {
        return plays.get(performance.getPlayID());
    }

    /**
     * Converts an integer amount (in cents) into a USD currency string.
     *
     * @param amount the amount in cents
     * @return the formatted USD currency string
     */
    private String usd(final int amount) {
        return NumberFormat.getCurrencyInstance(Locale.US)
                .format(amount / (double) Constants.PERCENT_FACTOR);
    }
}
