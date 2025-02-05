import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Scanner;

public class MyJDBC {
    public static void main(String[] args) {
        try {
            Connection connection = DriverManager.getConnection(
                    "jdbc:mysql://127.0.0.1:3306/shoeforsale?serverTimezone=UTC&useSSL=false&allowPublicKeyRetrieval=true",
                    "root",
                    "Elonmusk3000"
            );
            Repository repository = new Repository();
            Scanner scan = new Scanner(System.in);
            int customerID = repository.startLogIn();
            int orderID = repository.getPaymentStatus(customerID);

            while (true) {
                System.out.print("Do you want to choose by category or shoe?");
                System.out.println("CO=colour or CA=Category");
                System.out.println("-----------------------");
                System.out.println("To pay please type PAY, else your order will be saved for a later date.");
                System.out.println("Leave program by typing EXIT");
                String answer = scan.next().trim();

                if (answer.equalsIgnoreCase("CO") || answer.equalsIgnoreCase("colour")) {
                    repository.getColours();
                    repository.getShoeDetailsByColour();
                    repository.AddToCart(orderID);

                } else if (answer.equalsIgnoreCase("CA") || answer.equalsIgnoreCase("category")) {
                    repository.getCategories();
                    repository.getShoeDetailsByCategory();
                    repository.AddToCart(orderID);

                } else if (answer.equalsIgnoreCase("PAY")) {
                    repository.confirmPurchase(orderID);
                    System.out.println("Your order has been marked as paid, thank you!");
                    break;
                } else if (answer.equalsIgnoreCase("Exit")) {
                    System.out.println("Thank you for shopping! Your order has been saved.");
                    break;
                } else
                    System.out.println("You have to insert something");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

}
