package com.wxp.favorites.editors;

import java.io.IOException;
import java.io.LineNumberReader;
import java.io.PrintWriter;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.wxp.favorites.FavoritesLog;

/**
 * PropertyFile对象将这些联系为一个整体
 * @author Dell
 *
 */
public class PropertyFile extends PropertyElement{
	
	private PropertyCategory unnamedCategory;
	private List<PropertyCategory> categories;
	private List<PropertyFileListener> listeners;
	
	public PropertyFile(String content) {
		super(null);
		categories = new ArrayList<PropertyCategory>();
		listeners = new ArrayList<PropertyFileListener>();
		
		LineNumberReader reader = new LineNumberReader(new StringReader(content));
		try {
			unnamedCategory = new PropertyCategory(this, reader);
			while (true) {
				reader.mark(1);
				int ch = reader.read();
				if (ch == -1) {
					break;
				}
				reader.reset();
				categories.add(new PropertyCategory(this, reader));
			}
		} catch (IOException e) {
			FavoritesLog.logError(e);
		}
	}
	
	@Override
	public PropertyElement[] getChildren() {
		List<PropertyElement> children = new ArrayList<PropertyElement>();
		children.addAll(unnamedCategory.getEntries());
		children.addAll(categories);
		return children.toArray(new PropertyElement[children.size()]);
	}
	
	public void addCategory(PropertyCategory category) {
		addCategory(categories.size(), category);
	}
	
	public void addCategory(int index, PropertyCategory category) {
		if (!categories.contains(category)) {
			categories.add(index, category);
			categoryAdded(category);
		}
	}

	public void removeCategory(PropertyCategory category) {
		if (categories.remove(category)) {
			categoryRemoved(category);
		}
	}
	
	@Override
	public void removeFromParent() {
		// Nothing to do.
	}
	
	void addPropertyFileListener(PropertyFileListener listener) {
		if (!listeners.contains(listener)) {
			listeners.add(listener);
		}
	}
	
	void removePropertyFileListener(PropertyFileListener listener) {
		listeners.remove(listener);
	}
	
	public void keyChanged(PropertyCategory category, PropertyEntry entry) {
		Iterator<PropertyFileListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			iter.next().keyChanged(category,entry);
		}
	}
	
	public void valueChanged(PropertyCategory category, PropertyEntry entry) {
		Iterator<PropertyFileListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			iter.next().valueChanged(category,entry);
		}
	}
	
	public void nameChanged(PropertyCategory category) {
		Iterator<PropertyFileListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			iter.next().nameChanged(category);
		}
		
	}

	public void entryAdded(PropertyCategory category, PropertyEntry entry) {
		Iterator<PropertyFileListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			iter.next().entryAdded(category, entry);
		}
	}
	
	public void entryRemoved(PropertyCategory category, PropertyEntry entry) {
		Iterator<PropertyFileListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			iter.next().entryRemoved(category, entry);
		}
	}
	

	public PropertyFile(PropertyElement parent) {
		super(parent);
		// TODO Auto-generated constructor stub
	}

	private void categoryAdded(PropertyCategory category) {
		Iterator<PropertyFileListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			iter.next().categoryAdded(category);
		}
	}
	private void categoryRemoved(PropertyCategory category) {
		Iterator<PropertyFileListener> iter = listeners.iterator();
		while (iter.hasNext()) {
			iter.next().categoryRemoved(category);
		}
	}

	public String asText() {
		StringWriter stringWriter = new StringWriter(2000);
		PrintWriter writer = new PrintWriter(stringWriter);
		unnamedCategory.appendText(writer);
		Iterator<PropertyCategory> iter = categories.iterator();
		while (iter.hasNext()) {
			writer.println();
			iter.next().appendText(writer);
		}
		return stringWriter.toString();
	}

}
