package com.wxp.favorites.views;

import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Display;

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
		// If this is the UI thread, then make the change.
		if (Display.getCurrent() != null) {
			updateViewer(event);
			return;
		}
		
		// otherwise, redirect to execute on the UI thread.
		Display.getDefault().asyncExec(new Runnable() {
			
			@Override
			public void run() {
				updateViewer(event);
			}
		});
	}
	
	private void updateViewer(final FavoritesManagerEvent event) {
		// Use the set Redraw method to reduce flicker
		// when adding or removing multiple items in a table.
		viewer.getTable().setRedraw(false);
		try {
			viewer.remove(event.getItemsRemoved());
			viewer.add(event.getItemsAdded());
		} finally {
			viewer.getTable().setRedraw(true); // 使用setRedraw方法以当从查看器中添加和移除多个项目时降低闪烁。
		}
	}
}
