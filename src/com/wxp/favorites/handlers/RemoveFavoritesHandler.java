package com.wxp.favorites.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.handlers.HandlerUtil;

import com.wxp.favorites.model.FavoritesManager;
/**
 * RemoveFavoritesContributionItem 使用了新的处理器以执行操作。我们
 * 将这项功能从添加项分离出来以使它能在其他地方被优化。
 * 
 * Remove each currently selected object from the Favorites collection if it has
 * not already been removed.
 *
 * @author Dell
 *
 */
public class RemoveFavoritesHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			FavoritesManager.getManager().removeFavorites(
					((IStructuredSelection)selection).toArray());
		}
		return null;
	}

}
