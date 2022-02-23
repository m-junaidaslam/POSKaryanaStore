package Billing;

import java.awt.Color;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Image;
import java.awt.Insets;
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
import java.util.prefs.Preferences;

import javax.print.PrintService;
import javax.print.PrintServiceLookup;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.text.AbstractDocument;

import net.proteanit.sql.DbUtils;
import net.miginfocom.swing.MigLayout;

import javax.swing.JToggleButton;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

import Utils.CustomFontProvider;
import Utils.DoubleFilter;
import Utils.SizeFilter;
import Utils.RequestFocusListener;
import Utils.ShowDialog;

import Stock.Item;
import Stock.SizedItem;
import Stock.SuperItem;

import Main.Login;
import Main.StartMenu;
import Users.Employee;
import Billing.SubSaleDialog;

public class BillingSystem extends JFrame implements SubSaleDialog.SubSaleDialogCommunicator {

	/**
	 * Pulished By: Muhammad Junaid Aslam
	 * EMAIL: junaidaslam797@gmail.com
	 */
	
	private static final long serialVersionUID = 1L;
	Preferences prefs = Preferences.userNodeForPackage(BillingSystem.class);
	final String STOCK_STATUS_PREF_NAME = "pref_stock_status";
	SubSaleDialog subSaleDialog;
	private JPanel contentPane;
	private JTable tblItemsInfo;
	Connection connection = null;
	private JScrollPane scrollPane;
	private JButton btnDelete;
	GridBagConstraints gridBagConstraints = new GridBagConstraints();
	private JTextField textFieldSearch;
	private JTable tblBill;
	private JScrollPane scrollPane_1;
	private JPanel panelSales;
	private JScrollPane scrollPane_2;
	private JLabel lblTotalDisp;
	private JLabel lblTotalAmount;
	Object[] billColumns = { "نام", "گنتی", "قسم", "سائز", "رقم" };
	DefaultTableModel billModel = new DefaultTableModel();
	private JTextField textFieldAmountReceived;
	private JFrame frame = this;
	int[] billRowId = new int[200];
	String[] billRowType = new String[200];
	String[] billRowSize = new String[200];
	@SuppressWarnings({ "rawtypes", "unchecked" })
	ComboBoxModel comboModel = new DefaultComboBoxModel(new String[] {
			"کچھ نہیں", "گٹو", "ڈبہ", "بنڈل", "کارٹن", "درجن" });
	JToggleButton tglBtnNormal = new JToggleButton("عام");
	private JLabel label;
	PrintService requiredService = null;
	private boolean stockStatus;
	private CustomFontProvider cfp = new CustomFontProvider();

	/**
	 * Launch the application.
	 */

	/*------------------------Subtract Sized Item from Database--------------------*/
	public void subtractItem(int itemId, int sizeId, double units,
			String table_value, String type) {
		// TODO Auto-generated method stub
		String presentStock = "";
		long wholePresentStock = 0;
		long subPresentStock = 0;
		double relatedStock = 1;
		double relatedStock_1 = 1;
		Item thisItem = new Item();
		thisItem.setId(itemId);
		thisItem.getItemFromDb(connection);
		String typeCheck = new String();
		boolean isRelated = false;
		boolean isSized = false;

		switch (type) {

		case "کلو":
			typeCheck = thisItem.getRelationKg();
			isSized = false;
			if (!typeCheck.equals("اسٹاک"))
				isRelated = true;
			break;
		case "ڈبہ":
			typeCheck = thisItem.getRelationBox();
			isSized = false;
			if (!typeCheck.equals("اسٹاک"))
				isRelated = true;
			if (thisItem.getPricePacket().getSizeRelation(sizeId)
					.equals("/Box"))
				relatedStock_1 = thisItem.getPricePacket().getSizeStock(sizeId);
			break;
		case "گٹو":
			typeCheck = thisItem.getRelationGattu();
			isSized = false;
			isRelated = false;
			break;
		case "بنڈل":
			typeCheck = thisItem.getRelationBundle();
			isSized = false;
			isRelated = false;
			break;
		case "درجن":
			typeCheck = thisItem.getPriceDozen().getSizeRelation(sizeId);
			isSized = true;
			if (!typeCheck.equals("اسٹاک"))
				isRelated = true;
			if (thisItem.getPricePacket().getSizeRelation(sizeId)
					.equals("/Dozen"))
				relatedStock_1 = thisItem.getPricePacket().getSizeStock(sizeId);
			break;
		case "پیکٹ":
			typeCheck = thisItem.getPricePacket().getSizeRelation(sizeId);
			isSized = true;
			if (!typeCheck.equals("اسٹاک"))
				isRelated = true;
			break;
		case "کارٹن":
			typeCheck = thisItem.getPriceCarton().getSizeRelation(sizeId);
			isSized = true;
			isRelated = false;
			break;
		case "بالٹی":
			typeCheck = thisItem.getPriceBucket().getSizeRelation(sizeId);
			isSized = true;
			isRelated = false;
			break;
		case "بوتل":
			typeCheck = thisItem.getPriceBottle().getSizeRelation(sizeId);
			isSized = true;
			isRelated = false;
			break;
		case "ٹین":
			typeCheck = thisItem.getPriceTin().getSizeRelation(sizeId);
			isSized = true;
			isRelated = false;
			break;
		case "گیلن":
			typeCheck = thisItem.getPriceGallon().getSizeRelation(sizeId);
			isSized = true;
			isRelated = false;
			break;
		}
		try {
			String query = new String();
			PreparedStatement pst = null;
			ResultSet rs = null;
			switch (typeCheck) {
			case "/Gattu":
				query = "select GattuStock from NonSizedItemsRates where EID='"
						+ itemId + "'";
				pst = connection.prepareStatement(query);
				rs = pst.executeQuery();
				while (rs.next()) {
					presentStock = rs.getString("GattuStock");
				}
				pst.close();
				rs.close();
				break;
			case "/Box":
				if (thisItem.getRelationBox().equals("/Bundle"))
					query = "select BundleStock from NonSizedItemsRates where EID='"
							+ itemId + "'";
				else
					query = "select BoxStock from NonSizedItemsRates where EID='"
							+ itemId + "'";
				pst = connection.prepareStatement(query);
				rs = pst.executeQuery();
				while (rs.next()) {
					if (thisItem.getRelationBox().equals("/Bundle"))
						presentStock = rs.getString("BundleStock");
					else
						presentStock = rs.getString("BoxStock");
				}
				pst.close();
				rs.close();
				break;
			case "/Bundle":
				query = "select BundleStock from NonSizedItemsRates where EID='"
						+ itemId + "'";
				pst = connection.prepareStatement(query);
				rs = pst.executeQuery();
				while (rs.next()) {
					presentStock = rs.getString("BundleStock");
				}
				pst.close();
				rs.close();
				break;
			case "/Carton":
				query = "select StockSize" + (sizeId + 1)
						+ " from CartonItemsRates where EID='" + itemId + "'";
				pst = connection.prepareStatement(query);
				rs = pst.executeQuery();
				while (rs.next()) {
					presentStock = rs.getString("StockSize" + (sizeId + 1));
				}
				pst.close();
				rs.close();
				break;
			case "/Dozen":
				if (thisItem.getPriceDozen().getSizeRelation(sizeId)
						.equals("/Gattu"))
					query = "select GattuStock from NonSizedItemsRates where EID='"
							+ itemId + "'";
				else if (thisItem.getPriceDozen().getSizeRelation(sizeId)
						.equals("/Carton"))
					query = "select StockSize" + (sizeId + 1)
							+ " from CartonItemsRates where EID='" + itemId
							+ "'";
				else
					query = "select StockSize" + (sizeId + 1)
							+ " from DozenItemsRates where EID='" + itemId
							+ "'";
				pst = connection.prepareStatement(query);
				rs = pst.executeQuery();
				while (rs.next()) {
					presentStock = rs.getString("StockSize" + (sizeId + 1));
				}
				pst.close();
				rs.close();
				break;

			}
			if (isSized)
				query = "select StockSize" + (sizeId + 1) + " from "
						+ table_value + " where EID='" + itemId + "'";
			else
				query = "select " + table_value
						+ " from NonSizedItemsRates where EID='" + itemId + "'";
			pst = connection.prepareStatement(query);
			rs = pst.executeQuery();
			while (rs.next()) {
				if (isRelated && !typeCheck.equals("اسٹاک")) {
					if (isSized)
						relatedStock = relatedStock_1
								* rs.getDouble("StockSize" + (sizeId + 1));
					else
						relatedStock = relatedStock_1
								* rs.getDouble(table_value);
					if ((typeCheck.equals("/Box"))
							&& (thisItem.getRelationBox().equals("/Bundle"))) {
						relatedStock = relatedStock * thisItem.getStockBox();
					} else if ((typeCheck.equals("/Dozen"))
							&& (thisItem.getPriceDozen()
									.getSizeRelation(sizeId).equals("/Carton"))) {
						relatedStock = relatedStock
								* thisItem.getPriceDozen().getSizeStock(sizeId);
					} else if ((typeCheck.equals("/Dozen"))
							&& (thisItem.getPriceDozen()
									.getSizeRelation(sizeId).equals("/Gattu"))) {
						relatedStock = relatedStock * thisItem.getStockGattu();
					}
				} else {
					if (isSized)
						presentStock = rs.getString("StockSize" + (sizeId + 1));
					else
						presentStock = rs.getString(table_value);
				}

			}

			pst.close();
			rs.close();
		} catch (Exception e) {
			e.printStackTrace();
		}

		int len = presentStock.length();
		boolean dotDetected = false;
		for (int i = 0; i < len; i++) {
			if (dotDetected)
				subPresentStock = Long.valueOf(String.valueOf(subPresentStock)
						+ presentStock.charAt(i));
			else {
				if (presentStock.charAt(i) == '.')
					dotDetected = true;
				else
					wholePresentStock = Long.valueOf(String
							.valueOf(wholePresentStock)
							+ presentStock.charAt(i));
			}
		}
//		javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
		if ((((wholePresentStock * relatedStock) + subPresentStock) - units) > 0) {

			for (int i = 0; i < (units * relatedStock_1); i++) {
				if (relatedStock > 1) {
					if (subPresentStock <= 0) {
						wholePresentStock--;
						subPresentStock = (long) relatedStock - 1;
					} else
						subPresentStock--;
				} else
					wholePresentStock--;
			}

			if (wholePresentStock < 2) {
				if (isSized) {
//					JOptionPane.showMessageDialog(null, "آئٹم نمبر " + itemId
//							+ " کے " + type + " کے سائز " + (sizeId + 1)
//							+ " والا مال صرف " + wholePresentStock
//							+ " اکائیاں بچا ہے۔ ");
					ShowDialog.msg(cfp.getMediumPlainFont(), "آئٹم نمبر " + itemId
							+ " کے " + type + " کے سائز " + (sizeId + 1)
							+ " والا مال صرف " + wholePresentStock
							+ " اکائیاں بچا ہے۔ ");
				
				} else {
//					JOptionPane.showMessageDialog(null, "آئٹم نمبر " + itemId
//							+ " کے " + type + " کا مال صرف "
//							+ wholePresentStock + " اکائیاں بچا ہے۔ ");
					ShowDialog.msg(cfp.getMediumPlainFont(), "آئٹم نمبر " + itemId
							+ " کے " + type + " کا مال صرف "
							+ wholePresentStock + " اکائیاں بچا ہے۔ ");
				}
			}
		} else {
			wholePresentStock = 0;
			subPresentStock = 0;
			if (isSized) {
//				JOptionPane.showMessageDialog(null, "اب آئٹم نمبر " + itemId
//						+ " کے " + type + " کے سائز " + (sizeId + 1)
//						+ " والا مال ختم ہو گیا ہے۔");
				ShowDialog.msg(cfp.getMediumPlainFont(), "اب آئٹم نمبر " + itemId
						+ " کے " + type + " کے سائز " + (sizeId + 1)
						+ " والا مال ختم ہو گیا ہے۔");
			} else {
//				JOptionPane.showMessageDialog(null, "اب آئٹم نمبر " + itemId
//						+ " کے " + type + " کا مال ختم ہو گیا ہے۔");
				ShowDialog.msg(cfp.getMediumPlainFont(), "اب آئٹم نمبر " + itemId
						+ " کے " + type + " کا مال ختم ہو گیا ہے۔");
			}
		}

		try {
			String query = new String();
			PreparedStatement pst = null;
			switch (typeCheck) {
			case "/Gattu":
				query = "Update NonSizedItemsRates set GattuStock='"
						+ wholePresentStock + "." + subPresentStock
						+ "' where EID='" + itemId + "'";
				break;
			case "/Box":
				if (thisItem.getRelationBox().equals("/Bundle"))
					query = "Update NonSizedItemsRates set BundleStock='"
							+ wholePresentStock + "." + subPresentStock
							+ "' where EID='" + itemId + "'";
				else
					query = "Update NonSizedItemsRates set BoxStock='"
							+ wholePresentStock + "." + subPresentStock
							+ "' where EID='" + itemId + "'";
				break;
			case "/Bundle":
				query = "Update NonSizedItemsRates set BundleStock='"
						+ wholePresentStock + "." + subPresentStock
						+ "' where EID='" + itemId + "'";
				break;
			case "/Carton":
				query = "Update CartonItemsRates set StockSize" + (sizeId + 1)
						+ "='" + wholePresentStock + "." + subPresentStock
						+ "' where EID='" + itemId + "'";
				break;
			case "/Dozen":
				if (thisItem.getPriceDozen().getSizeRelation(sizeId)
						.equals("/Carton"))
					query = "Update CartonItemsRates set StockSize"
							+ (sizeId + 1) + "='" + wholePresentStock + "."
							+ subPresentStock + "' where EID='" + itemId + "'";
				else
					query = "Update DozenItemsRates set StockSize"
							+ (sizeId + 1) + "='" + wholePresentStock + "."
							+ subPresentStock + "' where EID='" + itemId + "'";
				break;
			default:
				if (isSized)
					query = "Update " + table_value + " set StockSize"
							+ (sizeId + 1) + "='" + wholePresentStock + "."
							+ subPresentStock + "' where EID='" + itemId + "'";
				else
					query = "Update NonSizedItemsRates set " + table_value
							+ "='" + wholePresentStock + "." + subPresentStock
							+ "' where EID='" + itemId + "'";
			}

			pst = connection.prepareStatement(query);
			pst.execute();

			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*------------------------Refresh Sales Point--------------------*/

	public void refreshSalesPoint() {
		panelSales = new JPanel();
		scrollPane_2.setColumnHeaderView(panelSales);
		panelSales.setLayout(new MigLayout());
		Item[] items = getAllItems();
		int sAloneItemsCount = 0;
		for(int i = 0; i < items.length; i++) {
			if(items[i].getSuperItem() == 0) {
				sAloneItemsCount++;
			}
		}
		Item[] sAloneItems = new Item[sAloneItemsCount];
		Item[] depItems = new Item[items.length - sAloneItemsCount];
		int sAloneItemsCounter = 0;
		int depItemsCounter = 0;
		for(int i = 0; i < items.length; i++) {
			if(items[i].getSuperItem() == 0) {
				//System.out.println("Items["+i+"] = " + items[i].getName());
				sAloneItems[sAloneItemsCounter] = items[i];
				sAloneItemsCounter++;
			} else {
				depItems[depItemsCounter] = items[i];
				depItemsCounter++;
			}
		}
		
		SuperItem[] superItems = getAllSuperItems(connection);
		int btnLength = superItems.length + sAloneItemsCount;
		JButton[] itemsButton = new JButton[btnLength];
		//System.out.println("Stand Alone Items Length: " + sAloneItemsCount);
		//System.out.println("Super Items Length: " + superItems.length);
		//System.out.println("Items Length: " + items.length);
		//System.out.println("Button Length: " + btnLength);
		
		int wrapper = 0;
		for (int k = 0; k < btnLength; k++) {
			if(k < superItems.length) {
				itemsButton[k] = new JButton(superItems[k].getName());
				itemsButton[k].setToolTipText(superItems[k].getId() + "");
				int tempIndex = k;
				itemsButton[k].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						subSaleDialog = new SubSaleDialog(BillingSystem.this, frame, superItems[tempIndex].getId(),
								superItems[tempIndex].getName(), depItems);
					}
				});
			} else {
				//System.out.println("Super Items Length: " + superItems.length);
				//System.out.println("Index: " + k);
				//System.out.println("Stand Alone Item["+k+"] = " + sAloneItems[k - superItems.length].getName());
				itemsButton[k] = new JButton(sAloneItems[k - superItems.length].getName());
				itemsButton[k].setToolTipText(sAloneItems[k -superItems.length].getId() + "");
				int tempIndex = k;
				itemsButton[k].addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent arg0) {
						// TODO Auto-generated method stub
						saleItem(sAloneItems[tempIndex -superItems.length].getId(), sAloneItems[tempIndex - superItems.length].getName());
					}
				});
	
			}
//			itemsButton[k].setFont(new Font("Times New Roman", Font.BOLD, 15));
			itemsButton[k].setFont(cfp.getSmallBoldFont());
			if (wrapper < 4) {
				panelSales.add(itemsButton[k],
						"width 135:135:135, height 50:50:50");
				wrapper++;
			} else {
				panelSales.add(itemsButton[k],
						"width 135:135:135, height 50:50:50, wrap");
				wrapper = 0;
			}

		}

	}

	/*-----------------------------Sale Item and Add to Bill-------------------------*/

	public void saleItem(int itemId, String itemName) {
		Item item = new Item();
		item.setId(itemId);
		item.setName(itemName);
		item.getItemFromDb(connection);
		SuperItem superItem = new SuperItem();
		superItem.setId(item.getSuperItem());
		superItem.getSuperItemFromDb(connection);
		item.setName(item.getName() + " " + superItem.getName());
		String type = item.getType();
		int intType = Integer.parseInt(type, 2);
		item.setIntType(intType);

		AbstractDocument doc = null;
		JTextField kgCount = new JTextField(10);
		doc = (AbstractDocument) kgCount.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		kgCount.setText("0");
		kgCount.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				kgCount.selectAll();
			}
		});
		JTextField gattuCount = new JTextField(10);
		doc = (AbstractDocument) gattuCount.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		gattuCount.setText("0");
		gattuCount.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				gattuCount.selectAll();
			}
		});
		JTextField boxCount = new JTextField(10);
		doc = (AbstractDocument) boxCount.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		boxCount.setText("0");
		boxCount.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				boxCount.selectAll();
			}
		});
		JTextField bundleCount = new JTextField(10);
		doc = (AbstractDocument) bundleCount.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		bundleCount.setText("0");
		bundleCount.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				bundleCount.selectAll();
			}
		});

		JLabel[] packetName = new JLabel[5];
		JLabel[] cartonName = new JLabel[5];
		JLabel[] bucketName = new JLabel[5];
		JLabel[] dozenName = new JLabel[5];
		JLabel[] bottleName = new JLabel[5];
		JLabel[] tinName = new JLabel[5];
		JLabel[] gallonName = new JLabel[5];

		JTextField[] packetCount = new JTextField[5];
		JTextField[] cartonCount = new JTextField[5];
		JTextField[] bucketCount = new JTextField[5];
		JTextField[] dozenCount = new JTextField[5];
		JTextField[] bottleCount = new JTextField[5];
		JTextField[] tinCount = new JTextField[5];
		JTextField[] gallonCount = new JTextField[5];

		for (int i = 0; i < 5; i++) {

			int j = i;

			packetName[i] = new JLabel(item.getPricePacket().getSizeName(i));

			packetCount[i] = new JTextField(10);
			doc = (AbstractDocument) packetCount[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			packetCount[i].setText("0");
			packetCount[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					packetCount[j].selectAll();
				}
			});

			cartonName[i] = new JLabel(item.getPriceCarton().getSizeName(i));
			cartonCount[i] = new JTextField(10);
			doc = (AbstractDocument) cartonCount[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			cartonCount[i].setText("0");
			cartonCount[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					cartonCount[j].selectAll();
				}
			});

			bucketName[i] = new JLabel(item.getPriceBucket().getSizeName(i));
			bucketCount[i] = new JTextField(10);
			doc = (AbstractDocument) bucketCount[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			bucketCount[i].setText("0");
			bucketCount[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					bucketCount[j].selectAll();
				}
			});

			dozenName[i] = new JLabel(item.getPriceDozen().getSizeName(i));
			dozenCount[i] = new JTextField(10);
			doc = (AbstractDocument) dozenCount[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			dozenCount[i].setText("0");
			dozenCount[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					dozenCount[j].selectAll();
				}
			});

			bottleName[i] = new JLabel(item.getPriceBottle().getSizeName(i));
			bottleCount[i] = new JTextField(10);
			doc = (AbstractDocument) bottleCount[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			bottleCount[i].setText("0");
			bottleCount[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					bottleCount[j].selectAll();
				}
			});

			tinName[i] = new JLabel(item.getPriceTin().getSizeName(i));
			tinCount[i] = new JTextField(10);
			doc = (AbstractDocument) tinCount[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			tinCount[i].setText("0");
			tinCount[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					tinCount[j].selectAll();
				}
			});

			gallonName[i] = new JLabel(item.getPriceGallon().getSizeName(i));
			gallonCount[i] = new JTextField(10);
			doc = (AbstractDocument) gallonCount[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			gallonCount[i].setText("0");
			gallonCount[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					gallonCount[j].selectAll();
				}
			});

		}

		
		JButton[] btnWeight = new JButton[5];
		JButton btnGattu = new JButton("+");
		JButton btnBox = new JButton("+");
		JButton btnBundle = new JButton("+");
		JButton[] btnPacket = new JButton[5];
		JButton[] btnCarton = new JButton[5];
		JButton[] btnBucket = new JButton[5];
		JButton[] btnDozen = new JButton[5];
		JButton[] btnBottle = new JButton[5];
		JButton[] btnTin = new JButton[5];
		JButton[] btnGallon = new JButton[5];
		
		
		btnWeight[0] = new JButton("کلو");
		btnWeight[1] = new JButton("آدھا");
		btnWeight[2] = new JButton("پا‏ؤ");
		btnWeight[3] = new JButton("پانچ");
		btnWeight[4] = new JButton("اڑھائی");
		
		Font smallBoldFont = cfp.getSmallBoldFont();
		
		for(int i = 0; i < 5; i++) {
//			btnWeight[i].setFont(new Font("Times New Roman", Font.BOLD, 16));
			btnWeight[i].setFont(smallBoldFont);
		}
