package hk.ust.cse.pishon.esgen.handlers;

import java.io.File;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import hk.ust.cse.pishon.esgen.views.ChangeView;

public class LoadScriptDataHandler extends AbstractHandler {

	private static final String ERROR_DIALOG_TITLE = "Load Error";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		ChangeView changeView = (ChangeView)page.findView(ChangeView.ID);
		FileDialog dialog = new FileDialog(shell, SWT.MULTI);
		dialog.setText("Select a *.cmdata file.");
		dialog.setFilterExtensions(new String[] {"*.cmdata"});
		String name = dialog.open();
		if(name == null){
			MessageDialog.openError(shell, ERROR_DIALOG_TITLE, "No file is selected.");
		}else{
			File f = new File(name);
			if(f.exists()){
				((ChangeView)changeView).setData(f);
			}else{
				MessageDialog.openError(shell, ERROR_DIALOG_TITLE, "It is not a valid script file.");
			}
		}
		return null;
	}

}
