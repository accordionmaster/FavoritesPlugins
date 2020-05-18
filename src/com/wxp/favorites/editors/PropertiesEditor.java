package com.wxp.favorites.editors;

import javax.naming.InitialContext;

import org.eclipse.core.commands.operations.IOperationHistory;
import org.eclipse.core.commands.operations.IUndoContext;
import org.eclipse.core.commands.operations.ObjectUndoContext;
import org.eclipse.core.commands.operations.OperationHistoryFactory;
import org.eclipse.core.commands.operations.UndoContext;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.ui.wizards.NewAnnotationWizardPage;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TreeColumnLayout;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ICellEditorListener;
import org.eclipse.jface.viewers.ICellEditorValidator;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorActionBarContributor;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.contexts.IContextActivation;
import org.eclipse.ui.contexts.IContextService;
import org.eclipse.ui.editors.text.TextEditor;
import org.eclipse.ui.ide.IGotoMarker;
import org.eclipse.ui.operations.RedoActionHandler;
import org.eclipse.ui.operations.UndoActionHandler;
import org.eclipse.ui.part.MultiPageEditorPart;
import org.eclipse.ui.texteditor.ITextEditor;

import com.wxp.favorites.FavoritesLog;
import com.wxp.favorites.views.AltClickCellEditListener;

/**
 * 新的PropertiesEditor是一个包含Properties和Source页的多页面编辑器。
 * Properties页面包含了一个显示属性名、值对的树，而Source页面显示
 * 
 * @author Dell
 *
 */
public class PropertiesEditor extends MultiPageEditorPart {

	// 创建两个字段和方法以创建Source和Properties页面。
	private TreeViewer treeViewer;
	private TextEditor textEditor;

	private TreeColumn keyColumn;
	private TreeColumn valueColumn;

	private PropertiesEditorContentProvider treeContentProvider;
	private PropertiesEditorLabelProvider treeLabelProvider;

	private boolean isPageModified;
	
	private UndoActionHandler undoAction;
	private RedoActionHandler redoAction;
	private IUndoContext undoContext;

	private final PropertyFileListener propertyFileListener = new PropertyFileListener() {

		@Override
		public void valueChanged(PropertyCategory category, PropertyEntry entry) {
			treeViewer.refresh(entry);
			treeModified();
		}

		@Override
		public void nameChanged(PropertyCategory category) {
			treeViewer.refresh(category);
			treeModified();
		}

		@Override
		public void keyChanged(PropertyCategory category, PropertyEntry entry) {
			treeViewer.refresh(entry);
			treeModified();
		}

		@Override
		public void entryRemoved(PropertyCategory category, PropertyEntry entry) {
			treeViewer.refresh();
			treeModified();
		}

		@Override
		public void entryAdded(PropertyCategory category, PropertyEntry entry) {
			treeViewer.refresh();
			treeModified();
		}

		@Override
		public void categoryRemoved(PropertyCategory category) {
			treeViewer.refresh();
			treeModified();
		}

		@Override
		public void categoryAdded(PropertyCategory category) {
			treeViewer.refresh();
			treeModified();
		}
	};
	private IAction removeAction;

	@Override
	public void init(IEditorSite site, IEditorInput input) throws PartInitException {
		if (!(input instanceof IFileEditorInput)) {
			throw new PartInitException("Invalid Input: Must be IFileEditorInput");
		}
		super.init(site, input);
	}

	@Override
	protected void createPages() {
		createPropertiesPage();
		createSourcePage();
		updateTitle();
		initTreeContent();
		initTreeEditors();
		createContextMenu();
		initKeyBindingContext();
		initUndoRedo();
	}

	private void createPropertiesPage() {
		Composite treeContainer = new Composite(getContainer(), SWT.NONE);
		TreeColumnLayout layout = new TreeColumnLayout();
		treeContainer.setLayout(layout);

		treeViewer = new TreeViewer(treeContainer, SWT.MULTI | SWT.FULL_SELECTION);
		Tree tree = treeViewer.getTree();
		tree.setHeaderVisible(true);

		keyColumn = new TreeColumn(tree, SWT.NONE);
		keyColumn.setText("Key");
		layout.setColumnData(keyColumn, new ColumnWeightData(2));

		valueColumn = new TreeColumn(tree, SWT.NONE);
		valueColumn.setText("Value");
		layout.setColumnData(valueColumn, new ColumnWeightData(3));

		int index = addPage(treeContainer);
		setPageText(index, "Properties");
		getSite().setSelectionProvider(treeViewer);
	}

