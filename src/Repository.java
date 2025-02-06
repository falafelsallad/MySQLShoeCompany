import java.io.FileInputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.util.*;
import java.sql.*;


public class Repository {
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
                System.out.println("Something went wrong when u tried to log in " + e.getMessage());
                e.printStackTrace();
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
            System.out.println("You are not a customer" + e.getMessage());
            e.printStackTrace();
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


                if (!scan.hasNext()){
                    System.out.println("Not valid input, try again");
                    scan.nextInt();
                    continue;
                }

                shoeIDInput = scan.nextInt();


                if (shoeControl(shoeIDInput) == 0) {
                    System.out.println("Shoe does not exist, please try again");
                    scan.nextInt();
                    continue;
                }

                System.out.println("How many would you like to add?");

                if (!scan.hasNext()){
                    System.out.println("Not valid input, try again");
                    scan.nextInt();
                    continue;
                }

                shoeAmount = scan.nextInt();


                callAddToCart.setInt(1, orderID); // orderID
                callAddToCart.setInt(2, shoeIDInput); // ShoeID
                callAddToCart.setInt(3, shoeAmount); // Quantity


                try (ResultSet rs = callAddToCart.executeQuery()) {
                    if (rs.next()) {
                        System.out.println(rs.getString(1));  // FELMEDDELANDE FRÅN SQL
                    }
                }

                try (PreparedStatement checkOrder = con.prepareStatement("SELECT * FROM orderitem WHERE orderID = ?")) {
                    checkOrder.setInt(1, orderID);
                    ResultSet resultSet = checkOrder.executeQuery();

                    System.out.println("Current order details:");
                    while (resultSet.next()) {
                        System.out.println("Order ID: " + resultSet.getInt("orderID"));
                        System.out.println("Shoe ID: " + resultSet.getInt("shoeID"));
                        System.out.println("Quantity: " + resultSet.getInt("amount"));
                        System.out.println("Total price: " + resultSet.getInt("price"));
                        System.out.println("-----------------------------\n");
                    }
                }
                if (shoeIDInput != 0) {
                    break;
                }

            } catch (SQLException e) {
                System.out.println("SQLL ERROR :" + e.getMessage());
                e.printStackTrace();
            }catch (InputMismatchException e){
                System.out.println("Pleace insert correct input. choose the article number or ");
                scan.next();
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


    public int categoryControl(String categoryName) {
        int ifCategoryExists = 0;
        try (Connection con = getConnection();
             CallableStatement callShoeControl = con.prepareCall("CALL categoryControl(?, ?)")) {
            callShoeControl.setString(1, categoryName);
            callShoeControl.executeQuery();
            ifCategoryExists = callShoeControl.getInt(2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ifCategoryExists;
    }

    public int colourControl(String colourName) {
        int ifcolourExists = 0;
        try (Connection con = getConnection();
             CallableStatement callShoeControl = con.prepareCall("CALL colourControl(?, ?)")) {
            callShoeControl.setString(1, colourName);
            callShoeControl.executeQuery();
            ifcolourExists = callShoeControl.getInt(2);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return ifcolourExists;
    }

    public void getCategories() {
        try (Connection con = getConnection();
             Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery("SELECT NAME as Category FROM category")) {
            while (rs.next()) {
                System.out.println(rs.getString("Category"));
            }
            System.out.println("-----------------------------" + "\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void getColours() {
        try (Connection con = getConnection();
             Statement statement = con.createStatement();
             ResultSet rs = statement.executeQuery("SELECT NAME as colour FROM colour")) {

            while (rs.next()) {
                System.out.println(rs.getString("colour"));
            }
            System.out.println("-----------------------------");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public void getShoeDetailsByCategory() {
        while (true) {
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter category number: ");
            String categoryNameInput = scanner.next().trim().toLowerCase();


            try (Connection con = getConnection();

                 CallableStatement callgetShoesByCategory = con.prepareCall("CALL GetShoesByCategory(?)")) {

                callgetShoesByCategory.setString(1, categoryNameInput);

                callgetShoesByCategory.executeQuery();
                if (categoryControl(categoryNameInput) == 0) {
                    System.out.println("Category does not exist, please try again");
                    continue;
                }

                try (ResultSet rs = callgetShoesByCategory.getResultSet()) {
                    while (rs.next()) {
                        int article = rs.getInt("ARTICLENUMBER");
                        int price = rs.getInt("price");
                        int size = rs.getInt("size");
                        int balance = rs.getInt("Storage_balance");
                        String brand = rs.getString("brand");
                        String colours = rs.getString("colour");
                        String categories = rs.getString("categories");

                        System.out.print("Article number: " + article + " | ");
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
                throw new RuntimeException("Error retrieving shoes by category"+ e.getMessage());
            }
        }
    }

    public void getShoeDetailsByColour() {
        while (true) {
            // Prompt the user for a colour
            Scanner scanner = new Scanner(System.in);
            System.out.print("Enter colour: ");
            String colourNameInput = scanner.next().trim().toLowerCase();


            try (Connection con = getConnection();

                 CallableStatement callgetShoeDetailsByColour = con.prepareCall("CALL GetShoesByColour(?)")) {


                callgetShoeDetailsByColour.setString(1, colourNameInput);

                callgetShoeDetailsByColour.executeQuery();

                if (colourControl(colourNameInput)==0) {
                    System.out.println("Shoe does not exist, please try again");
                    continue;
                }

                try (ResultSet rs = callgetShoeDetailsByColour.getResultSet()) {
                    while (rs.next()) {
                        int article = rs.getInt("ARTICLENUMBER");
                        int price = rs.getInt("price");
                        int size = rs.getInt("size");
                        int balance = rs.getInt("Storage_balance");
                        String brand = rs.getString("brand");
                        String colours = rs.getString("colours");
                        String categories = rs.getString("categories");

                        System.out.print("Article number: " + article + " | ");
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
                throw new RuntimeException("Error retrieving shoes by category"+  e.getMessage());
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
                    System.out.print("Categories: " + categories);
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
