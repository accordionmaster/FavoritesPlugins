package com.wxp.favorites.views;

import java.util.Comparator;

import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.ColumnLabelProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationEvent;
import org.eclipse.jface.viewers.ColumnViewerEditorActivationListener;
import org.eclipse.jface.viewers.ColumnViewerEditorDeactivationEvent;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.EditingSupport;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.TableViewerColumn;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISelectionListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.IHandlerActivation;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import org.eclipse.ui.texteditor.IWorkbenchActionDefinitionIds;

import com.wxp.favorites.FavoritesActivator;
import com.wxp.favorites.actions.FavoritesViewFilterAction;
import com.wxp.favorites.contributions.RemoveFavoritesContributionItem;
import com.wxp.favorites.handlers.RemoveFavoritesHandler;
import com.wxp.favorites.handlers.RenameFavoritesHandler;
import com.wxp.favorites.model.FavoritesManager;
import com.wxp.favorites.model.IFavoriteItem;
import com.wxp.favorites.preferences.PreferenceConstants;
import com.wxp.favorites.util.EditorUtil;

/**
 * This sample class demonstrates how to plug-in a new workbench view. The view
 * shows data obtained from the model. The sample creates a dummy model on the
 * fly, but a real implementation would connect to the model available either in
 * this or another plug-in (e.g. the workspace). The view is connected to the
 * model using a content provider.
 * <p>
 * The view uses a label provider to define how model objects should be
 * presented in the view. Each view can present the same model objects using
 * different labels and icons, if needed. Alternatively, a single label provider
 * can be shared between views in order to ensure that objects of the same type
 * are presented in the same way everywhere.
 * <p>
 */

public class FavoritesView extends ViewPart {
	
	private static final int NAME_COLUMN_INITIAL_WIDTH = 200;
	private static final int LOCATION_COLUMN_INITIAL_WIDTH = 450;

	/**
	 * The ID of the view as specified by the extension.
	 */
	public static final String ID = "com.wxp.favorites.views.FavoritesView";

	private TableViewer viewer;

	private TableColumn typeColumn;

	private TableColumn nameColumn;

	private TableColumn locationColumn;

	private FavoritesViewSorter sorter;

	private IHandler removeHandler;

	private RemoveFavoritesContributionItem removeContributionItem;

	private FavoritesViewFilterAction filterAction;

	private ISelectionListener pageSelectionListener;

	private IMemento memento;
	
