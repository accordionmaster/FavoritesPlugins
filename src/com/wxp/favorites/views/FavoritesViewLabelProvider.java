package com.wxp.favorites.views;

import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.swt.graphics.Image;

import com.wxp.favorites.model.IFavoriteItem;

public class FavoritesViewLabelProvider extends LabelProvider implements ITableLabelProvider{

	@Override
	public String getColumnText(Object obj, int index) {
		switch (index) {
		case 0:	// Type column
			return "";
		case 1:	// Name column
			if (obj instanceof IFavoriteItem) {
				return ((IFavoriteItem)obj).getName();
			}
			if (obj != null) {
				return obj.toString();
			}
			return "";
		case 2:	// Location column
			if (obj instanceof IFavoriteItem) {
				return ((IFavoriteItem)obj).getLocation();
			}
			return "";
		default:
			return "";
		}
	}

	@Override
	public Image getColumnImage(Object obj, int index) {
		if ((index == 0) && (obj instanceof IFavoriteItem)) {
			return ((IFavoriteItem)obj).getType().getImage();
		}
		return null;
	}

}