//		btnGattu.setFont(new Font("Times New Roman", Font.BOLD, 16));
		btnGattu.setFont(smallBoldFont);
//		btnBox.setFont(new Font("Times New Roman", Font.BOLD, 16));
		btnBox.setFont(smallBoldFont);
//		btnBundle.setFont(new Font("Times New Roman", Font.BOLD, 16));
		btnBundle.setFont(smallBoldFont);
		
		btnWeight[0].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(kgCount.getText().toString().matches("")) {
					kgCount.setText("0");
				}
				double newValue = Double.valueOf(kgCount.getText().toString());
				newValue = newValue + 1d;
				kgCount.setText("");
				kgCount.setText(newValue+"");
			}
		});
		btnWeight[1].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(kgCount.getText().toString().matches("")) {
					kgCount.setText("0");
				}
				double newValue = Double.valueOf(kgCount.getText().toString());
				newValue = newValue + 0.5d;
				kgCount.setText("");
				kgCount.setText(newValue+"");
			}
		});
		btnWeight[2].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(kgCount.getText().toString().matches("")) {
					kgCount.setText("0");
				}
				double newValue = Double.valueOf(kgCount.getText().toString());
				newValue = newValue + 0.25d;
				kgCount.setText("");
				kgCount.setText(newValue+"");
			}
		});
		btnWeight[3].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(kgCount.getText().toString().matches("")) {
					kgCount.setText("0");
				}
				double newValue = Double.valueOf(kgCount.getText().toString());
				newValue = newValue + 5d;
				kgCount.setText("");
				kgCount.setText(newValue+"");
			}
		});
		btnWeight[4].addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(kgCount.getText().toString().matches("")) {
					kgCount.setText("0");
				}
				double newValue = Double.valueOf(kgCount.getText().toString());
				newValue = newValue + 2.5d;
				kgCount.setText("");
				kgCount.setText(newValue+"");
			}
		});
		btnGattu.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(gattuCount.getText().toString().matches("")) {
					gattuCount.setText("0");
				}
				double newValue = Double.valueOf(gattuCount.getText().toString());
				newValue = newValue + 1d;
				gattuCount.setText("");
				gattuCount.setText(newValue+"");
			}
		});
		btnBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(boxCount.getText().toString().matches("")) {
					boxCount.setText("0");
				}
				double newValue = Double.valueOf(boxCount.getText().toString());
				newValue = newValue + 1d;
				boxCount.setText("");
				boxCount.setText(newValue+"");
			}
		});
		btnBundle.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
				if(bundleCount.getText().toString().matches("")) {
					bundleCount.setText("0");
				}
				double newValue = Double.valueOf(bundleCount.getText().toString());
				newValue = newValue + 1d;
				bundleCount.setText("");
				bundleCount.setText(newValue+"");
			}
		});
		
		for(int i = 0; i < 5; i++) {
			btnPacket[i] = new JButton("+");
			btnCarton[i] = new JButton("+");
			btnBucket[i] = new JButton("+");
			btnDozen[i] = new JButton("+");
			btnBottle[i] = new JButton("+");
			btnTin[i] = new JButton("+");
			btnGallon[i] = new JButton("+");
			
//			btnPacket[i].setFont(new Font("Times New Roman", Font.BOLD, 16));
			btnPacket[i].setFont(smallBoldFont);
//			btnCarton[i].setFont(new Font("Times New Roman", Font.BOLD, 16));
			btnCarton[i].setFont(smallBoldFont);
//			btnBucket[i].setFont(new Font("Times New Roman", Font.BOLD, 16));
			btnBucket[i].setFont(smallBoldFont);
//			btnDozen[i].setFont(new Font("Times New Roman", Font.BOLD, 16));
			btnDozen[i].setFont(smallBoldFont);
//			btnBottle[i].setFont(new Font("Times New Roman", Font.BOLD, 16));
			btnBottle[i].setFont(smallBoldFont);
//			btnTin[i].setFont(new Font("Times New Roman", Font.BOLD, 16));
			btnTin[i].setFont(smallBoldFont);
//			btnGallon[i].setFont(new Font("Times New Roman", Font.BOLD, 16));
			btnGallon[i].setFont(smallBoldFont);
			
			int j = i;
			
			btnPacket[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					if(packetCount[j].getText().toString().matches("")) {
						packetCount[j].setText("0");
					}
					double newValue = Double.valueOf(packetCount[j].getText().toString());
					newValue = newValue + 1d;
					packetCount[j].setText("");
					packetCount[j].setText(newValue+"");
				}
			});
			btnCarton[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					if(cartonCount[j].getText().toString().matches("")) {
						cartonCount[j].setText("0");
					}
					double newValue = Double.valueOf(cartonCount[j].getText().toString());
					newValue = newValue + 1d;
					cartonCount[j].setText("");
					cartonCount[j].setText(newValue+"");
				}
			});
			btnBucket[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					if(bucketCount[j].getText().toString().matches("")) {
						bucketCount[j].setText("0");
					}
					double newValue = Double.valueOf(bucketCount[j].getText().toString());
					newValue = newValue + 1d;
					bucketCount[j].setText("");
					bucketCount[j].setText(newValue+"");
				}
			});
			btnDozen[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					if(dozenCount[j].getText().toString().matches("")) {
						dozenCount[j].setText("0");
					}
					double newValue = Double.valueOf(dozenCount[j].getText().toString());
					newValue = newValue + 1d;
					dozenCount[j].setText("");
					dozenCount[j].setText(newValue+"");
				}
			});
			btnBottle[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					if(bottleCount[j].getText().toString().matches("")) {
						bottleCount[j].setText("0");
					}
					double newValue = Double.valueOf(bottleCount[j].getText().toString());
					newValue = newValue + 1d;
					bottleCount[j].setText("");
					bottleCount[j].setText(newValue+"");
				}
			});
			btnTin[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					if(tinCount[j].getText().toString().matches("")) {
						tinCount[j].setText("0");
					}
					double newValue = Double.valueOf(tinCount[j].getText().toString());
					newValue = newValue + 1d;
					tinCount[j].setText("");
					tinCount[j].setText(newValue+"");
				}
			});
			btnGallon[i].addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent arg0) {
					// TODO Auto-generated method stub
					if(gallonCount[j].getText().toString().matches("")) {
						gallonCount[j].setText("0");
					}
					double newValue = Double.valueOf(gallonCount[j].getText().toString());
					newValue = newValue + 1d;
					gallonCount[j].setText("");
					gallonCount[j].setText(newValue+"");
				}
			});
			
		}
		
		JPanel myPanel = new JPanel(new GridBagLayout());
		int gridY = 0;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		if ((type.charAt(10) == '1') && (item.getPriceKg() > 0)) {
			setGBagConst(0, gridY, 1, 1);
			myPanel.add(new JLabel("کلو"), gridBagConstraints);
			setGBagConst(1, gridY, 1, 1);
			myPanel.add(kgCount, gridBagConstraints);
			setGBagConst(2, gridY, 1, 1);
			myPanel.add(btnWeight[0], gridBagConstraints);
			setGBagConst(3, gridY, 1, 1);
			myPanel.add(btnWeight[1], gridBagConstraints);
			setGBagConst(4, gridY, 1, 1);
			myPanel.add(btnWeight[2], gridBagConstraints);
			setGBagConst(5, gridY, 1, 1);
			myPanel.add(btnWeight[3], gridBagConstraints);
			setGBagConst(6, gridY, 1, 1);
			myPanel.add(btnWeight[4], gridBagConstraints);
			gridY++;
		}

		if ((type.charAt(9) == '1') && (item.getPriceGattu() > 0)) {
			setGBagConst(0, gridY, 1, 1);
			myPanel.add(new JLabel("گٹو"), gridBagConstraints);
			setGBagConst(1, gridY, 1, 1);
			myPanel.add(gattuCount, gridBagConstraints);
			setGBagConst(2, gridY, 1, 1);
			myPanel.add(btnGattu, gridBagConstraints);
			gridY++;
		}

		if ((type.charAt(8) == '1') && (item.getPriceBox() > 0)) {
			setGBagConst(0, gridY, 1, 1);
			myPanel.add(new JLabel("ڈبہ"), gridBagConstraints);
			setGBagConst(1, gridY, 1, 1);
			myPanel.add(boxCount, gridBagConstraints);
			setGBagConst(2, gridY, 1, 1);
			myPanel.add(btnBox, gridBagConstraints);
			gridY++;
		}

		if ((type.charAt(7) == '1') && (item.getPriceBundle() > 0)) {
			setGBagConst(0, gridY, 1, 1);
			myPanel.add(new JLabel("بنڈل"), gridBagConstraints);
			setGBagConst(1, gridY, 1, 1);
			myPanel.add(bundleCount, gridBagConstraints);
			setGBagConst(2, gridY, 1, 1);
			myPanel.add(btnBundle, gridBagConstraints);
			gridY++;
		}

		if (intType > Integer.parseInt("00000001111", 2)) {

			gridBagConstraints.insets = new Insets(30, 30, 4, 4);

			gridY++;

			double[] priceSize1Items = new double[7];
			priceSize1Items[0] = item.getPriceGallon().getSizePrice(0);
			priceSize1Items[1] = item.getPriceTin().getSizePrice(0);
			priceSize1Items[2] = item.getPriceBottle().getSizePrice(0);
			priceSize1Items[3] = item.getPriceDozen().getSizePrice(0);
			priceSize1Items[4] = item.getPriceBucket().getSizePrice(0);
			priceSize1Items[5] = item.getPriceCarton().getSizePrice(0);
			priceSize1Items[6] = item.getPricePacket().getSizePrice(0);

			if ((type.charAt(6) == '1') && (priceSize1Items[6] > 0)) {
				gridBagConstraints.insets = new Insets(30, 4, 4, 4);
				setGBagConst(0, gridY, 1, 1);
				myPanel.add(new JLabel("پیکٹ"), gridBagConstraints);
				gridY++;
				gridBagConstraints.insets = new Insets(4, 4, 4, 4);
				for (int s = 0; s < 5; s++) {
					if (item.getPricePacket().getSizePrice(s) > 0) {
						setGBagConst(0, gridY, 1, 1);
						myPanel.add(new JLabel(item.getPricePacket()
								.getSizeName(s)), gridBagConstraints);
						setGBagConst(1, gridY, 1, 1);
						myPanel.add(packetCount[s], gridBagConstraints);
						setGBagConst(2, gridY, 1, 1);
						myPanel.add(btnPacket[s], gridBagConstraints);
					}
					gridY++;
				}
			}

			if ((type.charAt(5) == '1') && (priceSize1Items[5] > 0)) {
				int gridX = 0;
				boolean isEven = ((type.charAt(6) == '1') && (priceSize1Items[6] > 0)) ? true
						: false;
				if (isEven) {
					gridY -= 6;
					gridX = 4;
					gridBagConstraints.insets = new Insets(30, 30, 4, 4);
				} else
					gridBagConstraints.insets = new Insets(30, 4, 4, 4);
				setGBagConst(gridX, gridY, 1, 1);
				myPanel.add(new JLabel("کارٹن"), gridBagConstraints);
				gridY++;
				gridBagConstraints.insets = new Insets(4, 4, 4, 4);
				for (int s = 0; s < 5; s++) {
					if (item.getPriceCarton().getSizePrice(s) > 0) {
						if (isEven) {
							gridBagConstraints.insets = new Insets(4, 30, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(cartonName[s], gridBagConstraints);
						if (isEven) {
							gridBagConstraints.insets = new Insets(4, 4, 4, 4);
						}
						gridX++;
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(cartonCount[s], gridBagConstraints);
						gridX++;
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(btnCarton[s], gridBagConstraints);

						if (isEven)
							gridX = 4;
						else
							gridX = 0;

					}
					gridY++;
				}
			}

			if ((type.charAt(4) == '1') && (priceSize1Items[4] > 0)) {
				int gridX = 0;
				int dispId = 0;
				for (int i = 6; i >= 5; i--) {
					if ((type.charAt(i) == '1') && (priceSize1Items[i] > 0))
						dispId++;
				}
				if (dispId > 0) {
					gridX = dispId * 4;
					gridY -= 6;
					gridBagConstraints.insets = new Insets(30, 30, 4, 4);
				} else
					gridBagConstraints.insets = new Insets(30, 4, 4, 4);
				setGBagConst(gridX, gridY, 1, 1);
				myPanel.add(new JLabel("بالٹی"), gridBagConstraints);
				gridY++;
				gridBagConstraints.insets = new Insets(4, 4, 4, 4);

				for (int s = 0; s < 5; s++) {
					if (item.getPriceBucket().getSizePrice(s) > 0) {
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 30, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(bucketName[s], gridBagConstraints);
						gridX++;
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 4, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(bucketCount[s], gridBagConstraints);
						gridX++;
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(btnBucket[s], gridBagConstraints);
						
						if (dispId == 1)
							gridX = 4;
						else if (dispId == 2)
							gridX = 8;
						else
							gridX = 0;
					}
					gridY++;
				}
			}

			if ((type.charAt(3) == '1') && (priceSize1Items[3] > 0)) {
				int gridX = 0;
				int dispId = 0;
				for (int i = 6; i >= 4; i--) {
					if ((type.charAt(i) == '1') && (priceSize1Items[i] > 0))
						dispId++;
				}
				if (dispId > 0) {
					gridY -= 6;
					gridX = dispId * 4;
					gridBagConstraints.insets = new Insets(30, 30, 4, 4);
				} else
					gridBagConstraints.insets = new Insets(30, 4, 4, 4);
				setGBagConst(gridX, gridY, 1, 1);
				myPanel.add(new JLabel("درجن"), gridBagConstraints);
				gridY++;
				gridBagConstraints.insets = new Insets(4, 4, 4, 4);

				for (int s = 0; s < 5; s++) {
					if (item.getPriceDozen().getSizePrice(s) > 0) {
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 30, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(dozenName[s], gridBagConstraints);
						gridX++;
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 4, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(dozenCount[s], gridBagConstraints);
						gridX++;
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(btnDozen[s], gridBagConstraints);
						
						gridX = dispId * 4;
					}
					gridY++;
				}
			}

			if ((type.charAt(2) == '1') && (priceSize1Items[2] > 0)) {
				int gridX = 0;
				int dispId = 0;
				for (int i = 6; i >= 3; i--) {
					if ((type.charAt(i) == '1') && (priceSize1Items[i] > 0))
						dispId++;
				}
				if (dispId > 0) {
					if (dispId != 4)
						gridY -= 6;
					if (dispId > 3) {
						dispId -= 4;
					}
					gridX = dispId * 4;
					gridBagConstraints.insets = new Insets(30, 30, 4, 4);
				} else
					gridBagConstraints.insets = new Insets(30, 4, 4, 4);
				setGBagConst(gridX, gridY, 1, 1);
				myPanel.add(new JLabel("بوتل"), gridBagConstraints);
				gridY++;
				gridBagConstraints.insets = new Insets(4, 4, 4, 4);

				for (int s = 0; s < 5; s++) {
					if (item.getPriceBottle().getSizePrice(s) > 0) {
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 30, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(bottleName[s], gridBagConstraints);
						gridX++;
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 4, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(bottleCount[s], gridBagConstraints);
						gridX++;
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(btnBottle[s], gridBagConstraints);
						
						gridX = dispId * 4;
					}
					gridY++;
				}
			}

			if ((type.charAt(1) == '1') && (priceSize1Items[1] > 0)) {
				int gridX = 0;
				int dispId = 0;
				for (int i = 6; i >= 2; i--) {
					if ((type.charAt(i) == '1') && (priceSize1Items[i] > 0))
						dispId++;
				}
				if (dispId > 0) {
					if (dispId > 0) {
						if (dispId != 4)
							gridY -= 6;
						if (dispId > 3) {
							dispId -= 4;
						}
						gridX = dispId * 4;
					}
					gridBagConstraints.insets = new Insets(30, 30, 4, 4);
				} else
					gridBagConstraints.insets = new Insets(30, 4, 4, 4);
				setGBagConst(gridX, gridY, 1, 1);
				myPanel.add(new JLabel("ٹین"), gridBagConstraints);
				gridY++;
				gridBagConstraints.insets = new Insets(4, 4, 4, 4);

				for (int s = 0; s < 5; s++) {
					if (item.getPriceTin().getSizePrice(s) > 0) {
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 30, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(tinName[s], gridBagConstraints);
						gridX++;
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 4, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(tinCount[s], gridBagConstraints);
						gridX++;
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(btnTin[s], gridBagConstraints);
						
						gridX = dispId * 4;
					}
					gridY++;
				}
			}

			if ((type.charAt(0) == '1') && (priceSize1Items[0] > 0)) {
				int gridX = 0;
				int dispId = 0;
				for (int i = 6; i >= 1; i--) {
					if ((type.charAt(i) == '1') && (priceSize1Items[i] > 0))
						dispId++;
				}
				if (dispId > 0) {
					if (dispId > 0) {
						if (dispId != 4)
							gridY -= 6;
						if (dispId > 3) {
							dispId -= 4;
						}
						gridX = dispId * 4;
					}
					gridBagConstraints.insets = new Insets(30, 30, 4, 4);
				} else
					gridBagConstraints.insets = new Insets(30, 4, 4, 4);
				setGBagConst(gridX, gridY, 1, 1);
				myPanel.add(new JLabel("گیلن"), gridBagConstraints);
				gridY++;
				gridBagConstraints.insets = new Insets(4, 4, 4, 4);

				for (int s = 0; s < 5; s++) {
					if (item.getPriceGallon().getSizePrice(s) > 0) {
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 30, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(gallonName[s], gridBagConstraints);
						gridX++;
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 4, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(gallonCount[s], gridBagConstraints);
						gridX++;
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(btnGallon[s], gridBagConstraints);
						
						gridX = dispId * 4;
					}
					gridY++;
				}
			}

		}

		int result_1 = JOptionPane.CANCEL_OPTION;
//		javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
		if (intType > 0) {
//			result_1 = JOptionPane.showConfirmDialog(null, myPanel,
//					item.getName(), JOptionPane.OK_CANCEL_OPTION);
			result_1 = ShowDialog.panelCofirm(cfp.getSmallPlainFont(),
					myPanel,
					item.getName(),
					JOptionPane.OK_CANCEL_OPTION);
		} else {
//			JOptionPane.showMessageDialog(null, ": کچھ بھی نہیں چنا گیا");
			ShowDialog.msg(cfp.getMediumPlainFont(), ": کچھ بھی نہیں چنا گیا");
		}

		if (result_1 == JOptionPane.OK_OPTION) {

			double doubleKgCount = 0;
			double doubleGattuCount = 0;
			double doubleBoxCount = 0;
			double doubleBundleCount = 0;
			double[] doublePacketCount = new double[5];
			double[] doubleCartonCount = new double[5];
			double[] doubleBucketCount = new double[5];
			double[] doubleDozenCount = new double[5];
			double[] doubleBottleCount = new double[5];
			double[] doubleTinCount = new double[5];
			double[] doubleGallonCount = new double[5];
			try {
				doubleKgCount = Double.valueOf(kgCount.getText().toString());
				doubleGattuCount = Double.valueOf(gattuCount.getText()
						.toString());
				doubleBoxCount = Double.valueOf(boxCount.getText().toString());
				doubleBundleCount = Double.valueOf(bundleCount.getText()
						.toString());

				for (int n = 0; n < 5; n++) {
					doublePacketCount[n] = Double.valueOf(packetCount[n]
							.getText().toString());
					doubleCartonCount[n] = Double.valueOf(cartonCount[n]
							.getText().toString());
					doubleBucketCount[n] = Double.valueOf(bucketCount[n]
							.getText().toString());
					doubleDozenCount[n] = Double.valueOf(dozenCount[n]
							.getText().toString());
					doubleBottleCount[n] = Double.valueOf(bottleCount[n]
							.getText().toString());
					doubleTinCount[n] = Double.valueOf(tinCount[n].getText()
							.toString());
					doubleGallonCount[n] = Double.valueOf(gallonCount[n]
							.getText().toString());
				}
			} catch (Exception e) {
				e.printStackTrace();
//				javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
				JOptionPane.showMessageDialog(null,
						"خالی جگہ نہیں چھوڑی جا سکتی۔");
			}

			Object[] billRow = new Object[5];
			int billRowCount = tblBill.getRowCount();

			boolean isNormal = tglBtnNormal.isSelected();

			if (doubleKgCount > 0) {
				billRow[0] = item.getName();
				billRow[1] = doubleKgCount;
				billRow[2] = "کلو";
				billRow[3] = "";
				if ((!isNormal) && (item.getSpriceKg() > 0))
					billRow[4] = doubleKgCount * item.getSpriceKg();
				else
					billRow[4] = doubleKgCount * item.getPriceKg();
				billModel.addRow(billRow);
				double totalAmout = Double.valueOf(lblTotalAmount.getText())
						+ ((double) billRow[4]);
				lblTotalAmount.setText(totalAmout + "");
				billRowId[billRowCount] = item.getId();
				billRowType[billRowCount] = "00000000001";
				billRowCount++;
			}

			if (doubleGattuCount > 0) {
				billRow[0] = item.getName();
				billRow[1] = doubleGattuCount;
				billRow[2] = "گٹو";
				billRow[3] = "";
				if ((!isNormal) && (item.getSpriceGattu() > 0))
					billRow[4] = doubleGattuCount * item.getSpriceGattu();
				else
					billRow[4] = doubleGattuCount * item.getPriceGattu();
				billModel.addRow(billRow);
				double totalAmout = Double.valueOf(lblTotalAmount.getText())
						+ ((double) billRow[4]);
				lblTotalAmount.setText(totalAmout + "");
				billRowId[billRowCount] = item.getId();
				billRowType[billRowCount] = "00000000010";
				billRowCount++;
			}

			if (doubleBoxCount > 0) {
				billRow[0] = item.getName();
				billRow[1] = doubleBoxCount;
				billRow[2] = "ڈبہ";
				billRow[3] = "";
				if ((!isNormal) && (item.getSpriceBox() > 0))
					billRow[4] = doubleBoxCount * item.getSpriceBox();
				else
					billRow[4] = doubleBoxCount * item.getPriceBox();
				billModel.addRow(billRow);
				double totalAmout = Double.valueOf(lblTotalAmount.getText())
						+ ((double) billRow[4]);
				lblTotalAmount.setText(totalAmout + "");
				billRowId[billRowCount] = item.getId();
				billRowType[billRowCount] = "00000000100";
				billRowCount++;
			}

			if (doubleBundleCount > 0) {
				billRow[0] = item.getName();
				billRow[1] = doubleBundleCount;
				billRow[2] = "بنڈل";
				billRow[3] = "";
				if ((!isNormal) && (item.getSpriceBundle() > 0))
					billRow[4] = doubleBundleCount * item.getSpriceBundle();
				else
					billRow[4] = doubleBundleCount * item.getPriceBundle();
				billModel.addRow(billRow);
				double totalAmout = Double.valueOf(lblTotalAmount.getText())
						+ ((double) billRow[4]);
				lblTotalAmount.setText(totalAmout + "");
				billRowId[billRowCount] = item.getId();
				billRowType[billRowCount] = "00000001000";
				billRowCount++;
			}

			for (int y = 0; y < 5; y++) {
				if (doublePacketCount[y] > 0) {
					billRow[0] = item.getName();
					billRow[1] = doublePacketCount[y];
					billRow[2] = "پیکٹ";
					billRow[3] = item.getPricePacket().getSizeName(y)
							.toString();
					if ((!isNormal)
							&& (item.getPricePacket().getSizeSPrice(y) > 0))
						billRow[4] = doublePacketCount[y]
								* item.getPricePacket().getSizeSPrice(y);
					else
						billRow[4] = doublePacketCount[y]
								* item.getPricePacket().getSizePrice(y);
					billModel.addRow(billRow);
					double totalAmout = Double
							.valueOf(lblTotalAmount.getText())
							+ ((double) billRow[4]);
					lblTotalAmount.setText(totalAmout + "");
					billRowId[billRowCount] = item.getId();
					billRowType[billRowCount] = "00000010000";
					char[] ch = { '0', '0', '0', '0', '0' };
					ch[y] = '1';
					billRowSize[billRowCount] = String.valueOf(ch);
					billRowCount++;
				}
				if (doubleCartonCount[y] > 0) {
					billRow[0] = item.getName();
					billRow[1] = doubleCartonCount[y];
					billRow[2] = "کارٹن";
					billRow[3] = item.getPriceCarton().getSizeName(y)
							.toString();
					if ((!isNormal)
							&& (item.getPriceCarton().getSizeSPrice(y) > 0))
						billRow[4] = doubleCartonCount[y]
								* item.getPriceCarton().getSizeSPrice(y);
					else
						billRow[4] = doubleCartonCount[y]
								* item.getPriceCarton().getSizePrice(y);
					billModel.addRow(billRow);
					double totalAmout = Double
							.valueOf(lblTotalAmount.getText())
							+ ((double) billRow[4]);
					lblTotalAmount.setText(totalAmout + "");
					billRowId[billRowCount] = item.getId();
					billRowType[billRowCount] = "00000100000";
					char[] ch = { '0', '0', '0', '0', '0' };
					ch[y] = '1';
					billRowSize[billRowCount] = String.valueOf(ch);
					billRowCount++;
				}
				if (doubleBucketCount[y] > 0) {
					billRow[0] = item.getName();
					billRow[1] = doubleBucketCount[y];
					billRow[2] = "بالٹی";
					billRow[3] = item.getPriceBucket().getSizeName(y)
							.toString();
					if ((!isNormal)
							&& (item.getPriceBucket().getSizeSPrice(y) > 0))
						billRow[4] = doubleBucketCount[y]
								* item.getPriceBucket().getSizeSPrice(y);
					else
						billRow[4] = doubleBucketCount[y]
								* item.getPriceBucket().getSizePrice(y);
					billModel.addRow(billRow);
					double totalAmout = Double
							.valueOf(lblTotalAmount.getText())
							+ ((double) billRow[4]);
					lblTotalAmount.setText(totalAmout + "");
					billRowId[billRowCount] = item.getId();
					billRowType[billRowCount] = "00001000000";
					char[] ch = { '0', '0', '0', '0', '0' };
					ch[y] = '1';
					billRowSize[billRowCount] = String.valueOf(ch);
					billRowCount++;
				}
				if (doubleDozenCount[y] > 0) {
					billRow[0] = item.getName();
					billRow[1] = doubleDozenCount[y];
					billRow[2] = "درجن";
					billRow[3] = item.getPriceDozen().getSizeName(y).toString();
					if ((!isNormal)
							&& (item.getPriceDozen().getSizeSPrice(y) > 0))
						billRow[4] = doubleDozenCount[y]
								* item.getPriceDozen().getSizeSPrice(y);
					else
						billRow[4] = doubleDozenCount[y]
								* item.getPriceDozen().getSizePrice(y);
					billModel.addRow(billRow);
					double totalAmout = Double
							.valueOf(lblTotalAmount.getText())
							+ ((double) billRow[4]);
					lblTotalAmount.setText(totalAmout + "");
					billRowId[billRowCount] = item.getId();
					billRowType[billRowCount] = "00010000000";
					char[] ch = { '0', '0', '0', '0', '0' };
					ch[y] = '1';
					billRowSize[billRowCount] = String.valueOf(ch);
					billRowCount++;
				}
				if (doubleBottleCount[y] > 0) {
					billRow[0] = item.getName();
					billRow[1] = doubleBottleCount[y];
					billRow[2] = "بوتل";
					billRow[3] = item.getPriceBottle().getSizeName(y)
							.toString();
					if ((!isNormal)
							&& (item.getPriceBottle().getSizeSPrice(y) > 0))
						billRow[4] = doubleBottleCount[y]
								* item.getPriceBottle().getSizeSPrice(y);
					else
						billRow[4] = doubleBottleCount[y]
								* item.getPriceBottle().getSizePrice(y);
					billModel.addRow(billRow);
					double totalAmout = Double
							.valueOf(lblTotalAmount.getText())
							+ ((double) billRow[4]);
					lblTotalAmount.setText(totalAmout + "");
					billRowId[billRowCount] = item.getId();
					billRowType[billRowCount] = "00100000000";
					char[] ch = { '0', '0', '0', '0', '0' };
					ch[y] = '1';
					billRowSize[billRowCount] = String.valueOf(ch);
					billRowCount++;
				}
				if (doubleTinCount[y] > 0) {
					billRow[0] = item.getName();
					billRow[1] = doubleTinCount[y];
					billRow[2] = "ٹین";
					billRow[3] = item.getPriceTin().getSizeName(y).toString();
					if ((!isNormal)
							&& (item.getPriceTin().getSizeSPrice(y) > 0))
						billRow[4] = doubleTinCount[y]
								* item.getPriceTin().getSizeSPrice(y);
					else
						billRow[4] = doubleTinCount[y]
								* item.getPriceTin().getSizePrice(y);
					billModel.addRow(billRow);
					double totalAmout = Double
							.valueOf(lblTotalAmount.getText())
							+ ((double) billRow[4]);
					lblTotalAmount.setText(totalAmout + "");
					billRowId[billRowCount] = item.getId();
					billRowType[billRowCount] = "01000000000";
					char[] ch = { '0', '0', '0', '0', '0' };
					ch[y] = '1';
					billRowSize[billRowCount] = String.valueOf(ch);
					billRowCount++;
				}
				if (doubleGallonCount[y] > 0) {
					billRow[0] = item.getName();
					billRow[1] = doubleGallonCount[y];
					billRow[2] = "گیلن";
					billRow[3] = item.getPriceGallon().getSizeName(y)
							.toString();
					if ((!isNormal)
							&& (item.getPriceGallon().getSizeSPrice(y) > 0))
						billRow[4] = doubleGallonCount[y]
								* item.getPriceGallon().getSizeSPrice(y);
					else
						billRow[4] = doubleGallonCount[y]
								* item.getPriceGallon().getSizePrice(y);
					billModel.addRow(billRow);
					double totalAmout = Double
							.valueOf(lblTotalAmount.getText())
							+ ((double) billRow[4]);
					lblTotalAmount.setText(totalAmout + "");
					billRowId[billRowCount] = item.getId();
					billRowType[billRowCount] = "10000000000";
					char[] ch = { '0', '0', '0', '0', '0' };
					ch[y] = '1';
					billRowSize[billRowCount] = String.valueOf(ch);
					billRowCount++;
				}
			}

		}
		textFieldAmountReceived.requestFocus();
	}

	/*----------------------------Count Rows in Table Items Info---------------------*/
	public Item[] getAllItems() {
		Item[] items = null;
		try {
			String query = "Select * from ItemsInfo";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			if(rs != null) {
				//System.out.println("Result set is NOT null");
			} else {

				//System.out.println("Result set is null");
			}
			
			int itemsCount = 0;
			while(rs.next()) {
				itemsCount++;
			}
			items = new Item[itemsCount];
			//System.out.println("All Items Count: " + itemsCount);
			rs.close();
			rs = pst.executeQuery();
			int itemsCounter = 0;
			while (rs.next()) {
				items[itemsCounter] = new Item();
				items[itemsCounter].setId(rs.getInt("EID"));
				items[itemsCounter].setName(rs.getString("Name"));
				items[itemsCounter].setSuperItem(rs.getInt("SuperItem"));
				items[itemsCounter].setType(rs.getString("Type"));
				items[itemsCounter].setHasSpecial(rs.getBoolean("HasSpecial"));
				itemsCounter++;
			}
			rs.close();
			pst.close();
		} catch (Exception e) {
			e.printStackTrace();
		}
		return items;
	}

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
		tblItemsInfo.setFont(cfp.getSmallPlainFont());
		try {
			String query = "select EID, Name from ItemsInfo";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			tblItemsInfo.setModel(DbUtils.resultSetToTableModel(rs));
			DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
			centerRenderer.setHorizontalAlignment(JLabel.CENTER);
			for(int i = 0; i < 2; i++)
				tblItemsInfo.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
			if (tblItemsInfo.getRowCount() > 0)
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
					if (e.isPopupTrigger()
							&& e.getComponent() instanceof JTable) {
						int row = tblItemsInfo.getSelectedRow();
						int id = Integer.valueOf(tblItemsInfo.getModel()
								.getValueAt(row, 0).toString());
						String name = tblItemsInfo.getModel()
								.getValueAt(row, 1).toString();
						saleItem(id, name);
					}
				}
			});

			pst.close();
			rs.close();

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/*------------------------New Item----------------------*/
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void newItem() {
		AbstractDocument doc = null;

		JTextField name = new JTextField(10);
		doc = (AbstractDocument) name.getDocument();
//		name.setFont(new Font("Times New Roman", Font.BOLD, 16));
		name.setFont(cfp.getSmallBoldFont());
		doc.setDocumentFilter(new SizeFilter(12));
		name.addAncestorListener(new RequestFocusListener());
		name.setText("");
		
		JCheckBox superItem = new JCheckBox();
		JCheckBox specialRates = new JCheckBox();
		JCheckBox weight = new JCheckBox();
		JCheckBox gattu = new JCheckBox();
		JCheckBox box = new JCheckBox();
		JCheckBox bundle = new JCheckBox();
		JCheckBox packet = new JCheckBox();
		JCheckBox carton = new JCheckBox();
		JCheckBox bucket = new JCheckBox();
		JCheckBox dozen = new JCheckBox();
		JCheckBox bottle = new JCheckBox();
		JCheckBox tin = new JCheckBox();
		JCheckBox gallon = new JCheckBox();

		JComboBox kgRelation = new JComboBox();
		kgRelation.addItem("اسٹاک");

		JComboBox boxRelation = new JComboBox();
		boxRelation.addItem("اسٹاک");

		JComboBox[] packetRelation = new JComboBox[5];

		JComboBox[] dozenRelation = new JComboBox[5];

		for (int i = 0; i < 5; i++) {
			packetRelation[i] = new JComboBox();
			packetRelation[i].addItem("اسٹاک");

			dozenRelation[i] = new JComboBox();
			dozenRelation[i].addItem("اسٹاک");
		}

		JTextField kgPrice = new JTextField(5);
		doc = (AbstractDocument) kgPrice.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		kgPrice.setText("0");
		kgPrice.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				kgPrice.selectAll();
			}
		});

		JTextField kgStock = new JTextField(5);
		doc = (AbstractDocument) kgStock.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		kgStock.setText("0");
		kgStock.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				kgStock.selectAll();
			}
		});

		JTextField gattuPrice = new JTextField(5);
		doc = (AbstractDocument) gattuPrice.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		gattuPrice.setText("0");
		gattuPrice.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				gattuPrice.selectAll();
			}
		});

		JTextField gattuStock = new JTextField(5);
		doc = (AbstractDocument) gattuStock.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		gattuStock.setText("0");
		gattuStock.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				gattuStock.selectAll();
			}
		});

		JTextField boxPrice = new JTextField(5);
		doc = (AbstractDocument) boxPrice.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		boxPrice.setText("0");
		boxPrice.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				boxPrice.selectAll();
			}
		});

		JTextField boxStock = new JTextField(5);
		doc = (AbstractDocument) boxStock.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		boxStock.setText("0");
		boxStock.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				boxStock.selectAll();
			}
		});

		JTextField bundlePrice = new JTextField(5);
		doc = (AbstractDocument) bundlePrice.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		bundlePrice.setText("0");
		bundlePrice.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				bundlePrice.selectAll();
			}
		});

		JTextField bundleStock = new JTextField(5);
		doc = (AbstractDocument) bundleStock.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		bundleStock.setText("0");
		bundleStock.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				bundleStock.selectAll();
			}
		});

		JTextField kgSPrice = new JTextField(5);
		doc = (AbstractDocument) kgSPrice.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		kgSPrice.setText("0");
		kgSPrice.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				kgSPrice.selectAll();
			}
		});

		JTextField gattuSPrice = new JTextField(5);
		doc = (AbstractDocument) gattuSPrice.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		gattuSPrice.setText("0");
		gattuSPrice.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				gattuSPrice.selectAll();
			}
		});

		JTextField boxSPrice = new JTextField(5);
		doc = (AbstractDocument) boxSPrice.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		boxSPrice.setText("0");
		boxSPrice.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				boxSPrice.selectAll();
			}
		});

		JTextField bundleSPrice = new JTextField(5);
		doc = (AbstractDocument) bundleSPrice.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		bundleSPrice.setText("0");
		bundleSPrice.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				bundleSPrice.selectAll();
			}
		});

		JTextField[] packetName = new JTextField[5];
		JTextField[] cartonName = new JTextField[5];
		JTextField[] bucketName = new JTextField[5];
		JTextField[] dozenName = new JTextField[5];
		JTextField[] bottleName = new JTextField[5];
		JTextField[] tinName = new JTextField[5];
		JTextField[] gallonName = new JTextField[5];

		JTextField[] packetPrice = new JTextField[5];
		JTextField[] cartonPrice = new JTextField[5];
		JTextField[] bucketPrice = new JTextField[5];
		JTextField[] dozenPrice = new JTextField[5];
		JTextField[] bottlePrice = new JTextField[5];
		JTextField[] tinPrice = new JTextField[5];
		JTextField[] gallonPrice = new JTextField[5];

		JTextField[] packetSPrice = new JTextField[5];
		JTextField[] cartonSPrice = new JTextField[5];
		JTextField[] bucketSPrice = new JTextField[5];
		JTextField[] dozenSPrice = new JTextField[5];
		JTextField[] bottleSPrice = new JTextField[5];
		JTextField[] tinSPrice = new JTextField[5];
		JTextField[] gallonSPrice = new JTextField[5];

		JTextField[] packetStock = new JTextField[5];
		JTextField[] cartonStock = new JTextField[5];
		JTextField[] bucketStock = new JTextField[5];
		JTextField[] dozenStock = new JTextField[5];
		JTextField[] bottleStock = new JTextField[5];
		JTextField[] tinStock = new JTextField[5];
		JTextField[] gallonStock = new JTextField[5];

		for (int i = 0; i < 5; i++) {

			int j = i;

			packetName[i] = new JTextField(5);
			doc = (AbstractDocument) packetName[i].getDocument();
			doc.setDocumentFilter(new SizeFilter(7));
			packetName[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					packetName[j].selectAll();
				}
			});

			cartonName[i] = new JTextField(5);
			doc = (AbstractDocument) cartonName[i].getDocument();
			doc.setDocumentFilter(new SizeFilter(7));
			cartonName[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					cartonName[j].selectAll();
				}
			});

			bucketName[i] = new JTextField(5);
			doc = (AbstractDocument) bucketName[i].getDocument();
			doc.setDocumentFilter(new SizeFilter(7));
			bucketName[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					bucketName[j].selectAll();
				}
			});

			dozenName[i] = new JTextField(5);
			doc = (AbstractDocument) dozenName[i].getDocument();
			doc.setDocumentFilter(new SizeFilter(7));
			dozenName[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					dozenName[j].selectAll();
				}
			});

			bottleName[i] = new JTextField(5);
			doc = (AbstractDocument) bottleName[i].getDocument();
			doc.setDocumentFilter(new SizeFilter(7));
			bottleName[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					bottleName[j].selectAll();
				}
			});

			tinName[i] = new JTextField(5);
			doc = (AbstractDocument) tinName[i].getDocument();
			doc.setDocumentFilter(new SizeFilter(7));
			tinName[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					tinName[j].selectAll();
				}
			});

			gallonName[i] = new JTextField(5);
			doc = (AbstractDocument) gallonName[i].getDocument();
			doc.setDocumentFilter(new SizeFilter(7));
			gallonName[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					gallonName[j].selectAll();
				}
			});

			packetPrice[i] = new JTextField(5);
			doc = (AbstractDocument) packetPrice[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			packetPrice[i].setText("0");
			packetPrice[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					packetPrice[j].selectAll();
				}
			});

			cartonPrice[i] = new JTextField(5);
			doc = (AbstractDocument) cartonPrice[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			cartonPrice[i].setText("0");
			cartonPrice[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					cartonPrice[j].selectAll();
				}
			});

			bucketPrice[i] = new JTextField(5);
			doc = (AbstractDocument) bucketPrice[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			bucketPrice[i].setText("0");
			bucketPrice[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					bucketPrice[j].selectAll();
				}
			});

			dozenPrice[i] = new JTextField(5);
			doc = (AbstractDocument) dozenPrice[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			dozenPrice[i].setText("0");
			dozenPrice[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					dozenPrice[j].selectAll();
				}
			});

			bottlePrice[i] = new JTextField(5);
			doc = (AbstractDocument) bottlePrice[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			bottlePrice[i].setText("0");
			bottlePrice[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					bottlePrice[j].selectAll();
				}
			});

			tinPrice[i] = new JTextField(5);
			doc = (AbstractDocument) tinPrice[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			tinPrice[i].setText("0");
			tinPrice[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					tinPrice[j].selectAll();
				}
			});

			gallonPrice[i] = new JTextField(5);
			doc = (AbstractDocument) gallonPrice[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			gallonPrice[i].setText("0");
			gallonPrice[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					gallonPrice[j].selectAll();
				}
			});

			packetSPrice[i] = new JTextField(5);
			doc = (AbstractDocument) packetSPrice[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			packetSPrice[i].setText("0");
			packetSPrice[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					packetSPrice[j].selectAll();
				}
			});

			cartonSPrice[i] = new JTextField(5);
			doc = (AbstractDocument) cartonSPrice[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			cartonSPrice[i].setText("0");
			cartonSPrice[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					cartonSPrice[j].selectAll();
				}
			});

			bucketSPrice[i] = new JTextField(5);
			doc = (AbstractDocument) bucketSPrice[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			bucketSPrice[i].setText("0");
			bucketSPrice[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					bucketSPrice[j].selectAll();
				}
			});

			dozenSPrice[i] = new JTextField(5);
			doc = (AbstractDocument) dozenSPrice[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			dozenSPrice[i].setText("0");
			dozenSPrice[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					dozenSPrice[j].selectAll();
				}
			});

			bottleSPrice[i] = new JTextField(5);
			doc = (AbstractDocument) bottleSPrice[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			bottleSPrice[i].setText("0");
			bottleSPrice[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					bottleSPrice[j].selectAll();
				}
			});

			tinSPrice[i] = new JTextField(5);
			doc = (AbstractDocument) tinSPrice[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			tinSPrice[i].setText("0");
			tinSPrice[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					tinSPrice[j].selectAll();
				}
			});

			gallonSPrice[i] = new JTextField(5);
			doc = (AbstractDocument) gallonSPrice[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			gallonSPrice[i].setText("0");
			gallonSPrice[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					gallonSPrice[j].selectAll();
				}
			});

			packetStock[i] = new JTextField(5);
			doc = (AbstractDocument) packetStock[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			packetStock[i].setText("0");
			packetStock[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					packetStock[j].selectAll();
				}
			});

			cartonStock[i] = new JTextField(5);
			doc = (AbstractDocument) cartonStock[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			cartonStock[i].setText("0");
			cartonStock[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					cartonStock[j].selectAll();
				}
			});

			bucketStock[i] = new JTextField(5);
			doc = (AbstractDocument) bucketStock[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			bucketStock[i].setText("0");
			bucketStock[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					bucketStock[j].selectAll();
				}
			});

			dozenStock[i] = new JTextField(5);
			doc = (AbstractDocument) dozenStock[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			dozenStock[i].setText("0");
			dozenStock[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					dozenStock[j].selectAll();
				}
			});

			bottleStock[i] = new JTextField(5);
			doc = (AbstractDocument) bottleStock[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			bottleStock[i].setText("0");
			bottleStock[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					bottleStock[j].selectAll();
				}
			});

			tinStock[i] = new JTextField(5);
			doc = (AbstractDocument) tinStock[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			tinStock[i].setText("0");
			tinStock[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					tinStock[j].selectAll();
				}
			});

			gallonStock[i] = new JTextField(5);
			doc = (AbstractDocument) gallonStock[i].getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			gallonStock[i].setText("0");
			gallonStock[i].addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					gallonStock[j].selectAll();
				}
			});

		}

		JPanel myPanel = new JPanel(new GridBagLayout());
		gridBagConstraints.anchor = GridBagConstraints.NORTH;
		gridBagConstraints.insets = new Insets(4, 4, 4, 4);
		setGBagConst(0, 0, 1, 1);
		myPanel.add(new JLabel("نام"), gridBagConstraints);
		setGBagConst(1, 0, 20, 1);
		myPanel.add(name, gridBagConstraints);
		
		setGBagConst(0, 1, 1, 1);
		myPanel.add(new JLabel("مخصوص قسم"), gridBagConstraints);
		setGBagConst(1, 1, 1, 1);
		myPanel.add(superItem, gridBagConstraints);

		setGBagConst(0, 2, 1, 1);
		myPanel.add(new JLabel("سپیشل ریٹ"), gridBagConstraints);
		setGBagConst(1, 2, 1, 1);
		myPanel.add(specialRates, gridBagConstraints);

		setGBagConst(0, 3, 1, 1);
		myPanel.add(new JLabel(": آئٹم کی قسمیں چنیں"), gridBagConstraints);

		setGBagConst(0, 4, 1, 1);
		myPanel.add(new JLabel("وزن"), gridBagConstraints);
		setGBagConst(1, 4, 1, 1);
		myPanel.add(weight, gridBagConstraints);

		setGBagConst(0, 5, 1, 1);
		myPanel.add(new JLabel("گٹو"), gridBagConstraints);
		setGBagConst(1, 5, 1, 1);
		myPanel.add(gattu, gridBagConstraints);

		setGBagConst(0, 6, 1, 1);
		myPanel.add(new JLabel("ڈبہ"), gridBagConstraints);
		setGBagConst(1, 6, 1, 1);
		myPanel.add(box, gridBagConstraints);

		setGBagConst(0, 7, 1, 1);
		myPanel.add(new JLabel("بنڈل"), gridBagConstraints);
		setGBagConst(1, 7, 1, 1);
		myPanel.add(bundle, gridBagConstraints);

		setGBagConst(0, 8, 1, 1);
		myPanel.add(new JLabel("پیکٹ"), gridBagConstraints);
		setGBagConst(1, 8, 1, 1);
		myPanel.add(packet, gridBagConstraints);

		setGBagConst(0, 9, 1, 1);
		myPanel.add(new JLabel("کارٹن"), gridBagConstraints);
		setGBagConst(1, 9, 1, 1);
		myPanel.add(carton, gridBagConstraints);

		setGBagConst(0, 10, 1, 1);
		myPanel.add(new JLabel("بالٹی"), gridBagConstraints);
		setGBagConst(1, 10, 1, 1);
		myPanel.add(bucket, gridBagConstraints);

		setGBagConst(0, 11, 1, 1);
		myPanel.add(new JLabel("درجن"), gridBagConstraints);
		setGBagConst(1, 11, 1, 1);
		myPanel.add(dozen, gridBagConstraints);

		setGBagConst(0, 12, 1, 1);
		myPanel.add(new JLabel("بوتل"), gridBagConstraints);
		setGBagConst(1, 12, 1, 1);
		myPanel.add(bottle, gridBagConstraints);

		setGBagConst(0, 13, 1, 1);
		myPanel.add(new JLabel("ٹین"), gridBagConstraints);
		setGBagConst(1, 13, 1, 1);
		myPanel.add(tin, gridBagConstraints);

		setGBagConst(0, 14, 1, 1);
		myPanel.add(new JLabel("گیلن"), gridBagConstraints);
		setGBagConst(1, 14, 1, 1);
		myPanel.add(gallon, gridBagConstraints);

