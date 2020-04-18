package com.wxp.favorites.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;

public class AddToFavoritesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		MessageDialog.openConfirm(null, "Add", "The \"Add to Favorites\" handler was called");
		// TODO Auto-generated method stub
		return null;
	}


}
