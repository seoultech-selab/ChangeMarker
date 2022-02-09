package hk.ust.cse.pishon.esgen.handlers;
import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

import hk.ust.cse.pishon.esgen.model.EditOp;
import hk.ust.cse.pishon.esgen.views.MultiScriptView;

public class CopyHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
		ISelection selection = page.getSelection();
		MultiScriptView viewer = (MultiScriptView)page.findView(MultiScriptView.ID);
		if (viewer != null && selection != null && selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			for (Iterator<Object> iterator = strucSelection.iterator(); iterator.hasNext();) {
				Object element = iterator.next();
				if(element instanceof EditOp)
					viewer.addEditOp((EditOp)element);
			}
		}
		return null;
	}

}
