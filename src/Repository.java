import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.sql.*;
import java.util.Scanner;

public class Repository {
    shoes shoe=new shoes();
    Categories categorie=new Categories();
    Colours colour=new Colours();
    List <String> listColour= new ArrayList<>();
    List <String> listCategories=new ArrayList<>();
    Colours coloursFromDataBase;
    Categories categoriesFromDataBase;
    private Properties p = new Properties();

    public Repository() {
        try {
            p.load(new FileInputStream("C:\\Users\\Ägaren\\Documents\\GitHub\\MySQLShoeCompany\\src\\settings.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //INLOGGNING FÖR ANVÄNDARE
    public int startLogIn() {
        int customerIDFromLogIn;
        try (Connection con = DriverManager.getConnection(p.getProperty("url"),
                p.getProperty("user"),
                p.getProperty("password"));
             CallableStatement callLogin = con.prepareCall("CALL CustomerLogin (?,?,?)");) {
            Scanner scan = new Scanner(System.in);

            System.out.println("Enter username:");
            String username = scan.next();

            System.out.println("Enter password:");
            String password = scan.next();

            callLogin.setString(1, username);
            callLogin.setString(2, password);
            callLogin.registerOutParameter(3, Types.INTEGER);

            callLogin.executeQuery();
            customerIDFromLogIn = callLogin.getInt(3);

            if (callLogin.getInt(3) > 0) {
                System.out.println("Login successful, customer " + customerIDFromLogIn);
                ResultSet resultSet = callLogin.executeQuery("SELECT customer.firstname, customer.lastname FROM customer WHERE ID = " + customerIDFromLogIn);
                while (resultSet.next()) {
                    System.out.print("Welcome " + resultSet.getString("firstname") + " ");
                    System.out.println(resultSet.getString("lastname") + "!");
                }
            } else {
                System.out.println("Login failed");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return customerIDFromLogIn;
    }

    // KONTROLL OM DE ÄR AKTIVA ELLER BETALDA
    public int getPaymentStatus(int customersIDfromLogin) {
        try (Connection con = DriverManager.getConnection(p.getProperty("url"),
                p.getProperty("user"),
                p.getProperty("password"));
             CallableStatement callPaymentStatus = con.prepareCall("CALL CheckPaymentStatus(?,?)");) {
            callPaymentStatus.setInt(1, customersIDfromLogin);
            callPaymentStatus.registerOutParameter(2, Types.INTEGER);

            callPaymentStatus.executeQuery();
            customersIDfromLogin = callPaymentStatus.getInt(2);

            if (customersIDfromLogin == 0) {
                System.out.println("No active order, time to make a new one!");
            } else {
                System.out.println("Active Order ID: " + customersIDfromLogin);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return customersIDfromLogin;
    }


  // ADD TO CART
    public void AddToCart(int customerID, int orderID, int shoeID, int amountInOrder) {
        try (Connection con = DriverManager.getConnection(p.getProperty("url"),
                p.getProperty("user"),
                p.getProperty("password"));
             CallableStatement callAddToCart = con.prepareCall("CALL AddToCart(?,?,?,?)");) {

            // CallableStatement callAddToCart = connection.prepareCall("{call AddToCart(?, ?, ?, ?)}");
            callAddToCart.setInt(1, customerID); //customerID
            callAddToCart.setInt(2, orderID); // orderID
            callAddToCart.setInt(3, 1); // ShoeID
            callAddToCart.setInt(4, 1); // Quantity
            callAddToCart.executeQuery();

            ResultSet resultSet = callAddToCart.executeQuery("SELECT * FROM orderitem WHERE orderID = " + orderID);

            while (resultSet.next()) {
                System.out.println("Order ID: " + resultSet.getInt("orderID"));
                System.out.println("Shoe ID: " + resultSet.getInt("shoeID"));
                System.out.println("Quantity: " + resultSet.getInt("amount"));
                System.out.println("Price: " + resultSet.getInt("price"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // KATEGORIER IN I LIST
    public String getCategories(){
            try (Connection con = DriverManager.getConnection(
                    p.getProperty("url"),
                    p.getProperty("user"),
                    p.getProperty("password"));
                 Statement statement = con.createStatement();
                 ResultSet rs = statement.executeQuery("SELECT NAME FROM category")) {

                while (rs.next()) {
                    System.out.println(rs.getString("Name"));
                    categorie.setCategorie(rs.getString("Name"));
                    listCategories.add(rs.getString("Name"));
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return categorie.toString();
    }

    //FÄRGER IN I LIST
    public String getcolours(){
        try (Connection con = DriverManager.getConnection(
                p.getProperty("url"),
                p.getProperty("user"),
                p.getProperty("password"));
             Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery("SELECT NAME FROM colour")) {

            while (rs.next()) {
                System.out.println(rs.getString("Name"));
                colour.setColours(rs.getString("Name"));
                listColour.add(rs.getString("Name"));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return colour.toString();
    }
}
