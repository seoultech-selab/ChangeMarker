package hk.ust.cse.pishon.esgen.handlers;

import java.util.Iterator;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

import hk.ust.cse.pishon.esgen.model.EditOp;
import hk.ust.cse.pishon.esgen.model.Node;
import hk.ust.cse.pishon.esgen.views.MultiScriptView;
import hk.ust.cse.pishon.esgen.views.ScriptView;

public class DeleteOpHandler extends AbstractHandler implements IHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
		ISelection selection = page.getSelection();
		String partId = page.getActivePart().getSite().getId();
		IViewPart viewer = page.findView(partId);
		if (viewer != null && selection != null && selection instanceof IStructuredSelection) {
			IStructuredSelection strucSelection = (IStructuredSelection) selection;
			for (Iterator<Object> iterator = strucSelection.iterator(); iterator.hasNext();) {
				Object element = iterator.next();
				if(element instanceof EditOp) {
					if(partId.equals(ScriptView.ID))
						((ScriptView)viewer).removeEditOp((EditOp)element);
				} else if(element instanceof Node) {
					if(partId.equals(MultiScriptView.ID))
						((MultiScriptView)viewer).removeEditOp((Node)element);					
				}
					
			}
		}
		return null;
	}

}
