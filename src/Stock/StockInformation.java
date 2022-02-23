package Stock;

import java.awt.GridBagConstraints;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JLabel;
import javax.swing.border.EmptyBorder;

import java.awt.Font;
import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.prefs.Preferences;

import javax.swing.JTable;

import net.miginfocom.swing.MigLayout;
import net.proteanit.sql.DbUtils;

import javax.swing.JScrollPane;
import javax.swing.SwingConstants;
import javax.swing.JToggleButton;

import Users.Employee;
import Utils.CustomFontProvider;
import Billing.BillingSystem;
import Main.Login;
import Main.StartMenu;

public class StockInformation extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	Preferences prefs = Preferences.userNodeForPackage(BillingSystem.class);
	final String STOCK_STATUS_PREF_NAME = "pref_stock_status";
	JFrame frame = this;
	private JPanel contentPane;
	private JTable tblItemsInfo;
	private Employee operator = new Employee();
	String operatorName = operator.getName();
	Connection connection = null;
	private JScrollPane scrollPane;
	private JScrollPane scrollPane_1;
	private JPanel panelDetails;
	GridBagConstraints gridBagConstraints = new GridBagConstraints();
	private CustomFontProvider cfp = new CustomFontProvider();

	/**
	 * Launch the application.
	 */
	
	/*----------------Close this Frame--------------------*/
	public void close() {
		this.dispose();
	}
	
	/*--------------------Set Grid Bag Layout Constraints-------------*/
	public void setGBagConst(int x, int y, int w, int h) {
		gridBagConstraints.gridx = x;
		gridBagConstraints.gridy = y;
		gridBagConstraints.gridwidth = w;
		gridBagConstraints.gridheight = h;	
	}
	
	/*-----------------Refresh Items Table----------------*/
	public void refreshTable() {
//		tblItemsInfo.setFont(new Font("Times New Roman", Font.PLAIN, 15));
		tblItemsInfo.setFont(cfp.getSmallBoldFont());
		try {
			
			String query = "select EID, Name from ItemsInfo";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			tblItemsInfo.setModel(DbUtils.resultSetToTableModel(rs));
			if(tblItemsInfo.getRowCount() > 0)
				tblItemsInfo.setRowSelectionInterval(0, 0);
			tblItemsInfo.getColumnModel().getColumn(0).setMinWidth(10);
			tblItemsInfo.getColumnModel().getColumn(0).setPreferredWidth(10);
			tblItemsInfo.setRowHeight(25);
			tblItemsInfo.addMouseListener(new MouseAdapter() {
			    @Override
			    public void mouseReleased(MouseEvent e) {
			        int r = tblItemsInfo.rowAtPoint(e.getPoint());
			        if (r >= 0 && r < tblItemsInfo.getRowCount()) {
			        	tblItemsInfo.setRowSelectionInterval(r, r);
			        } else {
			        	tblItemsInfo.clearSelection();
			        }

			        int rowindex = tblItemsInfo.getSelectedRow();
			        if (rowindex < 0)
			            return;
			        refreshSalesPoint();
			    }
			});
			
			pst.close();
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*------------------------Refresh Sales Point--------------------*/
	
	public void refreshSalesPoint() {
		panelDetails = new JPanel();
		scrollPane_1.setViewportView(panelDetails);
		panelDetails.setLayout(new MigLayout("insets 10"));
		int row = tblItemsInfo.getSelectedRow();
		if(tblItemsInfo.isRowSelected(row)) {
			int eid = Integer.valueOf((tblItemsInfo.getModel().getValueAt(row, 0)).toString());
			Item detailItem = new Item();
			detailItem.setId(eid);
			detailItem.getItemFromDb(connection);
			
			JLabel kgName = new JLabel(" : "+" کلو اسٹاک");
			JLabel gattuName = new JLabel(" : "+" گٹو اسٹاک");
			JLabel boxName = new JLabel(" : "+" ڈبہ اسٹاک");
			JLabel bundleName = new JLabel(" : "+" بنڈل اسٹاک");
			
			JLabel kgRelation = new JLabel(detailItem.getRelationKg());
			JLabel boxRelation = new JLabel(detailItem.getRelationBox());
			
			JLabel kgStock = new JLabel(detailItem.getStockKg()+"");
			JLabel gattuStock = new JLabel(detailItem.getStockGattu()+"");
			JLabel boxStock = new JLabel(detailItem.getStockBox()+"");
			JLabel bundleStock = new JLabel(detailItem.getStockBundle()+"");
			
			JLabel pName = new JLabel(" : "+" پیکٹ اسٹاک");
			JLabel cName = new JLabel(" : "+" کارٹن اسٹاک");
			JLabel buName = new JLabel(" : "+" بالٹی اسٹاک");
			JLabel dName = new JLabel(" : "+" درجن اسٹاک");
			JLabel boName = new JLabel(" : "+" بوتل اسٹاک");
			JLabel tName = new JLabel(" : "+" ٹین اسٹاک");
			JLabel gName = new JLabel(" : "+" گیلن اسٹاک");
			
			Font mediumPlainFont = cfp.getMediumPlainFont();
			
//			kgName.setFont(new Font("Times New Roman", Font.PLAIN, 24));
			kgName.setFont(mediumPlainFont);
//			gattuName.setFont(new Font("Times New Roman", Font.PLAIN, 24));
			gattuName.setFont(mediumPlainFont);
//			boxName.setFont(new Font("Times New Roman", Font.PLAIN, 24));
			boxName.setFont(mediumPlainFont);
//			bundleName.setFont(new Font("Times New Roman", Font.PLAIN, 24));
			bundleName.setFont(mediumPlainFont);
//			pName.setFont(new Font("Times New Roman", Font.PLAIN, 24));
			pName.setFont(mediumPlainFont);
//			cName.setFont(new Font("Times New Roman", Font.PLAIN, 24));
			cName.setFont(mediumPlainFont);
//			buName.setFont(new Font("Times New Roman", Font.PLAIN, 24));
			buName.setFont(mediumPlainFont);
//			dName.setFont(new Font("Times New Roman", Font.PLAIN, 24));
			dName.setFont(mediumPlainFont);
//			boName.setFont(new Font("Times New Roman", Font.PLAIN, 24));
			boName.setFont(mediumPlainFont);
//			tName.setFont(new Font("Times New Roman", Font.PLAIN, 24));
			tName.setFont(mediumPlainFont);
//			gName.setFont(new Font("Times New Roman", Font.PLAIN, 24));
			gName.setFont(mediumPlainFont);
			
//			kgStock.setFont(new Font("Times New Roman", Font.PLAIN, 20));
			kgStock.setFont(mediumPlainFont);
//			gattuStock.setFont(new Font("Times New Roman", Font.PLAIN, 20));
			gattuStock.setFont(mediumPlainFont);
//			boxStock.setFont(new Font("Times New Roman", Font.PLAIN, 20));
			boxStock.setFont(mediumPlainFont);
//			bundleStock.setFont(new Font("Times New Roman", Font.PLAIN, 20));
			bundleStock.setFont(mediumPlainFont);
			
			JLabel[] packetName = new JLabel[5];
		    JLabel[] cartonName = new JLabel[5];
		    JLabel[] bucketName = new JLabel[5];
		    JLabel[] dozenName = new JLabel[5];
		    JLabel[] bottleName = new JLabel[5];
		    JLabel[] tinName = new JLabel[5];
		    JLabel[] gallonName = new JLabel[5];
		    
		    JLabel[] packetStock = new JLabel[5];
		    JLabel[] cartonStock = new JLabel[5];
		    JLabel[] bucketStock = new JLabel[5];
		    JLabel[] dozenStock = new JLabel[5];
		    JLabel[] bottleStock = new JLabel[5];
		    JLabel[] tinStock = new JLabel[5];
		    JLabel[] gallonStock = new JLabel[5];
		    
		    JLabel[] packetRelation = new JLabel[5];
		    JLabel[] cartonRelation = new JLabel[5];
		    JLabel[] bucketRelation = new JLabel[5];
		    JLabel[] dozenRelation = new JLabel[5];
		    JLabel[] bottleRelation = new JLabel[5];
		    JLabel[] tinRelation = new JLabel[5];
		    JLabel[] gallonRelation = new JLabel[5];
		
		    for(int i = 0;i < 5; i++) {
		    	packetName[i] = new JLabel();
		    	packetName[i].setText(detailItem.getPricePacket().getSizeName(i));
		    	
		    	cartonName[i] = new JLabel();
		    	cartonName[i].setText(detailItem.getPriceCarton().getSizeName(i));
			    	
		    	bucketName[i] = new JLabel();
		    	bucketName[i].setText(detailItem.getPriceBucket().getSizeName(i));
			    	
		    	dozenName[i] = new JLabel();
		    	dozenName[i].setText(detailItem.getPriceDozen().getSizeName(i));
			    	
		    	bottleName[i] = new JLabel();
		    	bottleName[i].setText(detailItem.getPriceBottle().getSizeName(i));
			    	
		    	tinName[i] = new JLabel();
		    	tinName[i].setText(detailItem.getPriceTin().getSizeName(i));
			    	
		    	gallonName[i] = new JLabel();
		    	gallonName[i].setText(detailItem.getPriceGallon().getSizeName(i));
			    
			    packetStock[i] = new JLabel();
		    	packetStock[i].setText(detailItem.getPricePacket().getSizeStock(i)+"");
			    	
		    	cartonStock[i] = new JLabel();
			    cartonStock[i].setText(detailItem.getPriceCarton().getSizeStock(i)+"");
			    	
			    bucketStock[i] = new JLabel();
			    bucketStock[i].setText(detailItem.getPriceBucket().getSizeStock(i)+"");
			    	
			    dozenStock[i] = new JLabel();
			    dozenStock[i].setText(detailItem.getPriceDozen().getSizeStock(i)+"");
		    	
			    bottleStock[i] = new JLabel();
			    bottleStock[i].setText(detailItem.getPriceBottle().getSizeStock(i)+"");
		    	
			    tinStock[i] = new JLabel();
			    tinStock[i].setText(detailItem.getPriceTin().getSizeStock(i)+"");
			    	
			    gallonStock[i] = new JLabel();
			    gallonStock[i].setText(detailItem.getPriceGallon().getSizeStock(i)+"");
			    
			    packetRelation[i] = new JLabel();
		    	packetRelation[i].setText(detailItem.getPricePacket().getSizeRelation(i)+"");
			    	
		    	cartonRelation[i] = new JLabel();
			    cartonRelation[i].setText(detailItem.getPriceCarton().getSizeRelation(i)+"");
			    	
			    bucketRelation[i] = new JLabel();
			    bucketRelation[i].setText(detailItem.getPriceBucket().getSizeRelation(i)+"");
			    	
			    dozenRelation[i] = new JLabel();
			    dozenRelation[i].setText(detailItem.getPriceDozen().getSizeRelation(i)+"");
		    	
			    bottleRelation[i] = new JLabel();
			    bottleRelation[i].setText(detailItem.getPriceBottle().getSizeRelation(i)+"");
		    	
			    tinRelation[i] = new JLabel();
			    tinRelation[i].setText(detailItem.getPriceTin().getSizeRelation(i)+"");
			    	
			    gallonRelation[i] = new JLabel();
			    gallonRelation[i].setText(detailItem.getPriceGallon().getSizeRelation(i)+"");
			    
			    
			    Font smallPlainFont = cfp.getSmallPlainFont();
			    
//			    packetName[i].setFont(new Font("Times New Roman", Font.PLAIN, 20));
			    packetName[i].setFont(smallPlainFont);
//			    cartonName[i].setFont(new Font("Times New Roman", Font.PLAIN, 20));
			    cartonName[i].setFont(smallPlainFont);
//			    bucketName[i].setFont(new Font("Times New Roman", Font.PLAIN, 20));
			    bucketName[i].setFont(smallPlainFont);
//			    dozenName[i].setFont(new Font("Times New Roman", Font.PLAIN, 20));
			    dozenName[i].setFont(smallPlainFont);
//			    bottleName[i].setFont(new Font("Times New Roman", Font.PLAIN, 20));
			    bottleName[i].setFont(smallPlainFont);
//			    tinName[i].setFont(new Font("Times New Roman", Font.PLAIN, 20));
			    tinName[i].setFont(smallPlainFont);
//			    gallonName[i].setFont(new Font("Times New Roman", Font.PLAIN, 20));
			    gallonName[i].setFont(smallPlainFont);
			    
//			    packetStock[i].setFont(new Font("Times New Roman", Font.PLAIN, 20));
			    packetStock[i].setFont(smallPlainFont);
//			    cartonStock[i].setFont(new Font("Times New Roman", Font.PLAIN, 20));
			    cartonStock[i].setFont(smallPlainFont);
//			    bucketStock[i].setFont(new Font("Times New Roman", Font.PLAIN, 20));
			    bucketStock[i].setFont(smallPlainFont);
//			    dozenStock[i].setFont(new Font("Times New Roman", Font.PLAIN, 20));
			    dozenStock[i].setFont(smallPlainFont);
//			    bottleStock[i].setFont(new Font("Times New Roman", Font.PLAIN, 20));
			    bottleStock[i].setFont(smallPlainFont);
//			    tinStock[i].setFont(new Font("Times New Roman", Font.PLAIN, 20));
			    tinStock[i].setFont(smallPlainFont);
//			    gallonStock[i].setFont(new Font("Times New Roman", Font.PLAIN, 20));
			    gallonStock[i].setFont(smallPlainFont);
			    
		    }
		    
		    
		    String type = detailItem.getType();
		    panelDetails.removeAll();
		    
		    JLabel name = new JLabel(detailItem.getName());
//		    name.setFont(new Font("Times New Roman", Font.BOLD, 28));
		    name.setFont(cfp.getMediumBoldFont());
		    panelDetails.add(name, "wrap");
		    
		    if(type.charAt(10) == '1') {
		    	panelDetails.add(kgName);
		    	panelDetails.add(kgStock);
			    panelDetails.add(kgRelation, "wrap");
			    
	    	}
		    
	    	if(type.charAt(9) == '1') {
			    panelDetails.add(gattuName);
			    panelDetails.add(gattuStock);
			    JLabel jLblStock = new JLabel("اسٹاک");
			    jLblStock.setFont(mediumPlainFont);
			    panelDetails.add(jLblStock, "wrap");
	    	}
		    
	    	if(type.charAt(8) == '1') {
			    panelDetails.add(boxName);
			    panelDetails.add(boxStock);
			    panelDetails.add(boxRelation, "wrap");
	    	}
		    
		    if(type.charAt(7) == '1') {
			    panelDetails.add(bundleName);
			    panelDetails.add(bundleStock);
			    JLabel jLblStock = new JLabel("اسٹاک");
			    jLblStock.setFont(mediumPlainFont);
			    panelDetails.add(jLblStock, "wrap");
		    }
		    
		    if(type.charAt(6) == '1') {
		    	panelDetails.add(pName, "wrap");
		    	for(int i = 0; i < 5; i++) {
		    		if(detailItem.getPricePacket().getSizePrice(i) > 0) {
				    	panelDetails.add(packetName[i]);
				    	panelDetails.add(packetStock[i]);
				    	panelDetails.add(packetRelation[i], "wrap");
		    		}
		    	}
		    }
		    
		    if(type.charAt(5) == '1') {
		    	panelDetails.add(cName, "wrap");
		    	for(int i = 0; i < 5; i++) {
		    		if(detailItem.getPriceCarton().getSizePrice(i) > 0) {
				    	panelDetails.add(cartonName[i]);
				    	panelDetails.add(cartonStock[i]);
				    	panelDetails.add(cartonRelation[i], "wrap");
		    		}
		    	}
		    }
		    
		    if(type.charAt(4) == '1') {
		    	panelDetails.add(buName, "wrap");
		    	for(int i = 0; i < 5; i++) {
		    		if(detailItem.getPriceBucket().getSizePrice(i) > 0) {
				    	panelDetails.add(bucketName[i]);
				    	panelDetails.add(bucketStock[i]);
				    	panelDetails.add(bucketRelation[i], "wrap");
		    		}
		    	}
		    }
		    
		    if(type.charAt(3) == '1') {
		    	panelDetails.add(dName, "wrap");
		    	for(int i = 0; i < 5; i++) {
		    		if(detailItem.getPriceDozen().getSizePrice(i) > 0) {
				    	panelDetails.add(dozenName[i]);
				    	panelDetails.add(dozenStock[i]);
				    	panelDetails.add(dozenRelation[i], "wrap");
		    		}
		    	}
		    }
		    
		    if(type.charAt(2) == '1') {
		    	panelDetails.add(boName, "wrap");
		    	for(int i = 0; i < 5; i++) {
		    		if(detailItem.getPriceBottle().getSizePrice(i) > 0) {
				    	panelDetails.add(bottleName[i]);
				    	panelDetails.add(bottleStock[i]);
				    	panelDetails.add(bottleRelation[i], "wrap");
		    		}
		    	}
		    }
		    
		    if(type.charAt(1) == '1') {
		    	panelDetails.add(tName, "wrap");
		    	for(int i = 0; i < 5; i++) {
		    		if(detailItem.getPriceTin().getSizePrice(i) > 0) {
				    	panelDetails.add(tinName[i]);
				    	panelDetails.add(tinStock[i]);
				    	panelDetails.add(tinRelation[i], "wrap");
		    		}
		    	}
		    }
		    
		    if(type.charAt(0) == '1') {
		    	panelDetails.add(gName, "wrap");
		    	for(int i = 0; i < 5; i++) {
		    		if(detailItem.getPriceGallon().getSizePrice(i) > 0) {
				    	panelDetails.add(gallonName[i]);
				    	panelDetails.add(gallonStock[i]);
				    	panelDetails.add(gallonRelation[i], "wrap");
		    		}
		    	}
		    }
		    
		}
		
	}
	
	
	/**
	 * Create the frame.
	 */
	public StockInformation(Connection connection, Employee operator) {

		this.connection = connection;
		this.operator = operator;
		Image iconTitle = new ImageIcon(this.getClass().getResource("/stock_icon.png")).getImage();
		setIconImage(iconTitle);
		setTitle("Stock Information");
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 849, 688);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		final JMenuItem LogOut = new JMenuItem("Log Out");
		LogOut.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
//				javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
				int action = JOptionPane.showConfirmDialog(null, "کیا آپ واقعی اپنا اکاؤنٹ بند کرنا چاہتے ہیں ؟", "Log Out", JOptionPane.YES_NO_OPTION);
				if(action == 0) {
					close();
					new Login();
				}
			}
		});
		
		final JMenuItem userInfo = new JMenuItem("Show Info");
		userInfo.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				JOptionPane.showMessageDialog(null, "ID : "+operator.getId()+"\nName : "+operator.getName()+"\nSurname : "+operator.getSurname()+"\nUsername : "+operator.getUsername());
			}
		});
		
		JLabel lblUser = new JLabel(operator.getName());
		lblUser.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				JPopupMenu userPopup = new JPopupMenu();
				userPopup.add(userInfo);
				userPopup.add(LogOut);
				userPopup.show(e.getComponent(), e.getY(), e.getY());
			}
		});
		Image iconUser = new ImageIcon(this.getClass().getResource("/connected_icon.png")).getImage();
		lblUser.setIcon(new ImageIcon(iconUser));
		lblUser.setBounds(602, 11, 190, 46);
		contentPane.add(lblUser);
		
		JLabel lblLogo = new JLabel("ملک اسلم کریانہ سٹور");
		lblLogo.setForeground(Color.BLUE);
