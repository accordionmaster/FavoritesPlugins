package com.wxp.swt.jface.sample;


import org.eclipse.jface.text.Document;
import org.eclipse.jface.text.TextPresentation;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;

public class TextViewerExample {

	public static void main(String[] args) {
		Display display = new Display();
		Shell shell = new Shell(display);
		shell.setText("Tree Viewer Example");
		shell.setBounds(100, 100, 225, 125);
		shell.setLayout(new FillLayout());

		final TextViewer textViewer = new TextViewer(shell, SWT.MULTI | SWT.V_SCROLL);
		
		String string = "This is plain text \n"
				+ "This is bold text \n"
				+ "This is red text \n";
		Document document = new Document(string);
		textViewer.setDocument(document);
		
		TextPresentation style = new TextPresentation();
		style.addStyleRange(new StyleRange(19, 17, null, null, SWT.BOLD));
		Color red = new Color(null,255, 0,0);
		style.addStyleRange(new StyleRange(37, 18, red, null));
		textViewer.changeTextPresentation(style, true);
		
		shell.open();
		while (!shell.isDisposed ()) {
		   if (!display.readAndDispatch ()) display.sleep ();
		}
		display.dispose ();
	}
}
