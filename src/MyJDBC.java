import java.sql.*;
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

            Statement statement = connection.createStatement();
            Scanner scan = new Scanner(System.in);


//            repository.startLogIn();
//            repository.getCategories();
            repository.getColours();

            repository.getShoeInfo(2);

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



}

