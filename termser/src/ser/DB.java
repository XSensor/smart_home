package ser;

import java.sql.*;

/**
 * 数据库操作
 */
public class DB {
    private static Connection conn = null;

    static {
        try {
            Class.forName("org.sqlite.JDBC");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
    /**
     * 获取数据库连接
     * @return
     * @throws SQLException
     * @throws ClassNotFoundException
     */
    public static Connection getConnection() throws SQLException, ClassNotFoundException {
        boolean needConnect = false;
        if(conn == null) needConnect = true;
        else if(conn.isClosed()) needConnect = true;
        if(needConnect) {
            conn = DriverManager.getConnection("jdbc:sqlite:data.db");
        }
        return conn;
    }

    /**
     * 获取一个数据库Statement实例
     * @return
     */
    public static Statement getStatement(){
        try{
            Connection c = getConnection();
            return c.createStatement();
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
    }
    // 获取一个 PreparedStatement
    public static PreparedStatement preStatement(String sql) {
        try {
            return getConnection().prepareStatement(sql);
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    // 执行查询操作
    public static ResultSet executeQuery(String sql){
        ResultSet set=null;
        try{
            Statement state=getStatement();
            set = state.executeQuery(sql);
        }catch(Exception ex){
            ex.printStackTrace();
            return null;
        }
        return set;
    }
    // 判断一个表是否存在
    public static boolean existTable(String table) {
        try {
            getStatement().executeQuery("select count(*) from " + table);
            return true;
        } catch(Exception e) {
            return false;
        }
    }
    // 执行一条SQL语句
    public static boolean execute(String sql){
        try{
            Statement state = getStatement();
            return state.execute(sql);
        }catch(Exception ex){
            ex.printStackTrace();
            return false;
        }
    }
    // 获取表的行数
    public static int getRowCount(String table) { return getRowCount(table, null); }
    public static int getRowCount(String table, String condition) {
        int count = -1;
        String sql = "SELECT COUNT(*) FROM " + table;
        if(condition != null)
            sql += condition;
        try {
            PreparedStatement ps = DB.preStatement(sql);
            ResultSet rs = ps.executeQuery();
            if(rs.next());
                count = rs.getInt(1);
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return count;
    }
}