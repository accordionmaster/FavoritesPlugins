package com.wxp.favorites.preferences;

import org.eclipse.jface.preference.*;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.IWorkbench;
import com.wxp.favorites.FavoritesActivator;

/**
 * This class represents a preference page that
 * is contributed to the Preferences dialog. By 
 * subclassing <samp>FieldEditorPreferencePage</samp>, we
 * can use the field support built into JFace that allows
 * us to create a page that is small and knows how to 
 * save, restore and apply itself.
 * <p>
 * This page is used to modify preferences only. They
 * are stored in the preference store that belongs to
 * the main plug-in class. That way, preferences can
 * be accessed directly via the preference store.
 */

public class FavoritesPreferencePage
	extends FieldEditorPreferencePage
	implements IWorkbenchPreferencePage {
	
	private BooleanFieldEditor namePrefEditor;
	private BooleanFieldEditor locationPrefEditor;

	public FavoritesPreferencePage() {
		super(GRID);
		setPreferenceStore(FavoritesActivator.getDefault().getPreferenceStore());
		setDescription("Favorites view column visibility:");
	}
	
	/* (non-Javadoc)
	 * @see org.eclipse.ui.IWorkbenchPreferencePage#init(org.eclipse.ui.IWorkbench)
	 */
	public void init(IWorkbench workbench) {
	}
	
	/**
	 * Creates the field editors. Field editors are abstractions of
	 * the common GUI blocks needed to manipulate various types
	 * of preferences. Each field editor knows how to save and
	 * restore itself.
	 */
	public void createFieldEditors() {
		namePrefEditor = 
				new BooleanFieldEditor(
						PreferenceConstants.FAVORITES_VIEW_NAME_COLUMN_VISIBLE, 
						"Show name column", getFieldEditorParent());
		addField(namePrefEditor);
		locationPrefEditor = 
				new BooleanFieldEditor(PreferenceConstants.FAVORITES_VIEW_LOCATION_COLUMN_VISIBLE,
						"Show location column", getFieldEditorParent());
		addField(locationPrefEditor);
	}
	
	/**
	 * 首选项页总体来说是比较好的，但存在两个问题。
	 * 第一，名称和位置列的可见性默认应为true。
	 * 第二，至少一列应一直可见。字段编辑器强制在创建过程中，基于编辑器类型和指定的参数对
	 * 它们的内容进行本地合法性验证。如果你想要在不同编辑器间进行验证，就必须重新覆盖checkState
	 * 方法。
	 */
	@Override
	protected void checkState() {
		super.checkState();
		if (!isValid()) {
			return;
		}
		if (!namePrefEditor.getBooleanValue() && !locationPrefEditor.getBooleanValue()) {
			setErrorMessage("Must have at least one column visible");
			setValid(false);
		}
		else {
			setErrorMessage(null);
			setValid(true);
		}
	}
	
	/**
	 * FieldEditorPropertyPage监听FieldEditor.IS_VALID属性变更事件，并随后在必要时调用
	 * checkState()和setValid()。布尔值字段编辑器从不会出于非法状态，因此它不会提交
	 * FieldEditor.IS_VALID属性改变事件，仅提供FieldEditor.VALUE属性改变事件。
	 * 覆盖FieldEditorPreferencePage propertyChange()方法以在收到FieldEditor.VALUE
	 * 属性变更事件时调用checkState()方法。
	 * 
	 */
	@Override
	public void propertyChange(PropertyChangeEvent event) {
		super.propertyChange(event);
		if (event.getProperty().equals(FieldEditor.VALUE)) {
			if (event.getSource() == namePrefEditor || event.getSource() == locationPrefEditor) {
				checkState();
			}
		}
	}
}