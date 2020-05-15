package com.wxp.favorites.editors;

// PropertyFile使用PropertyFileListener接口通知注册的监听器（如PropertiesEditor）
// 模型中已经发生了更改
public interface PropertyFileListener {

	public void keyChanged(PropertyCategory category, PropertyEntry entry);

	public void valueChanged(PropertyCategory category, PropertyEntry entry);

	public void nameChanged(PropertyCategory category);

	public void entryAdded(PropertyCategory category, PropertyEntry entry);

	public void entryRemoved(PropertyCategory category, PropertyEntry entry);

	public void categoryAdded(PropertyCategory category);

	public void categoryRemoved(PropertyCategory category);

}
