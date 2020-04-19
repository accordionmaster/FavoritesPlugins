package com.wxp.favorites.actions;

import org.eclipse.jface.action.IAction;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IEditorActionDelegate;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPart;

public class AddToFavoritesActionDelegate implements IObjectActionDelegate, IViewActionDelegate, IEditorActionDelegate{
	
	private IWorkbenchPart targetPart;

	public AddToFavoritesActionDelegate() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void run(IAction action) {
		MessageDialog.openInformation(
				targetPart.getSite().getShell(), 
				"Add to Favorites", 
				"Triggered the " + getClass().getName() + " action");
	}

	@Override
	public void selectionChanged(IAction action, ISelection selection) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void setActivePart(IAction action, IWorkbenchPart part) {
		this.targetPart = part;
	}

	@Override
	public void init(IViewPart view) {
		this.targetPart = view;
		
	}

	@Override
	public void setActiveEditor(IAction action, IEditorPart editor) {
		this.targetPart = editor;
	}

}
