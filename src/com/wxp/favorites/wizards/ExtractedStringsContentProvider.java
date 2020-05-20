package com.wxp.favorites.wizards;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class ExtractedStringsContentProvider implements IStructuredContentProvider {

	private static final Object[] items = {
			new ExtractedString("plugin_id", "com.wxp.favorites"),
            new ExtractedString("plugin_name", "Favorites Plug-in"),
            new ExtractedString("plugin_version", "1.0.0"),
            new ExtractedString("plugin_provider-name", "wxp"),
            new ExtractedString("plugin_class",
                  "com.wxp.favorites.FavoritesPlugin"),
            new ExtractedString("view_name", "Favorites"),
            new ExtractedString("view_icon", "icons/sample.gif"),
            new ExtractedString("view_category", "com.wxp.favorites"),
            new ExtractedString("view_class",
                  "com.wxp.favorites.views.FavoritesView"),
            new ExtractedString("view_id",
                  "com.wxp.favorites.views.FavoritesView"),
	};
	
	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
	}
	
	@Override
	public Object[] getElements(Object inputElement) {
		return items;
	}
	
	@Override
	public void dispose() {
	}

}
