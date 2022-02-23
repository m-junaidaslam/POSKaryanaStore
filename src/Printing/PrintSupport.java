package Printing;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import java.awt.print.PrinterJob;
import javax.swing.JTable;
import javax.swing.table.DefaultTableModel;
/**
 *
 * @author All Open source developers
 * @version 1.0.0.0
 * @since 2014/12/22
 */
/*This Printsupport java class was implemented to get printout.
* This class was specially designed to print a Jtable content to a paper.
* Specially this class formated to print 7cm width paper.
* Generally for pos thermel printer.
* Free to customize this source code as you want.
* Illustration of basic invoice is in this code.
* demo by gayan liyanaarachchi
 
 */

public class PrintSupport {
 
	static JTable itemsTable;
	public static  int total_item_count=0;
	public  static String title[] = new String[] {"Name", "No.", "Type", "Size", "Amount"};
	
	public void setItems(Object[][] printitem){
		Object data[][]=printitem;
        DefaultTableModel model = new DefaultTableModel();
        //assume jtable has 4 columns.
        model.addColumn(title[0]);
        model.addColumn(title[1]);
        model.addColumn(title[2]);
        model.addColumn(title[3]);
        model.addColumn(title[4]);

        int rowcount=printitem.length;
        
        addtomodel(model, data, rowcount);
       

        itemsTable = new JTable(model);
	}

	public static void addtomodel(DefaultTableModel model,Object [][]data,int rowcount){
		int count=0;
		while(count < rowcount){
			model.addRow(data[count]);
			count++;
		}
	    if(model.getRowCount()!=rowcount)
	    	addtomodel(model, data, rowcount);
	    
	}
          
	public Object[][] getTableData (JTable table) {
		int itemcount=table.getRowCount();
		total_item_count = itemcount;
		DefaultTableModel dtm = (DefaultTableModel) table.getModel();
		int nRow = dtm.getRowCount(), nCol =dtm.getColumnCount();
		Object[][] tableData = new Object[nRow][nCol];
		if(itemcount==nRow) {                                        //check is there any data loss.
			for (int i = 0 ; i < nRow ; i++){
			    for (int j = 0 ; j < nCol ; j++){
			        tableData[i][j] = dtm.getValueAt(i,j);           //pass data into object array.
			    }
			}
			if(tableData.length!=itemcount){                      //check for data losses in object array
				getTableData(table);                                  //recursively call method back to collect data
			}   
		} else {
		                                                       //collecting data again because of data loss.
			getTableData(table);
		}
		return tableData;                                       //return object array with data.
	}     
	
	public PageFormat getPageFormat(PrinterJob pj){
         PageFormat pf = pj.defaultPage(); 
        Paper paper = pf.getPaper();         
        double middleHeight =total_item_count *0.44;  // 0.5  dynamic----->change with the row count of jtable
        double headerHeight = 1.8;                  //fixed----->but can be mod 1.8
    	double footerHeight = 2.3;                  //fixed----->but can be mod
                
        double width = convert_CM_To_PPI(8);      //printer know only point per inch.default value is 72ppi
    	double height = convert_CM_To_PPI(headerHeight+middleHeight+footerHeight); 
        paper.setSize(width, height);
        paper.setImageableArea(convert_CM_To_PPI(0.1), 
                            convert_CM_To_PPI(0.2), 
                            width - convert_CM_To_PPI(0.35), 
                            height - convert_CM_To_PPI(0.3));   //define boarder size    after that print area width is about 180 points
            
        pf.setOrientation(PageFormat.PORTRAIT);           //select orientation portrait or landscape but for this time portrait
        pf.setPaper(paper);
            
        return pf;
	}
        
        
	protected static double convert_CM_To_PPI(double cm) {            
		return toPPI(cm * 0.393600787);            
	}
	
	protected static double toPPI(double inch) {            
		return inch * 72d;            
	}

}