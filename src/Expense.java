import java.io.Serial; // For serializing and deserializing objects for serialization and deserialization.
import java.io.Serializable; // For serializing and deserializing objects.
import java.util.Date; // For handling date and time in the application.

class Expense implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L; // For serializing and deserializing objects.

    private final double amount; // Amount of the expense
    private final String category; // Category of the expense
    private final String description; // Description of the expense
    private final Date timestamp; // Timestamp of the expense

    public Expense(double amount, String category, String description) {
        this.amount = amount;
        this.category = category;
        this.description = description;
        this.timestamp = new Date();
    }

    public double getAmount() {
        return amount;
    }

    public String getCategory() {
        return category;
    }

    public String getDescription() {
        return description;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    /* Override the toString() method to display the expense in a readable format
     with the timestamp, category, amount, and description*/
    @Override
    public String toString() {
        return String.format("%tF %tT - %s - $%.2f - %s",
                timestamp, timestamp, category, amount, description);
    }
}