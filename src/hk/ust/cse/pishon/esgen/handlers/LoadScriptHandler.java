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
import hk.ust.cse.pishon.esgen.views.ScriptList;
import hk.ust.cse.pishon.esgen.views.ScriptStatView;

public class LoadScriptHandler extends AbstractHandler {

	private static final String ERROR_DIALOG_TITLE = "Load Error";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		ScriptList scriptList = (ScriptList)page.findView(ScriptList.ID);
		try {
			if(scriptList == null) {
				page.showView(ScriptList.ID);
				scriptList = (ScriptList)page.findView(ScriptList.ID);
			}
			FileDialog dialog = new FileDialog(shell, SWT.MULTI);
			dialog.setText("Select Edit Scripts - *.obj files.");
			dialog.setFilterExtensions(new String[] {"*.obj"});
			dialog.open();
			String[] names = dialog.getFileNames();
			String filterPath = dialog.getFilterPath();
			if(names == null || names.length == 0){
				MessageDialog.openError(shell, ERROR_DIALOG_TITLE, "No files are selected.");
			}else{
				List<ScriptItem> items = new ArrayList<>();
				for(String name : names)
					items.add(new ScriptItem(filterPath + File.separator + name));
				scriptList.setInput(items);
				ScriptStatView statView = (ScriptStatView)page.findView(ScriptStatView.ID);
				if(statView != null) 
					statView.setInput();				
			}
		} catch (PartInitException e) {
			e.printStackTrace();
			MessageDialog.openError(shell, ERROR_DIALOG_TITLE, "Error occurred while script loading.");
		}
		return null;
	}

}