package hk.ust.cse.pishon.esgen.handlers;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

import hk.ust.cse.pishon.esgen.model.ScriptItem;
import hk.ust.cse.pishon.esgen.views.ChangeView;
import hk.ust.cse.pishon.esgen.views.ScriptList;

public class LoadWebScriptHandler extends AbstractHandler {

	private static final String ERROR_DIALOG_TITLE = "Load Error";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		ScriptList scriptList = (ScriptList)page.findView(ScriptList.ID);
		ChangeView changeView = (ChangeView)page.findView(ChangeView.ID);
		try {
			if(scriptList == null) {
				page.showView(ScriptList.ID);
				scriptList = (ScriptList)page.findView(ScriptList.ID);
			}
			FileDialog dialog = new FileDialog(shell, SWT.MULTI);
			dialog.setText("Select a *.obj file for scripts.");
			dialog.setFilterExtensions(new String[] {"*.obj"});
			String name = dialog.open();
			if(name == null){
				MessageDialog.openError(shell, ERROR_DIALOG_TITLE, "No file is selected.");
			}else{
				File f = new File(name);
				if(f.exists()){
					((ChangeView)changeView).setWebScripts(f);
				}else{
					MessageDialog.openError(shell, ERROR_DIALOG_TITLE, "It is not a valid script file.");
				}
			}			
		} catch (PartInitException e) {
			e.printStackTrace();
			MessageDialog.openError(shell, ERROR_DIALOG_TITLE, "Error occurred while script loading.");
		}
		return null;
	}

}