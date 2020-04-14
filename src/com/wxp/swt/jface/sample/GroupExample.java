package com.wxp.swt.jface.sample;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Shell;

public class GroupExample {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		
		shell.setText("Group Example");
		shell.setBounds(100, 100, 200, 200);
		
		Group group = new Group(shell, SWT.NULL);
		group.setText("My Group");
		group.setBounds(25, 25, 150, 125);
		final Button button = new Button(group, SWT.PUSH);
		button.setBounds(25, 25, 100, 75);
		button.setText("Click Me Now");
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				button.setText("I Was Clicked");
			}
		});
		
		shell.open();
		while (!shell.isDisposed()) {
			if (!display.readAndDispatch()) {
				display.sleep();
			}
		}
		display.dispose();
	}
}
