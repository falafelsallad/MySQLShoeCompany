import java.sql.*;

public class CustomerHandler {
    private final Connection connection;

    public CustomerHandler(Connection connection) {
        this.connection = connection;
    }

    public int login(String username, String password) throws SQLException {
        try (CallableStatement statement = connection.prepareCall("CALL CustomerLogin (?, ?, ?)")) {
            statement.setString(1, username);
            statement.setString(2, password);
            statement.registerOutParameter(3, Types.INTEGER);
            statement.executeQuery();

            return statement.getInt(3);
        }
    }

    public int checkUsername(String username) throws SQLException {
        try (CallableStatement statement = connection.prepareCall("CALL UsernameControl(?, ?)")) {
            statement.setString(1, username);
            statement.registerOutParameter(2, Types.INTEGER);
            statement.executeQuery();
            return statement.getInt(2);
        }
    }

    public void WelcomeMessage(int customerIDFromLogIn) throws SQLException {
        String query = "SELECT customer.firstname, customer.lastname FROM customer WHERE ID = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setInt(1, customerIDFromLogIn);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    System.out.print("Welcome " + rs.getString("firstname") + " ");
                    System.out.println(rs.getString("lastname") + "!");
                }
            }
        }
    }

    public int getPaymentStatus(int customersIDfromLogin) throws SQLException {
        int orderID;
        try (CallableStatement callPaymentStatus = connection.prepareCall("CALL CheckPaymentStatus(?, ?)")) {
            callPaymentStatus.setInt(1, customersIDfromLogin);
            callPaymentStatus.registerOutParameter(2, Types.INTEGER);

            callPaymentStatus.executeQuery();
            return callPaymentStatus.getInt(2);

        }
    }

    public int createOrder(int customerID) {
        int orderID = 0;
        try (CallableStatement callCreateOrder = connection.prepareCall("CALL CreateOrder(?,?)")) {
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
}
