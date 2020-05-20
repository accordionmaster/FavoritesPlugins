package com.wxp.favorites.model;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.sql.BatchUpdateException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResourceChangeEvent;
import org.eclipse.core.resources.IResourceChangeListener;
import org.eclipse.core.resources.IResourceDelta;
import org.eclipse.core.resources.IResourceDeltaVisitor;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.XMLMemento;

import com.wxp.favorites.FavoritesActivator;
import com.wxp.favorites.FavoritesLog;

public class FavoritesManager implements IResourceChangeListener{

	private static final String TAG_FAVORITES = "Favorites";
	private static final String TAG_FAVORITE = "Favorite";
	private static final String TAG_TYPEID = "TypeId";
	private static final String TAG_INFO = "Info";
	
	private static FavoritesManager manager;
	private Collection<IFavoriteItem> favorites;
	private List<FavoritesManagerListener> listeners = new ArrayList<FavoritesManagerListener>();
	
	private FavoritesManager() {
		/**
		 * Eclipse 使用org.eclipse.core.resources.IResourceChangeListener在资源
		 * 改变时通知注册的监听器。FavoritesManager需要将它的Favorites项列表与Eclipse
		 * 保持同步。这项功能通过实现org.eclipse.core.resources.IResourceChangeListener
		 * 接口并注册资源更改事件来实现。
		 * 同时，FavoritesActivator stop方法会调用新的FavoritesManager shutdown方法。
		 * 这样，当插件被关闭后，资源更改将不再通知管理程序。现在，无论何时发生资源更改，
		 * Eclipse将调用resourceChanged()方法。
		 * 
		 * FavoritesManager仅对已经发生的更改感兴趣，因此它在订阅更改事件是使用了
		 * IResourceChangeEvent.POST_CHANGE常量。
		 */
		ResourcesPlugin.getWorkspace().addResourceChangeListener(
				this, IResourceChangeEvent.POST_CHANGE);
	}
	
	public static FavoritesManager getManager() {
		if (manager == null) {
			manager = new FavoritesManager();
		}
		return manager;
	}
	
	public static void shutdown() {
		if (manager != null) {
			ResourcesPlugin.getWorkspace()
				.removeResourceChangeListener(manager);
		}
		manager.saveFavorites();
		manager = null;
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
		FavoriteItemType[] types = FavoriteItemType.getTypes();
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
				items.add(item);
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
		FavoriteItemType[] types = FavoriteItemType.getTypes();
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
	
	/**
	 * POST_CHANGE资源更改事件不是表示为单个更改，而是表示为描述一个或多个已经发生的
	 * 更改的层次结构。出于对效率的考虑，以这种方式将事件分批。将发生的每一个更改报告给
	 * 每一个感兴趣的对象将显著减慢系统的速度并减低用户的响应性。要看到该层次结构，添加以下代码
	 * 至FavoritesManager。
	 */
	@Override
	public void resourceChanged(IResourceChangeEvent event) {
//		System.out.println(
//				"FavoritesManager - resource change event");
//		try {
//			event.getDelta().accept(new IResourceDeltaVisitor() {
//				
//				@Override
//				public boolean visit(IResourceDelta delta) throws CoreException {
//					StringBuffer buf = new StringBuffer(80);
//					switch (delta.getKind()) {
//					case IResourceDelta.ADDED:
//						buf.append("ADDED");
//						break;
//					case IResourceDelta.REMOVED:
//						buf.append("REMOVED");
//						break;
//					case IResourceDelta.CHANGED:
//						buf.append("CHANGED");
//						break;
//					default:
//						buf.append("[");
//						buf.append(delta.getKind());
//						buf.append("]");
//						break;
//					}
//					buf.append(" ");
//					buf.append(delta.getResource());
//					System.out.println(buf);
//					return true;
//				}
//			});
//		} catch (CoreException e) {
//			FavoritesLog.logError(e);
//		}
		
		Collection<IFavoriteItem> itemsToRemove = new HashSet<IFavoriteItem>();
		
		try {
			event.getDelta().accept(new IResourceDeltaVisitor() {
				
				@Override
				public boolean visit(IResourceDelta delta) throws CoreException {
					if (delta.getKind() == IResourceDelta.REMOVED) {
						IFavoriteItem item = existingFavoriteFor(delta.getResource());
						if (item != null) {
							itemsToRemove.add(item);
						}
					}
					return true;
				}
			});
		} catch (CoreException e) {
			FavoritesLog.logError(e);
		}
		if (itemsToRemove.size() > 0) {
			removeFavorites(itemsToRemove.toArray(
					new IFavoriteItem[itemsToRemove.size()]));
		}
		
	}
	
	
}
