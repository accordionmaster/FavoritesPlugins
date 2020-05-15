package com.wxp.favorites.editors;

import org.eclipse.jface.action.ICoolBarManager;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.actions.ActionFactory;
import org.eclipse.ui.actions.LabelRetargetAction;
import org.eclipse.ui.ide.IDEActionFactory;
import org.eclipse.ui.part.EditorActionBarContributor;
import org.eclipse.ui.texteditor.ITextEditor;

public class PropertiesEditorContributor extends EditorActionBarContributor {

	private LabelRetargetAction retargetRemoveAction = 
			 	new LabelRetargetAction(ActionFactory.DELETE.getId(), "Remove");
	
	private static final String[] WORKBENCH_ACTION_IDS = { ActionFactory.DELETE.getId(), ActionFactory.UNDO.getId(),
			ActionFactory.REDO.getId(), ActionFactory.CUT.getId(), ActionFactory.COPY.getId(),
			ActionFactory.PASTE.getId(), ActionFactory.SELECT_ALL.getId(), ActionFactory.FIND.getId(),
			IDEActionFactory.BOOKMARK.getId() };

	private static final String[] TEXTEDITOR_ACTION_IDS = { ActionFactory.DELETE.getId(), ActionFactory.UNDO.getId(),
			ActionFactory.REDO.getId(), ActionFactory.CUT.getId(), ActionFactory.COPY.getId(),
			ActionFactory.PASTE.getId(), ActionFactory.SELECT_ALL.getId(), ActionFactory.FIND.getId(),
			IDEActionFactory.BOOKMARK.getId() };

	@Override
	public void setActiveEditor(IEditorPart part) {
		PropertiesEditor editor = (PropertiesEditor) part;
		setActivePage(editor, editor.getActivePage());
	}
	
	void setActivePage(PropertiesEditor editor, int pageIndex) {
		IActionBars actionBars = getActionBars();
		if (actionBars != null) {
			switch (pageIndex) {
			case 0:
				hookGlobalTreeActions(editor, actionBars);
				break;
			case 1:
				hookGlobalTextActions(editor, actionBars);
				break;
			}
			actionBars.updateActionBars();
		}
		
	}

	private void hookGlobalTextActions(PropertiesEditor editor, IActionBars actionBars) {
		ITextEditor textEditor = editor.getSourceEditor();
		for (int i = 0; i < WORKBENCH_ACTION_IDS.length; i++) {
			actionBars.setGlobalActionHandler(WORKBENCH_ACTION_IDS[i], textEditor.getAction(TEXTEDITOR_ACTION_IDS[i]));
		}
		
	}

	private void hookGlobalTreeActions(PropertiesEditor editor, IActionBars actionBars) {
		for (int i = 0; i < WORKBENCH_ACTION_IDS.length; i++) {
			actionBars.setGlobalActionHandler(WORKBENCH_ACTION_IDS[i], editor.getTreeAction(WORKBENCH_ACTION_IDS[i]));
		}
		
	}
	
	@Override
	public void init(IActionBars bars, IWorkbenchPage page) {
		super.init(bars, page);
		page.addPartListener(retargetRemoveAction);
	}
	
	@Override
	public void contributeToMenu(IMenuManager menuManager) {
		IMenuManager menu = new MenuManager("Property Editor");
		menuManager.prependToGroup(IWorkbenchActionConstants.MB_ADDITIONS, menu);
		menu.add(retargetRemoveAction);
	}
	
	@Override
	public void contributeToCoolBar(ICoolBarManager manager) {
		manager.add(new Separator());
		manager.add(retargetRemoveAction);
	}
	
	@Override
	public void dispose() {
		getPage().removePartListener(retargetRemoveAction);
		super.dispose();
	}

	public PropertiesEditorContributor() {
		// TODO Auto-generated constructor stub
	}

}
