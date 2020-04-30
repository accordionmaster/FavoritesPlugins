package com.wxp.favorites.propertyTester;

import org.eclipse.core.expressions.PropertyTester;

import com.wxp.favorites.model.FavoritesManager;
import com.wxp.favorites.model.IFavoriteItem;

/**
 * 当模型存在时，测试FavoritesManager是否包含指定对象
 * @author Dell
 *
 */
public class FavoritesTester extends PropertyTester {

	public FavoritesTester() {
	}

	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		
		boolean found = false;
		IFavoriteItem[] favorites = FavoritesManager.getManager().getFavorites();
		for (int i = 0; i < favorites.length; i++) {
			IFavoriteItem item = favorites[i];
			found = item.isFavoriteFor(receiver);
			if (found) {
				break;
			}
		}
		if ("isFavorite".equals(property)) {
			return found;
		}
		if ("notFavorite".equals(property)) {
			return !found;
		}
		return false;
	}

}
