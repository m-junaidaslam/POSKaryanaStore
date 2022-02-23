package Debt;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.text.AbstractDocument;
import net.proteanit.sql.DbUtils;
import Utils.CustomFontProvider;
import Utils.DoubleFilter;
import Utils.RequestFocusListener;
import Utils.SizeFilter;

import Users.Employee;
import Main.Login;
import Main.StartMenu;


public class DebtInformation extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JFrame frame = this;
	private JPanel contentPane;
	private JTable tblDebtInfo;
	Connection connection = null;
	private JScrollPane scrollPane;
	public static final String DATE_FORMAT_NOW = "dd-MM-yyyy hh:mm a";
	private JTextField textFieldSearch;
	private CustomFontProvider cfp = new CustomFontProvider();

	/**
	 * Launch the application.
	 */
	
	
	/*----------------------------Function to get current Date and Time-------------------*/
	public static String now() {
		//get current date and time as a String output   
		Calendar cal = Calendar.getInstance();
		SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT_NOW);
		return sdf.format(cal.getTime());

	}
	
	/*----------------Close this Frame--------------------*/
	public void close() {
		this.dispose();
	}
	
	/*-----------------Refresh Users Table----------------*/
	public void refreshTable() {
		try {
			String query = "select EID, Name, Surname, Place, Date, Debt from DebtInfo";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			tblDebtInfo.setModel(DbUtils.resultSetToTableModel(rs));
//			tblDebtInfo.setFont(new Font("Times New Roman", Font.BOLD, 20));
			tblDebtInfo.setFont(cfp.getSmallBoldFont());
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(JLabel.CENTER);
			for(int i = 0; i < 6; i++)
				tblDebtInfo.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
			if(tblDebtInfo.getRowCount() > 0)
				tblDebtInfo.setRowSelectionInterval(0, 0);
			tblDebtInfo.getColumnModel().getColumn(0).setPreferredWidth(10);
			tblDebtInfo.getColumnModel().getColumn(1).setPreferredWidth(70);
			tblDebtInfo.getColumnModel().getColumn(2).setPreferredWidth(50);
			tblDebtInfo.getColumnModel().getColumn(3).setPreferredWidth(50);
			tblDebtInfo.getColumnModel().getColumn(4).setPreferredWidth(200);
			tblDebtInfo.getColumnModel().getColumn(5).setPreferredWidth(70);
			tblDebtInfo.setRowHeight(25);
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
	    place.setFont(cfp.getMediumBoldFont());
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
	    myPanel.add(new JLabel("ادھار"));
	    myPanel.add(debt);

//	    javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
	    int result = JOptionPane.showConfirmDialog(null, myPanel, 
	               "گاہک کے کوائف درج کریں :", JOptionPane.OK_CANCEL_OPTION);
	    if (result == JOptionPane.OK_OPTION && !name.getText().isEmpty()) {
    		Customer cus = new Customer(name.getText(), surname.getText(), place.getText(), now(), Double.valueOf(debt.getText()));
    		cus.insertNewCustomer(connection, tblDebtInfo.getRowCount());
	    } else if(result == JOptionPane.OK_OPTION)
	    	JOptionPane.showMessageDialog(null, "نام والی جگہ خالی ہے !");
	    	
	    refreshTable();
	}
	
	
	/*-------------------------Update Debt----------------------------*/
	
	public void updateDebt() {
		Customer customer = new Customer();
		int row = tblDebtInfo.getSelectedRow();
		if(tblDebtInfo.isRowSelected(row)) {
			customer.setId(Integer.valueOf((int) tblDebtInfo.getModel().getValueAt(row, 0)));
			customer.getDataFromDb(connection, customer.getId());
			
			AbstractDocument doc = null;
			
		    JTextField debt = new JTextField();
		    doc = (AbstractDocument) debt.getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			debt.setText(customer.getDebt()+"");
			debt.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					debt.selectAll();
				}
			});

			JPanel myPanel = new JPanel(new GridLayout(7, 2, 5, 5));
			myPanel.add(new JLabel(": نام"));
			myPanel.add(new JLabel(customer.getName()));
			myPanel.add(new JLabel(": دوسرا نام"));
			myPanel.add(new JLabel(customer.getSurname()));
			myPanel.add(new JLabel(": جگہ"));
			myPanel.add(new JLabel(customer.getPlace()));
			myPanel.add(new JLabel(": قرض"));
			myPanel.add(debt);