//		lblLogo.setFont(new Font("Times New Roman", Font.BOLD, 36));
		lblLogo.setFont(cfp.getLargeBoldFont());
		lblLogo.setBounds(31, -3, 334, 79);
		contentPane.add(lblLogo);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(31, 76, 147, 495);
		contentPane.add(scrollPane);
		
		tblItemsInfo = new JTable();
		scrollPane.setViewportView(tblItemsInfo);
		
		JButton btnDone = new JButton("مکمل");
		btnDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
				String un = operator.getUsername();
				StartMenu sm = new StartMenu(connection, un);
				sm.setVisible(true);
			}
		});
		Image iconDone = new ImageIcon(this.getClass().getResource("/icon_done.png")).getImage();
		btnDone.setIcon(new ImageIcon(iconDone));
		btnDone.setHorizontalAlignment(SwingConstants.LEADING);
//		btnDone.setFont(new Font("Times New Roman", Font.BOLD, 36));
		btnDone.setFont(cfp.getLargeBoldFont());
		btnDone.setBounds(31, 582, 147, 46);
		contentPane.add(btnDone);
		frame.getRootPane().setDefaultButton(btnDone);
		
		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(188, 76, 624, 552);
		contentPane.add(scrollPane_1);
		
		JToggleButton tglBtnUseStock = new JToggleButton("اسٹاک بند ہے");
