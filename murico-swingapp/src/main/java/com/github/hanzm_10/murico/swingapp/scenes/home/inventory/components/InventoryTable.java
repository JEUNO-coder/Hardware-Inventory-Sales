package com.github.hanzm_10.murico.swingapp.scenes.home.inventory.components;

import java.io.IOException;
import java.sql.SQLException;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingUtilities;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;

import com.github.hanzm_10.murico.swingapp.constants.Styles;
import com.github.hanzm_10.murico.swingapp.lib.comparators.NumberWithSymbolsComparator;
import com.github.hanzm_10.murico.swingapp.lib.database.AbstractSqlFactoryDao;
import com.github.hanzm_10.murico.swingapp.lib.database.entity.item.ItemStock;
import com.github.hanzm_10.murico.swingapp.lib.logger.MuricoLogger;
import com.github.hanzm_10.murico.swingapp.lib.navigation.scene.SceneComponent;
import com.github.hanzm_10.murico.swingapp.lib.table_renderers.CurrencyRenderer;
import com.github.hanzm_10.murico.swingapp.lib.table_renderers.IdRenderer;
import com.github.hanzm_10.murico.swingapp.lib.table_renderers.ProgressLevelRenderer;
import com.github.hanzm_10.murico.swingapp.scenes.home.InventorySceneNew;
import com.github.hanzm_10.murico.swingapp.scenes.home.inventory.editors.ButtonEditor;
import com.github.hanzm_10.murico.swingapp.scenes.home.inventory.renderers.ButtonRenderer;
import com.github.hanzm_10.murico.swingapp.ui.ErrorDialog;

import net.miginfocom.swing.MigLayout;

public class InventoryTable implements SceneComponent {

	private static final Logger LOGGER = MuricoLogger.getLogger(InventoryTable.class);

	public static final int COL_ITEM_STOCK_ID = 0;
	public static final int COL_ITEM_ID = 1;
	public static final int COL_PACKAGING_TYPE = 2;
	public static final int COL_CATEGORY_TYPE = 3;
	public static final int COL_SUPPLIER_NAME = 4;
	public static final int COL_ITEM_NAME = 5;
	public static final int COL_UNIT_PRICE = 6;
	public static final int COL_STOCK_QUANTITY = 7;

	public static final int COL_MINIMUM_QUANTITY = 8;
	public static final int COL_ACTION = 9;

	private JPanel view;
	private JTable table;
	private DefaultTableModel tableModel;
	private JScrollPane scrollPane;
	private TableRowSorter<TableModel> rowSorter;

	private AtomicReference<ItemStock[]> itemStocks = new AtomicReference<>(new ItemStock[0]);
	private AtomicBoolean initialized = new AtomicBoolean(false);

	private InventorySceneNew parentScene;

	public InventoryTable(InventorySceneNew parentScene) {
		// Constructor
		this.parentScene = parentScene;
	}

	private void attachComponents() {
		view.setLayout(new MigLayout("insets 0", "[grow]", "[grow]"));

		view.add(scrollPane, "grow");
	}

	private void createComponents() {
		tableModel = new DefaultTableModel() {
			@Override
			public boolean isCellEditable(int row, int column) {
				return column == COL_ACTION;
			}
		};
		table = new JTable(tableModel);
		table.setGridColor(Styles.TERTIARY_COLOR);
		table.setShowGrid(true);
		table.setRowHeight(40);
		table.setFillsViewportHeight(true);

		var header = table.getTableHeader();
		var comparator = new NumberWithSymbolsComparator();
		var columnNames = ItemStock.getColumnNames();

		rowSorter = new TableRowSorter<>(tableModel);

		header.setReorderingAllowed(false);
		header.setBackground(Styles.SECONDARY_COLOR);
		header.setForeground(Styles.SECONDARY_FOREGROUND_COLOR);

		for (var columnName : columnNames) {
			tableModel.addColumn(columnName);
		}

		tableModel.addColumn("Action");

		rowSorter.setComparator(COL_ITEM_STOCK_ID, comparator);
		rowSorter.setComparator(COL_ITEM_ID, comparator);
		rowSorter.setComparator(COL_UNIT_PRICE, comparator);
		rowSorter.setComparator(COL_MINIMUM_QUANTITY, comparator);
		rowSorter.setComparator(COL_STOCK_QUANTITY, comparator);
		table.setRowSorter(rowSorter);

		scrollPane = new JScrollPane(table);
	}

	@Override
	public void destroy() {

	}

	public TableRowSorter<TableModel> getRowSorter() {
		return rowSorter;
	}

	public JTable getTable() {
		return table;
	}

