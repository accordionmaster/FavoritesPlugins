package com.wxp.swt.jface.sample;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class GridLayoutExample {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		
		shell.setText("GridLayout Example");
		shell.setBounds(100, 100, 200, 100);
		
		GridLayout layout = new GridLayout();
		layout.numColumns = 2;
		shell.setLayout(layout);
		
		Label label = new Label(shell, SWT.LEFT);
		label.setText("Enter your first and last name");
		GridData gridData = new GridData();
		gridData.horizontalSpan = 2;
		label.setLayoutData(gridData);
		
		label = new Label(shell, SWT.LEFT);
		label.setText("First:");
		Text text = new Text(shell, SWT.SINGLE | SWT.BORDER);
		gridData  = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		text.setLayoutData(gridData);
		
		label = new Label(shell, SWT.LEFT);
		label.setText("Last:");
		text = new Text(shell, SWT.SINGLE | SWT.BORDER);
		gridData = new GridData();
		gridData.horizontalAlignment = GridData.FILL;
		gridData.grabExcessHorizontalSpace = true;
		text.setLayoutData(gridData);
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
