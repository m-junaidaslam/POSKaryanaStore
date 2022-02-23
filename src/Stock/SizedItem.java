package Stock;

public class SizedItem {

	/*------------------------------------------------*
	 *--------------Members Declaration---------------*
	 *------------------------------------------------*/
	private String[] sizeName = new String[5];
	private double[] sizePrice = new double[5];
	private double[] sizeSPrice = new double[5];
	private double[] sizeStock = new double[5];
	private String[] sizeRelation = new String[5];
	
	/*------------------------------------------------*
	 *------------------Constructors------------------*
	 *------------------------------------------------*/
	public SizedItem() {
		super();
		
		this.sizeName[0] = "A";
		this.sizeName[1] = "B";
		this.sizeName[2] = "C";
		this.sizeName[3] = "D";
		this.sizeName[4] = "E";
		for(int i = 0; i < 5; i++) {
			this.sizePrice[i] = 0d;
			this.sizeSPrice[i] = 0d;
			this.sizeStock[i] = 0d;
			this.sizeRelation[i] = "اسٹاک";
		}
		
	}
	
	public SizedItem(String[] sizeName, double[] sizePrice, double[] sizeStock, String[] sizeRelation) {
		for(int i = 0; i < 5; i++) {
			this.sizeName[i] = sizeName[i];
			this.sizePrice[i] = sizePrice[i];
			this.sizeStock[i] = sizeStock[i];
			this.sizeRelation[i] = sizeRelation[i];
		}
	}
	

	/*------------------------------------------------*
	 *--------------Getters and Setters---------------*
	 *------------------------------------------------*/
	public String getSizeName(int i) {
		return this.sizeName[i];
	}
	
	public double getSizePrice(int i) {
		return this.sizePrice[i];
	}
	
	public double getSizeSPrice(int i) {
		return this.sizeSPrice[i];
	}
	
	public double getSizeStock(int i) {
		return this.sizeStock[i];
	}
	
	public String getSizeRelation(int i) {
		return this.sizeRelation[i];
	}
	
	public void setSizeName(String name, int i) {
		this.sizeName[i] = name;
	}
	
	public void setSizePrice(double price, int i) {
		this.sizePrice[i] = price;
	}
	
	public void setSizeSPrice(double price, int i) {
		this.sizeSPrice[i] = price;
	}
	
	public void setSizeStock(double stock, int i) {
		this.sizeStock[i] = stock;
	}
	
	public void setSizeRelation(String relation, int i) {
		this.sizeRelation[i] = relation;
	}
	
}
