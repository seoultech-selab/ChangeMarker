package hk.ust.cse.pishon.esgen.handlers;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.List;
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
import hk.ust.cse.pishon.esgen.views.ChangeView;

public class ExportHandler extends AbstractHandler {

	private static final String ERROR_DIALOG_TITLE = "Save Error";

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		IWorkbenchWindow window = HandlerUtil.getActiveWorkbenchWindow(event);
		IWorkbenchPage page = window.getActivePage();
		IViewPart changeView = page.findView(ChangeView.ID);
		List<Change> changes = null;
		if(changeView != null){
			changes = ((ChangeView)changeView).getInput();
			FileDialog dialog = new FileDialog(shell, SWT.SAVE);
			dialog.setFileName("scripts.zip");
			dialog.setText("Input file name to store edit scripts.");
			String name = dialog.open();
			if(name == null){
				MessageDialog.openError(shell, ERROR_DIALOG_TITLE, "No file name is selected.");
			}else{
				File f = new File(name);
				if(f.exists()){
					boolean overwrite = MessageDialog.openConfirm(shell, "Overwrite", "This command will overwrite the current file. Proceed?");
					if(overwrite){
						saveTo(f, changes);
					}
				}else{
					saveTo(f, changes);
				}
			}
		}else{
			MessageDialog.openError(shell, ERROR_DIALOG_TITLE, "Can't find changes/edit scripts.");
		}
		return null;
	}

	private void saveTo(File f, List<Change> changes) {
		FileOutputStream fos = null;
		ZipOutputStream zos = null;
		ZipEntry zipEntry = null;
		ObjectOutputStream out = null;
		try {
			fos = new FileOutputStream(f);
			zos = new ZipOutputStream(fos);
			zos.setLevel(ZipOutputStream.DEFLATED);
			//Export changes in an object file.
			zipEntry = new ZipEntry("changes.obj");
			zipEntry.setTime(System.currentTimeMillis());
			zos.putNextEntry(zipEntry);
			out = new ObjectOutputStream(zos);
			out.writeObject(changes);
			out.flush();
			zos.closeEntry();

			//Export text edit script to files.
			for(Change c : changes){
				zipEntry = new ZipEntry(c.getName() + ".txt");
				zipEntry.setTime(System.currentTimeMillis());
				zos.putNextEntry(zipEntry);
				zos.write(c.getScript().toString().getBytes());
				zos.flush();
				zos.closeEntry();
			}

		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				if(zos != null)
					zos.close();
				if(fos != null)
					fos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
