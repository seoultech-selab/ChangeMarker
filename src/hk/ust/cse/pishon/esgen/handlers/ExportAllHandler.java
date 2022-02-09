package hk.ust.cse.pishon.esgen.handlers;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import hk.ust.cse.pishon.esgen.model.Change;
import hk.ust.cse.pishon.esgen.model.Node;
import hk.ust.cse.pishon.esgen.views.ChangeView;

public class ExportAllHandler extends AbstractHandler {

	private static final String ERROR_DIALOG_TITLE = "Save Error";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		IViewPart changeView = page.findView(ChangeView.ID);
		Map<String, Node> scripts= null;
		if(changeView != null){
			scripts = ((ChangeView)changeView).getAllScripts();
			FileDialog dialog = new FileDialog(shell, SWT.SAVE);
			dialog.setFileName("scripts.obj");
			dialog.setText("Input file name to store edit scripts.");
			String name = dialog.open();
			if(name == null){
				MessageDialog.openError(shell, ERROR_DIALOG_TITLE, "No file name is selected.");
			}else{
				File f = new File(name);
				if(f.exists()){
					boolean overwrite = MessageDialog.openConfirm(shell, "Overwrite", "This command will overwrite the current file. Proceed?");
					if(overwrite){
						saveTo(f, scripts);
					}
				}else{
					saveTo(f, scripts);
				}
			}
		}else{
			MessageDialog.openError(shell, ERROR_DIALOG_TITLE, "Can't find changes/edit scripts.");
		}
		return null;
	}

	private void saveTo(File f, Map<String, Node> scripts) {
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(f));
			oos.writeObject(scripts);
			oos.flush();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(oos != null)
					oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
