package bard.util.ui;

import java.awt.Color;

import quick.dbtable.CellPropertiesModel;

public class BardCellPropertiesModel extends CellPropertiesModel {
	public Color getBackground(int row, int col) {
		if (row % 2 == 0)
			return Color.white;
		else
			return Color.lightGray;
	}
}