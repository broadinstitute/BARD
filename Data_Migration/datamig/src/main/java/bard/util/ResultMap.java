package bard.util;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.net.URL;
import java.sql.CallableStatement;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.UIManager;
import javax.swing.border.EmptyBorder;

import org.apache.commons.dbutils.QueryRunner;
import org.apache.log4j.xml.DOMConfigurator;
import org.jfree.report.modules.misc.tablemodel.ScrollableResultSetTableModel;

import quick.dbtable.DBTable;
import bard.util.dbutil.ColumnConverterHandler;
import bard.util.dbutil.SimpleMapHandler;
import bard.util.ui.MyTable;
import edu.scripps.fl.pubchem.PubChemDB;
import edu.scripps.fl.pubchem.PubChemFactory;

public class ResultMap extends JFrame {

	private JPanel contentPane;
	private JTable table;
	private DBTable contextTable;
	private JTextField textFieldAID;
	private JButton btnFetch;
	private JButton btnUpdate;
	private JTextField textFieldQuery;
	private List<Long> aidList;
	private int index = 1;
	private JComboBox comboBox;
	private Map elements = new HashMap();
	private Map results = new HashMap();
	private JComboBox dbComboBox;
	private JButton btnNext;
	private JButton btnNextPC;
	private JPanel panel_1;
	private JButton btnNewButton;
	private JTextField modUser;
	private boolean DO_TRANSFER;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) throws Exception {
		URL url = ResultMap.class.getClassLoader().getResource("log4j.config.xml");
		DOMConfigurator.configure(url);

		url = ResultMap.class.getClassLoader().getResource("hibernate.broad.cfg.xml");
		PubChemDB.setUp(url);

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					ResultMap frame = new ResultMap();
					frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	public static Map elementMap() throws Exception {
		Map map = (Map) new QueryRunner().query(Util.getConnection(), "select element_id, label from southern.element",
				new SimpleMapHandler(2, 1));
		return map;
	}

	public static Map resultMap() throws Exception {
		Map map = (Map) new QueryRunner().query(Util.getConnection(),
				"select result_type_id, result_type_name from southern.result_type_tree", new SimpleMapHandler(2, 1));
		return map;
	}

	public void dbSetup() throws Exception {
		List<Long> existingIds = (List<Long>) new QueryRunner().query(Util.getConnection(),
				"select distinct aid from southern.result_map order by aid", new ColumnConverterHandler(1, Long.class));
		DefaultComboBoxModel existingIdsModel = new DefaultComboBoxModel(existingIds.toArray(new Long[0]));
		dbComboBox.setModel(existingIdsModel);
		elements.putAll(elementMap());
		results.putAll(resultMap());
	}

	public ResultMap() throws Exception {
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(100, 100, 1000, 1000);
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);

		panel_1 = new JPanel();
		contentPane.add(panel_1, BorderLayout.CENTER);
		panel_1.setLayout(new BorderLayout(0, 0));

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT);
		panel_1.add(splitPane);

		table = new MyTable(elements, results);
		JScrollPane scroll = new JScrollPane();
		scroll.setViewportView(table);
		splitPane.setTopComponent(scroll);

		contextTable = new DBTable();
		contextTable.setControlPanelVisible(true);
		contextTable.setConnection(Util.getConnection());
		contextTable.autoCommit = false;
		contextTable.debug = false;
		contextTable.addInsertSql(
				"insert into southern.result_map_context(AID, PANELNO, ID, ATTRIBUTE1, VALUE1, ATTRIBUTE2, VALUE2) values(?,?,?,?,?,?,?)",
				"1,2,3,4,5,6,7");
		contextTable.addDeleteSql("delete from southern.result_map_context where aid = ? and panelno = ? and id = ?", "1,2,3");
		contextTable
				.addUpdateSql(
						"update southern.result_map_context set ATTRIBUTE1 = ?, VALUE1 = ?, ATTRIBUTE2 = ?, VALUE2 = ? where aid = ? and panelno = ? and id = ?",
						"4,5,6,7,1,2,3");
		contextTable.useOldColumnProperties(true);

		splitPane.setBottomComponent(contextTable);
		splitPane.setDividerLocation(0.85);

		JPanel panel = new JPanel();
		contentPane.add(panel, BorderLayout.NORTH);

		textFieldQuery = new JTextField();
		textFieldQuery.setText("doseresponse[filt] AND \"NIH Molecular Libraries Program\"[SourceCategory]");
		textFieldQuery.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					Set<Long> ids = PubChemFactory.getInstance().getAIDs(textFieldQuery.getText());
					aidList = new ArrayList(ids);
					DefaultComboBoxModel model = new DefaultComboBoxModel(aidList.toArray(new Long[0]));
					comboBox.setModel(model);
					model.setSelectedItem(aidList.get(0));
					fetch(comboBox.getModel().getSelectedItem());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		panel.add(textFieldQuery);
		textFieldQuery.setColumns(40);

		comboBox = new JComboBox();
		comboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					fetch(comboBox.getModel().getSelectedItem());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		panel.add(comboBox);

		btnNextPC = new JButton("Next");
		btnNextPC.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					doCommit();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				DefaultComboBoxModel model = (DefaultComboBoxModel) comboBox.getModel();
				int index = model.getIndexOf(model.getSelectedItem());
				model.setSelectedItem(model.getElementAt(index + 1));
			}
		});
		panel.add(btnNextPC);

		JLabel lblAssayAid = new JLabel("Assay AID:");
		panel.add(lblAssayAid);

		textFieldAID = new JTextField();
		textFieldAID.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					fetch(textFieldAID.getText());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		panel.add(textFieldAID);
		textFieldAID.setColumns(10);

		panel.add(new JLabel("Modified by:"));
		modUser = new JTextField("jasonr");
		panel.add(modUser);

		panel.add(new JLabel("In DB:"));
		btnFetch = new JButton("From DB");
		btnFetch.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					fetch(textFieldAID.getText());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});

		dbComboBox = new JComboBox();
		dbComboBox.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					fetch(dbComboBox.getModel().getSelectedItem());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		panel.add(dbComboBox);

		btnNext = new JButton("Next");
		btnNext.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					doCommit();
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
				DefaultComboBoxModel model = (DefaultComboBoxModel) dbComboBox.getModel();
				int index = model.getIndexOf(model.getSelectedItem());
				model.setSelectedItem(model.getElementAt(index + 1));
			}
		});
		panel.add(btnNext);
		panel.add(btnFetch);

		btnUpdate = new JButton("From PubChem");
		btnUpdate.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					String aid = textFieldAID.getText();
					new TidProcessor().process(Long.parseLong(aid));
					Util.getConnection().commit();
					fetch(textFieldAID.getText());
				} catch (Exception ex) {
					ex.printStackTrace();
				}
			}
		});
		panel.add(btnUpdate);

		JButton btn = new JButton("Commit");
		btn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				try {
					contextTable.save();
					doCommit();
				} catch (SQLException ex) {

				}
			}
		});
		panel.add(btn);

		JToggleButton tbtn = new JToggleButton("Transfer");
		btn.addItemListener(new ItemListener() {
			public void itemStateChanged(ItemEvent ev) {
				if (ev.getStateChange() == ItemEvent.SELECTED)
					DO_TRANSFER = true;
				else
					DO_TRANSFER = false;
			}
		});
		DO_TRANSFER = true;
		tbtn.setSelected(true);
		panel.add(tbtn);

		dbSetup();

	}

	public void doCommit() throws SQLException {
			contextTable.save();
			Util.getConnection().commit();
			
			if( DO_TRANSFER ) {
				Number aid = (Number) table.getModel().getValueAt(0, 0);
				System.out.println(String.format("Calling data_mig.result_map_util.transfer_result_map(%s)", aid));
				CallableStatement stmt = Util.getConnection().prepareCall("{data_mig.result_map_util.transfer_result_map(?)}");
				stmt.setLong(1, aid.longValue());
				stmt.execute();
			}
	}
	
	public void fetch(final Object aid) throws Exception {
		textFieldAID.setText(aid.toString());
		PreparedStatement ps = Util
				.getConnection()
				.prepareStatement(
						"select AID, TID, TIDNAME, PANELNO, SERIESNO, EXCLUDED_POINTS_SERIES_NO, PARENTTID, RELATIONSHIP, QUALIFIERTID, STATS_MODIFIER, RESULTTYPE, CONTEXTTID, CONTEXTITEM, ATTRIBUTE1, VALUE1, ATTRIBUTE2, VALUE2, CONCENTRATION, CONCENTRATIONUNIT, modified_by from southern.result_map rm where rm.aid = "
								+ aid + " order by rm.tid", ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = ps.executeQuery();
		rs.setFetchSize(200);
		table.setModel(new ScrollableResultSetTableModel(rs, true) {
			@Override
			public void setValueAt(Object ob, int row, int column) {
				try {
					int r = resultset.getRow();
					int c = this.getRowCount();
					resultset.absolute(row + 1);
					if (ob == null) {
						resultset.updateNull(column + 1);
					} else {
						resultset.updateObject(column + 1, ob);
					}
					resultset.updateString("modified_by", modUser.getText());
					resultset.updateRow();
					this.fireTableCellUpdated(row, column);
					this.fireTableCellUpdated(row, resultset.getMetaData().getColumnCount());
				} catch (SQLException ex) {
					ex.printStackTrace();
				}
			}

			@Override
			public boolean isCellEditable(int row, int col) {
				return true;
			}
		});

		// ps = Util.getConnection().prepareStatement(
		// "select AID, PANELNO, ATTRIBUTE1, VALUE1, ATTRIBUTE2, VALUE2 from result_map_context rmc where rmc.aid = "
		// + aid + " order by rmc.panelno",
		// ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_UPDATABLE);
		// rs = ps.executeQuery();
		// rs.setFetchSize(200);
		// contextTable.refresh(rs);

		// DBTableBld.updateDBMethods("select AID, PANELNO, ID, ATTRIBUTE1, VALUE1, ATTRIBUTE2, VALUE2 from result_map_context rmc where rmc.aid = "
		// + aid + " order by rmc.panelno",contextTable,
		// "result_map_context", new String[]{"id"}, false);

		contextTable.save();
		Util.getConnection().commit();

		contextTable
				.setSelectSql("select AID, PANELNO, ID, ATTRIBUTE1, VALUE1, ATTRIBUTE2, VALUE2 from southern.result_map_context rmc where rmc.aid = "
						+ aid + " order by rmc.panelno");
		contextTable.refresh();

		// if( table.getModel().getRowCount() == 0 ) {
		// new TidProcessor().process(Long.parseLong(aid.toString()));
		// Util.getConnection().commit();
		// fetch(aid);
		// }
	}
}