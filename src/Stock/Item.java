package Stock;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.swing.JOptionPane;
import Utils.SqliteConnection;

public class Item {

	/*------------------------------------------------*
	 *--------------Members Declaration---------------*
	 *------------------------------------------------*/
	private int id;
	private boolean isSized;
	private boolean hasSpecial;
	
	private String name;
	private String type;
	private int intType;
	private int superItem;
	
	private double priceKg = 0d;
	private double priceGattu = 0d;
	private double priceBox = 0d;
	private double priceBundle = 0d;
	
	private double spriceKg = 0d;
	private double spriceGattu = 0d;
	private double spriceBox = 0d;
	private double spriceBundle = 0d;
	
	private double stockKg = 0;
	private double stockGattu = 0;
	private double stockBox = 0;
	private double stockBundle = 0;
	
	private String relationKg;
	private String relationGattu;
	private String relationBox;
	private String relationBundle;
	
	private SizedItem pricePacket;
	private SizedItem priceCarton;
	private SizedItem priceBucket;
	private SizedItem priceDozen;
	private SizedItem priceBottle;
	private SizedItem priceTin;
	private SizedItem priceGallon;
	
	
	/*------------------------------------------------*
	 *------------------Constructors------------------*
	 *------------------------------------------------*/
	public Item() {
		super();
		this.id = 0;
		this.type = "00000000000";
		this.isSized = false;
		this.hasSpecial = false;
		this.name = "";
		this.superItem = 0;
		this.priceKg = 0;
		this.stockKg = 0;
		this.relationKg = "اسٹاک";
		this.priceGattu = 0;
		this.stockGattu = 0;
		this.relationGattu = "اسٹاک";
		this.priceBundle = 0;
		this.stockBundle = 0;
		this.relationBundle = "اسٹاک";
		this.priceBox = 0;
		this.stockBox = 0;
		this.relationBox = "اسٹاک";
		
		this.pricePacket = new SizedItem();
		this.priceCarton = new SizedItem();
		this.priceBucket = new SizedItem();
		this.priceDozen = new SizedItem();
		this.priceBottle = new SizedItem();
		this.priceTin = new SizedItem();
		this.priceGallon = new SizedItem();
		
	}
	
	public Item(String type, String name, int superItem, double priceKg, double stockKg, String relationKg,
			double priceGattu, double stockGattu, String relationGattu, double priceBundle, double stockBundle, 
			String relationBundle, double priceBox, double stockBox, String relationBox, SizedItem pricePacket,
			SizedItem priceCarton, SizedItem priceBucket, SizedItem priceDozen,
			SizedItem priceBottle, SizedItem priceTin, SizedItem priceGallon, boolean hasSpecial) {
		super();
		this.type = type;
		this.name = name;
		this.superItem = superItem;
		this.priceKg = priceKg;
		this.stockKg = stockKg;
		this.relationKg = relationKg;
		this.priceGattu = priceGattu;
		this.stockGattu = stockGattu;
		this.relationGattu = relationGattu;
		this.priceBundle = priceBundle;
		this.stockBundle = stockBundle;
		this.relationBundle = relationBundle;
		this.priceBox = priceBox;
		this.stockBox = stockBox;
		this.relationBox = relationBox;
		this.pricePacket = pricePacket;
		this.priceCarton = priceCarton;
		this.priceBucket = priceBucket;
		this.priceDozen = priceDozen;
		this.priceBottle = priceBottle;
		this.priceTin = priceTin;
		this.priceGallon = priceGallon;
		this.hasSpecial = hasSpecial;
		
		this.intType = Integer.parseInt(type, 2);
		
		if(this.intType > Integer.parseInt("00000001111", 2))
			isSized = true;
		else 
			isSized = false;
	}
	
	
	/*------------------------------------------------*
	 *--------------Getters and Setters---------------*
	 *------------------------------------------------*/
	
