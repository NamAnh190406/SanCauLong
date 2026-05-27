package Model;

/**
 * Shared data model for statistics rows.
 * Used by both ThongKeController variants and ChartService.
 */
public class StatRow {
    private String name;
    private long revenue;
    private int bookings;
    private int customers;

    public StatRow(String name, long revenue, int bookings, int customers) {
        this.name = name;
        this.revenue = revenue;
        this.bookings = bookings;
        this.customers = customers;
    }

    public String name()      { return name; }
    public long   revenue()   { return revenue; }
    public int    bookings()  { return bookings; }
    public int    customers() { return customers; }

    // Mutable setters used by aggregation logic
    public void addRevenue(long amount)    { this.revenue   += amount; }
    public void addBooking()               { this.bookings  += 1; }
    public void setCustomers(int customers){ this.customers  = customers; }
}
