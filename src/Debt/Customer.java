package Debt;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JOptionPane;
import Utils.SqliteConnection;

public class Customer {
	
	/*------------------------------------------------*
	 *--------------Members Declaration---------------*
	 *------------------------------------------------*/

	private int id;
	private String name;
	private String surname;
	private String place;
	private String date;
	private double debt;
	
	/*------------------------------------------------*
	 *------------------Constructors------------------*
	 *------------------------------------------------*/
	
	public Customer() {
		super();
		this.id = 0;
		this.name = null;
		this.surname = null;
		this.place = null;
		this.date = null;
		this.debt = 0;
	}
	
	public Customer(String name, String surname, String place,
			String date, double debt) {
		super();
		this.name = name;
		this.surname = surname;
		this.place = place;
		this.date = date;
		this.debt = debt;
	}
	
	
	/*------------------------------------------------*
	 *--------------Getters and Setters---------------*
	 *------------------------------------------------*/
	
	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
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

	public String getPlace() {
		return place;
	}

	public void setPlace(String place) {
		this.place = place;
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public double getDebt() {
		return debt;
	}

	public void setDebt(double debt) {
		this.debt = debt;
	}
	
	/*------------------------------------------------*
	 *---------------------Methods--------------------*
	 *------------------------------------------------*/
	
		
	/*------------Insert New Customer in Database---------------*/
	public void insertNewCustomer(Connection connection, int rowCount) {
		int countMatch = 0;
		if(rowCount > 0) {
			try {
				String query = "select Name from DebtInfo where Name='"+this.name+"'";
				PreparedStatement pst = connection.prepareStatement(query);
				ResultSet rs = pst.executeQuery();
				
				while(rs.next()) {
					countMatch++;
				}
				//System.out.println(countMatch);
				pst.close();
				rs.close();
				
			}catch(Exception e) {
				e.printStackTrace();
//				javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
				JOptionPane.showMessageDialog(null, "ڈیٹا بیس میں مسئلہ ہے !");
			}
		}
		//System.out.print(rowCount);
		if(countMatch <= 0) {
			try {
				String query = "insert into DebtInfo (EID, Name, Surname, Place, Date, Debt) values (?, ?, ?, ?, ?, ?)";
				PreparedStatement pst = connection.prepareStatement(query);
				String primayKey = Integer.toString(SqliteConnection.generateKey(connection, "DebtInfo"));
				pst.setString(1, primayKey);
				pst.setString(2, name);
				pst.setString(3, surname);
				pst.setString(4, place);
				pst.setString(5, date);
				pst.setDouble(6, debt);
				pst.execute();
				
//				javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
				JOptionPane.showMessageDialog(null, "گاہک ڈال دیا گیا !");
				
				pst.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
//			javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
			JOptionPane.showMessageDialog(null, "یہ نام پہلے سے موجود ہے !");
			
		}
	}
	
	
	/*-----------------Get All Customer's Data From Database---------------------*/
	public void getDataFromDb(Connection connection, int id) {
		try {	
			String query = "select * from DebtInfo where EID='"+id+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				this.id = rs.getInt("EID");
				this.name = rs.getString("Name");
				this.surname = rs.getString("Surname");
				this.place = rs.getString("Place");
				this.date = rs.getString("Date");
				this.debt = rs.getDouble("Debt");
			}
			
			pst.close();
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*--------------------------Update Customer----------------------------*/
	public void updateCustomer(Connection connection) {
		try {
			String query = "Update DebtInfo set Debt='"+this.debt+"', Date='"+this.date+"' where EID='"+this.id+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			
			pst.execute();
			
			pst.close();
		} catch(Exception e) {
			e.printStackTrace();
		}	
	}

}