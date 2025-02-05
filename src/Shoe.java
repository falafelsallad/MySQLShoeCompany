public class Shoe {
    private int articlenumber;
    private double price;
    private int size;
    private int storageBalance;
    private String brand;
    private Colour colours;
    private Category categories;


    public Shoe(int articlenumber, int price, int size, int storageBalance, String brand, String colours, String categories) {
    }

    public int getArticlenumber() {
        return articlenumber;
    }

    public void setArticlenumber(int articlenumber) {
        this.articlenumber = articlenumber;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
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

    public Colour getColours() {
        return colours;
    }

    public void setColours(Colour colours) {
        this.colours = colours;
    }

    public Category getCategories() {
        return categories;
    }

    public void setCategories(Category categories) {
        this.categories = categories;
    }
}
