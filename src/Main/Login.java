package Main;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Connection;
import java.sql.PreparedStatement;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPasswordField;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.plaf.FontUIResource;

import Users.Employee;
import Utils.CustomFontProvider;
import Utils.SqliteConnection;

public class Login {

	private boolean isConnected;
	private JFrame frmLogin;
	private JTextField textFieldUsername;
	private JLabel lblDbStatus = new JLabel("Disconnected");
	Connection connection = null;
	private JPasswordField passwordField;
	private int wrongLoginCount = 0;
	CustomFontProvider customFontProvider;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					Login window = new Login();
					window.frmLogin.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public Login() {

		initialize();
		frmLogin.setVisible(true);
		// connection = SqliteConnection.dbConnector();
		connection = SqliteConnection.connect();
		try {
			String query = "select * from EmployeeInfo";
			PreparedStatement pst = connection.prepareStatement(query);
			pst.executeQuery();
			isConnected = true;
			pst.close();
		} catch (Exception e) {
			isConnected = false;
		}
		if (isConnected) {
			lblDbStatus.setText("Connected");
			Image iconDbStatus = new ImageIcon(this.getClass().getResource("/connected_icon.png")).getImage();
			lblDbStatus.setIcon(new ImageIcon(iconDbStatus));
		}
	}

	/**
	 * Methods for local use
	 */

	public void proceedLogin(Employee emp) {
		frmLogin.dispose();
		String un = emp.getUsername();
		StartMenu sm = new StartMenu(connection, un);
		sm.setVisible(true);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize() {
		customFontProvider = new CustomFontProvider();
		frmLogin = new JFrame();
		frmLogin.setTitle("Login");
		Image iconTitle = new ImageIcon(this.getClass().getResource("/logintitle_icon.png")).getImage();
		frmLogin.setIconImage(iconTitle);
		frmLogin.setBounds(100, 100, 513, 292);
		frmLogin.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frmLogin.getContentPane().setLayout(null);

		JLabel lblUsername = new JLabel("استعمال کار");
		lblUsername.setFont(customFontProvider.getSmallBoldFont());
		lblUsername.setHorizontalAlignment(SwingConstants.CENTER);
		lblUsername.setBounds(165, 80, 103, 36);
		frmLogin.getContentPane().add(lblUsername);

		JLabel lblPassword = new JLabel("پاسورڈ");
		lblPassword.setFont(customFontProvider.getSmallBoldFont());
		lblPassword.setHorizontalAlignment(SwingConstants.CENTER);
		lblPassword.setBounds(165, 124, 103, 36);
		frmLogin.getContentPane().add(lblPassword);

		textFieldUsername = new JTextField();
		textFieldUsername.setFont(new Font("Times New Roman", Font.BOLD, 16));
		textFieldUsername.setBounds(278, 82, 192, 36);
		frmLogin.getContentPane().add(textFieldUsername);
		textFieldUsername.setColumns(10);

		JButton btnLogin = new JButton("Login");
		btnLogin.addActionListener(new ActionListener() {
			@SuppressWarnings("deprecation")
			public void actionPerformed(ActionEvent arg0) {
				Employee emp = new Employee();
				emp.setUsername(textFieldUsername.getText().toString());
				emp.setPassword(passwordField.getText().toString());
				if (!emp.getPassword().isEmpty() && !emp.getUsername().isEmpty()) {
					if (emp.loginAttempCheck(connection)) {
						proceedLogin(emp);
					} else {
						javax.swing.UIManager.put("OptionPane.messageFont",
								new FontUIResource(customFontProvider.getSmallBoldFont()));
						JOptionPane.showMessageDialog(null, "غلط نام یا پاسورڈ !");
						wrongLoginCount++;
						if (wrongLoginCount == 3) {
							System.exit(JFrame.EXIT_ON_CLOSE);
						}
					}
				} else {
					if (emp.getPassword().isEmpty() && emp.getUsername().isEmpty())
						JOptionPane.showMessageDialog(null, "نام اور پاسورڈ والی جگہ خالی ہے!");
					else if (emp.getUsername().isEmpty())
						JOptionPane.showMessageDialog(null, "نام والی جگہ خالی ہے!");
					else if (emp.getPassword().isEmpty())
						JOptionPane.showMessageDialog(null, "پاسورڈ والی جگہ خالی ہے!");

				}
				// JOptionPane.showMessageDialog(null, Employee.hashPassword("2"));
				// System.out.println(Employee.hashPassword("<PASSWORD>"));
			}
		});
		btnLogin.setFont(new Font("Times New Roman", Font.BOLD, 16));
		Image iconOk = new ImageIcon(this.getClass().getResource("/ok.png")).getImage();
		btnLogin.setIcon(new ImageIcon(iconOk));
		btnLogin.setBounds(363, 206, 103, 36);
		frmLogin.getContentPane().add(btnLogin);
		frmLogin.getRootPane().setDefaultButton(btnLogin);

		JLabel lblLoginIcon = new JLabel("");
		Image iconLogin = new ImageIcon(this.getClass().getResource("/login.png")).getImage();
		lblLoginIcon.setIcon(new ImageIcon(iconLogin));
		lblLoginIcon.setBounds(27, 62, 128, 157);
		frmLogin.getContentPane().add(lblLoginIcon);

		lblDbStatus = new JLabel("Disconnected");
		lblDbStatus.setHorizontalAlignment(SwingConstants.CENTER);
		lblDbStatus.setFont(new Font("Times New Roman", Font.BOLD, 16));
		Image iconDbStatus = new ImageIcon(this.getClass().getResource("/notconnected_icon.png")).getImage();
		lblDbStatus.setIcon(new ImageIcon(iconDbStatus));
		lblDbStatus.setBounds(328, 11, 142, 60);
		frmLogin.getContentPane().add(lblDbStatus);

		passwordField = new JPasswordField();
		passwordField.setBounds(278, 127, 192, 36);
		frmLogin.getContentPane().add(passwordField);

		JLabel lblLogo = new JLabel("ملک اسلم کریانہ سٹور");
		lblLogo.setForeground(Color.BLUE);
		lblLogo.setFont(customFontProvider.getLargeBoldFont());
		lblLogo.setBounds(32, 0, 288, 51);
		frmLogin.getContentPane().add(lblLogo);

		JLabel lblDevelopedByEngr = new JLabel("Copyright: Engr. Muhammad Junaid Aslam");
		lblDevelopedByEngr.setBounds(10, 230, 343, 23);
		frmLogin.getContentPane().add(lblDevelopedByEngr);

		// Code for status
		// change----------------------------------------------------------------------------
		/*
		 * lblDbStatus.setText("Connected");
		 * Image iconDbStatus = new
		 * ImageIcon(this.getClass().getResource("/connected_icon.png")).getImage();
		 * lblDbStatus.setIcon(new ImageIcon(iconDbStatus));
		 */
	}
}
