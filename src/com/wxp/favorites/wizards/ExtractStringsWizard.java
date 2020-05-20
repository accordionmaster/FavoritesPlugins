package com.wxp.favorites.wizards;

import java.lang.reflect.InvocationTargetException;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.Wizard;
import org.eclipse.ui.INewWizard;
import org.eclipse.ui.IWorkbench;

import com.wxp.favorites.FavoritesActivator;
import com.wxp.favorites.FavoritesLog;

public class ExtractStringsWizard extends Wizard implements INewWizard {
	
	private IStructuredSelection initialSelection;
	private SelectFilesWizardPage selectFilesPage;
	private SelectStringsWizardPage selectStringsPage;

	public ExtractStringsWizard() {
		IDialogSettings favoriteSettings = 
				FavoritesActivator.getDefault().getDialogSettings();
		IDialogSettings wizardSettings = 
				favoriteSettings.getSection("ExtractStringsWizard");
		if (wizardSettings == null) {
			wizardSettings = favoriteSettings.addNewSection("ExtractStringsWizard");
		}
		setDialogSettings(favoriteSettings);
	}

	@Override
	public void init(IWorkbench workbench, IStructuredSelection selection) {
		initialSelection = selection;
	}
	
	@Override
	public void addPages() {
		setWindowTitle("Extract");
		selectFilesPage = new SelectFilesWizardPage();
		addPage(selectFilesPage);
		selectStringsPage = new SelectStringsWizardPage();
		addPage(selectStringsPage);
		selectFilesPage.init(initialSelection);
	}
	
	

	@Override
	public boolean performFinish() {
		final ExtractedString[] extracted = selectStringsPage.getSelection();
		
		// Perform the operation in a separate thread
		// so that the operation can be canceled.
		try {
			getContainer().run(true, true, new IRunnableWithProgress() {
				
				@Override
				public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
					performOperation(extracted, monitor);
				}
			});
		} catch (InvocationTargetException e) {
			FavoritesLog.logError(e);
			return false;
		} catch (InterruptedException e) {
			// User canceled, so stop but don't close wizard.
			return false;
		}
		return true;
	}

	protected void performOperation(ExtractedString[] extracted, IProgressMonitor monitor) throws InterruptedException {
		monitor.beginTask("Extracting Strings", extracted.length);
		for (int i = 0; i < extracted.length; i++) {
			// Replace sleep with actual work
			Thread.sleep(1000);
			if (monitor.isCanceled()) {
				throw new InterruptedException("Cancled by user");
			}
			monitor.worked(1);
		}
		monitor.done();
	}
	
	/**
	 * Answer the selected source location.
	 * @return
	 */
	public IPath getSourceLocation() {
		return selectFilesPage.getSourceLocation();
	}
	
	
	public IPath getDestinationLocation() {
		return selectFilesPage.getDestinationLocation();
	}

}
