package com.wxp.favorites.editors;

import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.ui.about.IInstallationPageContainer;

import com.wxp.favorites.model.IFavoriteItem;

/**
 * 所有这些模型对象，仅当他们可以在树中正确显示时才有用。 为了完成这项任务，需要创建一个内容提供者和标签提供者。
 * 内容提供者提供树中出现的行和父子关系，但不提供实际的单元内容。
 * 
 * @author Dell
 *
 */
public class PropertiesEditorContentProvider implements ITreeContentProvider {

	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

	}

	@Override
	public Object[] getElements(Object element) {
		return getChildren(element);
	}

	@Override
	public Object[] getChildren(Object element) {
		if (element instanceof PropertyElement) {
			return ((PropertyElement) element).getChildren();
		}
		return null;
	}

	@Override
	public Object getParent(Object element) {
		if (element instanceof PropertyElement) {
			return ((PropertyElement) element).getParent();
		}
		return null;
	}

	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof PropertyElement) {
			return ((PropertyElement) element).getChildren().length > 0;
		}
		return false;
	}

	@Override
	public void dispose() {
	}

}
