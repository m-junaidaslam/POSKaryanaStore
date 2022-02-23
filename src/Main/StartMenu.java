package Main;

import java.awt.Image;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.EmptyBorder;
import javax.swing.JLabel;
import java.awt.Color;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;
import java.sql.Connection;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import Users.Employee;

import Billing.BillingSystem;
import Users.UsersManagement;
import Utils.CustomFontProvider;
import Stock.StockInformation;
import Debt.DebtInformation;



public class StartMenu extends JFrame {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Employee operator = new Employee();
	private JPanel contentPane;
	private CustomFontProvider customFontProvider = new CustomFontProvider();
	/**
	 * Launch the application.
	 */

	public void close() {
		this.dispose();	
	}
	
	/**
	 * Create the frame.
	 */
	public StartMenu(Connection connection, String username) {
		setTitle("Menu");
		Image iconTitle = new ImageIcon(this.getClass().getResource("/menutitle_icon.png")).getImage();
		setIconImage(iconTitle);
		operator.getDataFromDb(connection, username);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 565, 484);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		contentPane.setLayout(null);
		
		final JMenuItem LogOut = new JMenuItem("Log Out");
		LogOut.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
//				javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(customFontProvider.getSmallBoldFont()));
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
		lblUser.setBounds(349, 16, 190, 46);
		contentPane.add(lblUser);
		
		JLabel lblLogo = new JLabel("ملک اسلم کریانہ سٹور");
		lblLogo.setForeground(Color.BLUE);
//		lblLogo.setFont(new Font("Times New Roman", Font.BOLD, 36));
		lblLogo.setFont(customFontProvider.getLargeBoldFont());
		lblLogo.setBounds(38, 16, 301, 51);
		contentPane.add(lblLogo);
		
		JButton btnBillingSystem = new JButton("         بل کا نظام            ");
		btnBillingSystem.setBackground(Color.LIGHT_GRAY);
		Image iconBill = new ImageIcon(this.getClass().getResource("/bill_icon.png")).getImage();
		btnBillingSystem.setIcon(new ImageIcon(iconBill));
		btnBillingSystem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close();
				BillingSystem bs = new BillingSystem(connection, operator);
				bs.setVisible(true);
			}
		});
//		btnBillingSystem.setFont(new Font("Times New Roman", Font.BOLD, 25));
		btnBillingSystem.setFont(customFontProvider.getMediumBoldFont());
		btnBillingSystem.setBounds(120, 103, 320, 63);
		contentPane.add(btnBillingSystem);
		getRootPane().setDefaultButton(btnBillingSystem);
		
		JButton btnUsersManagement = new JButton("استعمال کاروں کی تفصیل   ");
		btnUsersManagement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				close();
				UsersManagement um = new UsersManagement(connection, operator);
				um.setVisible(true);
			}
		});
		Image iconUsers = new ImageIcon(this.getClass().getResource("/users_icon.png")).getImage();
		btnUsersManagement.setIcon(new ImageIcon(iconUsers));
		if(operator.isAdmin())
			btnUsersManagement.setEnabled(true);
		else
			btnUsersManagement.setEnabled(false);
//		btnUsersManagement.setFont(new Font("Times New Roman", Font.BOLD, 25));
		btnUsersManagement.setFont(customFontProvider.getMediumBoldFont());
		btnUsersManagement.setBackground(Color.LIGHT_GRAY);
		btnUsersManagement.setBounds(120, 177, 320, 63);
		contentPane.add(btnUsersManagement);
		
		JButton btnStockInfo = new JButton("     اسٹاک کی تفصیل        ");
		Image iconStock = new ImageIcon(this.getClass().getResource("/stock_icon.png")).getImage();
		btnStockInfo.setIcon(new ImageIcon(iconStock));
		btnStockInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close();
				StockInformation si = new StockInformation(connection, operator);
				si.setVisible(true);
			}
		});
		// TODO: Not yet implemented correctly. Disabled
		btnStockInfo.setEnabled(false);
//		btnStockInfo.setFont(new Font("Times New Roman", Font.BOLD, 25));
		btnStockInfo.setFont(customFontProvider.getMediumBoldFont());
		btnStockInfo.setBackground(Color.LIGHT_GRAY);
		btnStockInfo.setBounds(120, 251, 320, 63);
		contentPane.add(btnStockInfo);
		
		JButton btnDebtInfo = new JButton("        ادھار کی تفصیل     ");
		btnDebtInfo.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close();
				DebtInformation di = new DebtInformation(connection, operator);
				di.setVisible(true);
			}
		});
//		btnDebtInfo.setFont(new Font("Times New Roman", Font.BOLD, 25));
		btnDebtInfo.setFont(customFontProvider.getMediumBoldFont());
		Image iconDebtInfo = new ImageIcon(this.getClass().getResource("/icon_debtinfo.png")).getImage();
		btnDebtInfo.setIcon(new ImageIcon(iconDebtInfo));
		btnDebtInfo.setBackground(Color.LIGHT_GRAY);
		btnDebtInfo.setBounds(120, 325, 320, 63);
		contentPane.add(btnDebtInfo);
		if(!operator.isAdmin()) {
			btnDebtInfo.setEnabled(true);
		}
		
		JLabel label = new JLabel("Copyright: Engr. Muhammad Junaid Aslam");
		label.setBounds(248, 411, 301, 23);
		contentPane.add(label);
	}
}
