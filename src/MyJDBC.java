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
            Repository repository = new Repository();

            Scanner scan = new Scanner(System.in);
//            repository.getColours();
//            repository.getShoeDetailsByColour();
//           int customerID = repository.startLogIn();
//           int orderID = repository.getPaymentStatus(customerID);
//            repository.getCategories(); // TODO: FIXA SÅ ATT MAN KAN VÄLJA KATEGORI
//            repository.getShoeDetailsByCategory();
//            //repository.AddToCart(orderID);
//            System.out.println("Är du klar med ditt köp?? Y/N");
//            String svar= scan.next();
//            if (svar.equalsIgnoreCase("Y")){
//                repository.confirmPurchase(orderID);
//                System.out.println("Din betalning är klar");
//            }



        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}