	private void createSourcePage() {
		try {
			textEditor = new TextEditor();
			int index = addPage(textEditor, getEditorInput());
			setPageText(index, "Source");
		} catch (PartInitException e) {
			FavoritesLog.logError("Error creating nested text editor", e);
		}

	}

	private void updateTitle() {
		IEditorInput input = getEditorInput();
		setPartName(input.getName());
		setTitleToolTip(input.getToolTipText());
	}

	void initTreeContent() {
		treeContentProvider = new PropertiesEditorContentProvider();
		treeViewer.setContentProvider(treeContentProvider);
		treeLabelProvider = new PropertiesEditorLabelProvider();
		treeViewer.setLabelProvider(treeLabelProvider);

		// Reset the input from the text editor's content
		// after the editor initialization has completed.
		treeViewer.setInput(new PropertyFile(""));
		treeViewer.getTree().getDisplay().asyncExec(new Runnable() {

			@Override
			public void run() {
				updateTreeFromTextEditor();
			}
		});
		treeViewer.setAutoExpandLevel(TreeViewer.ALL_LEVELS);
	}

	/**
	 * 与创建createInlineEditor方法类似，创建一个新的initTreeEditors()方法。
	 * createPages()将调用它。该方法初始化两个TreeViewerColumn实例用于在名称和值两个列中分别管理 单元格编辑器。
	 */
	private void initTreeEditors() {
		TreeViewerColumn column1 = new TreeViewerColumn(treeViewer, keyColumn);
		TreeViewerColumn column2 = new TreeViewerColumn(treeViewer, valueColumn);

		column1.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return treeLabelProvider.getColumnText(element, 0);
			}
		});

		column2.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return treeLabelProvider.getColumnText(element, 1);
			}
		});

		/**
		 * 在第一列中，用户可以编辑类别的名称或名/值对的名称。EditorSupport用于初始化一个合适
		 * 的单元格编辑器，为单元格编辑器获取合适的文本，并保存修改过的文本至模型。
		 */
		column1.setEditingSupport(new EditingSupport(treeViewer) {

			TextCellEditor editor = null;

			@Override
			protected void setValue(Object element, Object value) {
				if (value == null) {
					return;
				}
				String text = ((String) value).trim();
				if (element instanceof PropertyCategory) {
					((PropertyCategory) element).setName(text);
				}
				if (element instanceof PropertyEntry) {
					((PropertyEntry) element).setKey(text);
				}
			}

			@Override
			protected Object getValue(Object element) {
				return treeLabelProvider.getColumnText(element, 0);
			}

			/**
			 * 单元格编辑器具有验证器以阻止不合法的输入访问模型对象。无论何时用户修改单元格编辑器的内容，isValid(Object)方法
			 * 当对象代表一个非法值时返回一个错误消息，当值是合法时返回null。
			 */
			@Override
			protected CellEditor getCellEditor(Object element) {
				if (editor == null) {
					Composite tree = (Composite) treeViewer.getControl();
					editor = new TextCellEditor(tree);
					editor.setValidator(new ICellEditorValidator() {

						@Override
						public String isValid(Object value) {
							if (((String) value).trim().length() == 0) {
								return "Key must not be empty string";
							}
							return null;
						}
					});

					editor.addListener(new ICellEditorListener() {

						@Override
						public void editorValueChanged(boolean oldValidState, boolean newValidState) {
							setErrorMessage(editor.getErrorMessage());
						}

						@Override
						public void cancelEditor() {
							setErrorMessage(null);
						}

						@Override
						public void applyEditorValue() {
							setErrorMessage(null);
						}

						private void setErrorMessage(String errorMessage) {
							getEditorSite().getActionBars().getStatusLineManager().setErrorMessage(errorMessage);
						}
					});
				}
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

		/**
		 * 我们为第二列创建一个类似的EditingSupport对象，但修改它以允许编辑名/值对中的值。
		 */
		column2.setEditingSupport(new EditingSupport(treeViewer) {
			TextCellEditor editor = null;

			@Override
			protected void setValue(Object element, Object value) {
				String text = ((String) value).trim();
				if (element instanceof PropertyEntry) {
					((PropertyEntry) element).setValue(text);
				}
			}

			@Override
			protected Object getValue(Object element) {
				return treeLabelProvider.getColumnText(element, 1);
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (editor == null) {
					Composite tree = (Composite) treeViewer.getControl();
					editor = new TextCellEditor(tree);
				}
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return element instanceof PropertyEntry;
			}
		});

		treeViewer.getColumnViewerEditor().addEditorActivationListener(new AltClickCellEditListener());

		treeViewer.getTree().addKeyListener(new KeyListener() {

			private boolean isAltPressed;

			@Override
			public void keyReleased(KeyEvent e) {
				if (e.keyCode == SWT.ALT) {
					isAltPressed = false;
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
				if (e.keyCode == SWT.ALT) {
					isAltPressed = true;
				}
				if (e.character == SWT.DEL) {
					removeAction.run();
				}

			}
		});
	}

	/**
	 * 通过代码方式而不是通过插件清单方式来添加操作至编辑器。 创建上下文菜单： 上下文菜单必须和编辑器同时创建。然而由于添加者可以根据选择添加或移除菜单项，它的
	 * 内容直到用户在点击鼠标右键之后，在菜单显示之前才能被确定。为了实现该功能，设置菜单的
	 * RemoveAllWhenShown属性为true以使菜单每次都重新创建，并添加一个菜单监听器以动态地
	 * 创建菜单。此外，菜单必须被注册至控件，以使它可以被显示。菜单还必须注册至编辑器站点，以使 其他插件可以向其添加命令。
	 */
	private void createContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager m) {
				PropertiesEditor.this.fillContextMenu(m);
			}
		});
		Tree tree = treeViewer.getTree();
		Menu menu = menuMgr.createContextMenu(tree);
		tree.setMenu(menu);
		getSite().registerContextMenu(menuMgr, treeViewer);
	}

	/**
	 * 动态创建上下文菜单 每当用户点击鼠标右键时，上下文菜单的内容必须被重新创建。这是因为添加者可以根据
	 * 编辑器的选择添加命令。此外，上下文菜单必须包含一个具有“edit”的分割线用于我们紫的命令
	 * 和另一个具有IWorkbenchActionConstants.MB_ADDITIONS的分隔线，表示那些被添加项将会出现 于上下文菜单中。
	 * 
	 * @param menuMgr
	 */
	protected void fillContextMenu(IMenuManager menuMgr) {
		menuMgr.add(undoAction);
		menuMgr.add(redoAction);
		menuMgr.add(new Separator("edit"));
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void initKeyBindingContext() {
		final IContextService service = getSite().getService(IContextService.class);

		treeViewer.getControl().addFocusListener(new FocusListener() {

			IContextActivation currentContext = null;

			@Override
			public void focusLost(FocusEvent e) {
				if (currentContext == null) {
					currentContext = service.activateContext("com.wxp.properties.editor.context");
				}
			}

			@Override
			public void focusGained(FocusEvent e) {
				if (currentContext != null) {
					service.deactivateContext(currentContext);
				}
			}
		});
	}
	
	private void initUndoRedo() {
		undoContext = new ObjectUndoContext(this);
		undoAction = new UndoActionHandler(getSite(), undoContext);
		redoAction = new RedoActionHandler(getSite(), undoContext);
	}
	
	private void setTreeUndoRedo() {
		final IActionBars actionBars = getEditorSite().getActionBars();
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction);
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction);
		actionBars.updateActionBars();
	}
	
	private void setTextEditorUndoRedo() {
		final IActionBars actionBars = getEditorSite().getActionBars();
		IAction undoAction2 = textEditor.getAction(ActionFactory.UNDO.getId());
		actionBars.setGlobalActionHandler(ActionFactory.UNDO.getId(), undoAction2);
		IAction redoAction2 = textEditor.getAction(ActionFactory.REDO.getId());
		actionBars.setGlobalActionHandler(ActionFactory.REDO.getId(), redoAction2);
		actionBars.updateActionBars();
		// 页面发生变化时清理撤销、重做栈。
		getOperationHistory().dispose(undoContext, true, true, false);
	}

	protected void updateTreeFromTextEditor() {
		PropertyFile propertyFile = (PropertyFile) treeViewer.getInput();
		propertyFile.removePropertyFileListener(propertyFileListener);
		propertyFile = new PropertyFile(
				textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput()).get());
		treeViewer.setInput(propertyFile);
		propertyFile.addPropertyFileListener(propertyFileListener);
	}

	void updateTextEditorFromTree() {
		textEditor.getDocumentProvider().getDocument(textEditor.getEditorInput())
				.set(((PropertyFile) treeViewer.getInput()).asText());
	}

	public void treeModified() {
		boolean wasDirty = isDirty();
		isPageModified = true;
		if (!wasDirty) {
			firePropertyChange(IEditorPart.PROP_DIRTY);
		}
	}

	@Override
	public boolean isDirty() {
		return isPageModified || super.isDirty();
	}

	/**
	 * 当在Properties和Source页面切换时，所有在Properties页面中做出的编辑必须自动同步至Source 页面，反之亦然。
	 * 在pageChange方法中加入setTreeUndoRedo和setTextEditorUndoRedo，这样新的撤销和
	 * 重做操作将会被关联至Edit菜单中的全局撤销和重做操作。
	 */
	@Override
	protected void pageChange(int newPageIndex) {
		switch (newPageIndex) {
		case 0:
			if (isDirty()) {
				updateTreeFromTextEditor();
			}
			setTreeUndoRedo();
			break;
		case 1:
			if (isPageModified) {
				updateTextEditorFromTree();
			}
			setTextEditorUndoRedo();
			break;
		}
		isPageModified = false;
		super.pageChange(newPageIndex);

		IEditorActionBarContributor contributor = getEditorSite().getActionBarContributor();
		if (contributor instanceof PropertiesEditorContributor) {
			((PropertiesEditorContributor) contributor).setActivePage(this, newPageIndex);
		}
	}

	/**
	 * 无论何时修改了文本编辑器，MultiPageEditorPart的addPage()方法都将使用handlePropertyChange(int)
	 * 方法以当编辑器内容改变时通知其他部分。
	 */
	@Override
	protected void handlePropertyChange(int propertyId) {
		if (propertyId == IEditorPart.PROP_DIRTY) {
			isPageModified = isDirty();
		}
		super.handlePropertyChange(propertyId);
	}

	// 当焦点移至编辑器时，将调用setFocus()方法。
	// 该方法必须随后根据当前选中的页面将焦点重定向至合适的编辑器。
	@Override
	public void setFocus() {
		switch (getActivePage()) {
		case 0:
			treeViewer.getTree().setFocus();
			break;
		case 1:
			textEditor.setFocus();
			break;
		}
	}

	// 当用户直接或间接请求显示一个标记时，要保证Source页面是活动的，然后将请求重定向至文本编辑器。
	// 当Properties页面是活动的时，你可以做一些不同的事。但是这将需要额外的编辑器模型结构。
	public void gotoMarker(IMarker marker) {
		setActivePage(1);
		((IGotoMarker) textEditor.getAdapter(IGotoMarker.class)).gotoMarker(marker);
	}

	/**
	 * 这三个方法用于保存编辑器的内容。如果isSaveAsAllowed()方法返回false，那么将不会调用doSaveAs()方法。
	 * 由于当前实现使用了嵌套的文本编辑器来保存内容至当前被编辑的文件，Properties页面中的
	 * 更改将不会被发觉除非用户切换至Source页面。必须修改一下方法以在保存前更新嵌套的文本编辑器。
	 * 由于保存操作一般都是长时间的操作，进度监视器用于与用户交流进度。
	 */
	@Override
	public void doSave(IProgressMonitor monitor) {
		if (getActivePage() == 0 && isPageModified) {
			updateTextEditorFromTree();
		}
		isPageModified = true;
		textEditor.doSave(monitor);
	}

	@Override
	public void doSaveAs() {
		if (getActivePage() == 0 && isPageModified) {
			updateTextEditorFromTree();
		}
		isPageModified = false;
		textEditor.doSaveAs();
		setInput(textEditor.getEditorInput());
		updateTitle();
	}

	// 返回“保存为”操作是否是由该组件支持。
	@Override
	public boolean isSaveAsAllowed() {
		return true;
	}

	public ITextEditor getSourceEditor() {
		return textEditor;
	}

	public IAction getTreeAction(String workbenchActionId) {
		if (ActionFactory.DELETE.getId().equals(workbenchActionId)) {
			return removeAction;
		}
		return null;
	}

	public IOperationHistory getOperationHistory() {
		// The workbench provides its own undo/redo manager
		// return PlatformUI.getWorkbench()
		// .getOperationSupport().getOperationHistory();

		// which, in this case, is the same as the default undo manager
		return OperationHistoryFactory.getOperationHistory();
	}

	public IUndoContext getUndoContext() {
		return undoContext;
	}
}
