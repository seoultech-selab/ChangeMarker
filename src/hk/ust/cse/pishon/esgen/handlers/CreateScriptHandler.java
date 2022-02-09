package hk.ust.cse.pishon.esgen.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.handlers.HandlerUtil;

import hk.ust.cse.pishon.esgen.views.MultiScriptView;

public class CreateScriptHandler extends AbstractHandler {
	
	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindow(event).getActivePage();
		MultiScriptView viewer = (MultiScriptView)page.findView(MultiScriptView.ID);
		if (viewer != null) {
			viewer.createNewScript();
			viewer.refresh();
		}
		return null;
	}
}
