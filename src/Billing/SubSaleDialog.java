package Billing;

import java.awt.Container;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JPanel;
import net.miginfocom.swing.MigLayout;

import Stock.Item;
import Utils.CustomFontProvider;


public class SubSaleDialog extends JDialog {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	SubSaleDialogCommunicator subSaleDialogCommunicator;
	private CustomFontProvider cfp = new CustomFontProvider();
	
	public SubSaleDialog(BillingSystem billingSystem, JFrame frame, int superItemId, String superItemName, Item[] depItems) {
		super(frame, superItemName, true);

		JDialog dialog = this;
		subSaleDialogCommunicator = billingSystem;
		
		JPanel subPanelSales = new JPanel();
		subPanelSales.setLayout(new MigLayout());
		
		Item[] spDepItems = null;
		int spDepItemsLength = 0;
		for(int i = 0; i < depItems.length; i++) {
			if(depItems[i].getSuperItem() == superItemId) {
				spDepItemsLength++;
			}
		}
		
		int spDepItemsCounter = 0;
		spDepItems = new Item[spDepItemsLength];
		for(int i = 0; i < depItems.length; i++) {
			if(depItems[i].getSuperItem() == superItemId) {
				spDepItems[spDepItemsCounter] = depItems[i];
				spDepItemsCounter++;
			}
		}
		
		JButton[] itemButtons = new JButton[spDepItems.length];
	
		int wrapper = 0;
		for(int i = 0; i < spDepItems.length; i++ ) {
			itemButtons[i] = new JButton(spDepItems[i].getName());
			itemButtons[i].setToolTipText(spDepItems[i].getId() + "");
			final Item tempItem = spDepItems[i];
			itemButtons[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					//System.out.println(depItems[tempIndex].getId());
					tempItem.getName();
					subSaleDialogCommunicator.respondSubSaleDialogCommunicator(tempItem.getId(),
							tempItem.getName());
					dialog.dispose();
				}
			});
//			itemButtons[i].setFont(new Font("Times New Roman", Font.BOLD, 15));
			itemButtons[i].setFont(cfp.getSmallPlainFont());
			if (wrapper < 4) {
				subPanelSales.add(itemButtons[i],
						"width 135:135:135, height 50:50:50");
				wrapper++;
			} else {
				subPanelSales.add(itemButtons[i],
						"width 135:135:135, height 50:50:50, wrap");
				wrapper = 0;
			}
		}
	
		Container c = getContentPane();
		c.setLayout(new MigLayout());

		c.add(subPanelSales, "wrap");
		this.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
		this.pack();
		this.setLocationRelativeTo(null);
		this.setVisible(true);
		
	}
	
	
	public interface SubSaleDialogCommunicator {
		public void respondSubSaleDialogCommunicator(int id, String name);
	}

}
