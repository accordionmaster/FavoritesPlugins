package com.wxp.swt.jface.sample;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;

public class TableExample {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		
		shell.setText("Table Example");
		shell.setBounds(100, 100, 200, 100);
		shell.setLayout(new FillLayout());
		
		final Table table = new Table(shell, SWT.SINGLE | SWT.BORDER | SWT.FULL_SELECTION);
		table.setHeaderVisible(true);
		table.setLinesVisible(true);
		TableColumn column_1 = new TableColumn(table, SWT.NULL);
		column_1.setText("Name");
		column_1.pack();
		TableColumn column_2 = new TableColumn(table, SWT.NULL);
		column_2.setText("Age");
		column_2.pack();
		TableItem item_1 = new TableItem(table, SWT.NULL);
		item_1.setText(new String[] {"Dan", "43"});
		
		TableItem item_2 = new TableItem(table, SWT.NULL);
		item_2.setText(new String[] {"Eric", "44"});
		table.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				TableItem[] selected = table.getSelection();
				if (selected.length > 0) {
					System.out.println("Name:" + selected[0].getText(0));
					System.out.println("Age:" + selected[0].getText(1));
				}
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
