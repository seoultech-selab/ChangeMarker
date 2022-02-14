package hk.ust.cse.pishon.esgen.handlers;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

import hk.ust.cse.pishon.esgen.model.EditOp;
import hk.ust.cse.pishon.esgen.model.Node;
import hk.ust.cse.pishon.esgen.views.MultiScriptView;

public class ReplaceOpHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		Shell shell = HandlerUtil.getActiveShell(event);
		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
		String viewId = event.getParameter("hk.ust.cse.pishon.esgen.commands.repalce.viewId");
		IViewPart viewer = page.findView(viewId);
		MultiScriptView multiView = (MultiScriptView)page.findView(MultiScriptView.ID);
		if(viewer != null && multiView != null) {
			ISelection from = viewer.getViewSite().getSelectionProvider().getSelection();
			ISelection to = multiView.getViewSite().getSelectionProvider().getSelection();
			if (from != null && from instanceof IStructuredSelection && !from.isEmpty()
					&& to != null && to instanceof IStructuredSelection && !to.isEmpty()) {
				IStructuredSelection strdFrom = (IStructuredSelection) from;
				IStructuredSelection strdTo = (IStructuredSelection) to;
				
				//Need to check validity of selection.
				List<Node> removeNodes = new ArrayList<>();
				Node parent = null;
				for (Iterator<Object> iterator = strdTo.iterator(); iterator.hasNext();) {
					Object element = iterator.next();
					if(element instanceof Node) {
						Node n = (Node)element;
						if(parent != null && n.parent != parent) {
							MessageDialog.openError(shell, "Selection Error", "Selecting operations in multiple scripts is not allowed.");
							return null;
						} else {
							parent = n.parent;
							removeNodes.add((Node)element);
						}						
					}
				}
				removeNodes.forEach(n -> multiView.removeEditOp(n));
				
				//Then add operations.
				for (Iterator<Object> iterator = strdFrom.iterator(); iterator.hasNext();) {
					Object element = iterator.next();
					if(element instanceof EditOp)
						multiView.addEditOp((EditOp)element);
				}
			} else {				
				MessageDialog.openError(shell, "Selection Error", "Operations are not properly selected.");
			}
			
		}
		return null;
	}

}
