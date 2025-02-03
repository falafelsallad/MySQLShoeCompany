import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.sql.*;
import java.util.Scanner;

import static java.sql.DriverManager.getConnection;

public class Repository {
    shoes shoe = new shoes();
    Categories category = new Categories();
    Colours colour = new Colours();
    List<String> listColour = new ArrayList<>();
    List<String> listCategories = new ArrayList<>();
    Colours coloursFromDataBase;
    Categories categoriesFromDataBase;
    private Properties p = new Properties();

    public Repository() {
        try {
            p.load(new FileInputStream("C:\\Users\\fatim\\Documents\\GitHub\\MySQLShoeCompany\\src\\settings.properties"));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    //INLOGGNING FÖR ANVÄNDARE
    public int startLogIn() {
        int customerIDFromLogIn;
        Scanner scan = new Scanner(System.in);
        while (true) {
            try (Connection con = getConnection();
                 CallableStatement callLogin = con.prepareCall("CALL CustomerLogin (?,?,?)")) {

                System.out.println("Enter username:");
                String username = scan.next();

                System.out.println("Enter password:");
                String password = scan.next();

                int ifUsernameExists = usernameControl(username);
                if (ifUsernameExists == 0) {
                    System.out.println("Username does not exist, please try again");
                    continue;
                }

                callLogin.setString(1, username);
                callLogin.setString(2, password);
                callLogin.registerOutParameter(3, Types.INTEGER);

                callLogin.executeQuery();


                customerIDFromLogIn = callLogin.getInt(3);

                if (customerIDFromLogIn > 0) {
                    System.out.println("Login successful, customer " + customerIDFromLogIn);
                    ResultSet resultSet = callLogin.executeQuery("SELECT customer.firstname, customer.lastname FROM customer WHERE ID = " + customerIDFromLogIn);
                    while (resultSet.next()) {
                        System.out.print("Welcome " + resultSet.getString("firstname") + " ");
                        System.out.println(resultSet.getString("lastname") + "!");
                    }
                }
                if (customerIDFromLogIn > 0) {
                    break;
                }

            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        }

        return customerIDFromLogIn;
    }

    public int usernameControl(String usernameInput) throws SQLException {
        int ifUsernameExists = 0;
        try (Connection con = getConnection();
        CallableStatement callUsernameControl = con.prepareCall("CALL UsernameControl(?,?)")){
            callUsernameControl.setString(1, usernameInput);
            callUsernameControl.registerOutParameter(2, Types.INTEGER);
            callUsernameControl.executeQuery();
            ifUsernameExists = callUsernameControl.getInt(2);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }


        return ifUsernameExists;
    }

    // KONTROLL OM DE ÄR AKTIVA ELLER BETALDA
    public int getPaymentStatus(int customersIDfromLogin) {
        int orderID = 0;
        try (Connection con = getConnection();
            CallableStatement callPaymentStatus = con.prepareCall("CALL CheckPaymentStatus(?,?)")) {
            callPaymentStatus.setInt(1, customersIDfromLogin);
            callPaymentStatus.registerOutParameter(2, Types.INTEGER);

            callPaymentStatus.executeQuery();
            orderID = callPaymentStatus.getInt(2);

            if (orderID == 0) {
                System.out.println("No active order, time to make a new one!");
                orderID = createOrder(customersIDfromLogin);
            } else {
                System.out.println("Active Order ID: " + orderID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orderID;
    }

    public int createOrder(int customerID) {
        int orderID = 0;
        try (Connection con = getConnection();
            CallableStatement callCreateOrder = con.prepareCall("CALL CreateOrder(?,?)")){
            callCreateOrder.setInt(1, customerID);
            callCreateOrder.registerOutParameter(2, Types.INTEGER);
            callCreateOrder.executeQuery();

            orderID = callCreateOrder.getInt(2);
            System.out.println("NEW ORDER CREATED, Order ID: " + orderID);

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return orderID;
    }


    // ADD TO CART
    public void AddToCart(int orderID) {
        Scanner scan = new Scanner(System.in);
        int shoeIDInput;
        int shoeAmount;
        while (true) {
            try (Connection con = getConnection();
                 CallableStatement callAddToCart = con.prepareCall("CALL AddToCart(?,?,?)");) {
                System.out.println("Which shoe would you like to add to your cart?");
                shoeIDInput = scan.nextInt();

                if (shoeControl(shoeIDInput) == 0) {
                    System.out.println("Shoe does not exist, please try again");
                    continue;
                }

                System.out.println("How many would you like to add?");
                shoeAmount = scan.nextInt();
                callAddToCart.setInt(1, orderID); // orderID
                callAddToCart.setInt(2, shoeIDInput); // ShoeID
                callAddToCart.setInt(3, shoeAmount); // Quantity
                callAddToCart.executeQuery();

                ResultSet resultSet = callAddToCart.executeQuery("SELECT * FROM orderitem WHERE orderID = " + orderID);

                while (resultSet.next()) { // TODO Shoes should have a name
                    System.out.println("Order ID: " + resultSet.getInt("orderID"));
                    System.out.println("Shoe: " + resultSet.getInt("shoeID"));
                    System.out.println("Quantity: " + resultSet.getInt("amount"));
                    System.out.println("Total price: " + resultSet.getInt("price"));
                    System.out.println("-----------------------------" + "\n");
                }
                if (shoeIDInput != 0) {
                    break;
                }

            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public int shoeControl(int shoeId) {
        int ifShoeExists = 0;
        try (Connection con = getConnection();
            CallableStatement callShoeControl = con.prepareCall("CALL ShoeControl(?, ?)")) {
            callShoeControl.setInt(1, shoeId);
            callShoeControl.executeQuery();
            ifShoeExists = callShoeControl.getInt(2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
            return ifShoeExists;
        }



    // KATEGORIER IN I LIST
    public void getCategories() {    //FUNKAR UTMÄRKT UTAN EN LISTA!
        try (Connection con = getConnection();
             Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery("SELECT ID as article, NAME FROM category")) {
            while (rs.next()) {
                System.out.print("Category: " + rs.getString("article") + ": ");
                System.out.println(rs.getString("Name"));
            }
            System.out.println("-----------------------------" + "\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    //FÄRGER IN I LIST
    public String getColours() {                                                         //FUNKAR UTMÄRKT UTAN EN LISTA HÄR OCKSÅ!
        try (Connection con = getConnection();
             Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery("SELECT NAME FROM colour")) {

            while (rs.next()) {
                System.out.println(rs.getString("Name"));
            }
            System.out.println("-----------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return colour.toString();
    }

    public void getShoeInfo(int shoeIDInput) {
        try (Connection con = getConnection();
            CallableStatement callgetShoeInfo = con.prepareCall("CALL GetShoeDetails(?)")) {
            callgetShoeInfo.setInt(1, shoeIDInput);
            ResultSet rs = callgetShoeInfo.executeQuery();

            while (rs.next()) {
                System.out.println("Name: " + rs.getInt("name"));
                System.out.println("Price: " + rs.getInt("Price"));
                System.out.println("Size: " + rs.getInt("Size"));
                System.out.println("Storage: " + rs.getInt("Balance"));
                System.out.println("Brand: " + rs.getString("Brand"));
                System.out.println("Colour: " + rs.getString("Colour"));
                System.out.println("Category: " + rs.getString("Category"));
                System.out.println("--------------------------" + "\n");
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void getShoeDetailsByCategory() {
        // Prompt the user for a category ID
        Scanner scanner = new Scanner(System.in);
        System.out.print("Enter category number: ");
        int categoryIDInput = scanner.nextInt();

        try (Connection con = getConnection();
             CallableStatement callgetShoesByCategory = con.prepareCall("CALL GetShoesByCategory(?)")) {

            // Set input parameter
            callgetShoesByCategory.setInt(1, categoryIDInput);

            // Starting SP
            callgetShoesByCategory.executeQuery();

            try (ResultSet rs = callgetShoesByCategory.getResultSet()) {
                while (rs.next()) {
                    int name = rs.getInt("name");
                    int price = rs.getInt("price");
                    int size = rs.getInt("size");
                    int balance = rs.getInt("Storage_balance");
                    String brand = rs.getString("brand");
                    String colours = rs.getString("colour");
                    String categories = rs.getString("categories");

                    System.out.println("Name: " + name);
                    System.out.println("Price: " + price);
                    System.out.println("Size: " + size);
                    System.out.println("Balance: " + balance);
                    System.out.println("Brand: " + brand);
                    System.out.println("Colours: " + colours);
                    System.out.println("Categories: " + categories);
                    System.out.println("-----------------------------" + "\n");
                }
            }


        } catch (SQLException e) {
            throw new RuntimeException("Error retrieving shoes by category", e);
        }
    }

    public void confirmPurchase(int activeOrderId) {
        try (Connection con = getConnection();
             CallableStatement confirmOrder = con.prepareCall("CALL confirmOrder(?)")) {
            confirmOrder.setInt(1, activeOrderId);
            confirmOrder.executeQuery();


        } catch (SQLException e) {
            System.out.println("Ett fel uppstod: " + e.getMessage());
        }
    }


    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(p.getProperty("url"), p.getProperty("user"), p.getProperty("password"));
    }

}
