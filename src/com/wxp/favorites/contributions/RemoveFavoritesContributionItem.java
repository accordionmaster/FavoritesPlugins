package com.wxp.favorites.contributions;

import java.util.Collections;

import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.expressions.IEvaluationContext;
import org.eclipse.jface.action.ContributionItem;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.CoolBar;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.MenuItem;
import org.eclipse.swt.widgets.ToolBar;
import org.eclipse.swt.widgets.ToolItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.internal.ViewSite;

import com.wxp.favorites.FavoritesLog;
import com.wxp.favorites.views.FavoritesView;

/**
 * 在程序中创建视图上下文需要几个步骤，如果你想要其他插件通过插件清单中的声明添加命令至你的 视图上下文，那么必须使用更多几个步骤以注册你的视图。
 * 1.创建添加项 第一步市创建将出现于上下文菜单的添加项。 对于Favorites视图来说，将选中元素从视图中移除是必要的。
 * RemoveFavoritesContributionItem继承了 ContributionItem，如果父类的isDynamic返回true，
 * 那么当每一次显示上下文菜单时，而不是上下文菜单第一次显示时，都会调用fill（）方法。
 * 这当你的操作更改可见性时是有用的。在我们的示例中，我们不覆盖isDynamic，这是因为我们的操作不改变可见性，而改变可用性。
 * 查看父类，isDynamic方法返回false。 Contribution that removes the currently selected
 * Favorite items
 * 
 * @author Dell
 *
 */
public class RemoveFavoritesContributionItem extends ContributionItem {

	private final IViewSite viewSite;
	private final IHandler handler;
	boolean enabled = false;
	private MenuItem menuItem;
	private ToolItem toolItem;

	public RemoveFavoritesContributionItem(IViewSite viewSite, IHandler handler) {
		this.viewSite = viewSite;
		this.handler = handler;
		viewSite.getSelectionProvider().addSelectionChangedListener(new ISelectionChangedListener() {

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				enabled = !event.getSelection().isEmpty();
				updateEnablement();
			}
		});

	}

	@Override
	public void fill(Menu menu, int index) {
		menuItem = new MenuItem(menu, SWT.NONE, index);
		menuItem.setText("Remove");
		menuItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				run();
			}
		});
		updateEnablement();
	}

	@Override
	public void fill(ToolBar parent, int index) {
		toolItem = new ToolItem(parent, SWT.NONE, index);
		toolItem.setToolTipText("Remove the selected favorite items");
		toolItem.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				run();
			}
		});
		updateEnablement();
	}

	protected void updateEnablement() {
		Image image = PlatformUI.getWorkbench().getSharedImages()
				.getImage(enabled ? ISharedImages.IMG_TOOL_DELETE : ISharedImages.IMG_TOOL_DELETE_DISABLED);
		if (menuItem != null) {
			menuItem.setImage(image);
			menuItem.setEnabled(enabled);
		}
		
		if (toolItem != null) {
			toolItem.setImage(image);
			toolItem.setEnabled(enabled);
		}
	}

	public void run() {
		final IHandlerService handlerService = (IHandlerService) viewSite.getService(IHandlerService.class);
		IEvaluationContext evaluationContext = handlerService.createContextSnapshot(true);
		ExecutionEvent event = new ExecutionEvent(null, Collections.EMPTY_MAP, null, evaluationContext);
		try {
			handler.execute(event);
		} catch (ExecutionException e) {
			FavoritesLog.logError(e);
		}

	}

}
