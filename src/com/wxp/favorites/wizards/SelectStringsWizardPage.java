package com.wxp.favorites.wizards;

import org.eclipse.core.resources.FileInfoMatcherDescription;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.text.projection.Fragment;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.wizard.IWizard;
import org.eclipse.jface.wizard.IWizardPage;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.FormAttachment;
import org.eclipse.swt.layout.FormData;
import org.eclipse.swt.layout.FormLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class SelectStringsWizardPage extends WizardPage {

	private CheckboxTableViewer checkboxTableViewer;
	private IPath sourceLocation;
	private ExtractedStringsModel stringModel;
	
	public SelectStringsWizardPage() {
		super("selectStrings");
		setTitle("Extract Strings");
		setDescription("Select the strings to be extracted");
	}
	
	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new FormLayout());
		setControl(container);
		
		checkboxTableViewer = CheckboxTableViewer.newCheckList(container, SWT.BORDER);
		checkboxTableViewer.setContentProvider(new ExtractedStringsContentProvider());
		checkboxTableViewer.setLabelProvider(new ExtractedStringsLabelProvider());
		
		final Table table = checkboxTableViewer.getTable();
		final FormData formData = new FormData();
		formData.bottom = new FormAttachment(100, 0);
		formData.right = new FormAttachment(100, 0);
		formData.top = new FormAttachment(0, 0);
		formData.left = new FormAttachment(0, 0);
		table.setLayoutData(formData);
		table.setHeaderVisible(true);
		
		final TableColumn tableColumn = new TableColumn(table, SWT.NONE);
		tableColumn.setWidth(200);
		tableColumn.setText("Key");
		
		final TableColumn tableColumn_1 = new TableColumn(table, SWT.NONE);
		tableColumn_1.setWidth(250);
		tableColumn_1.setText("Value");
	}
	
	/**
	 * 该页不是第一次创建时初始化它的内容，而是通过覆盖setVisible()方法在它变成可见时更新它的
	 * 内容。还需要一个访问方法以返回选中地字符串。
	 */
	@Override
	public void setVisible(boolean visible) {
		if (visible) {
			IPath location = ((ExtractStringsWizard) getWizard()).getSourceLocation();
			if (!location.equals(sourceLocation)) {
				sourceLocation = location;
				stringModel = new ExtractedStringsModel(sourceLocation);
				checkboxTableViewer.setInput(stringModel);
			}
		}
		super.setVisible(visible);
	}
	
	public ExtractedString[] getSelection() {
		Object[] checked = checkboxTableViewer.getCheckedElements();
		int count = checked.length;
		ExtractedString[] extracted = new ExtractedString[count];
		System.arraycopy(checked, 0, extracted, 0, count);
		return extracted;
	}
}
