package com.wxp.favorites.handlers;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.SubProgressMonitor;
import org.eclipse.ui.actions.WorkspaceModifyOperation;

/**
 * 当任何UI插件修改资源时，他应通过继承org.eclipse.ui.actions.WorkspaceModifyOperation
 * 封装资源修改代码。使用该操作的主要后果是一般作为工作区修改的后果（比如，对资源增量的触发，执行自动构建等）
 * 而发生的事件将被延迟知道最外层的操作已经成功完成之后发生。在Favorites视图中，
 * 如果你想要实现一个本身删除底层资源的删除操作，而不是仅删除引用该资源的Favorites项，
 * 那么它可以根据以下所示来实现。
 * 
 * 从WorkspaceModifyOperation继承并被Action或IActionDelegate调用的run()方法，
 * 首先调用execute()方法，然后触发一个更改事件。该事件包含了所有由execute()方法更改的
 * 资源。
 * 
 * @author Dell
 *
 */
public class DeleteResourcesOperation extends WorkspaceModifyOperation{

	private final IResource[] resources;
	
	public DeleteResourcesOperation(IResource[] resources) {
		this.resources = resources;
	}
	
	@Override
	protected void execute(IProgressMonitor monitor)
			throws CoreException, InvocationTargetException, InterruptedException {
		monitor.beginTask("Deleting resources...", resources.length);
		for (int i = 0; i < resources.length; i++) {
			if (monitor.isCanceled()) {
				break;
			}
			resources[i].delete(true, new SubProgressMonitor(monitor, 1));
		}
		monitor.done();
	}
	

}
