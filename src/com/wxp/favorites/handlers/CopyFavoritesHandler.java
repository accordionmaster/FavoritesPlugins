package com.wxp.favorites.handlers;

import java.util.Collection;
import java.util.HashSet;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.SWTError;
import org.eclipse.swt.dnd.Clipboard;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.part.ResourceTransfer;

import com.wxp.favorites.model.IFavoriteItem;

import org.eclipse.swt.dnd.TextTransfer;

/**
 * Copy each currently selected object in the Favorites view to the clipboard.
 */
public class CopyFavoritesHandler extends ClipboardHandler{

	@Override
	protected Object execute(ExecutionEvent event, Clipboard clipboard) {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (selection instanceof IStructuredSelection) {
			Object[] objects = 
					((IStructuredSelection)selection).toArray();
			if (objects.length > 0) {
				try {
					clipboard.setContents(new Object[] {
							asResources(objects), asText(objects),
					}, new Transfer[] {
							ResourceTransfer.getInstance(),
							TextTransfer.getInstance(),
					});
				} catch (SWTError e) {
					
				}
			}
		}
		return null;
	}
	
	public static IResource[] asResources(Object[] objects) {
		Collection<IResource> resources = 
				new HashSet<IResource>(objects.length);
		
		for (int i = 0; i < objects.length; i++) {
			Object each = objects[i];
			if (each instanceof IAdaptable) {
				IResource res = 
						(IResource) ((IAdaptable)each).getAdapter(IResource.class);
				if (res != null) {
					resources.add(res);
				}
			}
		}
		return resources.toArray(new IResource[resources.size()]);
	}

	public static String asText(Object[] objects) {
		StringBuffer buf = new StringBuffer();
		for (int i = 0; i < objects.length; i++) {
			Object each = objects[i];
			if (each instanceof IFavoriteItem) {
				buf.append("Favorite: ");
				buf.append(((IFavoriteItem)each).getName());
			}else if (each != null) {
				buf.append(each.toString());
				buf.append(System.getProperty("line.separator"));
			}
		}
		return buf.toString();
	}

}
