package com.wxp.favorites.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IMemento;

import com.wxp.favorites.views.FavoritesViewNameFilter;

public class FavoritesViewFilterAction extends Action {

	private final Shell shell;

	private final FavoritesViewNameFilter nameFilter;

	public FavoritesViewFilterAction(StructuredViewer viewer, String text) {
		super(text);
		shell = viewer.getControl().getShell();
		nameFilter = new FavoritesViewNameFilter(viewer);
	}

	@Override
	public void run() {
		InputDialog dialog = new InputDialog(shell, "Favorites View Filter",
				"Enter a name filter pattern" + " (* = any string, ? = any character)"
						+ System.getProperty("line.seperator") + "or an empty string for no filtering:",
				nameFilter.getPattern(), null);
		if (dialog.open() == InputDialog.OK) {
			nameFilter.setPattern(dialog.getValue().trim());
		}
	}

	public void saveState(IMemento memento) {
		nameFilter.saveState(memento);
	}
	
	public void init(IMemento memento) {
		nameFilter.init(memento);
	}
}
