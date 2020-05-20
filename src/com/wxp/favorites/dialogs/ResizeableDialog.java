package com.wxp.favorites.dialogs;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.window.IShellProvider;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

import com.wxp.favorites.FavoritesActivator;

public class ResizeableDialog extends Dialog{

	private static final String RESIZABLE_DIALOG_SETTINGS = "MyResizableDialogSettings"; 
	
	protected ResizeableDialog(IShellProvider parentShell) {
		super(parentShell);
	}
	
	public ResizeableDialog(Shell parentShell) {
		super(parentShell);
	}
	
	@Override
	protected Control createDialogArea(Composite parent) {
		Composite composite = (Composite) super.createDialogArea(parent);
		new Label(composite, SWT.NONE).setText("This is an example dialog");
		return composite;
	}
	
	@Override
	protected boolean isResizable() {
		return true;
	}
	
	@Override
	protected IDialogSettings getDialogBoundsSettings() {
		IDialogSettings settings = FavoritesActivator.getDefault().getDialogSettings();
		IDialogSettings section = settings.getSection(RESIZABLE_DIALOG_SETTINGS);
		if (section == null) {
			section = settings.addNewSection(RESIZABLE_DIALOG_SETTINGS);
		}
		return section;
	}

}
