package Users;

import java.awt.GridLayout;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPasswordField;
import javax.swing.JPopupMenu;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.JLabel;

import java.awt.Color;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.swing.JTable;

import net.proteanit.sql.DbUtils;

import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import Utils.CustomFontProvider;
import Utils.RequestFocusListener;
import Main.Login;
import Main.StartMenu;

public class UsersManagement extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JFrame frame = this;
	private JPanel contentPane;
	private JTable tblUsersInfo;
	private Employee operator = new Employee();
	Connection connection = null;
	private JScrollPane scrollPane;
	private CustomFontProvider cfp = new CustomFontProvider();

	/**
	 * Launch the application.
	 */
	
	/*----------------Close this Frame--------------------*/
	public void close() {
		this.dispose();
	}
	
	/*-----------------Refresh Users Table----------------*/
	public void refreshTable() {
		try {
			String query = "select EID, Name, Surname, IsAdmin, Username from EmployeeInfo";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			tblUsersInfo.setModel(DbUtils.resultSetToTableModel(rs));
			tblUsersInfo.setFont(cfp.getSmallPlainFont());
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(JLabel.CENTER);
			for(int i = 0; i < 5; i++)
				tblUsersInfo.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
			if(tblUsersInfo.getRowCount() > 1)
				tblUsersInfo.setRowSelectionInterval(1, 1);
			tblUsersInfo.getColumnModel().getColumn(0).setPreferredWidth(5);
			tblUsersInfo.getColumnModel().getColumn(1).setPreferredWidth(90);
			tblUsersInfo.getColumnModel().getColumn(2).setPreferredWidth(70);
			tblUsersInfo.getColumnModel().getColumn(3).setPreferredWidth(10);
			tblUsersInfo.getColumnModel().getColumn(4).setPreferredWidth(50);
			pst.close();
			rs.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	/*------------------------New User----------------------*/
	@SuppressWarnings("deprecation")
	public void newUser() {
		JTextField name = new JTextField();
		name.addAncestorListener( new RequestFocusListener() );
	    JTextField surname = new JTextField();
	    JTextField username = new JTextField();
	    JPasswordField password = new JPasswordField();
	    JPasswordField confirmPassword = new JPasswordField();
	    JCheckBox admin = new JCheckBox();

	    JPanel myPanel = new JPanel(new GridLayout(7, 2, 5, 5));
	    myPanel.add(new JLabel("Name"));
	    myPanel.add(name);
	    myPanel.add(new JLabel("Surname"));
	    myPanel.add(surname);
	    myPanel.add(new JLabel("Username"));
	    myPanel.add(username);
	    myPanel.add(new JLabel("Password"));
	    myPanel.add(password);
	    myPanel.add(new JLabel("Confirm Password"));
	    myPanel.add(confirmPassword);
	    myPanel.add(new JLabel("Administrator"));
	    myPanel.add(admin);

//	    javax.swing.UIManager.put("OptionPane.messageFont", cfp.getSmallBoldFont());
	    int result = JOptionPane.showConfirmDialog(null, myPanel, 
	               "اپنے کوائف درج کریں :", JOptionPane.OK_CANCEL_OPTION);
	    if (result == JOptionPane.OK_OPTION && !username.getText().isEmpty() && !password.getText().isEmpty()) {
	    	if(password.getText().equals(confirmPassword.getText())) {
	    		Employee emp = new Employee(admin.isSelected(), name.getText(), surname.getText(), username.getText(), password.getText());
	    		emp.insertNewEmployee(connection);
	    	} else {
	    		JOptionPane.showMessageDialog(null, "پاسورڈ مل نہیں رہے !");
	    	}
	    } else if(result == JOptionPane.OK_OPTION)
	    	JOptionPane.showMessageDialog(null, "یوزرنیم یا پاسورڈ والی جگہ خالی ہے !");
	    	
	    refreshTable();
	}
	
	/*------------------------Delete User-----------------------------*/
	public void deleteUser() {
		int row = tblUsersInfo.getSelectedRow();
		if(tblUsersInfo.isRowSelected(row) && (row != 0)) {
//			javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
			int action = JOptionPane.showConfirmDialog(null, "کیا آپ واقعی اکاؤنٹ ختم کرنا چاہتے ہیں ؟", "Delete", JOptionPane.YES_NO_OPTION);
			if(action == 0) { 
				try {
					String eid = (tblUsersInfo.getModel().getValueAt(row, 0)).toString();
					if(!eid.matches(String.valueOf(operator.getId()))) {
						String query = "delete from EmployeeInfo where EID='"+eid+"'";
						PreparedStatement pst = connection.prepareStatement(query);
						
						pst.execute();
						
//						javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
						JOptionPane.showMessageDialog(null, "اکاؤنٹ ختم کر دیا گیا !");
						
						pst.close();
					} else {
//						javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
						JOptionPane.showMessageDialog(null, "یہ اکا‏ئونٹ زیر استعمال ہے، اس لیے ختم نہیں کیا جا سکتا۔");
					}
				
				
				} catch (Exception e1) {
					e1.printStackTrace();
				}
			}
		} else {
//			javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
			JOptionPane.showMessageDialog(null, "ماسٹر اکاؤنٹ ختم نہیں ہو سکتا !");
		}
		refreshTable();
	}

	/**
	 * Create the frame.
	 */
	public UsersManagement(Connection connection, Employee operator) {

		this.connection = connection;
		this.operator = operator;

		setTitle("استعمال کاروں کی تفصیل");
		Image iconTitle = new ImageIcon(this.getClass().getResource("/usertitle_icon.png")).getImage();
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
		
		JLabel lblLogo = new JLabel("ملک اسلم کریانہ سٹور");
		lblLogo.setForeground(Color.BLUE);
//		lblLogo.setFont(new Font("Times New Roman", Font.BOLD, 36));
		lblLogo.setFont(cfp.getLargeBoldFont());
		lblLogo.setBounds(31, -3, 334, 68);
		contentPane.add(lblLogo);
		
		scrollPane = new JScrollPane();
		scrollPane.setBounds(31, 83, 546, 351);
		contentPane.add(scrollPane);
		
		tblUsersInfo = new JTable();
		tblUsersInfo.setRowHeight(25);
		scrollPane.setViewportView(tblUsersInfo);
		
		JButton btnNew = new JButton("نیا    ");
		btnNew.setHorizontalAlignment(SwingConstants.LEADING);
		Image iconSave = new ImageIcon(this.getClass().getResource("/icon_save.png")).getImage();
		btnNew.setIcon(new ImageIcon(iconSave));
//		btnNew.setFont(new Font("Times New Roman", Font.BOLD, 36));
		btnNew.setFont(cfp.getLargeBoldFont());
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
			    newUser();
			    
			}
		});
		btnNew.setBounds(587, 120, 180, 96);
		contentPane.add(btnNew);
		
		JButton btnDelete = new JButton("خاتمہ ");
		btnDelete.setHorizontalAlignment(SwingConstants.LEADING);
		Image iconDelete = new ImageIcon(this.getClass().getResource("/icon_delete.png")).getImage();
		btnDelete.setIcon(new ImageIcon(iconDelete));
//		btnDelete.setFont(new Font("Times New Roman", Font.BOLD, 36));
		btnDelete.setFont(cfp.getLargeBoldFont());
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteUser();
			}
			
		});
		btnDelete.setBounds(587, 227, 180, 96);
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
		btnDone.setFont(cfp.getLargeBoldFont());
		btnDone.setBounds(587, 338, 180, 96);
		contentPane.add(btnDone);
		frame.getRootPane().setDefaultButton(btnDone);		
		
		refreshTable();
	}
}