	public void setId(int id) {
		this.id = id;
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
	public int getSuperItem() {
		return superItem;
	}
	public void setSuperItem(int superItem) {
		this.superItem = superItem;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public double getPriceKg() {
		return priceKg;
	}
	public void setPriceKg(double priceKg) {
		this.priceKg = priceKg;
	}
	public SizedItem getPricePacket() {
		return pricePacket;
	}
	public void setPricePacket(SizedItem pricePacket) {
		this.pricePacket = pricePacket;
	}
	public SizedItem getPriceCarton() {
		return priceCarton;
	}
	public void setPriceCarton(SizedItem priceCarton) {
		this.priceCarton = priceCarton;
	}
	public SizedItem getPriceBucket() {
		return priceBucket;
	}
	public void setPriceBucket(SizedItem priceBucket) {
		this.priceBucket = priceBucket;
	}
	public double getPriceBundle() {
		return priceBundle;
	}
	public void setPriceBundle(double priceBundle) {
		this.priceBundle = priceBundle;
	}
	public SizedItem getPriceDozen() {
		return priceDozen;
	}
	public void setPriceDozen(SizedItem priceDozen) {
		this.priceDozen = priceDozen;
	}
	public SizedItem getPriceBottle() {
		return priceBottle;
	}
	public void setPriceBottle(SizedItem priceBottle) {
		this.priceBottle = priceBottle;
	}
	public SizedItem getPriceTin() {
		return priceTin;
	}
	public void setPriceTin(SizedItem priceTin) {
		this.priceTin = priceTin;
	}
	public double getPriceGattu() {
		return priceGattu;
	}
	public void setPriceGattu(double priceGattu) {
		this.priceGattu = priceGattu;
	}
	public boolean isSized() {
		return isSized;
	}
	public double getStockKg() {
		return stockKg;
	}
	public void setStockKg(double stockKg) {
		this.stockKg = stockKg;
	}
	public double getStockGattu() {
		return stockGattu;
	}
	public void setStockGattu(double stockGattu) {
		this.stockGattu = stockGattu;
	}
	public double getStockBox() {
		return stockBox;
	}
	public void setStockBox(double stockBox) {
		this.stockBox = stockBox;
	}
	public double getStockBundle() {
		return stockBundle;
	}
	public void setStockBundle(double stockBundle) {
		this.stockBundle = stockBundle;
	}
	public double getPriceBox() {
		return priceBox;
	}
	public void setPriceBox(double priceBox) {
		this.priceBox = priceBox;
	}
	public void setpriceGallon(SizedItem priceGallon) {
		this.priceGallon = priceGallon;
	}
	public SizedItem getPriceGallon() {
		return priceGallon;
	}
	public void setPriceGallon(SizedItem priceGallon) {
		this.priceGallon = priceGallon;
	}
	public int getIntType() {
		return intType;
	}
	public void setIntType(int intType) {
		this.intType = intType;
	}
	public double getSpriceKg() {
		return spriceKg;
	}

	public void setSpriceKg(double spriceKg) {
		this.spriceKg = spriceKg;
	}

	public double getSpriceGattu() {
		return spriceGattu;
	}

	public void setSpriceGattu(double spriceGattu) {
		this.spriceGattu = spriceGattu;
	}

	public double getSpriceBox() {
		return spriceBox;
	}

	public void setSpriceBox(double spriceBox) {
		this.spriceBox = spriceBox;
	}

	public double getSpriceBundle() {
		return spriceBundle;
	}

	public void setSpriceBundle(double spriceBundle) {
		this.spriceBundle = spriceBundle;
	}
	
	public String getRelationKg() {
		return relationKg;
	}

	public void setRelationKg(String relationKg) {
		this.relationKg = relationKg;
	}

	public String getRelationGattu() {
		return relationGattu;
	}

	public void setRelationGattu(String relationGattu) {
		this.relationGattu = relationGattu;
	}

	public String getRelationBox() {
		return relationBox;
	}

	public void setRelationBox(String relationBox) {
		this.relationBox = relationBox;
	}

	public String getRelationBundle() {
		return relationBundle;
	}

	public void setRelationBundle(String relationBundle) {
		this.relationBundle = relationBundle;
	}
	
	public boolean isHasSpecial() {
		return hasSpecial;
	}

	public void setHasSpecial(boolean hasSpecial) {
		this.hasSpecial = hasSpecial;
	}
	
	
	
	/*---------------------Add Special Prices-------------------*/
	public void addSpecialPrices(Connection connection, double spriceKg, double spriceGattu, double spriceBox, double spriceBundle, double[] spricePacket, double[] spriceCarton, double[] spriceBucket, double[] spriceDozen, double[] spriceBottle, double[] spriceTin, double[] spriceGallon) {
		this.spriceKg = spriceKg;
		this.spriceGattu = spriceGattu;
		this.spriceBox = spriceBox;
		this.spriceBundle = spriceBundle;
		
		for(int i = 0; i < 5; i++) {
			this.pricePacket.setSizeSPrice(spricePacket[i], i);
			this.priceCarton.setSizeSPrice(spriceCarton[i], i);
			this.priceBucket.setSizeSPrice(spriceBucket[i], i);
			this.priceDozen.setSizeSPrice(spriceDozen[i], i);
			this.priceBottle.setSizeSPrice(spriceBottle[i], i);
			this.priceTin.setSizeSPrice(spriceTin[i], i);
			this.priceGallon.setSizeSPrice(spriceGallon[i], i);
		}
		updateThisItem(connection, this.id);
	}

	/*------------Insert New Item in Database---------------*/
	public void insertNewItem(Connection connection) {
		int countMatch = 0;
		try {
			String query = "select Name from ItemsInfo where Name='"+name+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				countMatch++;
			}

			pst.close();
			rs.close();
			
		}catch(Exception e) {
			e.printStackTrace();
		}
		
		if(countMatch <= 0) {
			try {
				String query = "insert into ItemsInfo (EID, Name, SuperItem, Type, HasSpecial) values (?, ?, ?, ?, ?)";
				PreparedStatement pst = connection.prepareStatement(query);
				this.id = SqliteConnection.generateKey(connection, "ItemsInfo");
				String primaryKey = Integer.toString(this.id);
				pst.setString(1, primaryKey);
				pst.setString(2, name);
				pst.setInt(3, superItem);
				pst.setString(4, type);
				pst.setBoolean(5, hasSpecial);
				pst.execute();
				
				pst.close();
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			if(superItem > 0) {
				int pastElements = 0;
				try {
					String query = "select Elements from SuperItemsInfo where EID='"+superItem+"'";
					PreparedStatement pst = connection.prepareStatement(query);
					ResultSet rs = pst.executeQuery();
					while(rs.next()) {
						pastElements = rs.getInt("Elements");
					}
					pst.close();
					rs.close();
					
				}catch(Exception e) {
					e.printStackTrace();
				}
				
				try {
					pastElements++;
					String query = "Update SuperItemsInfo set Elements='"+pastElements+"' where EID='"+superItem+"'";
					PreparedStatement pst = connection.prepareStatement(query);
					pst.execute();
					
					pst.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
			if(isSized) {
				if(type.charAt(6) == '1') {
					setSizedItems(connection, "PacketItemsRates", this.pricePacket);
				}
				if(type.charAt(5) == '1') {
					setSizedItems(connection, "CartonItemsRates", this.priceCarton);
				}
				if(type.charAt(4) == '1') {
					setSizedItems(connection, "BucketItemsRates", this.priceBucket);
				}
				if(type.charAt(3) == '1') {
					setSizedItems(connection, "DozenItemsRates", this.priceDozen);
				}
				if(type.charAt(2) == '1') {
					setSizedItems(connection, "BottleItemsRates", this.priceBottle);
				}
				if(type.charAt(1) == '1') {
					setSizedItems(connection, "TinItemsRates", this.priceTin);
				}
				if(type.charAt(0) == '1') {
					setSizedItems(connection, "GallonItemsRates", this.priceGallon);
				}
			}
			if((type.charAt(7) == '1') || (type.charAt(8) == '1') || (type.charAt(9) == '1') || (type.charAt(10) == '1')){
				try {
					String query = "insert into NonSizedItemsRates (EID, KgPrice, GattuPrice, BoxPrice, BundlePrice, KgStock, GattuStock, BoxStock, BundleStock, KgRelation, BoxRelation) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
					PreparedStatement pst = connection.prepareStatement(query);
					pst.setInt(1, this.id);
					pst.setDouble(2, this.priceKg);
					pst.setDouble(3, this.priceGattu);
					pst.setDouble(4, this.priceBox);
					pst.setDouble(5, this.priceBundle);
					pst.setDouble(6, this.stockKg);
					pst.setDouble(7, this.stockGattu);
					pst.setDouble(8, this.stockBox);
					pst.setDouble(9, this.stockBundle);
					pst.setString(10, this.relationKg);
					pst.setString(11, this.relationBox);
					pst.execute();
					
					pst.close();
				} catch(Exception e) {
					e.printStackTrace();
				}
			}
			
		} else {
//			javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
			JOptionPane.showMessageDialog(null, "آ‏‏ئٹم پہلے سے موجود ہے !");
		}
	}
	
	/*-------------------------Add Sized Item into separate tables-------------------*/
	public void setSizedItems(Connection connection, String table, SizedItem item) {
		try {
			String query = new String();
			boolean isRelated = false;
			
			if(table.equals("PacketItemsRates") || table.equals("DozenItemsRates"))
				isRelated = true;
			
			if(isRelated)
				query = "insert into "+ table +" (EID, NameSize1, NameSize2, NameSize3, NameSize4, NameSize5, PriceSize1, PriceSize2, PriceSize3, PriceSize4, PriceSize5, StockSize1, StockSize2, StockSize3, StockSize4, StockSize5, RelationSize1, RelationSize2, RelationSize3, RelationSize4, RelationSize5) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			else
				query = "insert into "+ table +" (EID, NameSize1, NameSize2, NameSize3, NameSize4, NameSize5, PriceSize1, PriceSize2, PriceSize3, PriceSize4, PriceSize5, StockSize1, StockSize2, StockSize3, StockSize4, StockSize5) values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";
			PreparedStatement pst = connection.prepareStatement(query);
			pst.setInt(1, this.id);
			for(int i = 0; i < 5; i++) {
				pst.setString((i+2), item.getSizeName(i));
				pst.setDouble((i+7), item.getSizePrice(i));
				pst.setDouble(i+12, item.getSizeStock(i));
				if(isRelated)
					pst.setString((i+17), item.getSizeRelation(i));
				
			}
			
			pst.execute();
			
			pst.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*----------------------------Update Selected Item in Database------------------------*/
	public void updateThisItem(Connection connection, int eid) {
		this.id = eid;
		if(type.charAt(6) == '1') {
			updateSizedItems(connection, "PacketItemsRates", this.pricePacket);
		}
		if(type.charAt(5) == '1') {
			updateSizedItems(connection, "CartonItemsRates", this.priceCarton);
		}
		if(type.charAt(4) == '1') {
			updateSizedItems(connection, "BucketItemsRates", this.priceBucket);
		}
		if(type.charAt(3) == '1') {
			updateSizedItems(connection, "DozenItemsRates", this.priceDozen);
		}
		if(type.charAt(2) == '1') {
			updateSizedItems(connection, "BottleItemsRates", this.priceBottle);
		}
		if(type.charAt(1) == '1') {
			updateSizedItems(connection, "TinItemsRates", this.priceTin);
		}
		if(type.charAt(0) == '1') {
			updateSizedItems(connection, "GallonItemsRates", this.priceGallon);
		}
		if(type.charAt(10) == '1'){
			updateNonSizedItems(connection, "KgPrice", this.priceKg, "KgStock", this.stockKg, "KgSPrice", this.spriceKg);
		}
		if(type.charAt(9) == '1'){
			updateNonSizedItems(connection, "GattuPrice", this.priceGattu, "GattuStock", this.stockGattu, "gattuSPrice", this.spriceGattu);
		}
		if(type.charAt(8) == '1'){
			updateNonSizedItems(connection, "BoxPrice", this.priceBox, "BoxStock", this.stockBox, "BoxSPrice", this.spriceBox);
		}
		if(type.charAt(7) == '1'){
			updateNonSizedItems(connection, "BundlePrice", this.priceBundle, "BundleStock", this.stockBundle, "BundleSPrice", this.spriceBundle);
		}
		
	}
	
	/*------------------------------------Update Sized Item--------------------------------*/
	
	public void updateSizedItems(Connection connection, String table, SizedItem item) {
		try {
			String query = null;
			if(this.hasSpecial)
				query = "Update "+table+" set NameSize1='"+item.getSizeName(0)+"', NameSize2='"+item.getSizeName(1)+
				"', NameSize3='"+item.getSizeName(2)+"', NameSize4='"+item.getSizeName(3)+
				"', NameSize5='"+item.getSizeName(4)+"', PriceSize1='"+item.getSizePrice(0)+
				"', PriceSize2='"+item.getSizePrice(1)+"', PriceSize3='"+item.getSizePrice(2)+
				"', PriceSize4='"+item.getSizePrice(3)+"', PriceSize5='"+item.getSizePrice(4)+
				"', SPriceSize1='"+item.getSizeSPrice(0)+"', SPriceSize2='"+item.getSizeSPrice(1)+
				"', SPriceSize3='"+item.getSizeSPrice(2)+"', SPriceSize4='"+item.getSizeSPrice(3)+
				"', SPriceSize5='"+item.getSizeSPrice(4)+"', StockSize1='"+item.getSizeStock(0)+
				"', StockSize2='"+item.getSizeStock(1)+"', StockSize3='"+item.getSizeStock(2)+
				"', StockSize4='"+item.getSizeStock(3)+"', StockSize5='"+item.getSizeStock(4)+"' where EID='"+this.id+"'";
			else
				query = "Update "+table+" set NameSize1='"+item.getSizeName(0)+"', NameSize2='"+item.getSizeName(1)+"', NameSize3='"+item.getSizeName(2)+"', NameSize4='"+item.getSizeName(3)+"', NameSize5='"+item.getSizeName(4)+"', PriceSize1='"+item.getSizePrice(0)+"', PriceSize2='"+item.getSizePrice(1)+"', PriceSize3='"+item.getSizePrice(2)+"', PriceSize4='"+item.getSizePrice(3)+"', PriceSize5='"+item.getSizePrice(4)+"', StockSize1='"+item.getSizeStock(0)+"', StockSize2='"+item.getSizeStock(1)+"', StockSize3='"+item.getSizeStock(2)+"', StockSize4='"+item.getSizeStock(3)+"', StockSize5='"+item.getSizeStock(4)+"' where EID='"+this.id+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			
			pst.execute();
			
			pst.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*-----------------------------------Update Non Sized Item-----------------------------*/
	public void updateNonSizedItems(Connection connection, String value1, double price, String value2, double stock, String value3, double sprice) {
		try {
			String query = null;
			if(this.hasSpecial)
				query = "Update NonSizedItemsRates set "+value1+"='"+price+"', "+value2+"='"+stock+"', "+value3+"='"+sprice+"' where EID='"+this.id+"'";
			else
				query = "Update NonSizedItemsRates set "+value1+"='"+price+"', "+value2+"='"+stock+"' where EID='"+this.id+"'";
			
			PreparedStatement pst = connection.prepareStatement(query);
			pst.execute();
			pst.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	/*-----------------------------Getting Item Using Id from Database-----------------------------------*/
	public void getItemFromDb(Connection connection) {
	
		try {
			String query = "select Name, SuperItem, Type, HasSpecial from ItemsInfo where EID='"+this.id+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			while(rs.next()) {
				this.name = rs.getString("Name");
				this.superItem = rs.getInt("SuperItem");
				this.type = rs.getString("Type");
				this.hasSpecial = rs.getBoolean("HasSpecial");
			}
			pst.close();
			rs.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		
		getNonSizedItems(connection);
		if(type.charAt(6) == '1') {
			this.pricePacket = getSizedItems(connection, "PacketItemsRates");
		}
		if(type.charAt(5) == '1') {
			this.priceCarton = getSizedItems(connection, "CartonItemsRates");
		}
		if(type.charAt(4) == '1') {
			this.priceBucket = getSizedItems(connection, "BucketItemsRates");
		}
		if(type.charAt(3) == '1') {
			this.priceDozen = getSizedItems(connection, "DozenItemsRates");
		}
		if(type.charAt(2) == '1') {
			this.priceBottle = getSizedItems(connection, "BottleItemsRates");
		}
		if(type.charAt(1) == '1') {
			this.priceTin = getSizedItems(connection, "TinItemsRates");
		}
		if(type.charAt(0) == '1') {
			this.priceGallon = getSizedItems(connection, "GallonItemsRates");
		}
	}
	
/*------------------------------------Get Sized Item from Database--------------------------------*/
	
	
	public SizedItem getSizedItems(Connection connection, String table) {
		SizedItem sItem = new SizedItem();
		try {
			String query = new String();
			boolean isRelated = false;
			
			if(table.equals("PacketItemsRates") || table.equals("DozenItemsRates"))
				isRelated = true;
			
			if(isRelated)
				query = "select NameSize1, NameSize2, NameSize3, NameSize4, NameSize5, PriceSize1, PriceSize2, PriceSize3, PriceSize4, PriceSize5, SPriceSize1, SPriceSize2, SPriceSize3, SPriceSize4, SPriceSize5, StockSize1, StockSize2, StockSize3, StockSize4, StockSize5, RelationSize1, RelationSize2, RelationSize3, RelationSize4, RelationSize5 from "+table+" where EID='"+this.id+"'";
			else
				query = "select NameSize1, NameSize2, NameSize3, NameSize4, NameSize5, PriceSize1, PriceSize2, PriceSize3, PriceSize4, PriceSize5, SPriceSize1, SPriceSize2, SPriceSize3, SPriceSize4, SPriceSize5, StockSize1, StockSize2, StockSize3, StockSize4, StockSize5 from "+table+" where EID='"+this.id+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			
			int count = 0;
			while(count < 5) {
				sItem.setSizeName(rs.getString("NameSize"+(count+1)), count);
				sItem.setSizePrice(rs.getDouble("PriceSize"+(count+1)), count);
				sItem.setSizeSPrice(rs.getDouble("SPriceSize"+(count+1)), count);
				sItem.setSizeStock(rs.getDouble("StockSize"+(count+1)), count);
				if(isRelated)
					sItem.setSizeRelation(rs.getString("RelationSize"+(count+1)), count);
				count++;
			}
			pst.close();
			rs.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return sItem;
	}
	
	/*-----------------------------------Get Non Sized Item from Database-----------------------------*/
	public void getNonSizedItems(Connection connection) {
		try {
			String query = "select KgPrice, KgSPrice, KgStock, KgRelation, GattuPrice, GattuSPrice, GattuStock, BoxPrice, BoxSPrice, BoxStock, BoxRelation, BundlePrice, BundleSPrice, BundleStock from NonSizedItemsRates where EID='"+this.id+"'";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			
			while(rs.next()) {
				
				this.priceKg = rs.getDouble("KgPrice");
				this.spriceKg = rs.getDouble("KgSPrice");
				this.stockKg = rs.getDouble("KgStock");
				this.relationKg = rs.getString("KgRelation");
				
				this.priceGattu = rs.getDouble("GattuPrice");
				this.spriceGattu = rs.getDouble("GattuSPrice");
				this.stockGattu = rs.getDouble("GattuStock");
				
				this.priceBox = rs.getDouble("BoxPrice");
				this.spriceBox = rs.getDouble("BoxSPrice");
				this.stockBox = rs.getDouble("BoxStock");
				this.relationBox = rs.getString("BoxRelation");

				this.priceBundle = rs.getDouble("BundlePrice");
				this.spriceBundle = rs.getDouble("BundleSPrice");
				this.stockBundle = rs.getDouble("BundleStock");
					
			}
			pst.close();
			rs.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}
	
}