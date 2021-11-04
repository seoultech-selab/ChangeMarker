package hk.ust.cse.pishon.esgen.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import hk.ust.cse.pishon.esgen.views.ChangeView;

public class LoadChangeHandler extends AbstractHandler {

	private static final String ERROR_DIALOG_TITLE = "Load Error";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		IViewPart changeView = page.findView(ChangeView.ID);
		if(changeView != null){
			DirectoryDialog dialog = new DirectoryDialog(shell, SWT.OPEN);
			dialog.setText("Select a directory including changes.");
			String name = dialog.open();
			if(name == null){
				MessageDialog.openError(shell, ERROR_DIALOG_TITLE, "No directory is selected.");
			}else{
				File dir = new File(name);
				if(dir.exists() && dir.isDirectory()){
					((ChangeView)changeView).setChangePath(dir.getAbsolutePath());					
				}else{
					MessageDialog.openError(shell, ERROR_DIALOG_TITLE, "It is not a valid change directory.");
				}
			}
		}else{
			MessageDialog.openError(shell, ERROR_DIALOG_TITLE, "Can't find changes/edit scripts.");
		}
		return null;
	}

}
