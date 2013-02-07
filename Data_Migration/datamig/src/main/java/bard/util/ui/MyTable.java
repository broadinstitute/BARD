package bard.util.ui;

import java.awt.Color;
import java.awt.Component;
import java.util.Map;

import javax.swing.JTable;
import javax.swing.table.TableCellRenderer;

public class MyTable extends JTable {
	private Map elements;
	private Map results;
	
	public MyTable(Map elements, Map results) {
		this.elements = elements;
		this.results = results;
		setRowSelectionAllowed(true);
		setColumnSelectionAllowed(true);
		new ExcelAdapter(this);
		new DeleteAdapter(this);
	}

	public Component prepareRenderer(TableCellRenderer renderer, int row, int col) {
		Component comp = super.prepareRenderer(renderer, row, col);
		comp.setForeground(Color.BLACK);
		// even index, selected or not selected
		if (row % 2 == 0 && !isCellSelected(row, col)) {
			comp.setBackground(Color.lightGray);
		} else {
			comp.setBackground(Color.white);
		}

		String colName = getModel().getColumnName(col);
		if (colName.equals("RESULTTYPE") ) {
			Object value = getModel().getValueAt(row, col);
			if (value != null && !"".equals(value)) {
				if (results.containsKey(value.toString()))
					comp.setForeground(Color.RED);
			}
		}
		
		else if ( colName.equals("CONTEXTITEM") || colName.startsWith("ATTRIBUTE")) {
			Object value = getModel().getValueAt(row, col);
			if (value != null && !"".equals(value)) {
				if (elements.containsKey(value.toString()))
					comp.setForeground(Color.GREEN.darker().darker());
			}
		}
		return comp;
	}
}