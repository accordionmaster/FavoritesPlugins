package com.wxp.favorites.model;

import java.util.EventObject;

public class FavoritesManagerEvent extends EventObject{
	
	private static final long serialVersionUID = -6324781111016613900L;
	
	private final IFavoriteItem[] added;
	private final IFavoriteItem[] removed;

	public FavoritesManagerEvent(FavoritesManager source, IFavoriteItem[] itemsAdded,
			IFavoriteItem[] itemsRemoved) {
		super(source);
		added = itemsAdded;
		removed = itemsRemoved;
	}
	
	public IFavoriteItem[] getItemsAdded() {
		return added;
	}
	
	public IFavoriteItem[] getItemsRemoved() {
		return removed;
	}

}
