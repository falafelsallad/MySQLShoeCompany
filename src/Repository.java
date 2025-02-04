import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;
import java.sql.*;

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
            p.load(new FileInputStream("C:\\Users\\Ägaren\\Documents\\GitHub\\MySQLShoeCompany\\src\\settings.properties"));
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
                System.out.println("Welcome to shoe store 2025, please log in!");
                System.out.println("Enter username:");
                String username = scan.next().toLowerCase(Locale.ROOT).trim();

                System.out.println("Enter password:");
                String password = scan.next().toLowerCase(Locale.ROOT).trim();

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
                    System.out.println("Login successful, customer identification " + customerIDFromLogIn);
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
                System.out.println("Current in your cart: ");
                getCustomerCart(orderID);
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


    public int categoryControl(int shoeId) {
        int ifCategoryExists = 0;
        try (Connection con = getConnection();
             CallableStatement callShoeControl = con.prepareCall("CALL categoryControl(?, ?)")) {
            callShoeControl.setInt(1, shoeId);
            callShoeControl.executeQuery();
            ifCategoryExists = callShoeControl.getInt(2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ifCategoryExists;
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
    public void getColours() {                                                         //FUNKAR UTMÄRKT UTAN EN LISTA HÄR OCKSÅ!
        try (Connection con = getConnection();
             Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery("SELECT ID as colour, NAME FROM colour ORDER BY ID ASC")) {

            while (rs.next()) {
                System.out.print("Colour: " + rs.getString("colour") + ": ");
                System.out.println(rs.getString("Name"));
            }
            System.out.println("-----------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
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
        while (true) {
            // Prompt the user for a category
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter category number: ");
            int categoryIDInput = scanner.nextInt();


            try (Connection con = getConnection();

                 CallableStatement callgetShoesByCategory = con.prepareCall("CALL GetShoesByCategory(?)")) {

                // Set input parameter
                callgetShoesByCategory.setInt(1, categoryIDInput);

                //Hanterar ifall vi matar in kategori som ej finns
                callgetShoesByCategory.executeQuery();
                if (categoryControl(categoryIDInput) == 0) {
                    System.out.println("Shoe does not exist, please try again");
                    continue;
                }
                // Starting SP
                try (ResultSet rs = callgetShoesByCategory.getResultSet()) {
                    while (rs.next()) {
                        int name = rs.getInt("ARTICLENUMBER");
                        int price = rs.getInt("price");
                        int size = rs.getInt("size");
                        int balance = rs.getInt("Storage_balance");
                        String brand = rs.getString("brand");
                        String colours = rs.getString("colour");
                        String categories = rs.getString("categories");

                        System.out.print("Article number: " + name + " | ");
                        System.out.print("Price: " + price + " | ");
                        System.out.print("Size: " + size + " | ");
                        System.out.print("Balance: " + balance + " | ");
                        System.out.print("Brand: " + brand + " | ");
                        System.out.print("Colours: " + colours + " | ");
                        System.out.print("Categorie: " + categories);
                        System.out.println("\n");
                    }
                }

            break;
            } catch (SQLException e) {
                throw new RuntimeException("Error retrieving shoes by category", e);
            }
        }
    }

    public void getShoeDetailsByColour() {
        while (true) {
            // Prompt the user for a colour
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter colour: ");
            int colourIDinput = scanner.nextInt();


            try (Connection con = getConnection();

                 CallableStatement callgetShoeDetailsByColour = con.prepareCall("CALL GetShoesByColour(?)")) {

                // Set input parameter
                callgetShoeDetailsByColour.setInt(1, colourIDinput);

                //Hanterar ifall vi matar in kategori som ej finns
                callgetShoeDetailsByColour.executeQuery();
                if (categoryControl(colourIDinput) == 0) {
                    System.out.println("Shoe does not exist, please try again");
                    continue;
                }
                // Starting SP
                try (ResultSet rs = callgetShoeDetailsByColour.getResultSet()) {
                    while (rs.next()) {
                        int name = rs.getInt("ARTICLENUMBER");
                        int price = rs.getInt("price");
                        int size = rs.getInt("size");
                        int balance = rs.getInt("Storage_balance");
                        String brand = rs.getString("brand");
                        String colours = rs.getString("colours");
                        String categories = rs.getString("categories");

                        System.out.print("Article number: " + name + " | ");
                        System.out.print("Price: " + price + " | ");
                        System.out.print("Size: " + size + " | ");
                        System.out.print("Balance: " + balance + " | ");
                        System.out.print("Brand: " + brand + " | ");
                        System.out.print("Colours: " + colours + " | ");
                        System.out.print("Categorie: " + categories);
                        System.out.println("\n");
                    }
                }

                break;
            } catch (SQLException e) {
                throw new RuntimeException("Error retrieving shoes by category", e);
            }
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

    public void getCustomerCart(int activeOrderId){
        try (Connection con = getConnection();
             CallableStatement callgetCustomerCart = con.prepareCall("CALL getCustomerCart(?)")) {
            callgetCustomerCart.setInt(1, activeOrderId);

            try (ResultSet rs = callgetCustomerCart.executeQuery()) {
                if (!rs.isBeforeFirst()) {
                    System.out.println("No items found in the cart.");
                }
                while (rs.next()) {
                    int articleNumber = rs.getInt("articleNumber");
                    String brand = rs.getString("brand");
                    String colours = rs.getString("colours");
                    String categories = rs.getString("categories");
                    int price = rs.getInt("price");
                    int amount = rs.getInt("amount");
                    int size = rs.getInt("size");

                    System.out.print("Art nmbr: " + articleNumber + " | ");
                    System.out.print("Brand: " + brand + " | ");
                    System.out.print("Colours: " + colours + " | ");
                    System.out.print("Categorie: " + categories);
                    System.out.print("Price: " + price + " | ");
                    System.out.print("Amount: " + amount + " | ");
                    System.out.print("Size: " + size + " | ");



                    System.out.print("Price: " + price + " | ");

                    System.out.println();
                }
            }

        } catch (SQLException e) {
            System.out.println("Ett fel uppstod: " + e.getMessage());
        }
    }


    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(p.getProperty("url"), p.getProperty("user"), p.getProperty("password"));
    }

}
