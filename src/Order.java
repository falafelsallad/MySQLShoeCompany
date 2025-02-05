import java.util.List;

public class Order {
    private int id;
    private Customer customer;
    private List<Shoe> shoes;

    public Order(int id, Customer customer, List<Shoe> shoes) {
        this.id = id;
        this.customer = customer;
        this.shoes = shoes;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Customer getCustomer() {
        return customer;
    }

    public void setCustomer(Customer customer) {
        this.customer = customer;
    }

    public List<Shoe> getShoes() {
        return shoes;
    }

    public void setShoes(List<Shoe> shoes) {
        this.shoes = shoes;
    }
}
