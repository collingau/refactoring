package theater;

/**
 * Represents a play in the theater system.
 * Each play has a name and a type (e.g., tragedy or comedy).
 */
public class Play {

    private String name;
    private String type;

    public Play(String name, String type) {
        this.name = name;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public String getType() {
        return type;
    }
}
