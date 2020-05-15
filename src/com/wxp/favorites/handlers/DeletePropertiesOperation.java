package com.wxp.favorites.handlers;

import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.operations.AbstractOperation;
import org.eclipse.core.runtime.IAdaptable;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;

import com.wxp.favorites.editors.PropertyCategory;
import com.wxp.favorites.editors.PropertyElement;
import com.wxp.favorites.editors.PropertyEntry;
import com.wxp.favorites.editors.PropertyFile;

public class DeletePropertiesOperation extends AbstractOperation{

	private final PropertyElement[] elements;
	private PropertyElement[] parents;
	private int[] indexes;
	
	public DeletePropertiesOperation(PropertyElement[] elements) {
		super(getLabelFor(elements));
		this.elements = elements;
	}
	
	/**
	 * 构造函数调用getLabelFor()方法并基于当前选中元素为操作生成可读标签。该标签在所有
	 * 撤销、重做命令出现的地方出现，如在Edit菜单中。
	 * @param elements
	 * @return
	 */
	private static String getLabelFor(PropertyElement[] elements) {
		if (elements.length == 1) {
			PropertyElement first = elements[0];
			if (first instanceof PropertyEntry) {
				PropertyEntry propEntry = (PropertyEntry) first;
				return "Remove property " + propEntry.getKey();
			}
			if (first instanceof PropertyCategory) {
				PropertyCategory propEntry = (PropertyCategory) first;
				return "Remove category " + propEntry.getName();
			}
		}
		return "Remove properties";
	}


	/**
	 * execute()方法提示用户以确认操作并移除指定的属性。如果Info参数不是null，那么可以向
	 * 它请求一个UI上下文。在该上下文中可以在运行过程中提示用户输入信息。如果监视器参数不是
	 * null,那么它可以被用于在运行过程中向用户提供进度反馈。该方法仅当第一次执行操作时被调用。
	 */
	@Override
	public IStatus execute(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		// If a UI context has been provided.
		// then prompt the user to confirm the operation.
		
		if (info != null) {
			Shell shell = info.getAdapter(Shell.class);
			if (shell != null) {
				if (!MessageDialog.openQuestion(shell, "Remove properties", "Do you want to remove the currently selected properties?")) {
					return Status.CANCEL_STATUS;
				}
			}
		}
		
		// Perform the operation.
		
		return redo(monitor, info);
	}

	
	/**
	 * execute()方法调用redo()方法以执行实际的属性移除。该方法记录在两个额外字段中被移除的元素信息，
	 * 以是该操作可以被撤销。传递给redo()方法的参数与那些被提供给之前描述的execute()方法是
	 * 完全相同的。
	 */
	@Override
	public IStatus redo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		// Perform the operation, providing feedback to the user
		// through the progress monitor if one is provided.
		
		parents = new PropertyElement[elements.length];
		indexes = new int[elements.length];
		
		if (monitor != null) {
			monitor.beginTask("Remove properties", elements.length);
		}
		
		Shell shell = info.getAdapter(Shell.class);
		shell.setRedraw(false);
		try {
			for(int i = elements.length; --i >= 0;) {
				parents[i] = elements[i].getParent();
				PropertyElement[] children = parents[i].getChildren();
				for(int index = 0; index < children.length; index++) {
					if (children[index] == elements[i]) {
						indexes[i] = index;
						break;
					}
				}
				elements[i].removeFromParent();
				if (monitor != null) {
					monitor.worked(1);
				}
			}
		} finally {
			shell.setRedraw(true);
		}
		
		if (monitor != null) {
			monitor.done();
		}
		return Status.OK_STATUS;
	}

	/**
	 * undo()方法通过重新向模型插入被移除的元素反转当前的操作。
	 */
	@Override
	public IStatus undo(IProgressMonitor monitor, IAdaptable info) throws ExecutionException {
		Shell shell = info.getAdapter(Shell.class);
		shell.setRedraw(false);
		try {
			for (int i = 0; i < elements.length; i++) {
				if (parents[i] instanceof PropertyCategory) {
					((PropertyCategory) parents[i]).addEntry(indexes[i], (PropertyEntry) elements[i]);
				}else {
					((PropertyFile) parents[i]).addCategory(indexes[i], (PropertyCategory) elements[i]);
				}
			}
		} finally {
			shell.setRedraw(true);
		}
		return Status.OK_STATUS;
	}

}
