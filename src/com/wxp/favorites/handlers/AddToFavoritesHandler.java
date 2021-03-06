package com.wxp.favorites.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.wxp.favorites.model.FavoritesManager;

public class AddToFavoritesHandler extends AbstractHandler {

	/**
	 * 处理器添加选项至FavoritesManager，然后FavoritesManager通知FavoritesViewContentProvider，并由FavoritesViewContentProvider刷新表以显示新的信息。
	 */
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
//		MessageDialog.openConfirm(null, "Add", "The \"Add to Favorites\" handler was called");
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			FavoritesManager.getManager().addFavorites(((IStructuredSelection)selection).toArray());
		}
		return null;
	}


}
