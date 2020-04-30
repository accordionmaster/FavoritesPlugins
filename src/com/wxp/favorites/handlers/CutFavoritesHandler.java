package com.wxp.favorites.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;

public class CutFavoritesHandler extends AbstractHandler {
	
	IHandler copy = new CopyFavoritesHandler();
	IHandler remove = new RemoveFavoritesHandler();

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		copy.execute(event);
		remove.execute(event);
		return null;
	}
	
	@Override
	public void dispose() {
		copy.dispose();
		remove.dispose();
		super.dispose();
	}

	

}
