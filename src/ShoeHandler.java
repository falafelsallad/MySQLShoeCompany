import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ShoeHandler {

    private final Connection connection;


    public ShoeHandler(Connection connection) {
        this.connection = connection;
    }

    public List<Shoe> getShoeDetailsByColour(String colour) throws SQLException {
        List<Shoe> shoes = new ArrayList<>();
        try(CallableStatement callgetShoeDetailsByColour = connection.prepareCall("CALL GetShoesByColour(?)")) {
            callgetShoeDetailsByColour.setString(1, colour);
            try(ResultSet rs = callgetShoeDetailsByColour.executeQuery()) {
                while(rs.next()) {
                    Shoe shoe = new Shoe(
                    rs.getInt("ARTICLENUMBER"),
                    rs.getInt("price"),
                    rs.getInt("size"),
                    rs.getInt("Storage_balance"),
                    rs.getString("brand"),
                    rs.getString("colours"),
                    rs.getString("categories")
                    );
                    shoes.add(shoe);
                }
            }
        }

        return shoes;
    }
}
