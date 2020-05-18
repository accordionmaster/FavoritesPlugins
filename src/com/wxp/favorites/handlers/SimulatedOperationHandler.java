package com.wxp.favorites.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import com.wxp.favorites.FavoritesLog;

/**
 * A do nothing handler showing simulated progress in the window's status bar
 */
public class SimulatedOperationHandler extends AbstractHandler{

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		try {
			window.run(true, true, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					monitor.beginTask("simulate status bar progress:", 20);
					for (int i = 20; i > 0; --i) {
						monitor.subTask("seconds left = i");
						Thread.sleep(1000);
						monitor.worked(1);
					}
					monitor.done();
				}
			});
		} catch (InvocationTargetException e) {
			FavoritesLog.logError(e);
		} catch (InterruptedException e) {
			// User canceld the operation ... just ignore.
		}
		return null;
	}

}
