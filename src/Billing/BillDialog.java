package Billing;

import java.awt.Color;
import java.awt.Container;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.print.PrinterException;
import java.awt.print.PrinterJob;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import javax.print.PrintService;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import javax.swing.text.AbstractDocument;

import net.miginfocom.swing.MigLayout;
import net.proteanit.sql.DbUtils;
import Utils.CustomFontProvider;
import Utils.DoubleFilter;
import Utils.SizeFilter;
import Utils.RequestFocusListener;

import Users.Employee;
import Debt.Customer;
import Printing.PrintSupport;
import Printing.MyPrintable;

class BillDialog extends JDialog {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Connection connection;
	JTable debtTable;
	double remAmount;
	public static final String DATE_FORMAT_NOW = "dd-MM-yyyy hh:mm a";
	Employee operator;
	private CustomFontProvider cfp = new CustomFontProvider();
	
	public BillDialog(Connection connection, Employee operator, PrintService requiredPrintService, JFrame frame, String username, TableModel tableModel,
			String strTotalDisp, String strTotalAmount, String strReceivedDisp,
			String received) {
		super(frame, "Bill", true);

		JDialog dialog = this;
		this.connection = connection;

		JTable newTable = new JTable(tableModel);
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		for(int i = 0; i < 5; i++)
			newTable.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		newTable.getColumnModel().getColumn(1).setPreferredWidth(10);
		newTable.getColumnModel().getColumn(2).setPreferredWidth(25);
		newTable.getColumnModel().getColumn(3).setPreferredWidth(25);
		newTable.setRowHeight(25);
//		newTable.setFont(new Font("Times New Roman", Font.BOLD, 15));
		newTable.setFont(cfp.getSmallBoldFont());

		remAmount = Double.valueOf(received)
				- Double.valueOf(strTotalAmount);

//		Font title = new Font("Times New Roman", Font.BOLD, 19);
		Font title = cfp.getMediumBoldFont();
//		Font amount = new Font("Tahoma", Font.BOLD, 18);
		Font amount = cfp.getSmallBoldFont();

		JLabel lblTotalDisp = new JLabel(strTotalDisp);
		lblTotalDisp.setFont(title);
		lblTotalDisp.setText(strTotalDisp);

		JLabel lblTotalAmount = new JLabel(strTotalAmount);
		lblTotalAmount.setFont(amount);
		lblTotalAmount.setForeground(Color.BLUE);

		JLabel lblReceivedDisp = new JLabel(strReceivedDisp);
		lblReceivedDisp.setFont(title);
		lblReceivedDisp.setText(strReceivedDisp);

		JLabel lblReceived = new JLabel(received);
		lblReceived.setFont(amount);
		lblReceived.setForeground(Color.GREEN);

		JLabel lblRemAmountDisp = new JLabel("Remaining Amout = Rs.");
		lblRemAmountDisp.setFont(title);

		JLabel lblRemAmount = new JLabel(remAmount + "");
		lblRemAmount.setFont(amount);
		lblRemAmount.setForeground(Color.red);

		Image iconDone = new ImageIcon(frame.getClass().getResource("/ok.png"))
				.getImage();
		JButton btnDone = new JButton("Done", new ImageIcon(iconDone));
//		btnDone.setFont(new Font("Times New Roman", Font.BOLD, 16));
		btnDone.setFont(cfp.getSmallBoldFont());
		btnDone.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
			}
		});
		
		debtTable = new JTable();
		JScrollPane scrollPaneDebt = new JScrollPane();
		scrollPaneDebt.setViewportView(debtTable);
		
		Image iconDebt = new ImageIcon(frame.getClass().getResource("/icon_debtinfo_small.png")).getImage();
		JButton btnDebt = new JButton("ادھار میں ڈالیں", new ImageIcon(iconDebt));
//		btnDebt.setFont(new Font("Times New Roman", Font.BOLD, 16));
		btnDebt.setFont(cfp.getSmallBoldFont());
		btnDebt.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				dialog.dispose();
				
				loadDebtTable();
				JPanel myPanel = new JPanel(new MigLayout());
				
				JLabel lblDebt = new JLabel(("ادھار کی رقم: " + (-remAmount)));
//				lblDebt.setFont(new Font("Times New Roman", Font.BOLD, 20));
				lblDebt.setFont(cfp.getSmallBoldFont());
				
				myPanel.add(lblDebt, "center, growy, wrap");
				myPanel.add(scrollPaneDebt, "wrap");
				
				JButton btnNewCustomer = new JButton("نیا گاہک");
