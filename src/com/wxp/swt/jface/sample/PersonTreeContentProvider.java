package com.wxp.swt.jface.sample;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ITreeContentProvider;

public class PersonTreeContentProvider extends ArrayContentProvider implements ITreeContentProvider {

	@Override
	public Object[] getChildren(Object parentElement) {
		Person person = (Person) parentElement;
		return person.children;
	}

	@Override
	public Object getParent(Object element) {
		Person person = (Person) element;
		return person.parent;
	}

	@Override
	public boolean hasChildren(Object element) {
		Person person = (Person) element;
		return person.children.length > 0;
	}

}
