package ser;

import java.net.InetSocketAddress;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Main {
    public static boolean verify(String user, String passwd) {
        PreparedStatement ps = DB.preStatement("select * from user where name=? and passwd=?");
        try {
            ps.setString(1, user);
            ps.setString(2, passwd);
            ResultSet rs = ps.executeQuery();
            return rs.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static void main(String[] args) {
	// write your code here
        if (!DB.existTable("user")) {
            DB.execute("CREATE TABLE user(" +
                    "name VARCHAR(20)," +
                    "passwd VARCHAR(64)," +
                    "id int)");
            DB.execute("INSERT INTO user VALUES('test', '12345', '1')");
        }

        TermServer ter = new TermServer(new InetSocketAddress("0.0.0.0", 8008));
        new Thread(ter).start();

        AppServer ser = new AppServer(new InetSocketAddress("0.0.0.0", 8006));
        ser.run();
    }
}