	public TableModel getTableModel() {
		return tableModel;
	}

	@Override
	public JPanel getView() {
		return view == null ? (view = new JPanel()) : view;
	}

	@Override
	public void initializeComponents() {
		if (initialized.get()) {
			return;
		}

		createComponents();
		setTableRenderers();
		attachComponents();

		initialized.set(true);
	}

	@Override
	public boolean isInitialized() {
		return initialized.get();
	}

	@Override
	public void performBackgroundTask() {
		var factory = AbstractSqlFactoryDao.getSqlFactoryDao(AbstractSqlFactoryDao.MYSQL);

		try {
			var iStocks = factory.getItemDao().getItemStocks();
			itemStocks.set(iStocks);

			System.out.println("Item stocks: " + itemStocks.get().length);

			SwingUtilities.invokeLater(this::updateTableModel);
		} catch (SQLException | IOException e) {
			LOGGER.log(Level.SEVERE, "Error while fetching item stocks", e);

			ErrorDialog.showErrorDialog(SwingUtilities.getWindowAncestor(view), "Something went wrong",
					"An error occurred while fetching item stocks. Please try again later.");
		}
	}

	public void refresh() {
		if (table != null && tableModel != null) { // Added null check for model
			performBackgroundTask();
		} else {
			LOGGER.severe("Inventory table or model is null, cannot refresh.");
		}
	}

	private void setTableRenderers() {
		var columnModel = table.getColumnModel();
		var cellRenderer = new DefaultTableCellRenderer();

		cellRenderer.setHorizontalAlignment(DefaultTableCellRenderer.CENTER);
		columnModel.getColumn(COL_ACTION).setPreferredWidth(48);
		columnModel.getColumn(COL_ACTION).setMaxWidth(48);
		columnModel.getColumn(COL_ACTION).setMinWidth(48);

		columnModel.getColumn(COL_ITEM_STOCK_ID).setPreferredWidth(120);
		columnModel.getColumn(COL_ITEM_ID).setPreferredWidth(120);
		columnModel.getColumn(COL_PACKAGING_TYPE).setPreferredWidth(120);
		columnModel.getColumn(COL_CATEGORY_TYPE).setPreferredWidth(120);
		columnModel.getColumn(COL_SUPPLIER_NAME).setPreferredWidth(120);
		columnModel.getColumn(COL_ITEM_NAME).setPreferredWidth(120);
		columnModel.getColumn(COL_UNIT_PRICE).setPreferredWidth(120);
		columnModel.getColumn(COL_STOCK_QUANTITY).setPreferredWidth(120);
		columnModel.getColumn(COL_MINIMUM_QUANTITY).setPreferredWidth(120);

		columnModel.getColumn(COL_ITEM_STOCK_ID).setCellRenderer(new IdRenderer());
		columnModel.getColumn(COL_ITEM_ID).setCellRenderer(new IdRenderer());
		columnModel.getColumn(COL_PACKAGING_TYPE).setCellRenderer(cellRenderer);
		columnModel.getColumn(COL_CATEGORY_TYPE).setCellRenderer(cellRenderer);
		columnModel.getColumn(COL_SUPPLIER_NAME).setCellRenderer(cellRenderer);
		columnModel.getColumn(COL_ITEM_NAME).setCellRenderer(cellRenderer);
		columnModel.getColumn(COL_UNIT_PRICE).setCellRenderer(new CurrencyRenderer());
		columnModel.getColumn(COL_STOCK_QUANTITY).setCellRenderer(new ProgressLevelRenderer());
		columnModel.getColumn(COL_MINIMUM_QUANTITY).setCellRenderer(cellRenderer);
		var buttonRenderer = new ButtonRenderer();
		columnModel.getColumn(COL_ACTION).setCellRenderer(buttonRenderer);
		columnModel.getColumn(COL_ACTION).setCellEditor(new ButtonEditor(parentScene, buttonRenderer));
	}

	private void updateTableModel() {
		if (!initialized.get()) {
			initializeComponents();
		}

		var itemStocks = this.itemStocks.get();
		tableModel.setRowCount(0);

		for (var itemStock : itemStocks) {
			tableModel
					.addRow(new Object[] { itemStock._itemStockId(), itemStock._itemId(), itemStock.categoryType(),
							itemStock.packagingType(), itemStock.supplierName(), itemStock.itemName(),
							itemStock.unitPrice(), new ProgressLevelRenderer.StockInfo(itemStock._itemId(),
									itemStock.stockQuantity(), itemStock.minimumQuantity(), "unit(s)"),
							itemStock.minimumQuantity() });
		}

		table.revalidate();
		view.revalidate();
		view.repaint();
	}
}
