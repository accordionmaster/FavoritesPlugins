package com.wxp.favorites.perspectives;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

/**
 * 该类描述了透视图的布局。该类通过使用它的无参构造函数初始化，但可以使用IExecutableExetension接口赋予参数。
 * IPerspectiveFactory接口定义了一个单独大的方法，createInitialLayout()，它指定了初始页面的布局和透视图的
 * 可见操作集。工厂仅用于定义透视图的初始布局，并且然后就被忽略。默认的，布局区域包含了编辑器的空间，而不是视图。
 * 工厂可以添加附加视图。这些视图被放置于相对编辑器区域或另一个视图的位置。
 * 打开新建的FavoritesPerspectiveFactory类，修改以下内容，这样，Favorites视图将出现于编辑器区域的下方，并且
 * 标准Outline视图将会在它左侧显示。
 * @author Dell
 *
 */
public class FavoritesPerspectiveFactory implements IPerspectiveFactory {
	
	private static final String FAVORITES_VIEW_ID = 
			"com.wxp.favorites.views.FavoritesView";
	private static final String FAVORITES_ACTION_ID = 
			"com.wxp.favorites.workbenchActionSet";

	/**
	 * 在createInitialLayout方法内部，addView()方法用于添加标准Outline视图至编辑器区域的左侧，
	 * 它将占据窗口内部25%的区域。使用createEditor()方法，可以创建一个文件夹布局以占据编辑器区域下面
	 * 底部的三分之一。Favorites视图和标准Tasks视图被添加至文件夹布局，使得每一个都将在文件夹内部以具有
	 * 标签的形式堆叠出现。
	 * 然后，一个用于标准Problem视图的占位符被添加至该文件夹。如果用户打开Problem视图，它将在由占位符指定
	 * 的位置打开。最终，Favorites操作集被设为在透视图内部默认可见的。
	 * 为了打开Favorites透视图，选择Window--Open Perspective --Other。。。然后选择Favorites。
	 */
	@Override
	public void createInitialLayout(IPageLayout layout) {
		
		// Get the editor area.
		String editorArea = layout.getEditorArea();
		
		// Put the Outline view on the left.
		layout.addView(IPageLayout.ID_OUTLINE, IPageLayout.LEFT, 0.25f, editorArea);
		
		// Put the Favorites view on the bottom with
		// the Tasks view.
		IFolderLayout bottom = 
				layout.createFolder("bottom", IPageLayout.BOTTOM, 0.66f, editorArea);
		bottom.addView(FAVORITES_VIEW_ID);
		bottom.addView(IPageLayout.ID_TASK_LIST);
		bottom.addPlaceholder(IPageLayout.ID_PROBLEM_VIEW);
		
		// Add the Favorites action set.
		layout.addActionSet(FAVORITES_ACTION_ID);

	}

}
