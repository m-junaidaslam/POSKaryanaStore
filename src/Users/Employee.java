package Users;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import org.mindrot.jbcrypt.BCrypt;
import Utils.SqliteConnection;

public class Employee {
	
	/*------------------------------------------------*
	 *--------------Members Declaration---------------*
	 *------------------------------------------------*/
	private boolean isAdmin;

	private int id;
	private static int workload = 12;	//Encryption/Decryption Level
	
	private String name;
	private String surname;
	private String username;
	private String password;
	
	/*------------------------------------------------*
	 *------------------Constructors------------------*
	 *------------------------------------------------*/
	
	public Employee() {
		super();
		this.isAdmin = false;
		this.id = 0;
		this.name = null;
		this.surname = null;
		this.username = null;
		this.password = null;
	}
	
	public Employee(boolean isAdmin, String name,
			String surname, String username, String password) {
		super();
		this.isAdmin = isAdmin;
		this.name = name;
		this.surname = surname;
		this.username = username;
		this.password = password;
	}
	
	/*------------------------------------------------*
	 *--------------Getters and Setters---------------*
	 *------------------------------------------------*/
	public void setAdmin(Boolean isAdmin) {
		this.isAdmin = isAdmin;
	}
	public boolean isAdmin() {
		return isAdmin;
	}
	public int getId() {
		return id;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getSurname() {
		return surname;
	}
	public void setSurname(String surname) {
		this.surname = surname;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	/*------------------------------------------------*
	 *---------------------Methods--------------------*
	 *------------------------------------------------*/
	
	/*---------------Decrypt and Check Password------------------*/
	public static boolean checkPassword(String password_plaintext, String stored_hash) {
		boolean password_verified = false;

		if (null == stored_hash || !stored_hash.startsWith("$2a$"))
			throw new java.lang.IllegalArgumentException("Invalid hash provided for comparison");
		password_verified = BCrypt.checkpw(password_plaintext, stored_hash);
			
		return (password_verified);
	}
	
	/*-----------------------Hash Password-----------------------*/
	public static String hashPassword(String password_plaintext) {
		String salt = BCrypt.gensalt(workload);
		String hashed_password = BCrypt.hashpw(password_plaintext, salt);

		return (hashed_password);
	}
	
	/*------------Insert New Employee in Database---------------*/
	public void insertNewEmployee(Connection connection) {
		int countMatch = 0;
		try {
			String query = "select Username from EmployeeInfo where Username='"+username+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				countMatch++;
			}
			//System.out.println(hashed_password);
			
			pst.close();
			rs.close();
			
		}catch(Exception e) {
			e.printStackTrace();
//			javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
			JOptionPane.showMessageDialog(null, "غلط نام یا پاسورڈ !");
		}
		
		if(countMatch <= 0) {
			try {
				String query = "insert into EmployeeInfo (EID, Name, Surname, IsAdmin, Username, Password) values (?, ?, ?, ?, ?, ?)";
				PreparedStatement pst = connection.prepareStatement(query);
				String primayKey = Integer.toString(SqliteConnection.generateKey(connection, "EmployeeInfo"));
				pst.setString(1, primayKey);
				pst.setString(2, name);
				pst.setString(3, surname);
				pst.setBoolean(4, isAdmin);
				pst.setString(5, username);
				pst.setString(6, hashPassword(password));
				pst.execute();
				
//				javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
				JOptionPane.showMessageDialog(null, "اکاؤنٹ بن گیا !");
				
				pst.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
//			javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
			JOptionPane.showMessageDialog(null, "یہ نام پہلے سے موجود ہے !");
			
		}
	}
	
	/*---------------------Login Attempt Check--------------------------*/
	public Boolean loginAttempCheck(Connection connection) {
		String hashed_password = "";
		boolean pass = false;
		boolean user = false;
		try {
			String query = "select Password from EmployeeInfo where Username='"+username+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				hashed_password = rs.getString("Password");
				user = true;
			}
			if(user)
				pass = checkPassword(password, hashed_password);
			pst.close();
			rs.close();
			
		}catch(Exception e) {
			e.printStackTrace();
//			javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
			JOptionPane.showMessageDialog(null, "غلط نام یا پاسورڈ !");
		}
		
		return pass;
		
	}
	
	/*-----------------Get Employee's Data From Database---------------------*/
	public void getDataFromDb(Connection connection, String username) {
		try {	
			String query = "select * from EmployeeInfo where Username='"+username+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				this.id = rs.getInt("EID");
				this.isAdmin = rs.getBoolean("IsAdmin");
				this.name = rs.getString("Name");
				this.surname = rs.getString("Surname");
				this.username = rs.getString("Username");
				this.password = rs.getString("Password");
			}
			
			pst.close();
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}