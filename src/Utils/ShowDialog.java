package Utils;

import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

import java.awt.Font;

public final class ShowDialog {
	

	private ShowDialog() {;}
	
	public static void msg(Font font, String msg) {
		JLabel label = new JLabel(msg);
		label.setFont(font);
		JOptionPane.showMessageDialog(null,label);
	}
	
	public static int panelCofirm(Font font, JPanel panel, String msg, int option) {
		JLabel label = new JLabel(msg);
		label.setFont(font);
		return JOptionPane.showConfirmDialog(null, panel,
				msg, JOptionPane.OK_CANCEL_OPTION);
	}
	
}
