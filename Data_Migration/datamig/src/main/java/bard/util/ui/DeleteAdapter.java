package bard.util.ui;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.awt.datatransfer.*;
import java.util.*;

public class DeleteAdapter implements ActionListener {
	private String rowstring, value;
	private StringSelection stsel;
	private JTable jTable1;

	/**
	 * The Excel Adapter is constructed with a JTable on which it enables
	 * Copy-Paste and acts as a Clipboard listener.
	 */
	public DeleteAdapter(JTable myJTable) {
		jTable1 = myJTable;
		KeyStroke delete = KeyStroke.getKeyStroke("DELETE");
		jTable1.registerKeyboardAction(this, "Delete", delete, JComponent.WHEN_FOCUSED);
	}

	public void actionPerformed(ActionEvent e) {
		if (e.getActionCommand().compareTo("Delete") == 0) {
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
	}
}