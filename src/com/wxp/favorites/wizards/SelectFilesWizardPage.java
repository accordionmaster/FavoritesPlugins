package com.wxp.favorites.wizards;

import java.util.Iterator;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.ui.wizards.NewAnnotationWizardPage;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;

public class SelectFilesWizardPage extends WizardPage {

	private Text sourceFileField;
	private Text destinationFileField;
	private IPath initialSourcePath;
	
	protected SelectFilesWizardPage() {
		super("selectFiles");
		setTitle("Select files");
		setDescription("Select the source and destination file");
	}

	@Override
	public void createControl(Composite parent) {
		Composite container = new Composite(parent, SWT.NULL);
		final GridLayout gridLayout = new GridLayout();
		gridLayout.numColumns = 3;
		container.setLayout(gridLayout);
		setControl(container);
		
		final Label label = new Label(container, SWT.NONE);
		final GridData gridData = new GridData();
		gridData.horizontalSpan = 3;
		label.setLayoutData(gridData);
		
		label.setText("Select the plugin.xml file " +
				"from which strings will be extracted.");
		
		final Label label_1 = new Label(container, SWT.NONE);
		final GridData gridData_1 = new GridData(GridData.HORIZONTAL_ALIGN_END);
		label_1.setLayoutData(gridData_1);
		label_1.setText("Source File:");
		
		sourceFileField = new Text(container, SWT.BORDER);
		sourceFileField.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				updatePageComplete();
			}
		});
		sourceFileField.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		final Button button = new Button(container, SWT.NONE);
		button.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseForSourceFile();
			}
		});
		button.setText("Browse...");
		
		final Label label_2 = new Label(container, SWT.NONE);
		final GridData gridData_2 = new GridData();
		gridData_2.horizontalSpan = 3;
		label_2.setLayoutData(gridData_2);
		
		final Label label_3 = new Label(container, SWT.NONE);
		final GridData gridData_3 = new GridData();
		gridData_3.horizontalSpan = 3;
		label_3.setLayoutData(gridData_3);
		label_3.setText("Select the plugin.properties file " +
				"into which strings will be placed.");
		final Label lable_4 = new Label(container, SWT.NONE);
		final GridData gridData_4 = new GridData();
		gridData_4.horizontalIndent = 20;
		lable_4.setLayoutData(gridData_4);
		lable_4.setText("Destination File:");
		
		destinationFileField = new Text(container, SWT.BORDER);
		destinationFileField.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent e) {
				updatePageComplete();
			}
		});
		destinationFileField.setLayoutData(new GridData(GridData.HORIZONTAL_ALIGN_FILL));
		
		final Button button_1 = new Button(container, SWT.NONE);
		button_1.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				browseForDestinationFile();
			}
		});
		button_1.setText("Browse...");
		
		initContents();
	}
	
	// 分析当前选择并缓存结果
	public void init(IStructuredSelection selection) {
		if (selection == null) {
			return;
		}
		// Find the first plugin.xml file.
		Iterator iter = selection.iterator();
		while (iter.hasNext()) {
			Object item = iter.next();
			if (item instanceof IJavaElement) {
				IJavaElement javaElem = (IJavaElement) iter.next();
				try {
					 item = javaElem.getUnderlyingResource();
				} catch (JavaModelException e) {
					// Log and report the exception.
					e.printStackTrace();
					continue;
				}
			}
			if (item instanceof IFile) {
				IFile file = (IFile) item;
				if (file.getName().equals("plugin.xml")) {
					initialSourcePath = file.getLocation();
					break;
				}
				item = file.getProject();
			}
			if (item instanceof IProject) {
				IFile file = ((IProject) item).getFile("plugin.xml");
				if (file.exists()) {
					initialSourcePath = file.getLocation();
					break;
				}
			}
			
		}
	}
	
	// 根据init缓存结果初始化字段内容。
	private void initContents() {
		if (initialSourcePath == null) {
			setPageComplete(false);
			return;
		}
		IPath rootLoc = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		IPath path = initialSourcePath;
		if (rootLoc.isPrefixOf(path)) {
			path = path.setDevice(null).removeFirstSegments(rootLoc.segmentCount());
		}
		sourceFileField.setText(path.toString());
		destinationFileField.setText(path.removeLastSegments(1).append("plugin.properties").toString());
		updatePageComplete();
	}

	/**
	 * 向导在标题下面提供了一个可以显示反馈的消息区域，该区域用于向用户展示在前进至下一向导
	 * 页或执行操作需要用户输入的附加信息。在这种情况中，updatePageComplete()方法在确定
	 * 了初始内容之后又一次被不同的文本字段监听器在内容被更改时调用。改方法随后监听
	 * 当前文本字段的内容，显示错误或警告信息，并恰当地将Next和Finish按钮设为可用或不可用。
	 */
	private void updatePageComplete() {
		setPageComplete(false);
		IPath sourceLoc = getSourceLocation();
		if (sourceLoc == null || !sourceLoc.toFile().exists()) {
			setMessage(null);
			setErrorMessage("Please select an existing plugin.xml file");
			return;
		}
		IPath destinationLoc = getDestinationLocation();
		if (destinationLoc == null) {
			setMessage(null);
			setErrorMessage("Please specify a plugn.properties file to contain the extracted strings");
			return;
		}
		setPageComplete(true);
		
		IPath sourceDirPath = sourceLoc.removeLastSegments(1);
		IPath destinationDirPath = destinationLoc.removeLastSegments(1);
		if (!sourceDirPath.equals(destinationDirPath)) {
			setErrorMessage(null);
			setMessage("The plugin.properties file is typically located in the same directory as the plugin.xml file", WARNING);
			return;
		}
		
		if (!destinationDirPath.lastSegment().equals("plugin.properties")) {
			setErrorMessage(null);
			setMessage("The destination file is typically named plugin.properties", WARNING);
			return;
		}
		
		setMessage(null);
		setErrorMessage(null);
	}
	
	protected void browseForSourceFile() {
		IPath path = browse(getSourceLocation(), false);
		if (path == null) {
			return;
		}
		IPath rootLoc = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		if (rootLoc.isPrefixOf(path)) {
			path = path.setDevice(null).removeFirstSegments(rootLoc.segmentCount());
		}
		sourceFileField.setText(path.toString());
	}
	
	protected void browseForDestinationFile() {
		IPath path = browse(getDestinationLocation(), false);
		if (path == null) {
			return;
		}
		IPath rootLoc = ResourcesPlugin.getWorkspace().getRoot().getLocation();
		if (rootLoc.isPrefixOf(path)) {
			path = path.setDevice(null).removeFirstSegments(rootLoc.segmentCount());
		}
		destinationFileField.setText(path.toString());
	}
	
	private IPath browse(IPath path, boolean mustExist) {
		FileDialog dialog = new FileDialog(getShell(), mustExist ? SWT.OPEN : SWT.SAVE);
		if (path != null) {
			if (path.segmentCount() > 1) {
				dialog.setFilterPath(path.removeLastSegments(1).toOSString());
			}
			if (path.segmentCount() > 0) {
				dialog.setFileName(path.lastSegment());
			}
		}
		String result = dialog.open();
		if (result == null) {
			return null;
		}
		return new Path(result);
 	}
	
	public IPath getSourceLocation() {
		String text = sourceFileField.getText().trim();
		if (text.length() == 0) {
			return null;
		}
		IPath path = new Path(text);
		if (!path.isAbsolute()) {
			path = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(path);
		}
		return path;
	}
	
	public IPath getDestinationLocation() {
		String text = destinationFileField.getText().trim();
		if (text.length() == 0) {
			return null;
		}
		IPath path = new Path(text);
		if (!path.isAbsolute()) {
			path = ResourcesPlugin.getWorkspace().getRoot().getLocation().append(path);
		}
		return path;
	}

	
}
