package com.wxp.swt.jface.sample;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.RowLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class RowLayoutExample {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		
		shell.setText("RowLayout Example");
		shell.setBounds(100, 100, 400, 400);
		
		RowLayout layout = new RowLayout();
		layout.marginLeft = 10;
		layout.marginRight = 10;
		layout.marginTop = 10;
		layout.marginBottom = 10;
		layout.spacing = 10;
		shell.setLayout(layout);
		Button button;
		for (int i = 1; i <= 20; i++) {
			button = new Button(shell, SWT.PUSH);
			button.setText("B" + i);
			button.addSelectionListener(new SelectionAdapter() {
				@Override
				public void widgetSelected(SelectionEvent e) {
					System.out.println(((Button)e.widget).getText() + " was clicked!");
				}
			});
		}
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