//		javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
		int result = JOptionPane.showConfirmDialog(null, myPanel,
				": آئٹم کا نام اور اس کی قسمیں درج کریں",
				JOptionPane.OK_CANCEL_OPTION);
		String type = "";
		if (gallon.isSelected())
			type = "1";
		else
			type = "0";
		if (tin.isSelected())
			type = type + "1";
		else
			type = type + "0";
		if (bottle.isSelected())
			type = type + "1";
		else
			type = type + "0";
		if (dozen.isSelected())
			type = type + "1";
		else
			type = type + "0";
		if (bucket.isSelected())
			type = type + "1";
		else
			type = type + "0";
		if (carton.isSelected())
			type = type + "1";
		else
			type = type + "0";
		if (packet.isSelected())
			type = type + "1";
		else
			type = type + "0";
		if (bundle.isSelected())
			type = type + "1";
		else
			type = type + "0";
		if (box.isSelected())
			type = type + "1";
		else
			type = type + "0";
		if (gattu.isSelected())
			type = type + "1";
		else
			type = type + "0";
		if (weight.isSelected())
			type = type + "1";
		else
			type = type + "0";
		
		int intType = Integer.parseInt(type, 2);

		if ((result == JOptionPane.OK_OPTION) && (intType > 0)
				&& (!name.getText().toString().equals(""))) {
			myPanel.removeAll();

			int gridY = 0;

			if (weight.isSelected()) {
				setGBagConst(0, gridY, 1, 1);
				myPanel.add(new JLabel("کلو کی قیمت"), gridBagConstraints);
				setGBagConst(1, gridY, 1, 1);
				myPanel.add(kgPrice, gridBagConstraints);
				if(stockStatus) {
					setGBagConst(2, gridY, 1, 1);
					myPanel.add(new JLabel("اسٹاک"), gridBagConstraints);
					setGBagConst(3, gridY, 1, 1);
					myPanel.add(kgStock, gridBagConstraints);
					setGBagConst(4, gridY, 1, 1);
					myPanel.add(kgRelation, gridBagConstraints);
				}
				gridY++;
			}

			if (gattu.isSelected()) {
				if(stockStatus) {
					kgRelation.addItem("/Gattu");
					for (int i = 0; i < 5; i++) {
						packetRelation[i].addItem("/Gattu");
						dozenRelation[i].addItem("/Gattu");
					}
				}
				setGBagConst(0, gridY, 1, 1);
				myPanel.add(new JLabel("گٹو کی قیمت"), gridBagConstraints);
				setGBagConst(1, gridY, 1, 1);
				myPanel.add(gattuPrice, gridBagConstraints);
				if(stockStatus) {
					setGBagConst(2, gridY, 1, 1);
					myPanel.add(new JLabel("اسٹاک"), gridBagConstraints);
					setGBagConst(3, gridY, 1, 1);
					myPanel.add(gattuStock, gridBagConstraints);
				}
				gridY++;
			}

			if (box.isSelected()) {
				if(stockStatus) {
					for (int i = 0; i < 5; i++) {
						packetRelation[i].addItem("/Box");
					}
				}
				setGBagConst(0, gridY, 1, 1);
				myPanel.add(new JLabel("ڈبہ کی قیمت"), gridBagConstraints);
				setGBagConst(1, gridY, 1, 1);
				myPanel.add(boxPrice, gridBagConstraints);
				if(stockStatus) {
					setGBagConst(2, gridY, 1, 1);
					myPanel.add(new JLabel("اسٹاک"), gridBagConstraints);
					setGBagConst(3, gridY, 1, 1);
					myPanel.add(boxStock, gridBagConstraints);
					setGBagConst(4, gridY, 1, 1);
					myPanel.add(boxRelation, gridBagConstraints);
				}
				gridY++;
			}

			if (bundle.isSelected()) {
				if(stockStatus) {
					boxRelation.addItem("/Bundle");
				}
				setGBagConst(0, gridY, 1, 1);
				myPanel.add(new JLabel("بنڈل کی قیمت"), gridBagConstraints);
				setGBagConst(1, gridY, 1, 1);
				myPanel.add(bundlePrice, gridBagConstraints);
				if(stockStatus) {
					setGBagConst(2, gridY, 1, 1);
					myPanel.add(new JLabel("اسٹاک"), gridBagConstraints);
					setGBagConst(3, gridY, 1, 1);
					myPanel.add(bundleStock, gridBagConstraints);
				}
				gridY++;
			}

			if (intType > Integer.parseInt("00000001111", 2)) {

				int countSized = 0;
				for (int i = 0; i < 7; i++) {
					if (type.charAt(i) == '1')
						countSized++;
				}

				gridBagConstraints.insets = new Insets(30, 4, 4, 4);
				setGBagConst(1, gridY, 1, 1);
				myPanel.add(new JLabel("نام"), gridBagConstraints);
				setGBagConst(2, gridY, 1, 1);
				myPanel.add(new JLabel("قیمت"), gridBagConstraints);
				if(stockStatus) {
					setGBagConst(3, gridY, 1, 1);
					myPanel.add(new JLabel("اسٹاک"), gridBagConstraints);
				}
				
				if (countSized >= 2) {
					gridBagConstraints.insets = new Insets(30, 4, 4, 4);
					setGBagConst(6, gridY, 1, 1);
					myPanel.add(new JLabel("نام"), gridBagConstraints);
					setGBagConst(7, gridY, 1, 1);
					myPanel.add(new JLabel("قیمت"), gridBagConstraints);
					if(stockStatus) {
						setGBagConst(8, gridY, 1, 1);
						myPanel.add(new JLabel("اسٹاک"), gridBagConstraints);
					}
				}
				if (countSized > 2) {
					gridBagConstraints.insets = new Insets(30, 4, 4, 4);
					setGBagConst(11, gridY, 1, 1);
					myPanel.add(new JLabel("نام"), gridBagConstraints);
					setGBagConst(12, gridY, 1, 1);
					myPanel.add(new JLabel("قیمت"), gridBagConstraints);
					if(stockStatus) {
						setGBagConst(13, gridY, 1, 1);
						myPanel.add(new JLabel("اسٹاک"), gridBagConstraints);
					}
				}
				if (countSized > 3) {
					gridBagConstraints.insets = new Insets(30, 4, 4, 4);
					setGBagConst(16, gridY, 1, 1);
					myPanel.add(new JLabel("نام"), gridBagConstraints);
					setGBagConst(17, gridY, 1, 1);
					myPanel.add(new JLabel("قیمت"), gridBagConstraints);
					if(stockStatus) {
						setGBagConst(18, gridY, 1, 1);
						myPanel.add(new JLabel("اسٹاک"), gridBagConstraints);
					}
				}

				gridY++;

				if (packet.isSelected()) {
					gridBagConstraints.insets = new Insets(10, 4, 4, 4);
					setGBagConst(0, gridY, 1, 1);
					myPanel.add(new JLabel("پیکٹ"), gridBagConstraints);
					gridY++;
					gridBagConstraints.insets = new Insets(4, 4, 4, 4);
					for (int i = 0; i < 5; i++) {
						setGBagConst(0, gridY, 1, 1);
						myPanel.add(new JLabel("سائز " + (i + 1)),
								gridBagConstraints);
						setGBagConst(1, gridY, 1, 1);
						myPanel.add(packetName[i], gridBagConstraints);
						setGBagConst(2, gridY, 1, 1);
						myPanel.add(packetPrice[i], gridBagConstraints);
						if(stockStatus) {
							setGBagConst(3, gridY, 1, 1);
							myPanel.add(packetStock[i], gridBagConstraints);
							setGBagConst(4, gridY, 1, 1);
							myPanel.add(packetRelation[i], gridBagConstraints);
						}
						gridY++;
					}
				}

				if (carton.isSelected()) {
					if(stockStatus) {
						kgRelation.addItem("/Carton");
						for (int i = 0; i < 5; i++) {
							packetRelation[i].addItem("/Carton");
							dozenRelation[i].addItem("/Carton");
						}
					}
					int gridX = 0;
					boolean isEven = packet.isSelected();
					if (isEven) {
						gridY -= 6;
						gridX = 5;
						gridBagConstraints.insets = new Insets(10, 30, 4, 4);
					} else
						gridBagConstraints.insets = new Insets(10, 4, 4, 4);
					setGBagConst(gridX, gridY, 1, 1);
					myPanel.add(new JLabel("کارٹن"), gridBagConstraints);
					gridY++;
					gridBagConstraints.insets = new Insets(4, 4, 4, 4);
					for (int i = 0; i < 5; i++) {
						if (isEven) {
							gridBagConstraints.insets = new Insets(4, 30, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(new JLabel("سائز " + (i + 1)),
								gridBagConstraints);
						gridX++;
						if (isEven) {
							gridBagConstraints.insets = new Insets(4, 4, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(cartonName[i], gridBagConstraints);
						gridX++;
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(cartonPrice[i], gridBagConstraints);
						gridX++;
						if(stockStatus) {
							setGBagConst(gridX, gridY, 1, 1);
							myPanel.add(cartonStock[i], gridBagConstraints);
						}
						gridY++;
						if (isEven)
							gridX = 5;
						else
							gridX = 0;
					}
				}

				if (bucket.isSelected()) {
					int gridX = 0;
					int dispId = 0;
					for (int i = 6; i >= 5; i--) {
						if (type.charAt(i) == '1')
							dispId++;
					}
					if (dispId > 0) {
						gridX = dispId * 5;
						gridY -= 6;
						gridBagConstraints.insets = new Insets(10, 30, 4, 4);
					} else
						gridBagConstraints.insets = new Insets(10, 4, 4, 4);
					setGBagConst(gridX, gridY, 1, 1);
					myPanel.add(new JLabel("بالٹی"), gridBagConstraints);
					gridY++;
					gridBagConstraints.insets = new Insets(4, 4, 4, 4);

					for (int i = 0; i < 5; i++) {
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 30, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(new JLabel("سائز " + (i + 1)),
								gridBagConstraints);
						gridX++;
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 4, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(bucketName[i], gridBagConstraints);
						gridX++;
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(bucketPrice[i], gridBagConstraints);
						gridX++;
						if(stockStatus) {
							setGBagConst(gridX, gridY, 1, 1);
							myPanel.add(bucketStock[i], gridBagConstraints);
						}
						gridY++;
						if (dispId == 1)
							gridX = 5;
						else if (dispId == 2)
							gridX = 10;
						else
							gridX = 0;
					}
				}

				if (dozen.isSelected()) {
					if(stockStatus) {
						for (int i = 0; i < 5; i++) {
							packetRelation[i].addItem("/Dozen");
						}
					}
					int gridX = 0;
					int dispId = 0;
					for (int i = 6; i >= 4; i--) {
						if (type.charAt(i) == '1')
							dispId++;
					}
					if (dispId > 0) {
						gridY -= 6;
						gridX = dispId * 5;
						gridBagConstraints.insets = new Insets(10, 30, 4, 4);
					} else
						gridBagConstraints.insets = new Insets(10, 4, 4, 4);
					setGBagConst(gridX, gridY, 1, 1);
					myPanel.add(new JLabel("درجن"), gridBagConstraints);
					gridY++;
					gridBagConstraints.insets = new Insets(4, 4, 4, 4);

					for (int i = 0; i < 5; i++) {
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 30, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(new JLabel("سائز " + (i + 1)),
								gridBagConstraints);
						gridX++;
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 4, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(dozenName[i], gridBagConstraints);
						gridX++;
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(dozenPrice[i], gridBagConstraints);
						gridX++;
						if(stockStatus) {
							setGBagConst(gridX, gridY, 1, 1);
							myPanel.add(dozenStock[i], gridBagConstraints);
							gridX++;
							setGBagConst(gridX, gridY, 1, 1);
							myPanel.add(dozenRelation[i], gridBagConstraints);
						}
						gridY++;
						gridX = dispId * 5;
					}
				}

				if (bottle.isSelected()) {
					int gridX = 0;
					int dispId = 0;
					for (int i = 6; i >= 3; i--) {
						if (type.charAt(i) == '1')
							dispId++;
					}
					if (dispId > 0) {
						if (dispId != 4)
							gridY -= 6;
						if (dispId > 3) {
							dispId -= 4;
						}
						gridX = dispId * 5;
						if ((type.charAt(3) == '1') && (type.charAt(4) == '1')
								&& (type.charAt(5) == '1')
								&& (type.charAt(6) == '1'))
							gridBagConstraints.insets = new Insets(10, 4, 4, 4);
						else
							gridBagConstraints.insets = new Insets(10, 30, 4, 4);
					} else
						gridBagConstraints.insets = new Insets(10, 4, 4, 4);
					setGBagConst(gridX, gridY, 1, 1);
					myPanel.add(new JLabel("بوتل"), gridBagConstraints);
					gridY++;
					gridBagConstraints.insets = new Insets(4, 4, 4, 4);

					for (int i = 0; i < 5; i++) {
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 30, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(new JLabel("سائز " + (i + 1)),
								gridBagConstraints);
						gridX++;
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 4, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(bottleName[i], gridBagConstraints);
						gridX++;
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(bottlePrice[i], gridBagConstraints);
						gridX++;
						if(stockStatus) {
							setGBagConst(gridX, gridY, 1, 1);
							myPanel.add(bottleStock[i], gridBagConstraints);
						}
						gridY++;
						gridX = dispId * 5;
					}
				}

				if (tin.isSelected()) {
					int gridX = 0;
					int dispId = 0;
					for (int i = 6; i >= 2; i--) {
						if (type.charAt(i) == '1')
							dispId++;
					}
					if (dispId > 0) {
						if (dispId > 0) {
							if (dispId != 4)
								gridY -= 6;
							if (dispId > 3) {
								dispId -= 4;
							}
							gridX = dispId * 5;
						}
						if ((type.charAt(2) == '0') && (type.charAt(3) == '1')
								&& (type.charAt(4) == '1')
								&& (type.charAt(5) == '1')
								&& (type.charAt(6) == '1'))
							gridBagConstraints.insets = new Insets(10, 4, 4, 4);
						else
							gridBagConstraints.insets = new Insets(10, 30, 4, 4);
					} else
						gridBagConstraints.insets = new Insets(10, 4, 4, 4);
					setGBagConst(gridX, gridY, 1, 1);
					myPanel.add(new JLabel("ٹین"), gridBagConstraints);
					gridY++;
					gridBagConstraints.insets = new Insets(4, 4, 4, 4);

					for (int i = 0; i < 5; i++) {
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 30, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(new JLabel("سائز " + (i + 1)),
								gridBagConstraints);
						gridX++;
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 4, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(tinName[i], gridBagConstraints);
						gridX++;
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(tinPrice[i], gridBagConstraints);
						gridX++;
						if(stockStatus) {
							setGBagConst(gridX, gridY, 1, 1);
							myPanel.add(tinStock[i], gridBagConstraints);
						}
						gridY++;
						gridX = dispId * 5;
					}
				}

				if (gallon.isSelected()) {
					int gridX = 0;
					int dispId = 0;
					for (int i = 6; i >= 1; i--) {
						if (type.charAt(i) == '1')
							dispId++;
					}
					if (dispId > 0) {
						if (dispId > 0) {
							if (dispId != 4)
								gridY -= 6;
							if (dispId > 3) {
								dispId -= 4;
							}
							gridX = dispId * 5;
						}
						if ((type.charAt(1) == '0') && (type.charAt(2) == '0')
								&& (type.charAt(3) == '1')
								&& (type.charAt(4) == '1')
								&& (type.charAt(5) == '1')
								&& (type.charAt(6) == '1'))
							gridBagConstraints.insets = new Insets(10, 4, 4, 4);
						else
							gridBagConstraints.insets = new Insets(10, 30, 4, 4);
					} else
						gridBagConstraints.insets = new Insets(10, 4, 4, 4);
					setGBagConst(gridX, gridY, 1, 1);
					myPanel.add(new JLabel("گیلن"), gridBagConstraints);
					gridY++;
					gridBagConstraints.insets = new Insets(4, 4, 4, 4);

					for (int i = 0; i < 5; i++) {
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 30, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(new JLabel("سائز " + (i + 1)),
								gridBagConstraints);
						gridX++;
						if (dispId > 0) {
							gridBagConstraints.insets = new Insets(4, 4, 4, 4);
						}
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(gallonName[i], gridBagConstraints);
						gridX++;
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(gallonPrice[i], gridBagConstraints);
						gridX++;
						if(stockStatus) {
							setGBagConst(gridX, gridY, 1, 1);
							myPanel.add(gallonStock[i], gridBagConstraints);
						}
						gridY++;
						gridX = dispId * 5;
					}
				}

			}

//			javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
			int result_1 = JOptionPane
					.showConfirmDialog(null, myPanel, ":"
							+ " آئٹم کی تفصیلات درج کریں",
							JOptionPane.OK_CANCEL_OPTION);
			if (result_1 == JOptionPane.OK_OPTION) {

				double newPriceKg = 0;
				double newPriceGattu = 0;
				double newPriceBundle = 0;
				double newPriceBox = 0;

				double newStockKg = 0;
				double newStockGattu = 0;
				double newStockBundle = 0;
				double newStockBox = 0;

				String newRelationKg = "اسٹاک";
				String newRelationGattu = "اسٹاک";
				String newRelationBundle = "اسٹاک";
				String newRelationBox = "اسٹاک";

				String[] newPacketName = new String[5];
				double[] newPacketPrice = new double[5];
				double[] newPacketStock = new double[5];
				String[] newPacketRelation = new String[5];
				String[] newCartonName = new String[5];
				double[] newCartonPrice = new double[5];
				double[] newCartonStock = new double[5];
				String[] newCartonRelation = new String[5];
				String[] newBucketName = new String[5];
				double[] newBucketPrice = new double[5];
				double[] newBucketStock = new double[5];
				String[] newBucketRelation = new String[5];
				String[] newDozenName = new String[5];
				double[] newDozenPrice = new double[5];
				double[] newDozenStock = new double[5];
				String[] newDozenRelation = new String[5];
				String[] newBottleName = new String[5];
				double[] newBottlePrice = new double[5];
				double[] newBottleStock = new double[5];
				String[] newBottleRelation = new String[5];
				String[] newTinName = new String[5];
				double[] newTinPrice = new double[5];
				double[] newTinStock = new double[5];
				String[] newTinRelation = new String[5];
				String[] newGallonName = new String[5];
				double[] newGallonPrice = new double[5];
				double[] newGallonStock = new double[5];
				String[] newGallonRelation = new String[5];

				SizedItem newPacket = null;
				SizedItem newCarton = null;
				SizedItem newBucket = null;
				SizedItem newDozen = null;
				SizedItem newBottle = null;
				SizedItem newTin = null;
				SizedItem newGallon = null;

				try {
					newPriceKg = Double.valueOf(kgPrice.getText());
					newStockKg = Double.valueOf(kgStock.getText());
					newRelationKg = (String) kgRelation.getSelectedItem();
					newPriceGattu = Double.valueOf(gattuPrice.getText());
					newStockGattu = Double.valueOf(gattuStock.getText());
					newRelationGattu = "اسٹاک";
					newPriceBundle = Double.valueOf(bundlePrice.getText());
					newStockBundle = Double.valueOf(bundleStock.getText());
					newRelationBundle = "اسٹاک";
					newPriceBox = Double.valueOf(boxPrice.getText());
					newStockBox = Double.valueOf(boxStock.getText());
					newRelationBox = (String) boxRelation.getSelectedItem();

					for (int i = 0; i < 5; i++) {
						newPacketName[i] = packetName[i].getText();
						newPacketPrice[i] = Double.valueOf(packetPrice[i]
								.getText());
						newPacketStock[i] = Double.valueOf(packetStock[i]
								.getText());
						newPacketRelation[i] = (String) packetRelation[i]
								.getSelectedItem();
						newCartonName[i] = cartonName[i].getText();
						newCartonPrice[i] = Double.valueOf(cartonPrice[i]
								.getText());
						newCartonStock[i] = Double.valueOf(cartonStock[i]
								.getText());
						newCartonRelation[i] = "اسٹاک";
						newBucketName[i] = bucketName[i].getText();
						newBucketPrice[i] = Double.valueOf(bucketPrice[i]
								.getText());
						newBucketStock[i] = Double.valueOf(bucketStock[i]
								.getText());
						newBucketRelation[i] = "اسٹاک";
						newDozenName[i] = dozenName[i].getText();
						newDozenPrice[i] = Double.valueOf(dozenPrice[i]
								.getText());
						newDozenStock[i] = Double.valueOf(dozenStock[i]
								.getText());
						newDozenRelation[i] = (String) dozenRelation[i]
								.getSelectedItem();
						newBottleName[i] = bottleName[i].getText();
						newBottlePrice[i] = Double.valueOf(bottlePrice[i]
								.getText());
						newBottleStock[i] = Double.valueOf(bottleStock[i]
								.getText());
						newBottleRelation[i] = "اسٹاک";
						newTinName[i] = tinName[i].getText();
						newTinPrice[i] = Double.valueOf(tinPrice[i].getText());
						newTinStock[i] = Double.valueOf(tinStock[i].getText());
						newTinRelation[i] = "اسٹاک";
						newGallonName[i] = gallonName[i].getText();
						newGallonPrice[i] = Double.valueOf(gallonPrice[i]
								.getText());
						newGallonStock[i] = Double.valueOf(gallonStock[i]
								.getText());
						newGallonRelation[i] = "اسٹاک";

					}

					newPacket = new SizedItem(newPacketName, newPacketPrice,
							newPacketStock, newPacketRelation);
					newCarton = new SizedItem(newCartonName, newCartonPrice,
							newCartonStock, newCartonRelation);
					newBucket = new SizedItem(newBucketName, newBucketPrice,
							newBucketStock, newBucketRelation);
					newDozen = new SizedItem(newDozenName, newDozenPrice,
							newDozenStock, newDozenRelation);
					newBottle = new SizedItem(newBottleName, newBottlePrice,
							newBottleStock, newBottleRelation);
					newTin = new SizedItem(newTinName, newTinPrice,
							newTinStock, newTinRelation);
					newGallon = new SizedItem(newGallonName, newGallonPrice,
							newGallonStock, newGallonRelation);

					JComboBox comboSuperItems = new JComboBox();
					comboSuperItems.addItem("نئی");
					SuperItem[] superItems = getAllSuperItems(connection);
					if(superItems != null) {
						for(int i = 0; i < superItems.length; i++) {
							comboSuperItems.addItem(superItems[i].getName());
						}
					}
					
					JTextField fieldSuperItem = new JTextField(10);
					doc = (AbstractDocument) fieldSuperItem.getDocument();
//					fieldSuperItem.setFont(new Font("Times New Roman", Font.BOLD, 16));
					fieldSuperItem.setFont(cfp.getSmallBoldFont());
					doc.setDocumentFilter(new SizeFilter(12));
					fieldSuperItem.addAncestorListener(new RequestFocusListener());
					fieldSuperItem.setText("");
					
					int superItemId = 0;
					
					int resultSuperItem = JOptionPane.OK_OPTION;
					if(superItem.isSelected()) {
						myPanel.removeAll();
						
						setGBagConst(0, 0, 1, 1);
						myPanel.add(new JLabel("مخصوص قسم"), gridBagConstraints);
						setGBagConst(1, 0, 1, 1);
						myPanel.add(comboSuperItems, gridBagConstraints);
						
						setGBagConst(0, 1, 1, 1);
						myPanel.add(new JLabel("نئی مخصوص قسم"), gridBagConstraints);
						setGBagConst(1, 1, 1, 1);
						myPanel.add(fieldSuperItem, gridBagConstraints);
						
//						javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
						resultSuperItem = JOptionPane.showConfirmDialog(null, myPanel,
								": آئٹم کی مخصوص قسمیں درج کریں",
								JOptionPane.OK_CANCEL_OPTION);
						if(resultSuperItem == JOptionPane.OK_OPTION) {
							if(comboSuperItems.getSelectedIndex() > 0) {
								superItemId = comboSuperItems.getSelectedIndex();
							} else if((comboSuperItems.getSelectedIndex() == 0) && (!fieldSuperItem.getText().toString().matches(""))) {
								//System.out.println("Creating new Super Item: " + fieldSuperItem.getText().toString());
								
								SuperItem newSuperItem = new SuperItem(fieldSuperItem.getText().toString(), 0);
								superItemId = newSuperItem.insertNewSuperItem(connection);
								//System.out.println("New Super Item Id = " + superItemId);
							}
						}
					}
					
					
					Item newItem = new Item(type, name.getText(), superItemId, newPriceKg,
							newStockKg, newRelationKg, newPriceGattu,
							newStockGattu, newRelationGattu, newPriceBundle,
							newStockBundle, newRelationBundle, newPriceBox,
							newStockBox, newRelationBox, newPacket, newCarton,
							newBucket, newDozen, newBottle, newTin, newGallon,
							specialRates.isSelected());
					newItem.insertNewItem(connection);

					if ((result == JOptionPane.OK_OPTION) && (intType > 0)
							&& specialRates.isSelected()) {
						myPanel.removeAll();

						gridY = 0;

						if (weight.isSelected() && newPriceKg > 0) {
							setGBagConst(0, gridY, 1, 1);
							myPanel.add(new JLabel("کلو کی خاص قیمت"),
									gridBagConstraints);
							setGBagConst(1, gridY, 1, 1);
							myPanel.add(kgSPrice, gridBagConstraints);
							gridY++;
						}

						if (gattu.isSelected() && newPriceGattu > 0) {
							setGBagConst(0, gridY, 1, 1);
							myPanel.add(new JLabel("گٹو کی خاص قیمت"),
									gridBagConstraints);
							setGBagConst(1, gridY, 1, 1);
							myPanel.add(gattuSPrice, gridBagConstraints);
							gridY++;
						}

						if (box.isSelected() && newPriceBox > 0) {
							setGBagConst(0, gridY, 1, 1);
							myPanel.add(new JLabel("ڈبے کی خاص قیمت"),
									gridBagConstraints);
							setGBagConst(1, gridY, 1, 1);
							myPanel.add(boxSPrice, gridBagConstraints);
							gridY++;
						}

						if (bundle.isSelected() && newPriceBundle > 0) {
							setGBagConst(0, gridY, 1, 1);
							myPanel.add(new JLabel("بنڈل کی خاص قیمت"),
									gridBagConstraints);
							setGBagConst(1, gridY, 1, 1);
							myPanel.add(bundleSPrice, gridBagConstraints);
							gridY++;
						}

						if (intType > Integer.parseInt("00000001111", 2)) {

							gridY++;

							if (packet.isSelected() && newPacketPrice[0] > 0) {
								gridBagConstraints.insets = new Insets(30, 4,
										4, 4);
								setGBagConst(0, gridY, 1, 1);
								myPanel.add(new JLabel("پیکٹ کی خاص قیمتیں"),
										gridBagConstraints);
								gridY++;
								gridBagConstraints.insets = new Insets(4, 4, 4,
										4);
								for (int i = 0; i < 5; i++) {
									if (newPacketPrice[i] > 0) {
										setGBagConst(0, gridY, 1, 1);
										myPanel.add(
												new JLabel(newPacketName[i]),
												gridBagConstraints);
										setGBagConst(1, gridY, 1, 1);
										myPanel.add(packetSPrice[i],
												gridBagConstraints);

									}
									gridY++;
								}
							}

							if (carton.isSelected() && newCartonPrice[0] > 0) {
								int gridX = 0;
								boolean isEven = packet.isSelected();
								if (isEven) {
									gridY -= 6;
									gridX = 2;
									gridBagConstraints.insets = new Insets(10,
											30, 4, 4);
								} else
									gridBagConstraints.insets = new Insets(10,
											4, 4, 4);
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(new JLabel("کارٹن کی خاص قیمتیں"),
										gridBagConstraints);
								gridY++;
								gridBagConstraints.insets = new Insets(4, 4, 4,
										4);
								for (int i = 0; i < 5; i++) {
									if (newCartonPrice[i] > 0) {
										if (isEven) {
											gridBagConstraints.insets = new Insets(
													4, 30, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(
												new JLabel(newCartonName[i]),
												gridBagConstraints);
										gridX++;
										if (isEven) {
											gridBagConstraints.insets = new Insets(
													4, 4, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(cartonSPrice[i],
												gridBagConstraints);
										gridX++;

										if (isEven)
											gridX = 2;
										else
											gridX = 0;
									}
									gridY++;
								}
							}

							if (bucket.isSelected() && newBucketPrice[0] > 0) {
								int gridX = 0;
								int dispId = 0;
								for (int i = 6; i >= 5; i--) {
									if (type.charAt(i) == '1')
										dispId++;
								}
								if (dispId > 0) {
									gridX = dispId * 2;
									gridY -= 6;
									gridBagConstraints.insets = new Insets(10,
											30, 4, 4);
								} else
									gridBagConstraints.insets = new Insets(10,
											4, 4, 4);
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(new JLabel("بالٹی کی خاص قیمتیں"),
										gridBagConstraints);
								gridY++;
								gridBagConstraints.insets = new Insets(4, 4, 4,
										4);

								for (int i = 0; i < 5; i++) {
									if (newBucketPrice[i] > 0) {
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 30, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(
												new JLabel(newBucketName[i]),
												gridBagConstraints);
										gridX++;
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 4, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(bucketSPrice[i],
												gridBagConstraints);
										gridX++;
										if (dispId == 1)
											gridX = 2;
										else if (dispId == 2)
											gridX = 4;
										else
											gridX = 0;
									}
									gridY++;
								}
							}

							if (dozen.isSelected() && newDozenPrice[0] > 0) {
								int gridX = 0;
								int dispId = 0;
								for (int i = 6; i >= 4; i--) {
									if (type.charAt(i) == '1')
										dispId++;
								}
								if (dispId > 0) {
									gridY -= 6;
									gridX = dispId * 2;
									gridBagConstraints.insets = new Insets(10,
											30, 4, 4);
								} else
									gridBagConstraints.insets = new Insets(10,
											4, 4, 4);
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(new JLabel("درجن کی خاص قیمتیں"),
										gridBagConstraints);
								gridY++;
								gridBagConstraints.insets = new Insets(4, 4, 4,
										4);

								for (int i = 0; i < 5; i++) {
									if (newDozenPrice[i] > 0) {
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 30, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(
												new JLabel(newDozenName[i]),
												gridBagConstraints);
										gridX++;
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 4, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(dozenSPrice[i],
												gridBagConstraints);
										gridX++;
										gridX = dispId * 2;
									}
									gridY++;
								}
							}

							if (bottle.isSelected() && newBottlePrice[0] > 0) {
								int gridX = 0;
								int dispId = 0;
								for (int i = 6; i >= 3; i--) {
									if (type.charAt(i) == '1')
										dispId++;
								}
								if (dispId > 0) {
									if (dispId != 4)
										gridY -= 6;
									if (dispId > 3) {
										dispId -= 4;
									}
									gridX = dispId * 2;

									if ((type.charAt(3) == '1')
											&& (type.charAt(4) == '1')
											&& (type.charAt(5) == '1')
											&& (type.charAt(6) == '1'))
										gridBagConstraints.insets = new Insets(
												10, 4, 4, 4);
									else
										gridBagConstraints.insets = new Insets(
												10, 30, 4, 4);
								} else
									gridBagConstraints.insets = new Insets(10,
											4, 4, 4);
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(new JLabel("بوتل کی خاص قیمتیں"),
										gridBagConstraints);
								gridY++;
								gridBagConstraints.insets = new Insets(4, 4, 4,
										4);

								for (int i = 0; i < 5; i++) {
									if (newBottlePrice[i] > 0) {
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 30, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(
												new JLabel(newBottleName[i]),
												gridBagConstraints);
										gridX++;
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 4, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(bottleSPrice[i],
												gridBagConstraints);
										gridX = dispId * 2;
									}
									gridY++;
								}
							}

							if (tin.isSelected() && newTinPrice[0] > 0) {
								int gridX = 0;
								int dispId = 0;
								for (int i = 6; i >= 2; i--) {
									if (type.charAt(i) == '1')
										dispId++;
								}
								if (dispId > 0) {
									if (dispId > 0) {
										if (dispId != 4)
											gridY -= 6;
										if (dispId > 3) {
											dispId -= 4;
										}
										gridX = dispId * 2;
									}
									if ((type.charAt(2) == '0')
											&& (type.charAt(3) == '1')
											&& (type.charAt(4) == '1')
											&& (type.charAt(5) == '1')
											&& (type.charAt(6) == '1'))
										gridBagConstraints.insets = new Insets(
												10, 4, 4, 4);
									else
										gridBagConstraints.insets = new Insets(
												10, 30, 4, 4);
								} else
									gridBagConstraints.insets = new Insets(10,
											4, 4, 4);
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(new JLabel("ٹین کی خاص قیمتیں"),
										gridBagConstraints);
								gridY++;
								gridBagConstraints.insets = new Insets(4, 4, 4,
										4);

								for (int i = 0; i < 5; i++) {
									if (newTinPrice[i] > 0) {
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 30, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(new JLabel(newTinName[i]),
												gridBagConstraints);
										gridX++;
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 4, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(tinSPrice[i],
												gridBagConstraints);

										gridX = dispId * 2;
									}
									gridY++;
								}
							}

							if (gallon.isSelected() && newGallonPrice[0] > 0) {
								int gridX = 0;
								int dispId = 0;
								for (int i = 6; i >= 1; i--) {
									if (type.charAt(i) == '1')
										dispId++;
								}
								if (dispId > 0) {
									if (dispId > 0) {
										if (dispId != 4)
											gridY -= 6;
										if (dispId > 3) {
											dispId -= 4;
										}
										gridX = dispId * 2;
									}
									if ((type.charAt(1) == '0')
											&& (type.charAt(2) == '0')
											&& (type.charAt(3) == '1')
											&& (type.charAt(4) == '1')
											&& (type.charAt(5) == '1')
											&& (type.charAt(6) == '1'))
										gridBagConstraints.insets = new Insets(
												10, 4, 4, 4);
									else
										gridBagConstraints.insets = new Insets(
												10, 30, 4, 4);
								} else
									gridBagConstraints.insets = new Insets(10,
											4, 4, 4);
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(new JLabel("گیلن کی خاص قیمتیں"),
										gridBagConstraints);
								gridY++;
								gridBagConstraints.insets = new Insets(4, 4, 4,
										4);

								for (int i = 0; i < 5; i++) {
									if (newGallonPrice[i] > 0) {
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 30, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(
												new JLabel(newGallonName[i]),
												gridBagConstraints);
										gridX++;
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 4, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(gallonSPrice[i],
												gridBagConstraints);

										gridX = dispId * 2;
									}
									gridY++;
								}
							}

						}

//						javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
						int result_2 = JOptionPane.showConfirmDialog(null,
								myPanel, ":" + " آئٹم کی خاص قیمتیں درج کریں",
								JOptionPane.OK_CANCEL_OPTION);
						if (result_2 == JOptionPane.OK_OPTION) {

							double newSPriceKg = Double.valueOf(kgSPrice
									.getText());
							double newSPriceGattu = Double.valueOf(gattuSPrice
									.getText());
							double newSPriceBox = Double.valueOf(boxSPrice
									.getText());
							double newSPriceBundle = Double
									.valueOf(bundleSPrice.getText());

							double[] newSPricePacket = new double[5];
							double[] newSPriceCarton = new double[5];
							double[] newSPriceBucket = new double[5];
							double[] newSPriceDozen = new double[5];
							double[] newSPriceBottle = new double[5];
							double[] newSPriceTin = new double[5];
							double[] newSPriceGallon = new double[5];

							for (int i = 0; i < 5; i++) {
								newSPricePacket[i] = Double
										.valueOf(packetSPrice[i].getText());
								newSPriceCarton[i] = Double
										.valueOf(cartonSPrice[i].getText());
								newSPriceBucket[i] = Double
										.valueOf(bucketSPrice[i].getText());
								newSPriceDozen[i] = Double
										.valueOf(dozenSPrice[i].getText());
								newSPriceBottle[i] = Double
										.valueOf(bottleSPrice[i].getText());
								newSPriceTin[i] = Double.valueOf(tinSPrice[i]
										.getText());
								newSPriceGallon[i] = Double
										.valueOf(gallonSPrice[i].getText());
							}

							newItem.addSpecialPrices(connection, newSPriceKg,
									newSPriceGattu, newSPriceBox,
									newSPriceBundle, newSPricePacket,
									newSPriceCarton, newSPriceBucket,
									newSPriceDozen, newSPriceBottle,
									newSPriceTin, newSPriceGallon);
						}

					}

//					javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
					JOptionPane.showMessageDialog(null,
							"نیا آئٹم ڈال دیا گیا !");
				} catch (Exception e) {
					e.printStackTrace();
//					javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
					JOptionPane.showMessageDialog(null,
							"خالی جگہ نہیں چھوڑی جا سکتی !");
				}
			}
		}

		if ((result == 0) && (intType == 0)) {
			if (name.getText().toString().equals(""))
				JOptionPane.showMessageDialog(null,
						"آئٹم کا نام نہیں درج کیا گیا !");
			else {
//				javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
				JOptionPane.showMessageDialog(null,
						"آئٹم خالی ہے، آپریشن معطل !");
			}
		}
		refreshTable();
		refreshSalesPoint();
	}
	
	/*-----------------Get All Super Items From Database-----------------*/
	
	public SuperItem[] getAllSuperItems(Connection connection) {
		SuperItem[] superItems = null;
		try {
			String query = "Select * from SuperItemsInfo";
			PreparedStatement pst = connection.prepareStatement(query);
			ResultSet rs = pst.executeQuery();
			int superItemsCount = 0;
			while(rs.next()) {
				superItemsCount++;
			}
			superItems = new SuperItem[superItemsCount];
			//System.out.println("All Super Items Count: " + superItemsCount);
			rs.close();
			rs = pst.executeQuery();
			int superItemsCounter = 0;
			while(rs.next()) {
				superItems[superItemsCounter] = new SuperItem();
				superItems[superItemsCounter].setId(rs.getInt("EID"));
				superItems[superItemsCounter].setName(rs.getString("Name"));
				superItems[superItemsCounter].setElements(rs.getInt("Elements"));
				superItemsCounter++;
			}
			pst.close();
			rs.close();
		} catch(Exception e) {
			e.printStackTrace();
		}
		return superItems;
	}

	/*-----------------------------Update Item---------------------------*/
	public void updateItem() {
		int row = tblItemsInfo.getSelectedRow();
		if (tblItemsInfo.isRowSelected(row)) {
			int eid = Integer.valueOf((tblItemsInfo.getModel().getValueAt(row,
					0)).toString());
			Item updateItem = new Item();
			updateItem.setId(eid);
			updateItem.getItemFromDb(connection);
			
			JCheckBox specialRates = new JCheckBox();
			JCheckBox weight = new JCheckBox();
			JCheckBox gattu = new JCheckBox();
			JCheckBox box = new JCheckBox();
			JCheckBox bundle = new JCheckBox();
			JCheckBox packet = new JCheckBox();
			JCheckBox carton = new JCheckBox();
			JCheckBox bucket = new JCheckBox();
			JCheckBox dozen = new JCheckBox();
			JCheckBox bottle = new JCheckBox();
			JCheckBox tin = new JCheckBox();
			JCheckBox gallon = new JCheckBox();

			AbstractDocument doc = null;
			JTextField kgPrice = new JTextField(5);
			doc = (AbstractDocument) kgPrice.getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			kgPrice.setText(updateItem.getPriceKg() + "");
			kgPrice.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					kgPrice.selectAll();
				}
			});
			JTextField gattuPrice = new JTextField(5);
			doc = (AbstractDocument) gattuPrice.getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			gattuPrice.setText(updateItem.getPriceGattu() + "");
			gattuPrice.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					gattuPrice.selectAll();
				}
			});
			JTextField boxPrice = new JTextField(5);
			doc = (AbstractDocument) boxPrice.getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			boxPrice.setText(updateItem.getPriceBox() + "");
			boxPrice.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					boxPrice.selectAll();
				}
			});
			JTextField bundlePrice = new JTextField(5);
			doc = (AbstractDocument) bundlePrice.getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			bundlePrice.setText(updateItem.getPriceBundle() + "");
			bundlePrice.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					bundlePrice.selectAll();
				}
			});

			JTextField kgStock = new JTextField(5);
			doc = (AbstractDocument) kgStock.getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			kgStock.setText(updateItem.getStockKg() + "");
			kgStock.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					kgStock.selectAll();
				}
			});
			JTextField gattuStock = new JTextField(5);
			doc = (AbstractDocument) gattuStock.getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			gattuStock.setText(updateItem.getStockGattu() + "");
			gattuStock.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					gattuStock.selectAll();
				}
			});
			JTextField boxStock = new JTextField(5);
			doc = (AbstractDocument) boxStock.getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			boxStock.setText(updateItem.getStockBox() + "");
			boxStock.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					boxStock.selectAll();
				}
			});
			JTextField bundleStock = new JTextField(5);
			doc = (AbstractDocument) bundleStock.getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			bundleStock.setText(updateItem.getStockBundle() + "");
			bundleStock.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					bundleStock.selectAll();
				}
			});

			JTextField kgSPrice = new JTextField(5);
			doc = (AbstractDocument) kgSPrice.getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			kgSPrice.setText(updateItem.getSpriceKg() + "");
			kgSPrice.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					kgSPrice.selectAll();
				}
			});

			JTextField gattuSPrice = new JTextField(5);
			doc = (AbstractDocument) gattuSPrice.getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			gattuSPrice.setText(updateItem.getSpriceGattu() + "");
			gattuSPrice.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					gattuSPrice.selectAll();
				}
			});

			JTextField boxSPrice = new JTextField(5);
			doc = (AbstractDocument) boxSPrice.getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			boxSPrice.setText(updateItem.getSpriceBox() + "");
			boxSPrice.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					boxSPrice.selectAll();
				}
			});

			JTextField bundleSPrice = new JTextField(5);
			doc = (AbstractDocument) bundleSPrice.getDocument();
			doc.setDocumentFilter(new DoubleFilter());
			bundleSPrice.setText(updateItem.getSpriceBundle() + "");
			bundleSPrice.addFocusListener(new FocusListener() {
				@Override
				public void focusLost(final FocusEvent pE) {
				}

				@Override
				public void focusGained(final FocusEvent pE) {
					bundleSPrice.selectAll();
				}
			});

			JLabel[] packetName = new JLabel[5];
			JLabel[] cartonName = new JLabel[5];
			JLabel[] bucketName = new JLabel[5];
			JLabel[] dozenName = new JLabel[5];
			JLabel[] bottleName = new JLabel[5];
			JLabel[] tinName = new JLabel[5];
			JLabel[] gallonName = new JLabel[5];

			JTextField[] packetPrice = new JTextField[5];
			JTextField[] cartonPrice = new JTextField[5];
			JTextField[] bucketPrice = new JTextField[5];
			JTextField[] dozenPrice = new JTextField[5];
			JTextField[] bottlePrice = new JTextField[5];
			JTextField[] tinPrice = new JTextField[5];
			JTextField[] gallonPrice = new JTextField[5];

			JTextField[] packetSPrice = new JTextField[5];
			JTextField[] cartonSPrice = new JTextField[5];
			JTextField[] bucketSPrice = new JTextField[5];
			JTextField[] dozenSPrice = new JTextField[5];
			JTextField[] bottleSPrice = new JTextField[5];
			JTextField[] tinSPrice = new JTextField[5];
			JTextField[] gallonSPrice = new JTextField[5];

			JTextField[] packetStock = new JTextField[5];
			JTextField[] cartonStock = new JTextField[5];
			JTextField[] bucketStock = new JTextField[5];
			JTextField[] dozenStock = new JTextField[5];
			JTextField[] bottleStock = new JTextField[5];
			JTextField[] tinStock = new JTextField[5];
			JTextField[] gallonStock = new JTextField[5];

			for (int i = 0; i < 5; i++) {
				packetName[i] = new JLabel();
				packetName[i].setText(updateItem.getPricePacket()
						.getSizeName(i));

				cartonName[i] = new JLabel();
				cartonName[i].setText(updateItem.getPriceCarton()
						.getSizeName(i));

				bucketName[i] = new JLabel();
				bucketName[i].setText(updateItem.getPriceBucket()
						.getSizeName(i));

				dozenName[i] = new JLabel();
				dozenName[i].setText(updateItem.getPriceDozen().getSizeName(i));

				bottleName[i] = new JLabel();
				bottleName[i].setText(updateItem.getPriceBottle()
						.getSizeName(i));

				tinName[i] = new JLabel();
				tinName[i].setText(updateItem.getPriceTin().getSizeName(i));

				gallonName[i] = new JLabel();
				gallonName[i].setText(updateItem.getPriceGallon()
						.getSizeName(i));

				int j = i;

				packetPrice[i] = new JTextField(5);
				doc = (AbstractDocument) packetPrice[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				packetPrice[i].setText(updateItem.getPricePacket()
						.getSizePrice(i) + "");
				packetPrice[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						packetPrice[j].selectAll();
					}
				});

				cartonPrice[i] = new JTextField(5);
				doc = (AbstractDocument) cartonPrice[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				cartonPrice[i].setText(updateItem.getPriceCarton()
						.getSizePrice(i) + "");
				cartonPrice[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						cartonPrice[j].selectAll();
					}
				});

				bucketPrice[i] = new JTextField(5);
				doc = (AbstractDocument) bucketPrice[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				bucketPrice[i].setText(updateItem.getPriceBucket()
						.getSizePrice(i) + "");
				bucketPrice[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						bucketPrice[j].selectAll();
					}
				});

				dozenPrice[i] = new JTextField(5);
				doc = (AbstractDocument) dozenPrice[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				dozenPrice[i].setText(updateItem.getPriceDozen()
						.getSizePrice(i) + "");
				dozenPrice[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						dozenPrice[j].selectAll();
					}
				});

				bottlePrice[i] = new JTextField(5);
				doc = (AbstractDocument) bottlePrice[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				bottlePrice[i].setText(updateItem.getPriceBottle()
						.getSizePrice(i) + "");
				bottlePrice[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						bottlePrice[j].selectAll();
					}
				});

				tinPrice[i] = new JTextField(5);
				doc = (AbstractDocument) tinPrice[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				tinPrice[i].setText(updateItem.getPriceTin().getSizePrice(i)
						+ "");
				tinPrice[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						tinPrice[j].selectAll();
					}
				});

				gallonPrice[i] = new JTextField(5);
				doc = (AbstractDocument) gallonPrice[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				gallonPrice[i].setText(updateItem.getPriceGallon()
						.getSizePrice(i) + "");
				gallonPrice[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						gallonPrice[j].selectAll();
					}
				});

				packetSPrice[i] = new JTextField(5);
				doc = (AbstractDocument) packetSPrice[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				packetSPrice[i].setText(updateItem.getPricePacket().getSizeSPrice(i) + "");
				packetSPrice[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						packetSPrice[j].selectAll();
					}
				});

				cartonSPrice[i] = new JTextField(5);
				doc = (AbstractDocument) cartonSPrice[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				cartonSPrice[i].setText(updateItem.getPriceCarton().getSizeSPrice(i) + "");
				cartonSPrice[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						cartonSPrice[j].selectAll();
					}
				});

				bucketSPrice[i] = new JTextField(5);
				doc = (AbstractDocument) bucketSPrice[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				bucketSPrice[i].setText(updateItem.getPriceBucket().getSizeSPrice(i) + "");
				bucketSPrice[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						bucketSPrice[j].selectAll();
					}
				});

				dozenSPrice[i] = new JTextField(5);
				doc = (AbstractDocument) dozenSPrice[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				dozenSPrice[i].setText(updateItem.getPriceDozen().getSizeSPrice(i) + "");
				dozenSPrice[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						dozenSPrice[j].selectAll();
					}
				});

				bottleSPrice[i] = new JTextField(5);
				doc = (AbstractDocument) bottleSPrice[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				bottleSPrice[i].setText(updateItem.getPriceBottle().getSizeSPrice(i) + "");
				bottleSPrice[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						bottleSPrice[j].selectAll();
					}
				});

				tinSPrice[i] = new JTextField(5);
				doc = (AbstractDocument) tinSPrice[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				tinSPrice[i].setText(updateItem.getPriceTin().getSizeSPrice(i) + "");
				tinSPrice[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						tinSPrice[j].selectAll();
					}
				});

				gallonSPrice[i] = new JTextField(5);
				doc = (AbstractDocument) gallonSPrice[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				gallonSPrice[i].setText(updateItem.getPriceGallon().getSizeSPrice(i) + "");
				gallonSPrice[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						gallonSPrice[j].selectAll();
					}
				});

				packetStock[i] = new JTextField(5);
				doc = (AbstractDocument) packetStock[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				packetStock[i].setText(updateItem.getPricePacket()
						.getSizeStock(i) + "");
				packetStock[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						packetStock[j].selectAll();
					}
				});

				cartonStock[i] = new JTextField(5);
				doc = (AbstractDocument) cartonStock[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				cartonStock[i].setText(updateItem.getPriceCarton()
						.getSizeStock(i) + "");
				cartonStock[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						cartonStock[j].selectAll();
					}
				});

				bucketStock[i] = new JTextField(5);
				doc = (AbstractDocument) bucketStock[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				bucketStock[i].setText(updateItem.getPriceBucket()
						.getSizeStock(i) + "");
				bucketStock[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						bucketStock[j].selectAll();
					}
				});

				dozenStock[i] = new JTextField(5);
				doc = (AbstractDocument) dozenStock[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				dozenStock[i].setText(updateItem.getPriceDozen()
						.getSizeStock(i) + "");
				dozenStock[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						dozenStock[j].selectAll();
					}
				});

				bottleStock[i] = new JTextField(5);
				doc = (AbstractDocument) bottleStock[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				bottleStock[i].setText(updateItem.getPriceBottle()
						.getSizeStock(i) + "");
				bottleStock[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						bottleStock[j].selectAll();
					}
				});

				tinStock[i] = new JTextField(5);
				doc = (AbstractDocument) tinStock[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				tinStock[i].setText(updateItem.getPriceTin().getSizeStock(i)
						+ "");
				tinStock[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						tinStock[j].selectAll();
					}
				});

				gallonStock[i] = new JTextField(5);
				doc = (AbstractDocument) gallonStock[i].getDocument();
				doc.setDocumentFilter(new DoubleFilter());
				gallonStock[i].setText(updateItem.getPriceGallon()
						.getSizeStock(i) + "");
				gallonStock[i].addFocusListener(new FocusListener() {
					@Override
					public void focusLost(final FocusEvent pE) {
					}

					@Override
					public void focusGained(final FocusEvent pE) {
						gallonStock[j].selectAll();
					}
				});

			}

			JPanel myPanel = new JPanel(new GridBagLayout());
			gridBagConstraints.insets = new Insets(4, 4, 4, 4);
			setGBagConst(0, 0, 1, 1);
			myPanel.add(new JLabel(updateItem.getName()), gridBagConstraints);

			if (updateItem.isHasSpecial()) {
				setGBagConst(0, 1, 1, 1);
				myPanel.add(new JLabel("سپیشل ریٹ"), gridBagConstraints);
				setGBagConst(1, 1, 1, 1);
				myPanel.add(specialRates, gridBagConstraints);
			}

			setGBagConst(0, 2, 1, 1);
			myPanel.add(new JLabel(": آ‏ئٹم کی قسمیں منتخب کریں"),
					gridBagConstraints);

			String type = updateItem.getType();

			if (type.charAt(10) == '1') {
				setGBagConst(0, 3, 1, 1);
				myPanel.add(new JLabel("وزن"), gridBagConstraints);
				setGBagConst(1, 3, 1, 1);
				myPanel.add(weight, gridBagConstraints);
			}

			if (type.charAt(9) == '1') {
				setGBagConst(0, 4, 1, 1);
				myPanel.add(new JLabel("گٹو"), gridBagConstraints);
				setGBagConst(1, 4, 1, 1);
				myPanel.add(gattu, gridBagConstraints);
			}

			if (type.charAt(8) == '1') {
				setGBagConst(0, 5, 1, 1);
				myPanel.add(new JLabel("ڈبہ"), gridBagConstraints);
				setGBagConst(1, 5, 1, 1);
				myPanel.add(box, gridBagConstraints);
			}

			if (type.charAt(7) == '1') {
				setGBagConst(0, 6, 1, 1);
				myPanel.add(new JLabel("بنڈل"), gridBagConstraints);
				setGBagConst(1, 6, 1, 1);
				myPanel.add(bundle, gridBagConstraints);
			}

			if (type.charAt(6) == '1') {
				setGBagConst(0, 7, 1, 1);
				myPanel.add(new JLabel("پیکٹ"), gridBagConstraints);
				setGBagConst(1, 7, 1, 1);
				myPanel.add(packet, gridBagConstraints);
			}

			if (type.charAt(5) == '1') {
				setGBagConst(0, 8, 1, 1);
				myPanel.add(new JLabel("کارٹن"), gridBagConstraints);
				setGBagConst(1, 8, 1, 1);
				myPanel.add(carton, gridBagConstraints);
			}

			if (type.charAt(4) == '1') {
				setGBagConst(0, 9, 1, 1);
				myPanel.add(new JLabel("بالٹی"), gridBagConstraints);
				setGBagConst(1, 9, 1, 1);
				myPanel.add(bucket, gridBagConstraints);
			}

			if (type.charAt(3) == '1') {
				setGBagConst(0, 10, 1, 1);
				myPanel.add(new JLabel("درجن"), gridBagConstraints);
				setGBagConst(1, 10, 1, 1);
				myPanel.add(dozen, gridBagConstraints);
			}

			if (type.charAt(2) == '1') {
				setGBagConst(0, 11, 1, 1);
				myPanel.add(new JLabel("بوتل"), gridBagConstraints);
				setGBagConst(1, 11, 1, 1);
				myPanel.add(bottle, gridBagConstraints);
			}

			if (type.charAt(1) == '1') {
				setGBagConst(0, 12, 1, 1);
				myPanel.add(new JLabel("ٹین"), gridBagConstraints);
				setGBagConst(1, 12, 1, 1);
				myPanel.add(tin, gridBagConstraints);
			}

			if (type.charAt(0) == '1') {
				setGBagConst(0, 13, 1, 1);
				myPanel.add(new JLabel("گیلن"), gridBagConstraints);
				setGBagConst(1, 13, 1, 1);
				myPanel.add(gallon, gridBagConstraints);
			}

