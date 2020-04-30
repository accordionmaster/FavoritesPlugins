package com.wxp.favorites.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.wxp.favorites.FavoritesActivator;
import com.wxp.favorites.FavoritesLog;

public class FavoritesManager {

	private static final String TAG_FAVORITES = "Favorites";
	private static final String TAG_FAVORITE = "Favorite";
	private static final String TAG_TYPEID = "TypeId";
	private static final String TAG_INFO = "Info";
	
	private static FavoritesManager manager;
	private Collection<IFavoriteItem> favorites;
	private List<FavoritesManagerListener> listeners = new ArrayList<FavoritesManagerListener>();
	
	private FavoritesManager() {}
	
	public static FavoritesManager getManager() {
		if (manager == null) {
			manager = new FavoritesManager();
		}
		return manager;
	}
	
	public IFavoriteItem[] getFavorites() {
		if (favorites == null) {
			loadFavorites();
		}
		return favorites.toArray(new IFavoriteItem[favorites.size()]);
	}

	private void loadFavorites() {
		// temporary implementation
		// to prepopulate list with projects
		IProject[] projects = ResourcesPlugin.getWorkspace().getRoot().getProjects();
		favorites = new HashSet<IFavoriteItem>(projects.length);
		for (int i = 0; i < projects.length; i++) {
			favorites.add(new FavoriteResource(FavoriteItemType.WORKBENCH_PROJECT, projects[i]));
		}
		
	}
	
	public void addFavorites(Object[] objects) {
		if (objects == null) {
			return;
		}
		if (favorites == null) {
			loadFavorites();
		}
		Collection<IFavoriteItem> items = new HashSet<IFavoriteItem>(objects.length);
		for (int i = 0; i < objects.length; i++) {
			IFavoriteItem item = existingFavoriteFor(objects[i]);
			if (item == null) {
				item = newFavoriteFor(objects[i]);
				if (item != null && favorites.add(item)) {
					items.add(item);
				}
			}
		}
		if (items.size() > 0) {
			IFavoriteItem[] added = items.toArray(new IFavoriteItem[items.size()]);
			fireFavoritesChanged(added, IFavoriteItem.NONE);
		}
	}
	
	public IFavoriteItem newFavoriteFor(Object obj) {
		FavoriteItemType[] types = FavoriteItemType.geTypes();
		for (int i = 0; i < types.length; i++) {
			IFavoriteItem item = types[i].newFavorite(obj);
			if (item != null) {
				return item;
			}
		}
		return null;
	}

	private void fireFavoritesChanged(IFavoriteItem[] itemsAdded, IFavoriteItem[] ItemsRemoved) {
		FavoritesManagerEvent event = new FavoritesManagerEvent(this, itemsAdded, ItemsRemoved);
		for (Iterator<FavoritesManagerListener> iter = listeners.iterator(); iter.hasNext();) {
			iter.next().favoritesChanged(event);
		}
	}

	public void removeFavorites(Object[] objects) {
		if (objects == null) {
			return;
		}
		if (favorites == null) {
			loadFavorites();
		}
		Collection<IFavoriteItem> items = new HashSet<IFavoriteItem>(objects.length);
		for (int i = 0; i < objects.length; i++) {
			IFavoriteItem item = existingFavoriteFor(objects[i]);
			if (item != null && favorites.remove(item)) {
				items.remove(item);
			}
		}
		if (items.size() > 0) {
			IFavoriteItem[] removed = items.toArray(new IFavoriteItem[items.size()]);
			fireFavoritesChanged(IFavoriteItem.NONE, removed);
		}
	}

	private IFavoriteItem existingFavoriteFor(Object obj) {
		if (obj == null) {
			return null;
		}
		if (obj instanceof IFavoriteItem) {
			return (IFavoriteItem)obj;
		}
		Iterator<IFavoriteItem> iter = favorites.iterator();
		while (iter.hasNext()) {
			IFavoriteItem item = (IFavoriteItem) iter.next();
			if (item.isFavoriteFor(obj)) {
				return item;
			}
			
		}
		return null;
	}
	
	public IFavoriteItem[] existingFavoritesFor(Iterator<?> iter) {
		List<IFavoriteItem> result = new ArrayList<IFavoriteItem>(10);
		while (iter.hasNext()) {
			IFavoriteItem item = existingFavoriteFor(iter.next());
			if (item != null) {
				result.add(item);
			}
			
		}
		return (IFavoriteItem[]) result.toArray(new IFavoriteItem[result.size()]);
	}
	
	public void addFavoritesManagerListener(FavoritesManagerListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	public void removeFavoritesManagerListener(FavoritesManagerListener listener) {
		listeners.remove(listener);
	}
	
	private void loadFavorites(XMLMemento memento) {
		IMemento[] children = memento.getChildren(TAG_FAVORITE);
		for (int i = 0; i < children.length; i++) {
			IFavoriteItem item = newFavoriteFor(children[i].getString(TAG_TYPEID), children[i].getString(TAG_INFO));
			if (item != null) {
				favorites.add(item);
			}
		}
	}

	public IFavoriteItem newFavoriteFor(String typeId, String info) {
		FavoriteItemType[] types = FavoriteItemType.geTypes();
		for (int i = 0; i < types.length; i++) {
			if (types[i].getId().equals(typeId)) {
				return types[i].loadFavorite(info);
			}
		}
		return null;
	}

	public void saveFavorites() {
		if (favorites == null) {
			return;
		}
		XMLMemento memento = XMLMemento.createWriteRoot(TAG_FAVORITES);
		saveFavorites(memento);
		FileWriter writer = null;
		try {
			writer = new FileWriter(getFavoritesFile());
			memento.save(writer);
		} catch (IOException e) {
			FavoritesLog.logError(e);
		}finally {
			try {
				if (writer != null) {
					writer.close();
				}
			} catch (IOException e) {
				FavoritesLog.logError(e);
			}
		}
		
	}

	private File getFavoritesFile() {
		return FavoritesActivator.getDefault()
				.getStateLocation()
				.append("favorites.xml")
				.toFile();
	}

	private void saveFavorites(XMLMemento memento) {
		Iterator<IFavoriteItem> iter = favorites.iterator();
		while (iter.hasNext()) {
			IFavoriteItem item = (IFavoriteItem) iter.next();
			IMemento child = memento.createChild(TAG_FAVORITE);
			child.putString(TAG_TYPEID, item.getType().getId());
			child.putString(TAG_INFO, item.getInfo());
			
		}
		
	}
	
	
}
