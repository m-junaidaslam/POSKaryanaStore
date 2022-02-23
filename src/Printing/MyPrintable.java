package Printing;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.imageio.ImageIO;
import javax.swing.JTable;
import javax.swing.table.TableModel;

public class MyPrintable implements Printable {
	
	//public static final String DATE_FORMAT_NOW = "yyyy-MM-dd HH:mm:ss a";
	public static final String DATE_FORMAT_NOW = "dd-MM-yyyy hh:mm";
	public  static String title[] = new String[] {"چیز", "عدد", "رقم"};
	JTable itemsTable;
	String cashier = new String();
	String totalAmount = new String();
	String recAmount = new String();
	String balance = new String();
	
	public MyPrintable(JTable table, String cashier, String totalAmount, String recAmount, String balance) {
		// TODO Auto-generated constructor stub
		this.itemsTable = table;
		this.cashier = cashier;
		this.totalAmount = totalAmount;
		this.recAmount = recAmount;
		this.balance = balance;
	}
	
	@Override
	public int print(Graphics graphics, PageFormat pageFormat, 
	                int pageIndex) throws PrinterException {    
		int result = NO_SUCH_PAGE;    
	    if (pageIndex == 0) {                    
		    Graphics2D g2d = (Graphics2D) graphics; 
		                    
		    g2d.translate((double) pageFormat.getImageableX(),(double) pageFormat.getImageableY()); 
		    Font font = new Font("Monospaced",Font.PLAIN,10);       
		    g2d.setFont(font);
		                       
			                
		    try {
		    	/*
		         * Draw Image*
		           assume that printing reciept has logo on top 
		         * that logo image is in .gif format .png also support
		         * image resolution is width 100px and height 50px
		         * image located in root--->image folder 
		         */
		    	int x=5;                                        //print start at 100 on x axies
		        int y=0;                                          //print start at 10 on y axies
		        int imagewidth=40;
		        int imageheight=20;
		        BufferedImage read = ImageIO.read(getClass().getResource("/shop_logo_resize.png"));
		        g2d.drawImage(read,x,y,imagewidth,imageheight,null);         //draw image
		        //g2d.drawLine(0, y+60, 180, y+60);                          //draw line
		    } catch (IOException e) {
		    	e.printStackTrace();
		    }
			try{
				/*Draw Header*/
				int y=7;
				g2d.drawString("تاریخ : "+now(), 55, y);                                //print date
				g2d.drawString("دکاندار : "+cashier, 110, y+12);  
			
				/*Draw Colums*/
				g2d.setFont(new Font("Monospaced",Font.PLAIN,8)); 
				g2d.drawLine(0, y+17, 200, y+17);
				g2d.setFont(new Font("Monospaced",Font.BOLD,10));
				g2d.drawString(title[0], 130 ,y+27);
				g2d.drawLine(44, y+17, 44, y+32);
				g2d.drawString(title[1], 45 ,y+27);
				g2d.drawLine(66, y+17, 66, y+32);
				g2d.drawString(title[2], 5 ,y+27);
				//g2d.drawString(title[3], 110 ,y+30);
				//g2d.drawString(title[4], 145 ,y+30);
				g2d.setFont(new Font("Monospaced",Font.PLAIN,8));
				g2d.drawLine(0, y+32, 200, y+32);
				g2d.setFont(new Font("Monospaced",Font.BOLD,8));
		   
				int cH = 0;
				TableModel mod = itemsTable.getModel();
		        
				for(int i = 0;i < mod.getRowCount() ; i++){
			    	/*Assume that all parameters are in string data type for this situation
			                 * All other premetive data types are accepted.
			                */
			    	/*String itemName = mod.getValueAt(i, 0).toString();
			    	String itemNo = mod.getValueAt(i, 1).toString();
			        String itemType = mod.getValueAt(i, 2).toString();
			        String itemSize = mod.getValueAt(i, 3).toString();
			        String itemAmount = mod.getValueAt(i, 4).toString();
			    	*/
					
					String item = mod.getValueAt(i, 0).toString() + "-" + mod.getValueAt(i, 2) + "-" +mod.getValueAt(i, 3);
					String qty = mod.getValueAt(i, 1).toString();
					String amount = mod.getValueAt(i, 4).toString();
					if(qty.endsWith(".0"))
						qty = qty.substring(0, qty.length() - 2);
					if(amount.endsWith(".0"))
						amount = amount.substring(0, amount.length() - 2);
					
					g2d.setFont(new Font("Monospaced",Font.BOLD,8));
			    	cH = (y+45) + (12*i);                             //shifting drawing line
			    	g2d.drawString(item , 67, cH);
			    	g2d.drawString(qty, 45, cH);
					g2d.setFont(new Font("Monospaced",Font.BOLD,10));
			    	g2d.drawString(amount, 5, cH);
			        //g2d.drawString(itemSize , 110, cH);
			        //g2d.drawString(itemAmount , 145, cH);
			    	g2d.drawLine(44, cH-12, 44, cH+6);
			    	g2d.drawLine(66, cH-12, 66, cH+6);
				}
				g2d.setFont(new Font("Monospaced",Font.PLAIN,8));
				g2d.drawLine(0, cH+7, 200, cH+7);
				g2d.setFont(new Font("Monospaced",Font.BOLD,10));

				g2d.drawString("ٹوٹل رقم :", 67, cH+17);
				if(totalAmount.endsWith(".0"))
					totalAmount = totalAmount.substring(0, totalAmount.length() - 2);
				g2d.drawString(totalAmount, 5, cH+17);
				
				g2d.drawString("وصول کردہ رقم :", 67, cH+29);
				if(recAmount.endsWith(".0"))
					recAmount = recAmount.substring(0, recAmount.length() - 2);
				g2d.drawString(recAmount, 5, cH+29);
				
				g2d.drawLine(0, cH+34, 200, cH+34);
				
				g2d.drawString("بقایا رقم :", 67, cH+44);
				if(balance.endsWith(".0"))
					balance = balance.substring(0, balance.length() - 2);
				g2d.drawString(balance, 5, cH+44);
				
				
				
			//	/*Footer*/
				g2d.drawLine(0, cH+48, 200, cH+48);
				font = new Font("Arial",Font.BOLD,12) ;                  //changed font size
				g2d.setFont(font);
		        g2d.drawString("! شکریہ",85, cH+61);
		                                                                         //end of the reciept
			} catch(Exception r){
				r.printStackTrace();
			}
		  
			result = PAGE_EXISTS;    
		}    
		return result;
  	}
	
	
/*----------------------------Function to get current Date and Time-------------------*/
	public static String now() {
		//get current date and time as a String output   
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}
 
}
         
/*
 ################# THIS IS HOW TO USE THIS CLASS #######################
 
 Printsupport ps=new Printsupport();
 Object printitem [][]=ps.getTableData(jTable);
 ps.setItems(printitem);
       
 PrinterJob pj = PrinterJob.getPrinterJob();
 pj.setPrintable(new MyPrintable(),ps.getPageFormat(pj));
       try {
            pj.print();
           
            }
        catch (PrinterException ex) {
                ex.printStackTrace();
            }
 ################## JOIN TO SHARE KNOWLADGE ########################### */
 