//			javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
			int result = JOptionPane.showConfirmDialog(null, myPanel,
					": اپڈیٹ کرنے کیلیے اقسام منتخب کریں",
					JOptionPane.OK_CANCEL_OPTION);

			type = "";
			if (gallon.isSelected())
				type = "1";
			else
				type = "0";
			if (tin.isSelected())
				type = type + "1";
			else
				type = type + "0";
			if (bottle.isSelected())
				type = type + "1";
			else
				type = type + "0";
			if (dozen.isSelected())
				type = type + "1";
			else
				type = type + "0";
			if (bucket.isSelected())
				type = type + "1";
			else
				type = type + "0";
			if (carton.isSelected())
				type = type + "1";
			else
				type = type + "0";
			if (packet.isSelected())
				type = type + "1";
			else
				type = type + "0";
			if (bundle.isSelected())
				type = type + "1";
			else
				type = type + "0";
			if (box.isSelected())
				type = type + "1";
			else
				type = type + "0";
			if (gattu.isSelected())
				type = type + "1";
			else
				type = type + "0";
			if (weight.isSelected())
				type = type + "1";
			else
				type = type + "0";

			int intType = Integer.parseInt(type, 2);

			if ((result == JOptionPane.OK_OPTION) && intType > 0) {

				myPanel.removeAll();
				int gridY = 0;

				if (type.charAt(10) == '1') {
					setGBagConst(0, gridY, 1, 1);
					myPanel.add(new JLabel("کلو کی قیمت"), gridBagConstraints);
					setGBagConst(1, gridY, 1, 1);
					myPanel.add(kgPrice, gridBagConstraints);
					if(stockStatus) {
						setGBagConst(2, gridY, 1, 1);
						myPanel.add(new JLabel("اسٹاک"), gridBagConstraints);
						setGBagConst(3, gridY, 1, 1);
						myPanel.add(kgStock, gridBagConstraints);
						setGBagConst(4, gridY, 1, 1);
						myPanel.add(new JLabel(updateItem.getRelationKg()),
								gridBagConstraints);
					}
					gridY++;
				}

				if (type.charAt(9) == '1') {
					setGBagConst(0, gridY, 1, 1);
					myPanel.add(new JLabel("گٹو کی قیمت"), gridBagConstraints);
					setGBagConst(1, gridY, 1, 1);
					myPanel.add(gattuPrice, gridBagConstraints);
					if(stockStatus) {
						setGBagConst(2, gridY, 1, 1);
						myPanel.add(new JLabel("اسٹاک"), gridBagConstraints);
						setGBagConst(3, gridY, 1, 1);
						myPanel.add(gattuStock, gridBagConstraints);
					}
					gridY++;
				}

				if (type.charAt(8) == '1') {
					setGBagConst(0, gridY, 1, 1);
					myPanel.add(new JLabel("ڈبے کی قیمت"), gridBagConstraints);
					setGBagConst(1, gridY, 1, 1);
					myPanel.add(boxPrice, gridBagConstraints);
					setGBagConst(2, gridY, 1, 1);
					if(stockStatus) {
						myPanel.add(new JLabel("اسٹاک"), gridBagConstraints);
						setGBagConst(3, gridY, 1, 1);
						myPanel.add(boxStock, gridBagConstraints);
						setGBagConst(4, gridY, 1, 1);
						myPanel.add(new JLabel(updateItem.getRelationBox()),
								gridBagConstraints);
					}
					gridY++;
				}

				if (type.charAt(7) == '1') {
					setGBagConst(0, gridY, 1, 1);
					myPanel.add(new JLabel("بنڈل کی قیمت"), gridBagConstraints);
					setGBagConst(1, gridY, 1, 1);
					myPanel.add(bundlePrice, gridBagConstraints);
					if(stockStatus) {
						setGBagConst(2, gridY, 1, 1);
						myPanel.add(new JLabel("اسٹاک"), gridBagConstraints);
						setGBagConst(3, gridY, 1, 1);
						myPanel.add(bundleStock, gridBagConstraints);
					}
					gridY++;
				}

				if (intType > Integer.parseInt("00000001111", 2)) {

					int countSized = 0;
					for (int i = 0; i < 7; i++) {
						if (type.charAt(i) == '1')
							countSized++;
					}

					gridBagConstraints.insets = new Insets(30, 4, 4, 4);
					setGBagConst(1, gridY, 1, 1);
					myPanel.add(new JLabel("قیمت"), gridBagConstraints);
					if(stockStatus) {
						setGBagConst(2, gridY, 1, 1);
						myPanel.add(new JLabel("اسٹاک"), gridBagConstraints);
					}

					if (countSized >= 2) {
						setGBagConst(5, gridY, 1, 1);
						myPanel.add(new JLabel("قیمت"), gridBagConstraints);
						if(stockStatus) {
							setGBagConst(6, gridY, 1, 1);
							myPanel.add(new JLabel("اسٹاک"), gridBagConstraints);
						}
					}
					if (countSized > 2) {

						setGBagConst(9, gridY, 1, 1);
						myPanel.add(new JLabel("قیمت"), gridBagConstraints);
						if(stockStatus) {
							setGBagConst(10, gridY, 1, 1);
							myPanel.add(new JLabel("اسٹاک"), gridBagConstraints);
						}
					}
					if (countSized > 3) {
						setGBagConst(13, gridY, 1, 1);
						myPanel.add(new JLabel("قیمت"), gridBagConstraints);
						if(stockStatus) {
							setGBagConst(14, gridY, 1, 1);
							myPanel.add(new JLabel("اسٹاک"), gridBagConstraints);
						}
					}

					gridY++;

					if (type.charAt(6) == '1') {
						gridBagConstraints.insets = new Insets(10, 4, 4, 4);
						setGBagConst(0, gridY, 1, 1);
						myPanel.add(new JLabel("پیکٹ"), gridBagConstraints);
						gridY++;
						gridBagConstraints.insets = new Insets(4, 4, 4, 4);
						for (int i = 0; i < 5; i++) {
							if (updateItem.getPricePacket().getSizePrice(i) > 0) {
								setGBagConst(0, gridY, 1, 1);
								myPanel.add(packetName[i], gridBagConstraints);
								setGBagConst(1, gridY, 1, 1);
								myPanel.add(packetPrice[i], gridBagConstraints);
								if(stockStatus) {
									setGBagConst(2, gridY, 1, 1);
									myPanel.add(packetStock[i], gridBagConstraints);
									setGBagConst(3, gridY, 1, 1);
									myPanel.add(new JLabel(updateItem
											.getPricePacket().getSizeRelation(i)),
											gridBagConstraints);
								}
							}
							gridY++;
						}
					}

					if (type.charAt(5) == '1') {
						int gridX = 0;
						boolean isEven = (type.charAt(6) == '1') ? true : false;
						if (isEven) {
							gridY -= 6;
							gridX = 4;
							gridBagConstraints.insets = new Insets(10, 30, 4, 4);
						} else
							gridBagConstraints.insets = new Insets(10, 4, 4, 4);
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(new JLabel("کارٹن"), gridBagConstraints);
						gridY++;
						gridBagConstraints.insets = new Insets(4, 4, 4, 4);
						for (int i = 0; i < 5; i++) {
							if (updateItem.getPriceCarton().getSizePrice(i) > 0) {
								if (isEven) {
									gridBagConstraints.insets = new Insets(4,
											30, 4, 4);
								}
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(cartonName[i], gridBagConstraints);
								gridX++;
								if (isEven) {
									gridBagConstraints.insets = new Insets(4,
											4, 4, 4);
								}
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(cartonPrice[i], gridBagConstraints);
								gridX++;
								if(stockStatus) {
									setGBagConst(gridX, gridY, 1, 1);
									myPanel.add(cartonStock[i], gridBagConstraints);
								}
								if (isEven)
									gridX = 4;
								else
									gridX = 0;
							}
							gridY++;

						}
					}

					if (type.charAt(4) == '1') {
						int gridX = 0;
						int dispId = 0;
						for (int i = 6; i >= 5; i--) {
							if (type.charAt(i) == '1')
								dispId++;
						}
						if (dispId > 0) {
							gridX = dispId * 4;
							gridY -= 6;
							gridBagConstraints.insets = new Insets(10, 30, 4, 4);
						} else
							gridBagConstraints.insets = new Insets(10, 4, 4, 4);
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(new JLabel("بالٹی"), gridBagConstraints);
						gridY++;
						gridBagConstraints.insets = new Insets(4, 4, 4, 4);

						for (int i = 0; i < 5; i++) {
							if (updateItem.getPriceBucket().getSizePrice(i) > 0) {
								if (dispId > 0) {
									gridBagConstraints.insets = new Insets(4,
											30, 4, 4);
								}
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(bucketName[i], gridBagConstraints);
								gridX++;
								if (dispId > 0) {
									gridBagConstraints.insets = new Insets(4,
											4, 4, 4);
								}
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(bucketPrice[i], gridBagConstraints);
								gridX++;
								if(stockStatus) {
									setGBagConst(gridX, gridY, 1, 1);
									myPanel.add(bucketStock[i], gridBagConstraints);
								}
								
								if (dispId == 1)
									gridX = 4;
								else if (dispId == 2)
									gridX = 8;
								else
									gridX = 0;
							}
							gridY++;
						}
					}

					if (type.charAt(3) == '1') {
						int gridX = 0;
						int dispId = 0;
						for (int i = 6; i >= 4; i--) {
							if (type.charAt(i) == '1')
								dispId++;
						}
						if (dispId > 0) {
							gridY -= 6;
							gridX = dispId * 4;
							gridBagConstraints.insets = new Insets(10, 30, 4, 4);
						} else
							gridBagConstraints.insets = new Insets(10, 4, 4, 4);
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(new JLabel("درجن"), gridBagConstraints);
						gridY++;
						gridBagConstraints.insets = new Insets(4, 4, 4, 4);

						for (int i = 0; i < 5; i++) {
							if (updateItem.getPriceDozen().getSizePrice(i) > 0) {
								if (dispId > 0) {
									gridBagConstraints.insets = new Insets(4,
											30, 4, 4);
								}
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(dozenName[i], gridBagConstraints);
								gridX++;
								if (dispId > 0) {
									gridBagConstraints.insets = new Insets(4,
											4, 4, 4);
								}
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(dozenPrice[i], gridBagConstraints);
								gridX++;
								if(stockStatus) {
									setGBagConst(gridX, gridY, 1, 1);
									myPanel.add(dozenStock[i], gridBagConstraints);
									gridX++;
									setGBagConst(gridX, gridY, 1, 1);
									myPanel.add(new JLabel(updateItem
											.getPriceDozen().getSizeRelation(i)),
											gridBagConstraints);
								}

								gridX = dispId * 4;
							}
							gridY++;
						}
					}

					if (type.charAt(2) == '1') {
						int gridX = 0;
						int dispId = 0;
						for (int i = 6; i >= 3; i--) {
							if (type.charAt(i) == '1')
								dispId++;
						}
						if (dispId > 0) {
							if (dispId != 4)
								gridY -= 6;
							if (dispId > 3) {
								dispId -= 4;
							}
							gridX = dispId * 4;
							gridBagConstraints.insets = new Insets(10, 30, 4, 4);
						} else
							gridBagConstraints.insets = new Insets(10, 4, 4, 4);
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(new JLabel("بوتل"), gridBagConstraints);
						gridY++;
						gridBagConstraints.insets = new Insets(4, 4, 4, 4);

						for (int i = 0; i < 5; i++) {
							if (updateItem.getPriceBottle().getSizePrice(i) > 0) {
								if (dispId > 0) {
									gridBagConstraints.insets = new Insets(4,
											30, 4, 4);
								}
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(bottleName[i], gridBagConstraints);
								gridX++;
								if (dispId > 0) {
									gridBagConstraints.insets = new Insets(4,
											4, 4, 4);
								}
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(bottlePrice[i], gridBagConstraints);
								gridX++;
								if(stockStatus) {
									setGBagConst(gridX, gridY, 1, 1);
									myPanel.add(bottleStock[i], gridBagConstraints);
								}
								
								gridX = dispId * 4;
							}
							gridY++;
						}
					}

					if (type.charAt(1) == '1') {
						int gridX = 0;
						int dispId = 0;
						for (int i = 6; i >= 2; i--) {
							if (type.charAt(i) == '1')
								dispId++;
						}
						if (dispId > 0) {
							if (dispId > 0) {
								if (dispId != 4)
									gridY -= 6;
								if (dispId > 3) {
									dispId -= 4;
								}
								gridX = dispId * 4;
							}
							gridBagConstraints.insets = new Insets(10, 30, 4, 4);
						} else
							gridBagConstraints.insets = new Insets(10, 4, 4, 4);
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(new JLabel("ٹین"), gridBagConstraints);
						gridY++;
						gridBagConstraints.insets = new Insets(4, 4, 4, 4);

						for (int i = 0; i < 5; i++) {
							if (updateItem.getPriceTin().getSizePrice(i) > 0) {
								if (dispId > 0) {
									gridBagConstraints.insets = new Insets(4,
											30, 4, 4);
								}
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(tinName[i], gridBagConstraints);
								gridX++;
								if (dispId > 0) {
									gridBagConstraints.insets = new Insets(4,
											4, 4, 4);
								}
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(tinPrice[i], gridBagConstraints);
								gridX++;
								if(stockStatus) {
									setGBagConst(gridX, gridY, 1, 1);
									myPanel.add(tinStock[i], gridBagConstraints);
								}
								
								gridX = dispId * 4;
							}
							gridY++;
						}
					}

					if (type.charAt(0) == '1') {
						int gridX = 0;
						int dispId = 0;
						for (int i = 6; i >= 1; i--) {
							if (type.charAt(i) == '1')
								dispId++;
						}
						if (dispId > 0) {
							if (dispId > 0) {
								if (dispId != 4)
									gridY -= 6;
								if (dispId > 3) {
									dispId -= 4;
								}
								gridX = dispId * 4;
							}
							gridBagConstraints.insets = new Insets(10, 30, 4, 4);
						} else
							gridBagConstraints.insets = new Insets(10, 4, 4, 4);
						setGBagConst(gridX, gridY, 1, 1);
						myPanel.add(new JLabel("گیلن"), gridBagConstraints);
						gridY++;
						gridBagConstraints.insets = new Insets(4, 4, 4, 4);

						for (int i = 0; i < 5; i++) {
							if (updateItem.getPriceGallon().getSizePrice(i) > 0) {
								if (dispId > 0) {
									gridBagConstraints.insets = new Insets(4,
											30, 4, 4);
								}
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(gallonName[i], gridBagConstraints);
								gridX++;
								if (dispId > 0) {
									gridBagConstraints.insets = new Insets(4,
											4, 4, 4);
								}
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(gallonPrice[i], gridBagConstraints);
								gridX++;
								if(stockStatus) {
									setGBagConst(gridX, gridY, 1, 1);
									myPanel.add(gallonStock[i], gridBagConstraints);
								}
								
								gridX = dispId * 4;
							}
							gridY++;
						}
					}

				}

//				javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
				int result_1 = JOptionPane.showConfirmDialog(null, myPanel,
						"نئے ریٹ درج کریں :", JOptionPane.OK_CANCEL_OPTION);
				if (result_1 == JOptionPane.OK_OPTION) {

					double newPriceKg = Double.valueOf(kgPrice.getText());
					double newStockKg = Double.valueOf(kgStock.getText());
					double newPriceGattu = Double.valueOf(gattuPrice.getText());
					double newStockGattu = Double.valueOf(gattuStock.getText());
					double newPriceBundle = Double.valueOf(bundlePrice
							.getText());
					double newStockBundle = Double.valueOf(bundleStock
							.getText());
					double newPriceBox = Double.valueOf(boxPrice.getText());
					double newStockBox = Double.valueOf(boxStock.getText());

					String[] newPacketName = new String[5];
					double[] newPacketPrice = new double[5];
					double[] newPacketStock = new double[5];
					String[] newPacketRelation = new String[5];
					String[] newCartonName = new String[5];
					double[] newCartonPrice = new double[5];
					double[] newCartonStock = new double[5];
					String[] newCartonRelation = new String[5];
					String[] newBucketName = new String[5];
					double[] newBucketPrice = new double[5];
					double[] newBucketStock = new double[5];
					String[] newBucketRelation = new String[5];
					String[] newDozenName = new String[5];
					double[] newDozenPrice = new double[5];
					double[] newDozenStock = new double[5];
					String[] newDozenRelation = new String[5];
					String[] newBottleName = new String[5];
					double[] newBottlePrice = new double[5];
					double[] newBottleStock = new double[5];
					String[] newBottleRelation = new String[5];
					String[] newTinName = new String[5];
					double[] newTinPrice = new double[5];
					double[] newTinStock = new double[5];
					String[] newTinRelation = new String[5];
					String[] newGallonName = new String[5];
					double[] newGallonPrice = new double[5];
					double[] newGallonStock = new double[5];
					String[] newGallonRelation = new String[5];

					for (int i = 0; i < 5; i++) {
						newPacketName[i] = packetName[i].getText();
						newPacketPrice[i] = Double.valueOf(packetPrice[i]
								.getText());
						newPacketStock[i] = Double.valueOf(packetStock[i]
								.getText());
						newPacketRelation[i] = updateItem.getPricePacket()
								.getSizeRelation(i);
						newCartonName[i] = cartonName[i].getText();
						newCartonPrice[i] = Double.valueOf(cartonPrice[i]
								.getText());
						newCartonStock[i] = Double.valueOf(cartonStock[i]
								.getText());
						newCartonRelation[i] = updateItem.getPriceCarton()
								.getSizeRelation(i);
						newBucketName[i] = bucketName[i].getText();
						newBucketPrice[i] = Double.valueOf(bucketPrice[i]
								.getText());
						newBucketStock[i] = Double.valueOf(bucketStock[i]
								.getText());
						newBucketRelation[i] = updateItem.getPriceBucket()
								.getSizeRelation(i);
						newDozenName[i] = dozenName[i].getText();
						newDozenPrice[i] = Double.valueOf(dozenPrice[i]
								.getText());
						newDozenStock[i] = Double.valueOf(dozenStock[i]
								.getText());
						newDozenRelation[i] = updateItem.getPriceDozen()
								.getSizeRelation(i);
						newBottleName[i] = bottleName[i].getText();
						newBottlePrice[i] = Double.valueOf(bottlePrice[i]
								.getText());
						newBottleStock[i] = Double.valueOf(bottleStock[i]
								.getText());
						newBottleRelation[i] = updateItem.getPriceBottle()
								.getSizeRelation(i);
						newTinName[i] = tinName[i].getText();
						newTinPrice[i] = Double.valueOf(tinPrice[i].getText());
						newTinStock[i] = Double.valueOf(tinStock[i].getText());
						newTinRelation[i] = updateItem.getPriceTin()
								.getSizeRelation(i);
						newGallonName[i] = gallonName[i].getText();
						newGallonPrice[i] = Double.valueOf(gallonPrice[i]
								.getText());
						newGallonStock[i] = Double.valueOf(gallonStock[i]
								.getText());
						newGallonRelation[i] = updateItem.getPriceGallon()
								.getSizeRelation(i);
					}

					SizedItem newPacket = new SizedItem(newPacketName,
							newPacketPrice, newPacketStock, newPacketRelation);
					SizedItem newCarton = new SizedItem(newCartonName,
							newCartonPrice, newCartonStock, newCartonRelation);
					SizedItem newBucket = new SizedItem(newBucketName,
							newBucketPrice, newBucketStock, newBucketRelation);
					SizedItem newDozen = new SizedItem(newDozenName,
							newDozenPrice, newDozenStock, newDozenRelation);
					SizedItem newBottle = new SizedItem(newBottleName,
							newBottlePrice, newBottleStock, newBottleRelation);
					SizedItem newTin = new SizedItem(newTinName, newTinPrice,
							newTinStock, newTinRelation);
					SizedItem newGallon = new SizedItem(newGallonName,
							newGallonPrice, newGallonStock, newGallonRelation);

					Item newItem = new Item(type, updateItem.getName(), updateItem.getSuperItem(),
							newPriceKg, newStockKg, updateItem.getRelationKg(),
							newPriceGattu, newStockGattu,
							updateItem.getRelationGattu(), newPriceBundle,
							newStockBundle, updateItem.getRelationBundle(),
							newPriceBox, newStockBox,
							updateItem.getRelationBox(), newPacket, newCarton,
							newBucket, newDozen, newBottle, newTin, newGallon,
							specialRates.isSelected());
					newItem.updateThisItem(connection, eid);

					if ((result == JOptionPane.OK_OPTION) && (intType > 0)
							&& specialRates.isSelected()) {
						myPanel.removeAll();

						gridY = 0;

						if (weight.isSelected()) {
							setGBagConst(0, gridY, 1, 1);
							myPanel.add(new JLabel("کلو کی خاص قیمت"),
									gridBagConstraints);
							setGBagConst(1, gridY, 1, 1);
							myPanel.add(kgSPrice, gridBagConstraints);
							gridY++;
						}

						if (gattu.isSelected()) {
							setGBagConst(0, gridY, 1, 1);
							myPanel.add(new JLabel("گٹو کی خاص قیمت"),
									gridBagConstraints);
							setGBagConst(1, gridY, 1, 1);
							myPanel.add(gattuSPrice, gridBagConstraints);
							gridY++;
						}

						if (box.isSelected()) {
							setGBagConst(0, gridY, 1, 1);
							myPanel.add(new JLabel("ڈبے کی خاص قیمت"),
									gridBagConstraints);
							setGBagConst(1, gridY, 1, 1);
							myPanel.add(boxSPrice, gridBagConstraints);
							gridY++;
						}

						if (bundle.isSelected()) {
							setGBagConst(0, gridY, 1, 1);
							myPanel.add(new JLabel("بنڈل کی خاص قیمت"),
									gridBagConstraints);
							setGBagConst(1, gridY, 1, 1);
							myPanel.add(bundleSPrice, gridBagConstraints);
							gridY++;
						}

						if (intType > Integer.parseInt("00000001111", 2)) {

							gridY++;

							if (packet.isSelected()) {
								gridBagConstraints.insets = new Insets(30, 4,
										4, 4);
								setGBagConst(0, gridY, 1, 1);
								myPanel.add(new JLabel("پیکٹ کی خاص قیمتیں"),
										gridBagConstraints);
								gridY++;
								gridBagConstraints.insets = new Insets(4, 4, 4,
										4);
								for (int i = 0; i < 5; i++) {
									if (newPacketPrice[i] > 0) {
										setGBagConst(0, gridY, 1, 1);
										myPanel.add(
												new JLabel(newPacketName[i]),
												gridBagConstraints);
										setGBagConst(1, gridY, 1, 1);
										myPanel.add(packetSPrice[i],
												gridBagConstraints);

									}
									gridY++;
								}
							}

							if (carton.isSelected()) {
								int gridX = 0;
								boolean isEven = packet.isSelected();
								if (isEven) {
									gridY -= 6;
									gridX = 2;
									gridBagConstraints.insets = new Insets(10,
											30, 4, 4);
								} else
									gridBagConstraints.insets = new Insets(10,
											4, 4, 4);
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(new JLabel("کارٹن کی خاص قیمتیں"),
										gridBagConstraints);
								gridY++;
								gridBagConstraints.insets = new Insets(4, 4, 4,
										4);
								for (int i = 0; i < 5; i++) {
									if (newCartonPrice[i] > 0) {
										if (isEven) {
											gridBagConstraints.insets = new Insets(
													4, 30, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(
												new JLabel(newPacketName[i]),
												gridBagConstraints);
										gridX++;
										if (isEven) {
											gridBagConstraints.insets = new Insets(
													4, 4, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(cartonSPrice[i],
												gridBagConstraints);
										gridX++;

										if (isEven)
											gridX = 2;
										else
											gridX = 0;
									}
									gridY++;
								}
							}

							if (bucket.isSelected()) {
								int gridX = 0;
								int dispId = 0;
								for (int i = 6; i >= 5; i--) {
									if (type.charAt(i) == '1')
										dispId++;
								}
								if (dispId > 0) {
									gridX = dispId * 2;
									gridY -= 6;
									gridBagConstraints.insets = new Insets(10,
											30, 4, 4);
								} else
									gridBagConstraints.insets = new Insets(10,
											4, 4, 4);
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(new JLabel("بالٹی کی خاص قیمتیں"),
										gridBagConstraints);
								gridY++;
								gridBagConstraints.insets = new Insets(4, 4, 4,
										4);

								for (int i = 0; i < 5; i++) {
									if (newBucketPrice[i] > 0) {
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 30, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(
												new JLabel(newBucketName[i]),
												gridBagConstraints);
										gridX++;
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 4, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(bucketSPrice[i],
												gridBagConstraints);
										gridX++;
										if (dispId == 1)
											gridX = 2;
										else if (dispId == 2)
											gridX = 4;
										else
											gridX = 0;
									}
									gridY++;
								}
							}

							if (dozen.isSelected()) {
								int gridX = 0;
								int dispId = 0;
								for (int i = 6; i >= 4; i--) {
									if (type.charAt(i) == '1')
										dispId++;
								}
								if (dispId > 0) {
									gridY -= 6;
									gridX = dispId * 2;
									gridBagConstraints.insets = new Insets(10,
											30, 4, 4);
								} else
									gridBagConstraints.insets = new Insets(10,
											4, 4, 4);
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(new JLabel("درجن کی خاص قیمتیں"),
										gridBagConstraints);
								gridY++;
								gridBagConstraints.insets = new Insets(4, 4, 4,
										4);

								for (int i = 0; i < 5; i++) {
									if (newDozenPrice[i] > 0) {
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 30, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(
												new JLabel(newDozenName[i]),
												gridBagConstraints);
										gridX++;
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 4, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(dozenSPrice[i],
												gridBagConstraints);
										gridX++;
										gridX = dispId * 2;
									}
									gridY++;
								}
							}

							if (bottle.isSelected()) {
								int gridX = 0;
								int dispId = 0;
								for (int i = 6; i >= 3; i--) {
									if (type.charAt(i) == '1')
										dispId++;
								}
								if (dispId > 0) {
									if (dispId != 4)
										gridY -= 6;
									if (dispId > 3) {
										dispId -= 4;
									}
									gridX = dispId * 2;

									if ((type.charAt(3) == '1')
											&& (type.charAt(4) == '1')
											&& (type.charAt(5) == '1')
											&& (type.charAt(6) == '1'))
										gridBagConstraints.insets = new Insets(
												10, 4, 4, 4);
									else
										gridBagConstraints.insets = new Insets(
												10, 30, 4, 4);
								} else
									gridBagConstraints.insets = new Insets(10,
											4, 4, 4);
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(new JLabel("بوتل"),
										gridBagConstraints);
								gridY++;
								gridBagConstraints.insets = new Insets(4, 4, 4,
										4);

								for (int i = 0; i < 5; i++) {
									if (newBottlePrice[i] > 0) {
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 30, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(
												new JLabel(newBottleName[i]),
												gridBagConstraints);
										gridX++;
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 4, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(bottleSPrice[i],
												gridBagConstraints);
										gridX = dispId * 2;
									}
									gridY++;
								}
							}

							if (tin.isSelected()) {
								int gridX = 0;
								int dispId = 0;
								for (int i = 6; i >= 2; i--) {
									if (type.charAt(i) == '1')
										dispId++;
								}
								if (dispId > 0) {
									if (dispId > 0) {
										if (dispId != 4)
											gridY -= 6;
										if (dispId > 3) {
											dispId -= 4;
										}
										gridX = dispId * 2;
									}
									if ((type.charAt(2) == '0')
											&& (type.charAt(3) == '1')
											&& (type.charAt(4) == '1')
											&& (type.charAt(5) == '1')
											&& (type.charAt(6) == '1'))
										gridBagConstraints.insets = new Insets(
												10, 4, 4, 4);
									else
										gridBagConstraints.insets = new Insets(
												10, 30, 4, 4);
								} else
									gridBagConstraints.insets = new Insets(10,
											4, 4, 4);
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(new JLabel("ٹین کی خاص قیمتیں"),
										gridBagConstraints);
								gridY++;
								gridBagConstraints.insets = new Insets(4, 4, 4,
										4);

								for (int i = 0; i < 5; i++) {
									if (newTinPrice[i] > 0) {
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 30, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(new JLabel(newTinName[i]),
												gridBagConstraints);
										gridX++;
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 4, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(tinSPrice[i],
												gridBagConstraints);

										gridX = dispId * 2;
									}
									gridY++;
								}
							}

							if (gallon.isSelected()) {
								int gridX = 0;
								int dispId = 0;
								for (int i = 6; i >= 1; i--) {
									if (type.charAt(i) == '1')
										dispId++;
								}
								if (dispId > 0) {
									if (dispId > 0) {
										if (dispId != 4)
											gridY -= 6;
										if (dispId > 3) {
											dispId -= 4;
										}
										gridX = dispId * 2;
									}
									if ((type.charAt(1) == '0')
											&& (type.charAt(2) == '0')
											&& (type.charAt(3) == '1')
											&& (type.charAt(4) == '1')
											&& (type.charAt(5) == '1')
											&& (type.charAt(6) == '1'))
										gridBagConstraints.insets = new Insets(
												10, 4, 4, 4);
									else
										gridBagConstraints.insets = new Insets(
												10, 30, 4, 4);
								} else
									gridBagConstraints.insets = new Insets(10,
											4, 4, 4);
								setGBagConst(gridX, gridY, 1, 1);
								myPanel.add(new JLabel("گیلن کی خاص قیمتیں"),
										gridBagConstraints);
								gridY++;
								gridBagConstraints.insets = new Insets(4, 4, 4,
										4);

								for (int i = 0; i < 5; i++) {
									if (newGallonPrice[i] > 0) {
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 30, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(
												new JLabel(newGallonName[i]),
												gridBagConstraints);
										gridX++;
										if (dispId > 0) {
											gridBagConstraints.insets = new Insets(
													4, 4, 4, 4);
										}
										setGBagConst(gridX, gridY, 1, 1);
										myPanel.add(gallonSPrice[i],
												gridBagConstraints);

										gridX = dispId * 2;
									}
									gridY++;
								}
							}

						}

//						javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
						int result_2 = JOptionPane.showConfirmDialog(null,
								myPanel, "آئٹم کی تفصیلات درج کریں :",
								JOptionPane.OK_CANCEL_OPTION);
						if (result_2 == JOptionPane.OK_OPTION) {

							double newSPriceKg = Double.valueOf(kgSPrice
									.getText());
							double newSPriceGattu = Double.valueOf(gattuSPrice
									.getText());
							double newSPriceBox = Double.valueOf(boxSPrice
									.getText());
							double newSPriceBundle = Double
									.valueOf(bundleSPrice.getText());

							double[] newSPricePacket = new double[5];
							double[] newSPriceCarton = new double[5];
							double[] newSPriceBucket = new double[5];
							double[] newSPriceDozen = new double[5];
							double[] newSPriceBottle = new double[5];
							double[] newSPriceTin = new double[5];
							double[] newSPriceGallon = new double[5];

							for (int i = 0; i < 5; i++) {
								newSPricePacket[i] = Double
										.valueOf(packetSPrice[i].getText());
								newSPriceCarton[i] = Double
										.valueOf(cartonSPrice[i].getText());
								newSPriceBucket[i] = Double
										.valueOf(bucketSPrice[i].getText());
								newSPriceDozen[i] = Double
										.valueOf(dozenSPrice[i].getText());
								newSPriceBottle[i] = Double
										.valueOf(bottleSPrice[i].getText());
								newSPriceTin[i] = Double.valueOf(tinSPrice[i]
										.getText());
								newSPriceGallon[i] = Double
										.valueOf(gallonSPrice[i].getText());
							}

							newItem.addSpecialPrices(connection, newSPriceKg,
									newSPriceGattu, newSPriceBox,
									newSPriceBundle, newSPricePacket,
									newSPriceCarton, newSPriceBucket,
									newSPriceDozen, newSPriceBottle,
									newSPriceTin, newSPriceGallon);
						}

					}

				}
				if (result_1 == JOptionPane.OK_OPTION) {
//					javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
					JOptionPane.showMessageDialog(null, "تبدیلی کر دی گئی !");
				}
			}
			if ((result == 0) && (intType == 0)) {
//				javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
				JOptionPane.showMessageDialog(null,
						"آ‏ئٹم خالی ہے، آپریشن معطل !");
			}
		}
		refreshTable();
		refreshSalesPoint();
	}

	/*------------------------Delete Item-----------------------------*/
	public void deleteItem() {
		int row = tblItemsInfo.getSelectedRow();
		if (tblItemsInfo.isRowSelected(row)) {
//			javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
			int action = JOptionPane.showConfirmDialog(null,
					"کیا آپ واقعی اس آئٹم کو ختم کرنا چاہتے ہیں ؟", "Delete",
					JOptionPane.YES_NO_OPTION);
			if (action == 0) {
				int eid = Integer.valueOf((tblItemsInfo.getModel().getValueAt(
						row, 0)).toString());
				String type = new String();
				int superItemId = 0;
				try {
					String query = "select SuperItem, Type from ItemsInfo where EID='"
							+ eid + "'";
					PreparedStatement pst = connection.prepareStatement(query);
					ResultSet rs = pst.executeQuery();
					while (rs.next()) {
						superItemId = rs.getInt("SuperItem");
						type = rs.getString("Type");
					}
					pst.close();
					rs.close();

				} catch (Exception e) {
					e.printStackTrace();
				}

				deleteItemFromDatabase(eid, "ItemsInfo");
				
				if(superItemId > 0) {
					int pastElements = 0;
					try {
						String query = "select Elements from SuperItemsInfo where EID='"+superItemId+"'";
						PreparedStatement pst = connection.prepareStatement(query);
						ResultSet rs = pst.executeQuery();
						while(rs.next()) {
							pastElements = rs.getInt("Elements");
						}
						pst.close();
						rs.close();
						
					}catch(Exception e) {
						e.printStackTrace();
					}
					pastElements--;
					if(pastElements > 0) {
						try {
							String query = "Update SuperItemsInfo set Elements='"+pastElements+"' where EID='"+superItemId+"'";
							PreparedStatement pst = connection.prepareStatement(query);
							pst.execute();
							
							pst.close();
						} catch(Exception e) {
							e.printStackTrace();
						}
					} else {
						try {
							String query = "delete from SuperItemsInfo where EID='" + superItemId + "'";
							PreparedStatement pst = connection.prepareStatement(query);
							pst.execute();
							pst.close();
	
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}

				int intType = Integer.parseInt(type, 2);
				if (intType > Integer.parseInt("00000001111", 2)) {
					if (type.charAt(6) == '1') {
						deleteItemFromDatabase(eid, "PacketItemsRates");
					}
					if (type.charAt(5) == '1') {
						deleteItemFromDatabase(eid, "CartonItemsRates");
					}
					if (type.charAt(4) == '1') {
						deleteItemFromDatabase(eid, "BucketItemsRates");
					}
					if (type.charAt(3) == '1') {
						deleteItemFromDatabase(eid, "DozenItemsRates");
					}
					if (type.charAt(2) == '1') {
						deleteItemFromDatabase(eid, "BottleItemsRates");
					}
					if (type.charAt(1) == '1') {
						deleteItemFromDatabase(eid, "TinItemsRates");
					}
					if (type.charAt(0) == '1') {
						deleteItemFromDatabase(eid, "GallonItemsRates");
					}
				}
				if ((type.charAt(7) == '1') || (type.charAt(8) == '1')
						|| (type.charAt(9) == '1') || (type.charAt(10) == '1')) {
					deleteItemFromDatabase(eid, "NonSizedItemsRates");
//					javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
					JOptionPane
							.showMessageDialog(null, "آئٹم ختم کر دیا گیا !");
				}
			}
		}
		refreshTable();
		refreshSalesPoint();
	}

	/*----------------------------Delete Item Completely from Database------------------------*/
	public void deleteItemFromDatabase(int eid, String table) {
		try {
			String query = "delete from " + table + " where EID='" + eid + "'";
			PreparedStatement pst = connection.prepareStatement(query);
			pst.execute();
			pst.close();

		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}

	/**
	 * Create the frame.
	 */
	@SuppressWarnings("unchecked")
	public BillingSystem(Connection connection, Employee operator) {

		this.connection = connection;
		this.stockStatus = prefs.getBoolean(STOCK_STATUS_PREF_NAME, false);
		setTitle("Billing System");
		Image iconTitle = new ImageIcon(this.getClass().getResource(
				"/bill2_icon.png")).getImage();
		setIconImage(iconTitle);
		setExtendedState(java.awt.Frame.MAXIMIZED_BOTH);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1296, 754);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		setContentPane(contentPane);
		for (int t = 0; t < 200; t++) {
			billRowId[t] = 0;
			billRowType[t] = "00000000000";
			billRowSize[t] = "00000";
		}

		final JMenuItem LogOut = new JMenuItem("Log Out");
		LogOut.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				// TODO Auto-generated method stub
//				javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
				int action = JOptionPane.showConfirmDialog(null,
						"کیا آپ واقعی اپنا اکاؤنٹ بند کرنا چاہتے ہیں ؟",
						"Log Out", JOptionPane.YES_NO_OPTION);
				if (action == 0) {
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
				JOptionPane.showMessageDialog(
						null,
						"ID : " + operator.getId() + "\nName : "
								+ operator.getName() + "\nSurname : "
								+ operator.getSurname() + "\nUsername : "
								+ operator.getUsername());
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
		Image iconUser = new ImageIcon(this.getClass().getResource(
				"/connected_icon.png")).getImage();
		contentPane.setLayout(null);
		lblUser.setIcon(new ImageIcon(iconUser));
		lblUser.setBounds(1108, 11, 160, 46);
		contentPane.add(lblUser);
		
		@SuppressWarnings("rawtypes")
		JComboBox cbPrinterSelector = new JComboBox();
        cbPrinterSelector.setBounds(954, 8, 144, 52);
        cbPrinterSelector.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				String printer = (String) cbPrinterSelector.getSelectedItem();
				PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
				for (int i = 0; i < services.length; i++) {
					   //System.out.println(services[i].getName());
					   if(services[i].getName().contains(printer)) {
						   requiredService = services[i];
					   }
					}
			}
		});
        cbPrinterSelector.setSelectedItem(0);
        contentPane.add(cbPrinterSelector);
        
        requiredService = null;
		int selectedIndex = 0;
			cbPrinterSelector.removeAllItems();
		PrintService[] services = PrintServiceLookup.lookupPrintServices(null, null);
		for (int i = 0; i < services.length; i++) {
		   //System.out.println(services[i].getName());
		   if(services[i].getName().contains("POS80")) {
			   requiredService = services[i];
			   selectedIndex = i;
		   }
		   cbPrinterSelector.addItem(services[i].getName().toString());
		   
		}
		cbPrinterSelector.setSelectedIndex(selectedIndex);
        
		JLabel lblLogo = new JLabel("ملک اسلم کریانہ سٹور");
		lblLogo.setForeground(Color.BLUE);
//		lblLogo.setFont(new Font("Times New Roman", Font.BOLD, 36));
		lblLogo.setFont(cfp.getLargeBoldFont());
		lblLogo.setBounds(491, -8, 282, 68);
		contentPane.add(lblLogo);

		scrollPane = new JScrollPane();

		scrollPane.setBounds(10, 115, 208, 385);
		contentPane.add(scrollPane);

		tblItemsInfo = new JTable();
		scrollPane.setViewportView(tblItemsInfo);

		JButton btnNew = new JButton("نیا آئٹم           ");
		Image iconSave = new ImageIcon(this.getClass().getResource(
				"/icon_save.png")).getImage();
		btnNew.setIcon(new ImageIcon(iconSave));
		if (!operator.isAdmin())
			btnNew.setEnabled(false);
//		btnNew.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnNew.setFont(cfp.getSmallBoldFont());
		btnNew.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				newItem();
			}
		});
		btnNew.setBounds(10, 513, 208, 57);
		contentPane.add(btnNew);

		btnDelete = new JButton("آئٹم کا خاتمہ    ");
		if (!operator.isAdmin())
			btnDelete.setEnabled(false);
		btnDelete.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				deleteItem();
			}
		});
		Image iconDelete = new ImageIcon(this.getClass().getResource(
				"/icon_delete.png")).getImage();
		btnDelete.setIcon(new ImageIcon(iconDelete));
