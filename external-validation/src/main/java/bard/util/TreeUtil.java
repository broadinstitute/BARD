package bard.util;

import java.util.Comparator;
import java.util.Enumeration;

import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.MutableTreeNode;

public class TreeUtil {
	public static void sortNode(DefaultMutableTreeNode parent, Comparator<DefaultMutableTreeNode> comparator) {
		int n = parent.getChildCount();
		for (int i = 0; i < n - 1; i++) {
			int min = i;
			for (int j = i + 1; j < n; j++) {
				if (comparator.compare((DefaultMutableTreeNode) parent.getChildAt(min), (DefaultMutableTreeNode) parent.getChildAt(j)) > 0) {
					min = j;
				}
			}
			if (i != min) {
				MutableTreeNode a = (MutableTreeNode) parent.getChildAt(i);
				MutableTreeNode b = (MutableTreeNode) parent.getChildAt(min);
				parent.insert(b, i);
				parent.insert(a, min);
			}
		}
	}

	public static void sortTreeDepthFirst(DefaultMutableTreeNode root, Comparator<DefaultMutableTreeNode> comparator) {
		Enumeration e = root.depthFirstEnumeration();
		while (e.hasMoreElements()) {
			DefaultMutableTreeNode node = (DefaultMutableTreeNode) e.nextElement();
			if (!node.isLeaf()) {
				sortNode(node, comparator);
			}
		}
	}
}