//				btnNewCustomer.setFont(new Font("Times New Roman", Font.BOLD, 16));
				btnNewCustomer.setFont(cfp.getSmallBoldFont());
				btnNewCustomer.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						newUser();
					}
						
				});
				JButton btnAddDebt = new JButton("ادھار ڈالیں");
//				btnAddDebt.setFont(new Font("Times New Roman", Font.BOLD, 16));
				btnAddDebt.setFont(cfp.getSmallBoldFont());
				btnAddDebt.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						int r = debtTable.getSelectedRow();
				        if (r >= 0 && r < debtTable.getRowCount()) {
				        	debtTable.setRowSelectionInterval(r, r);
				        } else {
				        	debtTable.clearSelection();
				        }

				        int rowindex = debtTable.getSelectedRow();
				        if (rowindex < 0)
				            return;
				        Customer customer = new Customer();
						int row = debtTable.getSelectedRow();
						if(debtTable.isRowSelected(row)) {
							customer.setId(Integer.valueOf((int) debtTable.getModel().getValueAt(row, 0)));
							customer.getDataFromDb(connection, customer.getId());
							customer.setDebt(customer.getDebt()-remAmount);
							//get current date and time as a String output   
							Calendar cal = Calendar.getInstance();
							String DATE_FORMAT_NOW = "dd-MM-yyyy hh:mm a";
							SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
							customer.setDate(sdf.format(cal.getTime()));
					    	customer.updateCustomer(connection);
					    	
					    	JOptionPane.showMessageDialog(null, customer.getName()+" کا ادھار "+customer.getDebt()+ " " +"ہو گیا ہے۔");
								
						} else {
//							javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
							JOptionPane.showMessageDialog(null, "کوئی اکاؤنٹ نہیں چنا گیا !");
					    }
						JOptionPane.getRootFrame().dispose();
					}
						
				});
				
				myPanel.add(btnNewCustomer, "right, split 2, span 2 2, growx, growy");
				myPanel.add(btnAddDebt, "right, span 2 2, growx, growy");
				//JOptionPane.showInputDialog(arg0, arg1)
				
				JOptionPane.showMessageDialog(null, myPanel,
						": گاہک چنیں", JOptionPane.PLAIN_MESSAGE);
			}
		});

				
				
		JTable printTable = new JTable(tableModel);
		Image iconPrint = new ImageIcon(frame.getClass().getResource(
				"/print_icon.png")).getImage();
		JButton btnPrint = new JButton("Print", new ImageIcon(iconPrint));
//		btnPrint.setFont(new Font("Times New Roman", Font.BOLD, 16));
		btnPrint.setFont(cfp.getSmallBoldFont());
		
		btnPrint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				PrintSupport ps = new PrintSupport();
				Object printitem[][] = ps.getTableData(printTable);
				ps.setItems(printitem);
				PrinterJob pj = PrinterJob.getPrinterJob();
				
				try {
					pj.setPrintService(requiredPrintService);
				} catch (PrinterException e) {
					 //TODO Auto-generated catch block
					e.printStackTrace();
				}
					pj.setPrintable(new MyPrintable(printTable, username,
							strTotalAmount, received, (remAmount + "")), ps
							.getPageFormat(pj));
					
					try {
						pj.print(); 
					} catch (PrinterException ex) {
						ex.printStackTrace();
//						javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
						JOptionPane.showMessageDialog(null, "پرنٹر بند ہے۔");
					}
				dialog.dispose();
			}
		});

		JButton debtNPrint = new JButton("ادھار اور پرنٹ");
