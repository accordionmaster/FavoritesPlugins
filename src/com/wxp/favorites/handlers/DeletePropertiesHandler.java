package com.wxp.favorites.handlers;

import java.util.Iterator;

import javax.sound.midi.MidiChannel;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.commands.IHandlerListener;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.handlers.HandlerUtil;

import com.wxp.favorites.editors.PropertiesEditor;
import com.wxp.favorites.editors.PropertyElement;

/**
 * 不移除选中属性，DeletePropertiesHandler现在必须创建一个奖杯移除的属性的数组，并稍后传递
 * 至一个DeletePropertiesOperation的实例。该操作与一个用于提示用户和进度监视器输入用户反馈的
 * UI上下文一起被传递至编辑器的撤销、重做管理程序以用于执行。如果在运行过程中发生了异常，你可以
 * 使用ExceptionsDetailsDialog而不是下面的MessageDialog。
 * @author Dell
 *
 */
public class DeletePropertiesHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelection selection = HandlerUtil.getCurrentSelection(event);
		if (!(selection instanceof IStructuredSelection)) {
			return null;
		}
		final IEditorPart editor = HandlerUtil.getActiveEditor(event);
		if (!(editor instanceof PropertiesEditor)) {
			return null;
		}
		return execute((PropertiesEditor) editor, (IStructuredSelection) selection);
	}

	private Object execute(final PropertiesEditor editor, IStructuredSelection selection) {
		
		// Build an array of properties to be removed.
		Iterator<?> iter = selection.iterator();
		int size = selection.size();
		PropertyElement[] elements = new PropertyElement[size];
		for (int i = 0; i < size; i++) {
			elements[i] = (PropertyElement) ((Object) iter.next());
		}
		
		// Build the operation to be performed.
		DeletePropertiesOperation op = 
				new DeletePropertiesOperation(elements);
		op.addContext(editor.getUndoContext());
		
		// The progress monitor so the operation can inform the user.
		IProgressMonitor monitor = editor.getEditorSite().getActionBars()
				.getStatusLineManager().getProgressMonitor();
		
		// An adapter for providing UI context to the operation.
		IAdaptable info = new IAdaptable() {
			
			@Override
			public Object getAdapter(Class adapter) {
				if (Shell.class.equals(adapter)) {
					return editor.getSite().getShell();
				}
				return null;
			}
		};
		
		// Execute the operation.
		try {
			editor.getOperationHistory().execute(op, monitor, info);
		} catch (ExecutionException e) {
			MessageDialog.openError(editor.getSite().getShell(), "Remove Properties Error", "Exception while removing properties: " + e.getMessage());
		}
		
		return null;
	}

}