//			javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
		    int result = JOptionPane.showConfirmDialog(null, myPanel, 
		               "گاہک کا ادھار تبدیل کریں :", JOptionPane.OK_CANCEL_OPTION);
		    if (result == JOptionPane.OK_OPTION) {
		    	customer.setDebt(Double.valueOf(debt.getText()));
		    	customer.setDate(now());
		    	customer.updateCustomer(connection);
		    }
			
		} else {
//			javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
			JOptionPane.showMessageDialog(null, "کوئی اکاؤنٹ نہیں چنا گیا !");
		}
		refreshTable();
	}
	
	
	/*------------------------Delete Customer-----------------------------*/
	public void deleteCustomer() {
		int row = tblDebtInfo.getSelectedRow();
		if(tblDebtInfo.isRowSelected(row)) {
//			javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
			int action = JOptionPane.showConfirmDialog(null, "کیا آپ واقعی اس گاہک کے اکاؤنٹ کو ختم کرنا چاہتے ہیں ؟", "Delete", JOptionPane.YES_NO_OPTION);
			if(action == 0) { 
				try {
					String eid = (tblDebtInfo.getModel().getValueAt(row, 0)).toString();
					String query = "delete from DebtInfo where EID='"+eid+"'";
					PreparedStatement pst = connection.prepareStatement(query);
					
					pst.execute();
					
//					javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
					JOptionPane.showMessageDialog(null, "اکاؤنٹ ختم کر دیا گیا !");
					
					pst.close();
				
				
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} else {
//			javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
			JOptionPane.showMessageDialog(null, "کوئی اکاؤنٹ نہیں چنا گیا !");
		}
		refreshTable();
	}

	/**
	 * Create the frame.
	 */
	public DebtInformation(Connection connection, Employee operator) {
		
		this.connection = connection;
		setTitle("ادھار کی تفصیل");
		Image iconTitle = new ImageIcon(this.getClass().getResource("/icon_debtinfo.png")).getImage();
		setIconImage(iconTitle);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 808, 484);
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
		
		Font largeBoldFont = cfp.getLargeBoldFont();
		
		JLabel lblLogo = new JLabel("ملک اسلم کریانہ سٹور");
		lblLogo.setForeground(Color.BLUE);
//		lblLogo.setFont(new Font("Times New Roman", Font.BOLD, 36));
		lblLogo.setFont(largeBoldFont);
		lblLogo.setBounds(31, -3, 334, 68);
		contentPane.add(lblLogo);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(31, 83, 546, 351);
		contentPane.add(scrollPane);
		
		tblDebtInfo = new JTable();
		scrollPane.setViewportView(tblDebtInfo);
		
		JButton btnNewDebt = new JButton("نیا    ");
		btnNewDebt.setHorizontalAlignment(SwingConstants.LEADING);
		Image iconSave = new ImageIcon(this.getClass().getResource("/icon_save.png")).getImage();
		btnNewDebt.setIcon(new ImageIcon(iconSave));
//		btnNewDebt.setFont(new Font("Times New Roman", Font.BOLD, 36));
		btnNewDebt.setFont(largeBoldFont);
		btnNewDebt.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    newUser();
			    
			}
		});
		btnNewDebt.setBounds(587, 129, 180, 68);
		contentPane.add(btnNewDebt);
		
		JButton btnDelete = new JButton("خاتمہ ");
		btnDelete.setHorizontalAlignment(SwingConstants.LEADING);
		Image iconDelete = new ImageIcon(this.getClass().getResource("/icon_delete.png")).getImage();
		btnDelete.setIcon(new ImageIcon(iconDelete));
//		btnDelete.setFont(new Font("Times New Roman", Font.BOLD, 36));
		btnDelete.setFont(largeBoldFont);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteCustomer();
			}
			
		});
		btnDelete.setBounds(587, 287, 180, 68);
		contentPane.add(btnDelete);
		
		JButton btnDone = new JButton("مکمل  ");
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
		btnDone.setFont(largeBoldFont);
		btnDone.setBounds(587, 366, 180, 68);
		contentPane.add(btnDone);
		frame.getRootPane().setDefaultButton(btnDone);
		
		JButton btnUpdate = new JButton("تبدیلی");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateDebt();
			}
		});
		Image iconUpdate = new ImageIcon(this.getClass().getResource("/icon_update.png")).getImage();
		btnUpdate.setIcon(new ImageIcon(iconUpdate));
		btnUpdate.setHorizontalAlignment(SwingConstants.LEADING);
//		btnUpdate.setFont(new Font("Times New Roman", Font.BOLD, 36));
		btnUpdate.setFont(largeBoldFont);
		btnUpdate.setBounds(587, 208, 180, 68);
		contentPane.add(btnUpdate);
		
		textFieldSearch = new JTextField();
//		textFieldSearch.setFont(new Font("Times New Roman", Font.BOLD, 16));
		textFieldSearch.setFont(cfp.getSmallBoldFont());
		this.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				textFieldSearch.requestFocus();
			}
		});
		textFieldSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				try {
					String query = "select EID, Name, Surname, Place, Date, Debt from DebtInfo where Name LIKE ?";
					PreparedStatement pst = connection.prepareStatement(query);
					pst.setString(1, "%" + textFieldSearch.getText() + "%");
					ResultSet rs = pst.executeQuery();

					tblDebtInfo.setModel(DbUtils.resultSetToTableModel(rs));

					pst.close();
					rs.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		textFieldSearch.setBounds(331, 24, 190, 30);
		contentPane.add(textFieldSearch);
		textFieldSearch.setColumns(10);
		
		JLabel lblSearch = new JLabel(" تلاش ");
//		lblSearch.setFont(new Font("Times New Roman", Font.BOLD, 19));
		lblSearch.setFont(cfp.getMediumPlainFont());
		lblSearch.setBounds(531, 24, 50, 30);
		contentPane.add(lblSearch);
		
		refreshTable();
	}
}
