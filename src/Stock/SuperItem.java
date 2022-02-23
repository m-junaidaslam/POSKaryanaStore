package Stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import Utils.SqliteConnection;


public class SuperItem {
	/*------------------------------------------------*
	 *--------------Members Declaration---------------*
	 *------------------------------------------------*/
	private int id;
	private String name;
	private int elements;
	
	/*------------------------------------------------*
	 *------------------Constructors------------------*
	 *------------------------------------------------*/
	public SuperItem() {
		this.id = 0;
		this.name = "";
		this.elements = 0;		
	}
	
	public SuperItem(String name, int elements) {
		this.name = name;
		this.elements = elements;
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

	public int getElements() {
		return elements;
	}

	public void setElements(int elements) {
		this.elements = elements;
	}
	
	/*------------------------------------------------*
	 *------------------Methods-----------------------*
	 *------------------------------------------------*/
	
	/*------------Insert New SuperItem in Database---------------*/
	public int insertNewSuperItem(Connection connection) {
		int countMatch = 0;
		int matchId = 0;
		//System.out.println("Came into new Super Item with Count Match: " + countMatch);
		try {
			String query = "select EID from SuperItemsInfo where Name='"+name+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				countMatch++;
				matchId = rs.getInt("EID");
			}

			pst.close();
			rs.close();
		}catch(Exception e) {
			e.printStackTrace();
		}
		if(countMatch <= 0) {
			try {
				String query = "insert into SuperItemsInfo (EID, Name, Elements) values (?, ?, ?)";
				PreparedStatement pst = connection.prepareStatement(query);
				this.id = SqliteConnection.generateKey(connection, "SuperItemsInfo");
				String primayKey = Integer.toString(this.id);
				pst.setString(1, primayKey);
				pst.setString(2, name);
				pst.setInt(3, elements);
				pst.execute();
				
				pst.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		} else {
//			javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
			JOptionPane.showMessageDialog(null, "آئٹم کی یہ مخصوص قسم پہلے سے موجود ہے۔");
			return matchId;
		}
		return this.id;
	}
	
	/*----------------------------Delete SuperItem from Database------------------------*/
	public void deleteSuperItemFromDatabase(Connection connection, int eid) {
		try {
			String query1 = "delete from SuperItemsInfo where EID='" + eid + "'";
			PreparedStatement pst1 = connection.prepareStatement(query1);
			pst1.execute();
			pst1.close();
			
			String query2 = "select EID from ItemsInfo where SuperItem='"+eid+"'";
			PreparedStatement pst2 = connection.prepareStatement(query2);
			ResultSet rs1 = pst2.executeQuery();
			
			int[] itemIds = new int[rs1.getFetchSize()];
			int itemsCount = 0;
			while(rs1.next()) {
				itemIds[itemsCount] = rs1.getInt("EID");
			}
			pst2.close();
			rs1.close();
			
//			javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
			int action = JOptionPane.showConfirmDialog(null,
					"کیا آپ واقعی اس قسم کے ہر آئٹم کو ختم کرنا چاہتے ہیں ؟", "Delete",
					JOptionPane.YES_NO_OPTION);
			if (action == 0) {
				for(int i = 0; i < itemIds.length; i++) {
					String type = new String();
					try {
						String query3 = "select Type from ItemsInfo where EID='"
								+ itemIds[i] + "'";
						PreparedStatement pst3 = connection.prepareStatement(query3);
						ResultSet rs2 = pst3.executeQuery();
						while (rs2.next())
							type = rs2.getString("Type");
						pst3.close();
						rs2.close();
	
					} catch (Exception e) {
						e.printStackTrace();
					}
	
					deleteItemFromDatabase(connection, itemIds[i], "ItemsInfo");
	
					int intType = Integer.parseInt(type, 2);
					if (intType > Integer.parseInt("00000001111", 2)) {
						if (type.charAt(6) == '1') {
							deleteItemFromDatabase(connection, itemIds[i], "PacketItemsRates");
						}
						if (type.charAt(5) == '1') {
							deleteItemFromDatabase(connection, itemIds[i], "CartonItemsRates");
						}
						if (type.charAt(4) == '1') {
							deleteItemFromDatabase(connection, itemIds[i], "BucketItemsRates");
						}
						if (type.charAt(3) == '1') {
							deleteItemFromDatabase(connection, itemIds[i], "DozenItemsRates");
						}
						if (type.charAt(2) == '1') {
							deleteItemFromDatabase(connection, itemIds[i], "BottleItemsRates");
						}
						if (type.charAt(1) == '1') {
							deleteItemFromDatabase(connection, itemIds[i], "TinItemsRates");
						}
						if (type.charAt(0) == '1') {
							deleteItemFromDatabase(connection, itemIds[i], "GallonItemsRates");
						}
					}
					if ((type.charAt(7) == '1') || (type.charAt(8) == '1')
							|| (type.charAt(9) == '1') || (type.charAt(10) == '1')) {
						deleteItemFromDatabase(connection, itemIds[i], "NonSizedItemsRates");
//						javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
						JOptionPane
								.showMessageDialog(null, "آئٹم ختم کر دیا گیا !");
					}
				}
			}


		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/*----------------------------Delete Item Completely from Database------------------------*/
	public void deleteItemFromDatabase(Connection connection, int eid, String table) {
		try {
			String query = "delete from " + table + " where EID='" + eid + "'";
			PreparedStatement pst = connection.prepareStatement(query);
			pst.execute();
			pst.close();

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	/*-----------------------------Getting Item Using Id from Database-----------------------------------*/
	public void getSuperItemFromDb(Connection connection) {
		try {
			String query = "select * from SuperItemsInfo where EID='"+this.id+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				this.name = rs.getString("Name");
				this.elements = rs.getInt("Elements");
			}
			pst.close();
			rs.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
}
