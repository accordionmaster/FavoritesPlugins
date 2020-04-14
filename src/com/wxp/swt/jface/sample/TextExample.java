package com.wxp.swt.jface.sample;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;

public class TextExample {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		
		shell.setText("Text Example");
		shell.setBounds(100, 100, 200, 100);
		shell.setLayout(new FillLayout());
		
		final Text text = new Text(shell, SWT.MULTI);
		
		text.addVerifyListener(new VerifyListener() {
			
			@Override
			public void verifyText(VerifyEvent e) {
				e.doit = e.text.length() == 0 
						|| Character.isDigit(e.text.charAt(0)); 
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