//		btnDelete.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnDelete.setFont(cfp.getSmallBoldFont());
		btnDelete.setBounds(10, 647, 208, 57);
		contentPane.add(btnDelete);

		JButton btnUpdate = new JButton("آئٹم میں تبدیلی   ");
		if (!operator.isAdmin())
			btnUpdate.setEnabled(false);
		Image iconUpdate = new ImageIcon(this.getClass().getResource(
				"/icon_update.png")).getImage();
		btnUpdate.setIcon(new ImageIcon(iconUpdate));
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				updateItem();
			}
		});
//		btnUpdate.setFont(new Font("Times New Roman", Font.BOLD, 20));
		btnUpdate.setFont(cfp.getSmallBoldFont());
		btnUpdate.setBounds(10, 581, 208, 57);
		contentPane.add(btnUpdate);

		JLabel lblSearch = new JLabel("تلاش");
//		lblSearch.setFont(new Font("Times New Roman", Font.BOLD, 18));
		lblSearch.setFont(cfp.getSmallBoldFont());
		lblSearch.setBounds(180, 73, 38, 31);
		contentPane.add(lblSearch);

		textFieldSearch = new JTextField();
		this.addWindowListener(new WindowAdapter() {
			public void windowOpened(WindowEvent e) {
				textFieldSearch.requestFocus();
			}
		});
		textFieldSearch.addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent arg0) {
				try {
					String query = "select EID, Name from ItemsInfo where Name LIKE ?";
					PreparedStatement pst = connection.prepareStatement(query);
					pst.setString(1, "%" + textFieldSearch.getText() + "%");
					ResultSet rs = pst.executeQuery();

					tblItemsInfo.setModel(DbUtils.resultSetToTableModel(rs));

					pst.close();
					rs.close();

				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});

		textFieldSearch.setBounds(10, 76, 160, 29);
		contentPane.add(textFieldSearch);
		textFieldSearch.setColumns(10);

		scrollPane_1 = new JScrollPane();
		scrollPane_1.setBounds(949, 131, 319, 385);
		contentPane.add(scrollPane_1);

		tblBill = new JTable();
		scrollPane_1.setViewportView(tblBill);
		billModel.setColumnIdentifiers(billColumns);
		tblBill.setModel(billModel);
		tblBill.setFont(cfp.getSmallBoldFont());
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(JLabel.CENTER);
		for(int i = 0; i < 5; i++)
			tblBill.getColumnModel().getColumn(i).setCellRenderer(centerRenderer);
		tblBill.getColumnModel().getColumn(1).setPreferredWidth(45);
		tblBill.getColumnModel().getColumn(2).setPreferredWidth(30);
		tblBill.getColumnModel().getColumn(3).setPreferredWidth(35);
		tblBill.setRowHeight(25);

		JLabel lblBill = new JLabel("بل");
		lblBill.setFont(cfp.getLargeBoldFont());
		lblBill.setHorizontalAlignment(SwingConstants.CENTER);
		lblBill.setBounds(949, 68, 319, 57);
		contentPane.add(lblBill);
		scrollPane_2 = new JScrollPane();
		scrollPane_2.setBounds(228, 49, 711, 655);
		contentPane.add(scrollPane_2);
		
		Font smallBoldFont = cfp.getSmallBoldFont();

		lblTotalDisp = new JLabel("Total Amount =  Rs.");
		lblTotalDisp.setHorizontalAlignment(SwingConstants.RIGHT);