//		tglBtnUseStock.setFont(new Font("Times New Roman", Font.BOLD, 20));
		tglBtnUseStock.setFont(cfp.getSmallBoldFont());
		tglBtnUseStock.addItemListener(new ItemListener() {
		   public void itemStateChanged(ItemEvent ev) {
		      if(ev.getStateChange()==ItemEvent.SELECTED){
					tglBtnUseStock.setText("اسٹاک بند ہے");
					tglBtnUseStock.setForeground(Color.DARK_GRAY);
					prefs.putBoolean(STOCK_STATUS_PREF_NAME, false);
					//System.out.println("Toggle Button selected");
					tblItemsInfo.setVisible(false);
					panelDetails.setVisible(false);
		      } else if(ev.getStateChange() == ItemEvent.DESELECTED) {
		    	  	// TODO: Functionality not yet implemented
//		    	  	tglBtnUseStock.setText("اسٹاک کھلا ہے");
		    	  	tglBtnUseStock.setText("اسٹاک بند ہے");
					tglBtnUseStock.setForeground(Color.RED);
					prefs.putBoolean(STOCK_STATUS_PREF_NAME, false);
					tblItemsInfo.setVisible(false);
					panelDetails.setVisible(false);
					//System.out.println("Toggle Button not selected");
		      }
		   }
		});	
		
		tglBtnUseStock.setBounds(339, 11, 204, 46);
		contentPane.add(tglBtnUseStock);
		
		refreshTable();
		refreshSalesPoint();
		
		//System.out.println("StockStatus: " + prefs.getBoolean(STOCK_STATUS_PREF_NAME, false));
		
		if(prefs.getBoolean(STOCK_STATUS_PREF_NAME, false)) {
			tglBtnUseStock.setSelected(false);
			tblItemsInfo.setVisible(true);
			panelDetails.setVisible(true);
		} else {
			tglBtnUseStock.setSelected(true);
			tblItemsInfo.setVisible(false);
			panelDetails.setVisible(false);
		}
	}
}