package com.wxp.favorites.editors;

import java.io.PrintWriter;

import org.eclipse.ui.editors.text.TextEditor;

/**
 * 一个PropertyEntry对象表示属性文件中的一个名/值对。
 * @author Dell
 *
 */
public class PropertyEntry extends PropertyElement{

	String key;
	String value;
	
	public PropertyEntry(PropertyCategory parent, String key, String value) {
		super(parent);
		this.key = key;
		this.value = value;
	}
	
	public String getKey() {
		return key;
	}
	
	public String getValue() {
		return value;
	}

	@Override
	public PropertyElement[] getChildren() {
		return NO_CHILDREN;
	}
	
	public void setKey(String text) {
		if (key.equals(text)) {
			return;
		}
		key = text;
		((PropertyCategory) getParent()).keyChanged(this);
	}
	
	public void setValue(String text) {
		if (value.equals(text)) {
			return;
		}
		value = text;
		((PropertyCategory) getParent()).valueChanged(this);
	}

	@Override
	public void removeFromParent() {
		((PropertyCategory) getParent()).removeEntry(this);
		
	}

	public void appendText(PrintWriter writer) {
		writer.print(key);
		writer.print(" = ");
		writer.println(value);
	}

}