//		lblTotalDisp.setFont(new Font("Times New Roman", Font.BOLD, 19));
		lblTotalDisp.setFont(smallBoldFont);
		lblTotalDisp.setBounds(959, 526, 197, 31);
		contentPane.add(lblTotalDisp);

		lblTotalAmount = new JLabel("0.0");
//		lblTotalAmount.setFont(new Font("Tahoma", Font.BOLD, 18));
		lblTotalAmount.setFont(smallBoldFont);
		lblTotalAmount.setBounds(1166, 526, 102, 31);
		contentPane.add(lblTotalAmount);

		JLabel lblAmountReceived = new JLabel("Amount Received =  Rs.");
		lblAmountReceived.setHorizontalAlignment(SwingConstants.RIGHT);
//		lblAmountReceived.setFont(new Font("Times New Roman", Font.BOLD, 19));
		lblAmountReceived.setFont(smallBoldFont);
		lblAmountReceived.setBounds(949, 556, 207, 31);
		contentPane.add(lblAmountReceived);

		AbstractDocument doc = null;
		textFieldAmountReceived = new JTextField(10);
		doc = (AbstractDocument) textFieldAmountReceived.getDocument();
		doc.setDocumentFilter(new DoubleFilter());
		textFieldAmountReceived.setText("");
		textFieldAmountReceived.addFocusListener(new FocusListener() {
			@Override
			public void focusLost(final FocusEvent pE) {
			}

			@Override
			public void focusGained(final FocusEvent pE) {
				textFieldAmountReceived.selectAll();
			}
		});
