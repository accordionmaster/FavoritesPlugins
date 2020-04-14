package com.wxp.swt.jface.sample;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class CompositeExample {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		
		shell.setText("Composite Example");
		shell.setBounds(100, 100, 200, 200);
		
		Composite composite = new Composite(shell, SWT.BORDER);
		composite.setBounds(25, 25, 150, 125);
		final Button button = new Button(composite, SWT.PUSH);
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
