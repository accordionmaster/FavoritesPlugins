package com.wxp.favorites.model;

import org.eclipse.core.runtime.IAdaptable;

/**
 * IFavoriteItem 隐藏了不同类型的Favorites对象之间的区别。
 * 这项功能使得FavoritesManager和FavoritesView可以以一种统一的方式
 * 处理所有的Favorites项。
 * @author Dell
 *
 */
public interface IFavoriteItem extends IAdaptable {

	String getName();
	void setName(String newName);
	String getLocation();
	boolean isFavoriteFor(Object obj);
	FavoriteItemType getType();
	String getInfo();
	
	static IFavoriteItem[] NONE = new IFavoriteItem[] {};
}
