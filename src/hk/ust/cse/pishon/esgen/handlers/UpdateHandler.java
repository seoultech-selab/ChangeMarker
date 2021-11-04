package hk.ust.cse.pishon.esgen.handlers;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.ui.IViewPart;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.handlers.HandlerUtil;

import hk.ust.cse.pishon.esgen.compare.CompareSelectionProvider;
import hk.ust.cse.pishon.esgen.model.EditOp;
import hk.ust.cse.pishon.esgen.views.ScriptView;

public class UpdateHandler extends AbstractHandler {

	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		ISelectionProvider selectionProvider = HandlerUtil.getActiveEditor(event).getSite().getSelectionProvider();
		if(selectionProvider instanceof CompareSelectionProvider){
			ISelection leftSelection = ((CompareSelectionProvider) selectionProvider).getLeftSelection();
			ISelection rightSelection = ((CompareSelectionProvider) selectionProvider).getRightSelection();
			EditOp update = new EditOp(EditOp.OP_UPDATE, (ITextSelection)leftSelection, (ITextSelection)rightSelection);
			IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
			IWorkbenchPage page = window.getActivePage();
			IViewPart viewer = page.findView(ScriptView.ID);
			if(viewer instanceof ScriptView){
				((ScriptView) viewer).addEditOp(update);
			}
		}
		return null;
	}

}
