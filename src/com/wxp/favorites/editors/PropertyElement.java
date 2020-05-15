package com.wxp.favorites.editors;

/**
 * 完善树，以使文本编辑器中的内容出现于树中。为了完成这项任务，需要创建一个可以分析文本编辑器内容的模型。
 * 然后将该模型和一个标签提供者一起，关联至树。
 * 我们首先所有属性模型对象引入一个新的PropertyElement超类。
 * @author Dell
 *
 */
public abstract class PropertyElement {

	public static final PropertyElement[] NO_CHILDREN = {};
	private PropertyElement parent;
	
	public PropertyElement(PropertyElement parent) {
		this.parent = parent;
	}
	
	public PropertyElement getParent() {
		return parent;
	}
	
	public abstract PropertyElement[] getChildren();
	public abstract void removeFromParent();
}
