package com.wxp.swt.jface.sample;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;

public class ComboExample {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		
		shell.setText("Combo Example");
		shell.setBounds(100, 100, 200, 100);
		shell.setLayout(new FillLayout(SWT.VERTICAL));
		
		final Combo combo_1 = new Combo(shell, SWT.READ_ONLY);
		final Combo combo_2 = new Combo(shell, SWT.DROP_DOWN);
		final Label label = new Label(shell, SWT.CENTER);
		
		combo_1.setItems(new String[] {
				"First", "Second", "Third"
		});
		combo_2.setText("First");
		combo_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				label.setText("Selected:" + combo_1.getText());
			}
		});
		combo_2.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				label.setText("Entered:" + combo_2.getText());
				
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
