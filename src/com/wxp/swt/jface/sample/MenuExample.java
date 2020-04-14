package com.wxp.swt.jface.sample;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.Shell;

public class MenuExample {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		
		shell.setText("Menu Example");
		shell.setBounds(100, 100, 200, 100);
		Menu menu = new Menu(shell, SWT.BAR);
		shell.setMenuBar(menu);
		MenuItem fileMenu = new MenuItem(menu, SWT.CASCADE);
		fileMenu.setText("&File");
		Menu subMenu = new Menu(shell, SWT.DROP_DOWN);
		fileMenu.setMenu(subMenu);
		MenuItem selectItem = new MenuItem(subMenu, SWT.NULL);
		selectItem.setText("&Select Me Now \tCtrl +'S'");
		selectItem.setAccelerator(SWT.CTRL + 'S');
		selectItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("I was selected!");
			}
		});
		MenuItem sep = new MenuItem(subMenu, SWT.SEPARATOR);
		MenuItem exitItem = new MenuItem(subMenu, SWT.NULL);
		exitItem.setText("&Exit");
		exitItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				shell.dispose();
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
