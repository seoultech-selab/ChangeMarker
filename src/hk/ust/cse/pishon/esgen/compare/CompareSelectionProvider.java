package hk.ust.cse.pishon.esgen.compare;

import org.eclipse.core.runtime.Assert;
import org.eclipse.core.runtime.ListenerList;
import org.eclipse.jface.text.IRegion;
import org.eclipse.jface.text.ITextSelection;
import org.eclipse.jface.text.TextSelection;
import org.eclipse.jface.text.TextViewer;
import org.eclipse.jface.viewers.IPostSelectionProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.FocusListener;
import org.eclipse.swt.widgets.Widget;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.services.ISourceProviderService;

import hk.ust.cse.pishon.esgen.sourceprovider.SelectionState;

public class CompareSelectionProvider implements IPostSelectionProvider {
	private class InternalListener implements ISelectionChangedListener, FocusListener {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			doSelectionChanged(event);
		}

		@Override
		public void focusGained(FocusEvent e) {
			// expecting a StyledText widget here
			doFocusChanged(e.widget);
		}

		/*
		 * @see FocusListener#focusLost
		 */
		@Override
		public void focusLost(FocusEvent e) {
			// do not reset due to focus behavior on GTK
			//fViewerInFocus= null;
		}
	}

	private class InternalPostSelectionListener implements ISelectionChangedListener {
		@Override
		public void selectionChanged(SelectionChangedEvent event) {
			doPostSelectionChanged(event);
		}

	}

	private TextViewer[] fViewers;

	private TextViewer fViewerInFocus;
	private TextViewer fViewerLostFocus;
	private TextViewer fViewerLeft;
	private TextViewer fViewerRight;
	private ListenerList fSelectionChangedListeners;
	private ListenerList fPostSelectionChangedListeners;

	public CompareSelectionProvider() {
		fSelectionChangedListeners = new ListenerList();
		fPostSelectionChangedListeners = new ListenerList();
		// nothing more to do here, Compare Editor is initializing
	}

	/**
	 * @param viewers All viewers that can provide a selection
	 * @param viewerInFocus the viewer currently in focus or <code>null</code>
	 */
	public void setViewers(TextViewer[] viewers, TextViewer viewerInFocus) {
		Assert.isNotNull(viewers);
		fViewers= viewers;
		InternalListener listener= new InternalListener();
		fViewerLeft = viewers[0];
		fViewerRight = viewers[1];
		resetStatus();

		for (int i= 0; i < fViewers.length; i++) {
			TextViewer viewer= fViewers[i];
			viewer.addSelectionChangedListener(listener);
			viewer.addPostSelectionChangedListener(new InternalPostSelectionListener());
			StyledText textWidget = viewer.getTextWidget();
			textWidget.addFocusListener(listener);
		}

		if(viewerInFocus == null){
			fViewerInFocus = fViewerLeft;
		}else{
			fViewerInFocus= viewerInFocus;
		}
		updateFocus();
	}

	private void doFocusChanged(Widget control) {
		for (int i= 0; i < fViewers.length; i++) {
			if (fViewers[i].getTextWidget() == control) {
				propagateFocusChanged(fViewers[i]);
				return;
			}
		}
	}

	final void doPostSelectionChanged(SelectionChangedEvent event) {
		ISelectionProvider provider= event.getSelectionProvider();
		if (provider == fViewerInFocus) {
			firePostSelectionChanged();
		}
	}

	final void doSelectionChanged(SelectionChangedEvent event) {
		ISelectionProvider provider= event.getSelectionProvider();
		if (provider == fViewerInFocus) {
			fireSelectionChanged();
		}
	}

	final void propagateFocusChanged(TextViewer viewer) {
		if (viewer != fViewerInFocus) { // OK to compare by identity
			fViewerLostFocus = fViewerInFocus;
			fViewerInFocus= viewer;
			updateFocus();
			fireSelectionChanged();
			firePostSelectionChanged();
		}
	}

	private void updateFocus() {
		ISourceProviderService sourceProviderService = (ISourceProviderService) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getService(ISourceProviderService.class);
		SelectionState stateService = (SelectionState) sourceProviderService
				.getSourceProvider(SelectionState.FOCUS);
		if(fViewerInFocus == fViewerLeft){
			stateService.setFocus(SelectionState.OLD);
		}else if(fViewerInFocus == fViewerRight){
			stateService.setFocus(SelectionState.NEW);
		}else{
			stateService.setFocus(SelectionState.NONE);
		}
	}

	private void resetStatus() {
		ISourceProviderService sourceProviderService = (ISourceProviderService) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
				.getService(ISourceProviderService.class);
		SelectionState stateService = (SelectionState) sourceProviderService
				.getSourceProvider(SelectionState.FOCUS);
		stateService.setFocus(SelectionState.NONE);
		stateService.setOldSelected(false);
		stateService.setNewSelected(false);
	}

	private void fireSelectionChanged() {
		if (fSelectionChangedListeners != null) {
			ISelection selection = getSelection();
			SelectionChangedEvent event= new SelectionChangedEvent(this, selection);

			Object[] listeners= fSelectionChangedListeners.getListeners();
			for (int i= 0; i < listeners.length; i++) {
				ISelectionChangedListener listener= (ISelectionChangedListener) listeners[i];
				listener.selectionChanged(event);
			}
			ISourceProviderService sourceProviderService = (ISourceProviderService) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getService(ISourceProviderService.class);
			SelectionState stateService = (SelectionState) sourceProviderService
					.getSourceProvider(SelectionState.OLD_SELECTED);
			if(fViewerInFocus == fViewerLeft){
				stateService.setOldSelected(!selection.isEmpty() && ((ITextSelection)selection).getText().length() > 0);
			}else if(fViewerInFocus == fViewerRight){
				stateService.setNewSelected(!selection.isEmpty() && ((ITextSelection)selection).getText().length() > 0);
			}
		}
	}

	private void firePostSelectionChanged() {
		if (fPostSelectionChangedListeners != null) {
			ISelection selection = getSelection();
			SelectionChangedEvent event= new SelectionChangedEvent(this, selection);

			Object[] listeners= fPostSelectionChangedListeners.getListeners();
			for (int i= 0; i < listeners.length; i++) {
				ISelectionChangedListener listener= (ISelectionChangedListener) listeners[i];
				listener.selectionChanged(event);
			}
			ISourceProviderService sourceProviderService = (ISourceProviderService) PlatformUI.getWorkbench().getActiveWorkbenchWindow()
					.getService(ISourceProviderService.class);
			SelectionState stateService = (SelectionState) sourceProviderService
					.getSourceProvider(SelectionState.OLD_SELECTED);
			if(fViewerInFocus == fViewerLeft){
				stateService.setOldSelected(!selection.isEmpty() && ((ITextSelection)selection).getText().length() > 0);
			}else if(fViewerInFocus == fViewerRight){
				stateService.setNewSelected(!selection.isEmpty() && ((ITextSelection)selection).getText().length() > 0);
			}
		}
	}

	/*
	 * @see ISelectionProvider#addSelectionChangedListener
	 */
	@Override
	public void addSelectionChangedListener(ISelectionChangedListener listener) {
		fSelectionChangedListeners.add(listener);
	}

	/*
	 * @see ISelectionProvider#removeSelectionChangedListener
	 */
	@Override
	public void removeSelectionChangedListener(ISelectionChangedListener listener) {
		fSelectionChangedListeners.remove(listener);
	}

	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IPostSelectionProvider#addPostSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void addPostSelectionChangedListener(ISelectionChangedListener listener) {
		fPostSelectionChangedListeners.add(listener);
	}


	/* (non-Javadoc)
	 * @see org.eclipse.jface.viewers.IPostSelectionProvider#removePostSelectionChangedListener(org.eclipse.jface.viewers.ISelectionChangedListener)
	 */
	@Override
	public void removePostSelectionChangedListener(ISelectionChangedListener listener) {
		fPostSelectionChangedListeners.remove(listener);
	}

	/*
	 * @see ISelectionProvider#getSelection
	 */
	@Override
	public ISelection getSelection() {
		if (fViewerInFocus != null) {
			return fViewerInFocus.getSelection();
		}
		return TextSelection.emptySelection();
	}

	public ISelection getOtherSelection() {
		if (fViewerLostFocus != null) {
			return fViewerLostFocus.getSelection();
		}
		return TextSelection.emptySelection();
	}

	public ISelection getLeftSelection() {
		if (fViewerLeft != null) {
			return fViewerLeft.getSelection();
		}
		return TextSelection.emptySelection();
	}

	public ISelection getRightSelection() {
		if (fViewerRight != null) {
			return fViewerRight.getSelection();
		}
		return TextSelection.emptySelection();
	}

	@Override
	public void setSelection(ISelection selection) {
		setSelection(selection, true);
	}

	public void setSelection(ISelection selection, boolean reveal) {
		if (fViewerInFocus != null) {
			if (reveal && !isSelectionInsideVisibleRegion(fViewerInFocus, selection))
				resetVisibleRegion();
			fViewerInFocus.setSelection(selection, reveal);
		}
	}

	private void resetVisibleRegion() {
		if (fViewers == null)
			return;

		for (int i= 0; i < fViewers.length; i++)
			fViewers[i].setVisibleRegion(0, fViewers[i].getDocument().getLength());
	}

	private static boolean isSelectionInsideVisibleRegion(TextViewer textViewer, ISelection selection) {
		if (!(selection instanceof ITextSelection))
			return false;

		ITextSelection textSelection= (ITextSelection)selection;
		IRegion visibleRegion= textViewer.getVisibleRegion();

		return textSelection.getOffset() >= visibleRegion.getOffset() && textSelection.getOffset() + textSelection.getLength() <= visibleRegion.getOffset() + visibleRegion.getLength();
	}
}