	private final IPropertyChangeListener propertyChangeListener = 
			new IPropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent event) {
					if (event.getProperty().equals(
							PreferenceConstants.FAVORITES_VIEW_NAME_COLUMN_VISIBLE) || event.getProperty().equals(PreferenceConstants.FAVORITES_VIEW_LOCATION_COLUMN_VISIBLE)) {
						updateColumnWidths();
					}
				}
			};

	@Override
	public void createPartControl(Composite parent) {
		createTableViewer(parent);
		updateColumnWidths();
		createTableSorter();
		createContributions();
		createContextMenu();
		createToolbarButtons();
		createViewPulldownMenu();
		hookKeyboard();
		hookGlobalHandlers();
		hookDragAndDrop();
		createInlineEditor();
		hookPageSelection();
		hookMouse();
		FavoritesActivator.getDefault().getPreferenceStore()
			.addPropertyChangeListener(propertyChangeListener);
	}

	private void createTableViewer(Composite parent) {

		viewer = new TableViewer(parent, SWT.MULTI | SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION);
		final Table table = viewer.getTable();

		/**
		 * Eclipse 为自动调成大小的表提供了TableColumnLayout
		 */
		TableColumnLayout layout = new TableColumnLayout();
		parent.setLayout(layout);

		typeColumn = new TableColumn(table, SWT.LEFT);
		typeColumn.setText("");
		layout.setColumnData(typeColumn, new ColumnPixelData(18));

		nameColumn = new TableColumn(table, SWT.LEFT);
		nameColumn.setText("Name");
		layout.setColumnData(nameColumn, new ColumnWeightData(4));

		locationColumn = new TableColumn(table, SWT.LEFT);
		locationColumn.setText("Location");
		layout.setColumnData(locationColumn, new ColumnWeightData(9));

		table.setHeaderVisible(true);
		table.setLinesVisible(true);

		viewer.setContentProvider(new FavoritesViewContentProvider());
		viewer.setLabelProvider(new FavoritesViewLabelProvider());
		viewer.setInput(FavoritesManager.getManager());

		getSite().setSelectionProvider(viewer);
//		getViewSite().setSelectionProvider(viewer);
	}
	
	private void updateColumnWidths() {
		IPreferenceStore prefs = FavoritesActivator.getDefault().getPreferenceStore();
		
		boolean showNameColumn = prefs.getBoolean(PreferenceConstants.FAVORITES_VIEW_NAME_COLUMN_VISIBLE);
		nameColumn.setWidth(showNameColumn ? NAME_COLUMN_INITIAL_WIDTH : 0);
		
		boolean showLocationColumn = prefs.getBoolean(PreferenceConstants.FAVORITES_VIEW_LOCATION_COLUMN_VISIBLE);
		locationColumn.setWidth(showLocationColumn ? LOCATION_COLUMN_INITIAL_WIDTH : 0);
	}

	private void createTableSorter() {
		Comparator<IFavoriteItem> nameComparator = new Comparator<IFavoriteItem>() {
			@Override
			public int compare(IFavoriteItem i1, IFavoriteItem i2) {
				return i1.getName().compareTo(i2.getName());
			}
		};
		Comparator<IFavoriteItem> locationComparator = new Comparator<IFavoriteItem>() {
			@Override
			public int compare(IFavoriteItem i1, IFavoriteItem i2) {
				return i1.getLocation().compareTo(i2.getLocation());
			}
		};
		Comparator<IFavoriteItem> typeComparator = new Comparator<IFavoriteItem>() {
			@Override
			public int compare(IFavoriteItem i1, IFavoriteItem i2) {
				return i1.getType().compareTo(i2.getType());
			}
		};

		sorter = new FavoritesViewSorter(viewer, new TableColumn[] { nameColumn, locationColumn, typeColumn },
				new Comparator[] { nameComparator, locationComparator, typeComparator });
		if (memento != null) {
			sorter.init(memento);
		}
		viewer.setSorter(sorter);
	}

	private void createContributions() {
		removeHandler = new RemoveFavoritesHandler();
		removeContributionItem = new RemoveFavoritesContributionItem(getViewSite(), removeHandler);
	}

	private void createContextMenu() {
		MenuManager menuMgr = new MenuManager("#PopupMenu");
		menuMgr.setRemoveAllWhenShown(true);
		menuMgr.addMenuListener(new IMenuListener() {

			@Override
			public void menuAboutToShow(IMenuManager manager) {
				FavoritesView.this.fillContextMenu(manager);

			}
		});
		Menu menu = menuMgr.createContextMenu(viewer.getControl());
		viewer.getControl().setMenu(menu);
		getSite().registerContextMenu(menuMgr, viewer);
	}

	protected void fillContextMenu(IMenuManager menuMgr) {
		menuMgr.add(new Separator("edit"));
		menuMgr.add(removeContributionItem);
		menuMgr.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
	}

	private void createToolbarButtons() {
		IToolBarManager toolBarMgr = getViewSite().getActionBars().getToolBarManager();
		toolBarMgr.add(new GroupMarker("edit"));
		toolBarMgr.add(removeContributionItem);
	}

	private void createViewPulldownMenu() {
		IMenuManager menu = getViewSite().getActionBars().getMenuManager();
		filterAction = new FavoritesViewFilterAction(viewer, "Filter...");

		if (memento != null) {
			filterAction.init(memento);
		}
		menu.add(filterAction);
	}

	private void hookKeyboard() {
		viewer.getControl().addKeyListener(new KeyAdapter() {
			@Override
			public void keyReleased(KeyEvent e) {
				handleKeyReleased(e);
			}
		});
	}

	protected void handleKeyReleased(KeyEvent e) {
		if (e.keyCode == SWT.F2 && e.stateMask == 0) {
			new RenameFavoritesHandler().editElement(this);
		}
		if (e.character == SWT.DEL && e.stateMask == 0) {
			removeContributionItem.run();
		}
	}

	private void hookGlobalHandlers() {
		IHandlerService handlerService = (IHandlerService) getViewSite().getService(IHandlerService.class);
		viewer.addSelectionChangedListener(new ISelectionChangedListener() {

			private IHandlerActivation removeActivation;

			@Override
			public void selectionChanged(SelectionChangedEvent event) {
				if (event.getSelection().isEmpty()) {
					if (removeActivation != null) {
						handlerService.deactivateHandler(removeActivation);
						removeActivation = null;
					}

				} else {
					if (removeActivation == null) {
						removeActivation = handlerService.activateHandler(IWorkbenchActionDefinitionIds.DELETE,
								removeHandler);
					}
				}

			}
		});
	}

	private void hookDragAndDrop() {
		new FavoritesDragSource(viewer);
		new FavoritesDropTarget(viewer);
	}

	/**
	 * 当用户双击Favorites视图的文件时，应打开一个文件编辑器。
	 */
	private void hookMouse() {
		viewer.getTable().addMouseListener(new MouseAdapter() {
			@Override
			public void mouseDoubleClick(MouseEvent e) {
				EditorUtil.openEditor(getSite().getPage(), viewer.getSelection());
			}
		});
	}

	private void hookPageSelection() {
		pageSelectionListener = new ISelectionListener() {

			@Override
			public void selectionChanged(IWorkbenchPart part, ISelection selection) {
				pageSelectionChanged(part, selection);
			}
		};
		getSite().getPage().addPostSelectionListener(pageSelectionListener);

	}

	protected void pageSelectionChanged(IWorkbenchPart part, ISelection selection) {
		if (part == this) {
			return;
		}
		if (!(selection instanceof IStructuredSelection)) {
			return;
		}
		IStructuredSelection sel = (IStructuredSelection) selection;
		IFavoriteItem[] items = FavoritesManager.getManager().existingFavoritesFor(sel.iterator());
		if (items.length > 0) {
			viewer.setSelection(new StructuredSelection(items), true);
		}

	}

	private void createInlineEditor() {
		TableViewerColumn column = new TableViewerColumn(viewer, nameColumn);

		column.setLabelProvider(new ColumnLabelProvider() {
			@Override
			public String getText(Object element) {
				return ((IFavoriteItem) element).getName();
			}
		});

		column.setEditingSupport(new EditingSupport(viewer) {

			TextCellEditor editor = null;

			@Override
			protected void setValue(Object element, Object value) {
				((IFavoriteItem) element).setName((String) value);
				viewer.refresh(element);
			}

			@Override
			protected Object getValue(Object element) {
				return ((IFavoriteItem) element).getName();
			}

			@Override
			protected CellEditor getCellEditor(Object element) {
				if (editor == null) {
					Composite table = (Composite) viewer.getControl();
					editor = new TextCellEditor(table);
				}
				return editor;
			}

			@Override
			protected boolean canEdit(Object element) {
				return true;
			}
		});

		/**
		 * 此时，用户可以在上下文菜单中选择Rename命令或在Favoites视图中点击名称以命名一个特定
		 * 的收藏夹项。TableViewerColumn默认提供了“点击重命名”行为，而这种行为与我们想要的不是
		 * 十分符合。要修改这种行为，我们添加下面的方法以允许单元格编辑仅当它在程序中被触发，
		 * 如如从RenameFavoritesHandler，或当用户按下Alt并点击Favorites视图中的名称以触发。
		 */
		viewer.getColumnViewerEditor().addEditorActivationListener(new ColumnViewerEditorActivationListener() {

			@Override
			public void beforeEditorActivated(ColumnViewerEditorActivationEvent event) {
				if (event.eventType == event.MOUSE_CLICK_SELECTION) {
					if (!(event.sourceEvent instanceof MouseEvent)) {
						event.cancel = true;
					} else {
						MouseEvent m = (MouseEvent) event.sourceEvent;
						if ((m.stateMask & SWT.ALT) == 0) {
							event.cancel = true;
						}
					}
				} else if (event.eventType != event.PROGRAMMATIC) {
					event.cancel = true;
				}

			}

			@Override
			public void beforeEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {

			}

			@Override
			public void afterEditorDeactivated(ColumnViewerEditorDeactivationEvent event) {

			}

			@Override
			public void afterEditorActivated(ColumnViewerEditorActivationEvent event) {

			}
		});

	}

	/**
	 * The constructor
	 */
	public FavoritesView() {
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	/**
	 * For testing purpose only.
	 * 
	 * @return the table viewer in the Favorites view
	 */
	public TableViewer getFavoritesViewer() {
		return viewer;
	}

	public IStructuredSelection getSelection() {
		return (IStructuredSelection) viewer.getSelection();
	}

	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		viewer.addSelectionChangedListener(listener);
	}

	@Override
	public void dispose() {
		if (pageSelectionListener != null) {
			getSite().getPage().removePostSelectionListener(pageSelectionListener);
			FavoritesActivator.getDefault().getPreferenceStore().removePropertyChangeListener(propertyChangeListener);
		}
		super.dispose();
	}

	public void saveState(IMemento memento) {
		super.saveState(memento);
		sorter.saveState(memento);
		filterAction.saveState(memento);
	}

	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		this.memento = memento;
	}

}
