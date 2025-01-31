public class shoes {
    private int price;
    private int size;
    private int storageBalance;
    private String brand;
    private String colour;
    private String Category;

    public shoes(){}

    public shoes(int price, int size, int storageBalance, String brand, String colour, String category) {
        this.price = price;
        this.size = size;
        this.storageBalance = storageBalance;
        this.brand = brand;
        this.colour = colour;
        Category = category;
    }

    public int getPrice() {
        return price;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public int getStorageBalance() {
        return storageBalance;
    }

    public void setStorageBalance(int storageBalance) {
        this.storageBalance = storageBalance;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }

    public String getColour() {
        return colour;
    }

    public void setColour(String colour) {
        this.colour = colour;
    }


    @Override
    public String toString() {
        return "price: " + price +
                " size: " + size +
                " storage balance: " + storageBalance +
                " brand: " + brand +
                " colour: " + colour +
                " Category: " + Category + " ";
    }
}
