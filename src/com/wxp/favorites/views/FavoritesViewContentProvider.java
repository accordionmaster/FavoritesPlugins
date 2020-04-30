package com.wxp.favorites.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;

import com.wxp.favorites.model.FavoritesManager;
import com.wxp.favorites.model.FavoritesManagerEvent;
import com.wxp.favorites.model.FavoritesManagerListener;

public class FavoritesViewContentProvider implements IStructuredContentProvider, FavoritesManagerListener {
	
	private TableViewer viewer;
	private FavoritesManager manager;

	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		this.viewer = (TableViewer) viewer;
		if (manager != null) {
			manager.removeFavoritesManagerListener(this);
		}
		manager = (FavoritesManager)newInput;
		if (manager != null) {
			manager.addFavoritesManagerListener(this);
		}
	}
	
	

	@Override
	public void dispose() {
	}

	@Override
	public Object[] getElements(Object parent) {
		return manager.getFavorites();
	}

	@Override
	public void favoritesChanged(FavoritesManagerEvent event) {
		viewer.getTable().setRedraw(false);
		try {
			viewer.remove(event.getItemsRemoved());
			viewer.add(event.getItemsAdded());
		} finally {
			viewer.getTable().setRedraw(true); // 使用setRedraw方法以当从查看器中添加和移除多个项目时降低闪烁。
		}
	}
}
