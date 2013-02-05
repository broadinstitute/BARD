package bard.util.ui;

import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.StringTokenizer;

import javax.swing.JComponent;
import javax.swing.JOptionPane;
import javax.swing.JTable;
import javax.swing.KeyStroke;

import net.balusc.util.ObjectConverter;

/**
 * ExcelAdapter enables Copy-Paste Clipboard functionality on JTables. The
 * clipboard data format used by the adapter is compatible with the clipboard
 * format used by Excel. This provides for clipboard interoperability between
 * enabled JTables and Excel.
 */
public class ExcelAdapter implements ActionListener {
	private String rowstring, value;
	private Clipboard system;
	private StringSelection stsel;
	private JTable jTable1;

	/**
	 * The Excel Adapter is constructed with a JTable on which it enables
	 * Copy-Paste and acts as a Clipboard listener.
	 */
	public ExcelAdapter(JTable myJTable) {
		jTable1 = myJTable;
		KeyStroke copy = KeyStroke.getKeyStroke(KeyEvent.VK_C, ActionEvent.CTRL_MASK, false);
		// Identifying the copy KeyStroke user can modify this
		// to copy on some other Key combination.
		KeyStroke paste = KeyStroke.getKeyStroke(KeyEvent.VK_V, ActionEvent.CTRL_MASK, false);
		KeyStroke cut = KeyStroke.getKeyStroke(KeyEvent.VK_X, ActionEvent.CTRL_MASK, false);
		// Identifying the Paste KeyStroke user can modify this
		// to copy on some other Key combination.
		jTable1.registerKeyboardAction(this, "Copy", copy, JComponent.WHEN_FOCUSED);
		jTable1.registerKeyboardAction(this, "Paste", paste, JComponent.WHEN_FOCUSED);
		jTable1.registerKeyboardAction(this, "Cut", cut, JComponent.WHEN_FOCUSED);
		system = Toolkit.getDefaultToolkit().getSystemClipboard();
	}

	/**
	 * Public Accessor methods for the Table on which this adapter acts.
	 */
	public JTable getJTable() {
		return jTable1;
	}

	public void setJTable(JTable jTable1) {
		this.jTable1 = jTable1;
	}

	/**
	 * This method is activated on the Keystrokes we are listening to in this
	 * implementation. Here it listens for Copy and Paste ActionCommands.
	 * Selections comprising non-adjacent cells result in invalid selection and
	 * then copy action cannot be performed. Paste is done by aligning the upper
	 * left corner of the selection with the 1st element in the current
	 * selection of the JTable.
	 */
	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareTo("Copy") == 0 || e.getActionCommand().compareTo("Cut") == 0) {
			StringBuffer sbf = new StringBuffer();
			// Check to ensure we have selected only a contiguous block of
			// cells
			int numcols = jTable1.getSelectedColumnCount();
			int numrows = jTable1.getSelectedRowCount();
			int[] rowsselected = jTable1.getSelectedRows();
			int[] colsselected = jTable1.getSelectedColumns();
			if (!((numrows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0] && numrows == rowsselected.length) && (numcols - 1 == colsselected[colsselected.length - 1]
					- colsselected[0] && numcols == colsselected.length))) {
				JOptionPane.showMessageDialog(null, "Invalid Copy Selection", "Invalid Copy Selection", JOptionPane.ERROR_MESSAGE);
				return;
			}
			for (int i = 0; i < numrows; i++) {
				for (int j = 0; j < numcols; j++) {
					Object obj = jTable1.getValueAt(rowsselected[i], colsselected[j]); 
					sbf.append(obj == null ? "" : obj);
					if (j < numcols - 1)
						sbf.append("\t");
				}
				sbf.append("\n");
			}
			stsel = new StringSelection(sbf.toString());
			system = Toolkit.getDefaultToolkit().getSystemClipboard();
			system.setContents(stsel, stsel);
		}
		if (e.getActionCommand().compareTo("Cut") == 0) {
			int numcols = jTable1.getSelectedColumnCount();
			int numrows = jTable1.getSelectedRowCount();
			int[] rowsselected = jTable1.getSelectedRows();
			int[] colsselected = jTable1.getSelectedColumns();
			if (!((numrows - 1 == rowsselected[rowsselected.length - 1] - rowsselected[0] && numrows == rowsselected.length) && (numcols - 1 == colsselected[colsselected.length - 1]
					- colsselected[0] && numcols == colsselected.length))) {
				JOptionPane.showMessageDialog(null, "Invalid Delete Selection", "Invalid Delete Selection", JOptionPane.ERROR_MESSAGE);
				return;
			}
			for (int i = 0; i < numrows; i++) {
				for (int j = 0; j < numcols; j++) {
					jTable1.setValueAt(null, rowsselected[i], colsselected[j]);
				}
			}
		}
		if (e.getActionCommand().compareTo("Paste") == 0) {
			int startRow = (jTable1.getSelectedRows())[0];
			int startCol = (jTable1.getSelectedColumns())[0];
			try {
				String trstring = (String) (system.getContents(this).getTransferData(DataFlavor.stringFlavor));
				StringTokenizer st1 = new StringTokenizer(trstring, "\n");
				for (int i = 0; st1.hasMoreTokens(); i++) {
					rowstring = st1.nextToken();
					String[] cells = rowstring.split("\t");
					for (int j = 0; j < cells.length; j++) {
						value = (String) cells[j];
						if (startRow + i < jTable1.getRowCount() && startCol + j < jTable1.getColumnCount())
							try {
								jTable1.setValueAt( ObjectConverter.convert(value, jTable1.getColumnClass(startCol + j)) , startRow + i, startCol + j);
							}
							catch(Exception ex) {
								ex.printStackTrace();
							}
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
			}
		}
	}
}