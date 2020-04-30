package com.wxp.favorites.handlers;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.jdt.core.dom.TryStatement;
import org.eclipse.jdt.ui.JavaUI;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.part.ResourceTransfer;

import com.wxp.favorites.model.FavoritesManager;

public class PasteFavoritesHandler extends ClipboardHandler {

	@Override
	protected Object execute(ExecutionEvent event, Clipboard clipboard) {
		paste(clipboard, ResourceTransfer.getInstance());
		paste(clipboard, JavaUI.getJavaElementClipboardTransfer());
		return null;
	}

	private void paste(Clipboard clipboard, Transfer transfer) {
		Object[] elements = (Object[])clipboard.getContents(transfer);
		if (elements != null && elements.length != 0) {
			FavoritesManager.getManager().addFavorites(elements);
		}
	}

}
