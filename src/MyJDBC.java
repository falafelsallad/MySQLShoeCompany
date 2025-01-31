import java.sql.*;
import java.util.Scanner;

public class MyJDBC {
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/shoeforsale?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
                    "trash",
                    "password"
            );
            Statement statement = connection.createStatement();
            Scanner scan = new Scanner(System.in);

            System.out.println("Enter username:");
            String username = scan.next();

            System.out.println("Enter password:");
            String password = scan.next();

            CallableStatement callLogin = connection.prepareCall("{call CustomerLogin(?, ?, ?)}");
            callLogin.setString(1, username);
            callLogin.setString(2, password);
            callLogin.registerOutParameter(3, Types.INTEGER);
            callLogin.executeQuery();
            int customerID = callLogin.getInt(3);

            if (callLogin.getInt(3) > 0) {
                System.out.println("Login successful, customer " + customerID);
                ResultSet resultSet = statement.executeQuery("SELECT customer.firstname, customer.lastname FROM customer WHERE ID = " + customerID);
                while (resultSet.next()) {
                    System.out.print("Welcome " + resultSet.getString("firstname") + " ");
                    System.out.println(resultSet.getString("lastname") + "!");
                }
            } else {
                System.out.println("Login failed");
            }


            CallableStatement callPaymentStatus = connection.prepareCall("{call checkpaymentstatus(? , ?)}");
            callPaymentStatus.setInt(1, customerID);
            callPaymentStatus.registerOutParameter(2, Types.INTEGER);
            callPaymentStatus.executeQuery();
            int activeOrderID = callPaymentStatus.getInt(2);
            if (activeOrderID == 0) {
                System.out.println("No active order");
            } else {
                System.out.println("Active Order ID: " + activeOrderID);
            }



            //4, 1, 1, 1
            CallableStatement callAddToCart = connection.prepareCall("{call AddToCart(?, ?, ?, ?)}");
            callAddToCart.setInt(1, customerID);
            callAddToCart.setInt(2, activeOrderID); // orderID
            callAddToCart.setInt(3, 1); // ShoeID
            callAddToCart.setInt(4, 1); // Quantity
            callAddToCart.executeQuery();

            ResultSet resultSet = statement.executeQuery("SELECT * FROM orderitem WHERE orderID = " + activeOrderID);

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



}

