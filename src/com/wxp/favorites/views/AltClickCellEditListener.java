package com.wxp.favorites.views;

import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.MouseEvent;

public class AltClickCellEditListener extends ColumnViewerEditorActivationListener {

	@Override
	public void beforeEditorActivated(ColumnViewerEditorActivationEvent event) {
		if (event.eventType == ColumnViewerEditorActivationEvent.MOUSE_CLICK_SELECTION) {
			if (!(event.sourceEvent instanceof MouseEvent)) {
				event.cancel = true;
			} else {
				MouseEvent mouseEvent = (MouseEvent) event.sourceEvent;
				if ((mouseEvent.stateMask & SWT.ALT) == 0) {
					event.cancel = true;
				}
			}
		} else if (event.eventType != ColumnViewerEditorActivationEvent.PROGRAMMATIC) {
			event.cancel = true;
		}

	}

	@Override
	public void afterEditorActivated(ColumnViewerEditorActivationEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void beforeEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
		// TODO Auto-generated method stub

	}

	@Override
	public void afterEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {
		// TODO Auto-generated method stub

	}

}
