package com.wxp.favorites.wizards;

import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.ILabelProviderListener;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

public class ExtractedStringsLabelProvider extends LabelProvider implements ITableLabelProvider {

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof ExtractedString) {
			ExtractedString extractedString = (ExtractedString) element;
			switch (columnIndex) {
			case 0:
				return extractedString.getKey();
			case 1:
				return extractedString.getValue();
			default:
				return "";
			}
		}
		if (element == null) {
			return "<null>";
		}
		try {
			return element.toString();
		} catch (Exception e) {
			return e.toString();
		}
	}
}