//		textFieldAmountReceived.setFont(new Font("Tahoma", Font.BOLD, 18));
		textFieldAmountReceived.setFont(smallBoldFont);
		textFieldAmountReceived.setBounds(1158, 553, 110, 37);
		contentPane.add(textFieldAmountReceived);
		textFieldAmountReceived.setColumns(10);
		frame.addWindowListener( new WindowAdapter() {
		    public void windowOpened( WindowEvent e ){
		        textFieldAmountReceived.requestFocus();
		    }
		});

		JButton btnDeleteItem = new JButton("آئٹم کا خاتمہ");
		Image deleteIcon = new ImageIcon(this.getClass().getResource(
				"/deletetable_icon.png")).getImage();
		btnDeleteItem.setIcon(new ImageIcon(deleteIcon));
		btnDeleteItem.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				int row = tblBill.getSelectedRow();
				if (row >= 0) {
					int rowCount = tblBill.getRowCount();
					for (int t = row; t < 199; t++) {
						billRowId[t] = billRowId[t + 1];
						billRowType[t] = billRowType[t + 1];
						billRowSize[t] = billRowSize[t + 1];
					}

					billRowId[rowCount - 1] = 0;
					billRowType[rowCount - 1] = "00000000000";
					billRowSize[rowCount - 1] = "00000";

					double removeAmount = Double.valueOf((tblBill.getModel()
							.getValueAt(row, 4)).toString());
					billModel.removeRow(row);
					double totalAmount = Double.valueOf(lblTotalAmount
							.getText().toString()) - removeAmount;
					lblTotalAmount.setText("" + totalAmount);
				} else {
//					javax.swing.UIManager.put("OptionPane.messageFont", new FontUIResource(cfp.getSmallBoldFont()));
					JOptionPane.showMessageDialog(null,
							"بل میں سے کوئی آئٹم منتخب نہیں کیا گیا !");
				}
			}
		});
