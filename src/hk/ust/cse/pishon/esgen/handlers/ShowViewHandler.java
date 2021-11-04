package hk.ust.cse.pishon.esgen.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.handlers.HandlerUtil;

public class ShowViewHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		IWorkbenchPage page = HandlerUtil.getActiveWorkbenchWindowChecked(event).getActivePage();
		String viewId = event.getParameter("hk.ust.cse.pishon.esgen.views.showView.viewId");
		try {
			page.showView(viewId);
		} catch (PartInitException e) {
			e.printStackTrace();
		}
		return null;
	}

}
