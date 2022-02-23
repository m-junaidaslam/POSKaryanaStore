package Utils;

//import java.sql.*;

public class SqliteConnection3 {
//	Connection conn = null;
//	
//	public static Connection dbConnector() {
//		
//		try {
//			Class.forName("org.sqlite.JDBC");
//			String dbUrl = "jdbc:sqlite:"+System.getProperty("user.dir").replace("\\", "\\\\")+"\\StoreDB.sqlite";
//			//Connection conn = DriverManager.getConnection("jdbc:sqlite:F:\\Workspaces\\JAVA Windows\\MalikAslamKaryanaStore\\DBs\\StoreDB.sqlite");
//			//Connection conn = DriverManager.getConnection("jdbc:sqlite::resource:StoreDB.sqlite");
//			Connection conn = DriverManager.getConnection(dbUrl);
//			
//			return conn;
//		}catch(Exception e) {
//			e.printStackTrace();
//			return null;
//		}
//	}
//
//	public static int generateKey(Connection connection, String table) {
//		int generatedKey = 1;
//		int refKey = 1;
//		try {
//			String query = "select EID from "+table;
//			PreparedStatement pst = connection.prepareStatement(query);
//			ResultSet rs = pst.executeQuery();
//			
//			while(rs.next()) {
//				
//				int key = Integer.valueOf(rs.getString("EID"));
//				if(refKey == key)
//					generatedKey = ++key;
//				else {
//					generatedKey = refKey;
//					break;
//				}
//				refKey++;
//				
//			}
//			
//		} catch(Exception e) {
//			e.printStackTrace();
//		}
//		return generatedKey;
//	}
}
