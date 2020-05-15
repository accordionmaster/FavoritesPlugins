package com.wxp.favorites.editors;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

/**
 * 标签提供者转换由内容提供者返回的行元素对象为可以在表的单元格中显示的图像和文本。
 * @author Dell
 *
 */
public class PropertiesEditorLabelProvider extends LabelProvider implements ITableLabelProvider{

	@Override
	public Image getColumnImage(Object element, int columnIndex) {
		return null;
	}

	@Override
	public String getColumnText(Object element, int columnIndex) {
		if (element instanceof PropertyCategory) {
			PropertyCategory  category = (PropertyCategory) element; 
			switch (columnIndex) {
			case 0:
				return category.getName();
			case 1:
				return "";
			}
		}
		
		if (element instanceof PropertyEntry) {
			PropertyEntry entry = (PropertyEntry) element;
			switch (columnIndex) {
			case 0:
				return entry.getKey();
			case 1:
				return entry.getValue();
			}
		}
		
		if (element == null) {
			return "<null>";
		}
		
		return element.toString();
	}

}