//		btnDeleteItem.setFont(new Font("Times New Roman", Font.BOLD, 22));
		btnDeleteItem.setFont(smallBoldFont);
		btnDeleteItem.setBounds(949, 598, 157, 51);
		contentPane.add(btnDeleteItem);

		JButton btnCheckOut = new JButton("بل بنائیں");
		Image checkOutIcon = new ImageIcon(this.getClass().getResource(
				"/ok.png")).getImage();
		btnCheckOut.setIcon(new ImageIcon(checkOutIcon));
		btnCheckOut.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int rowCount = 0;
				rowCount = tblBill.getRowCount();
				String newLblTotalDisp = lblTotalDisp.getText().toString();
				String newLblTotalAmount = lblTotalAmount.getText().toString();
				String newLblAmountReceived = lblAmountReceived.getText()
						.toString();

				if (!textFieldAmountReceived.getText().toString().equals("")) {
					if (rowCount > 0) {

						if(stockStatus) {
							for (int s = 0; s < rowCount; s++) {
								int billIntType = Integer.parseInt(billRowType[s],
										2);
								int billIntTypeRef = Integer.parseInt(
										"00000001111", 2);
								if (billIntType > billIntTypeRef) {
	
									if (billRowType[s].charAt(0) == '1') {
										for (int q = 0; q < 5; q++) {
											if (billRowSize[s].charAt(q) == '1')
												subtractItem(billRowId[s], q,
														((double) tblBill
																.getValueAt(s, 1)),
														"GallonItemsRates", "گیلن");
										}
									}
									if (billRowType[s].charAt(1) == '1') {
										for (int q = 0; q < 5; q++) {
											if (billRowSize[s].charAt(q) == '1')
												subtractItem(billRowId[s], q,
														((double) tblBill
																.getValueAt(s, 1)),
														"TinItemsRates", "ٹین");
										}
									}
									if (billRowType[s].charAt(2) == '1') {
										for (int q = 0; q < 5; q++) {
											if (billRowSize[s].charAt(q) == '1')
												subtractItem(billRowId[s], q,
														((double) tblBill
																.getValueAt(s, 1)),
														"BottleItemsRates", "بوتل");
										}
									}
									if (billRowType[s].charAt(3) == '1') {
										for (int q = 0; q < 5; q++) {
											if (billRowSize[s].charAt(q) == '1') {
												subtractItem(billRowId[s], q,
														((double) tblBill
																.getValueAt(s, 1)),
														"DozenItemsRates", "درجن");
	
											}
										}
									}
									if (billRowType[s].charAt(4) == '1') {
										for (int q = 0; q < 5; q++) {
											if (billRowSize[s].charAt(q) == '1')
												subtractItem(billRowId[s], q,
														((double) tblBill
																.getValueAt(s, 1)),
														"BucketItemsRates", "بالٹی");
										}
									}
									if (billRowType[s].charAt(5) == '1') {
										for (int q = 0; q < 5; q++) {
											if (billRowSize[s].charAt(q) == '1')
												subtractItem(billRowId[s], q,
														((double) tblBill
																.getValueAt(s, 1)),
														"CartonItemsRates", "کارٹن");
										}
									}
									if (billRowType[s].charAt(6) == '1') {
										for (int q = 0; q < 5; q++) {
											if (billRowSize[s].charAt(q) == '1')
												subtractItem(billRowId[s], q, ((double) tblBill
																.getValueAt(s, 1)),
														"PacketItemsRates", "پیکٹ");
										}
									}
	
								} else {
									if (billRowType[s].charAt(7) == '1')
										subtractItem(
												billRowId[s],
												0,
												((double) tblBill.getValueAt(s, 1)),
												"BundleStock", "بنڈل");
									if (billRowType[s].charAt(8) == '1')
										subtractItem(
												billRowId[s],
												0,
												((double) tblBill.getValueAt(s, 1)),
												"BoxStock", "ڈبہ");
									if (billRowType[s].charAt(9) == '1')
										subtractItem(
												billRowId[s],
												0,
												((double) tblBill.getValueAt(s, 1)),
												"GattuStock", "گٹو");
									if (billRowType[s].charAt(10) == '1')
										subtractItem(
												billRowId[s],
												0,
												((double) tblBill.getValueAt(s, 1)),
												"KgStock", "کلو");
	
								}
	
							}
						}
						
						new BillDialog(connection, operator, requiredService, frame, operator.getName(), tblBill
								.getModel(), newLblTotalDisp,
								newLblTotalAmount, newLblAmountReceived,
								textFieldAmountReceived.getText().toString());

					} else {
						ShowDialog.msg(cfp.getMediumBoldFont(), "بل خالی ہے !");
					}

				} else {
//					JOptionPane.showMessageDialog(null,
//							"حاصل کردہ رقم درج کریں !");
					ShowDialog.msg(cfp.getMediumBoldFont(), "حاصل کردہ رقم درج کریں !");
				}

			}

		});
//		btnCheckOut.setFont(new Font("Times New Roman", Font.BOLD, 22));
		btnCheckOut.setFont(cfp.getMediumBoldFont());
		btnCheckOut.setBounds(1111, 598, 157, 51);
		contentPane.add(btnCheckOut);
		frame.getRootPane().setDefaultButton(btnCheckOut);

		JButton btnNewBill = new JButton("نیا بل    ");
		Image newBillIcon = new ImageIcon(this.getClass().getResource(
				"/newbill_icon.png")).getImage();
		btnNewBill.setIcon(new ImageIcon(newBillIcon));
		btnNewBill.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				int rowCount = tblBill.getRowCount();
				// Remove rows one by one from the end of the table
				for (int p = rowCount - 1; p >= 0; p--) {
					billModel.removeRow(p);
				}
				lblTotalAmount.setText("0");
				textFieldAmountReceived.setText("0");
				tglBtnNormal.setSelected(true);
			}
		});
//		btnNewBill.setFont(new Font("Times New Roman", Font.BOLD, 22));
		btnNewBill.setFont(cfp.getMediumBoldFont());
		btnNewBill.setBounds(949, 653, 319, 51);
		contentPane.add(btnNewBill);

		tglBtnNormal.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				if (tglBtnNormal.isSelected()) {
					tglBtnNormal.setText("عام");
					tglBtnNormal.setForeground(Color.DARK_GRAY);
				} else {
					tglBtnNormal.setText("خاص");
					tglBtnNormal.setForeground(Color.RED);
				}
			}
		});
//		tglBtnNormal.setFont(new Font("Times New Roman", Font.BOLD, 22));
		tglBtnNormal.setFont(cfp.getMediumBoldFont());
		tglBtnNormal.setBounds(949, 89, 93, 31);
		tglBtnNormal.setSelected(true);
		tglBtnNormal.setForeground(Color.DARK_GRAY);
		contentPane.add(tglBtnNormal);

		JLabel lblShopLogo = new JLabel("");
		Image iconShopIcon = new ImageIcon(this.getClass().getResource(
				"/shop_logo.png")).getImage();
		iconShopIcon = iconShopIcon.getScaledInstance(100, 60, Image.SCALE_AREA_AVERAGING);
		lblShopLogo.setIcon(new ImageIcon(iconShopIcon));
		lblShopLogo.setBounds(80, 11, 102, 57);
		contentPane.add(lblShopLogo);

		JButton btnDone = new JButton("");
		Image iconBack = new ImageIcon(this.getClass().getResource(
				"/icon_back.png")).getImage();
		btnDone.setIcon(new ImageIcon(iconBack));
		btnDone.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				close();
				String un = operator.getUsername();
				StartMenu sm = new StartMenu(connection, un);
				sm.setVisible(true);
			}
		});
		btnDone.setFont(cfp.getSmallBoldFont());
		btnDone.setBounds(10, 14, 60, 46);
		contentPane.add(btnDone);
		
		label = new JLabel("Copyright: Engr. Muhammad Junaid Aslam");
		label.setBounds(197, 11, 252, 23);
		contentPane.add(label);

		refreshSalesPoint();
		refreshTable();

	}

	@Override
	public void respondSubSaleDialogCommunicator(int id, String name) {
		// TODO Auto-generated method stub
		saleItem(id, name);
	}
}