//		debtNPrint.setFont(new Font("Times New Roman", Font.BOLD, 16));
		debtNPrint.setFont(cfp.getSmallBoldFont());
		debtNPrint.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				btnDebt.doClick();
				btnPrint.doClick();
			}
		});
		
		if(!operator.isAdmin()) {
			btnDebt.setVisible(false);
			debtNPrint.setVisible(false);
		}
		
		JScrollPane scrollPaneBill = new JScrollPane();
		scrollPaneBill.setViewportView(newTable);
		Container c = getContentPane();
		c.setLayout(new MigLayout());

		c.add(scrollPaneBill, "wrap");

		c.add(lblTotalDisp, "split2, right");
		c.add(lblTotalAmount, "wrap, left");

		c.add(lblReceivedDisp, "split2, right");
		c.add(lblReceived, "wrap, left");

		c.add(lblRemAmountDisp, "split2, right");
		c.add(lblRemAmount, "wrap, left");
		
		if(remAmount < 0) {
			c.add(btnDebt,"right, split 4, span 4 4, growx, growy");
			c.add(debtNPrint, "right, span 4 4, growx, growy");
			c.add(btnDone, "right, span 3 3, growx, growy");
			c.add(btnPrint, "right, span 3 3, growx, growy");
		} else {
			c.add(btnDone, "right, split 2, span 2 2, growx, growy");
			c.add(btnPrint, "right, span 2 2, growx, growy");
		}
		newTable.setFocusable(false);
		
		getRootPane().setDefaultButton(btnPrint);
		
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
	}
	
	public void loadDebtTable() {
		try {
			String query = "select EID, Name, Surname, Place, Date, Debt from DebtInfo";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			debtTable.setModel(DbUtils.resultSetToTableModel(rs));
//			debtTable.setFont(new Font("Times New Roman", Font.BOLD, 14));
			debtTable.setFont(cfp.getSmallPlainFont());
			DefaultTableCellRenderer centerDebtCellRenderer = new DefaultTableCellRenderer();
			centerDebtCellRenderer.setHorizontalAlignment(JLabel.CENTER);
			for(int i = 0; i < 6; i++)
				debtTable.getColumnModel().getColumn(i).setCellRenderer(centerDebtCellRenderer);
			if(debtTable.getRowCount() > 0)
				debtTable.setRowSelectionInterval(0, 0);
			debtTable.getColumnModel().getColumn(0).setPreferredWidth(5);
			debtTable.getColumnModel().getColumn(1).setPreferredWidth(50);
			debtTable.getColumnModel().getColumn(2).setPreferredWidth(40);
			debtTable.getColumnModel().getColumn(3).setPreferredWidth(40);
			debtTable.getColumnModel().getColumn(4).setPreferredWidth(110);
			debtTable.getColumnModel().getColumn(5).setPreferredWidth(50);
			pst.close();
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}

	}

	/*------------------------New Customer------------------------*/
	public void newUser() {
		AbstractDocument doc = null;
		
		JTextField name = new JTextField();
		doc = (AbstractDocument) name.getDocument();
//		name.setFont(new Font("Times New Roman", Font.BOLD, 16));
		name.setFont(cfp.getSmallBoldFont());
		doc.setDocumentFilter(new SizeFilter(20));
		name.addAncestorListener(new RequestFocusListener());
		name.setText("");
		
	    JTextField surname = new JTextField();
	    doc = (AbstractDocument) surname.getDocument();
//		surname.setFont(new Font("Times New Roman", Font.BOLD, 16));
		surname.setFont(cfp.getSmallBoldFont());
		doc.setDocumentFilter(new SizeFilter(20));
		surname.setText("");
		
	    JTextField place = new JTextField();
	    doc = (AbstractDocument) place.getDocument();
//		place.setFont(new Font("Times New Roman", Font.BOLD, 16));
		place.setFont(cfp.getSmallBoldFont());
		doc.setDocumentFilter(new SizeFilter(20));
		place.setText("");
	    
	    JTextField debt = new JTextField();
	    doc = (AbstractDocument) debt.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		debt.setText("0");
		debt.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				debt.selectAll();
			}
		});
	    
	    JPanel myPanel = new JPanel(new GridLayout(4, 2, 5, 5));
	    myPanel.add(new JLabel("پہلا نام"));
	    myPanel.add(name);
	    myPanel.add(new JLabel("دوسرا نام"));
	    myPanel.add(surname);
	    myPanel.add(new JLabel("جگہ"));
	    myPanel.add(place);
	    //myPanel.add(new JLabel("ادھار"));
	    //myPanel.add(debt);

//	    javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
	    int result = JOptionPane.showConfirmDialog(null, myPanel, 
	               "گاہک کے کوائف درج کریں :", JOptionPane.OK_CANCEL_OPTION);
	    if (result == JOptionPane.OK_OPTION && !name.getText().isEmpty()) {
    		Customer cus = new Customer(name.getText(), surname.getText(), place.getText(), now(), Double.valueOf(debt.getText()));
    		cus.insertNewCustomer(connection, debtTable.getRowCount());
	    } else if(result == JOptionPane.OK_OPTION)
	    	JOptionPane.showMessageDialog(null, "نام والی جگہ خالی ہے !");
	    	
	    loadDebtTable();
	}
	
	/*----------------------------Function to get current Date and Time-------------------*/
	public static String now() {
		//get current date and time as a String output   
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}

}