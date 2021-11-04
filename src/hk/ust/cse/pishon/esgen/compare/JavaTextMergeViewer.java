package hk.ust.cse.pishon.esgen.compare;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.jface.text.source.CompositeRuler;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchPart;

public class JavaTextMergeViewer extends TextMergeViewer {

	private List<SourceViewer> viewers;

	public JavaTextMergeViewer(Composite parent, CompareConfiguration configuration) {
		super(parent, configuration);
	}

	@Override
	protected void createControls(Composite composite) {
		super.createControls(composite);
		IWorkbenchPart part = getCompareConfiguration().getContainer().getWorkbenchPart();
		if (part != null) {
			CompareSelectionProvider cesp = new CompareSelectionProvider();
			part.getSite().setSelectionProvider(cesp);
			/*
			 * Based on TextMergeViewer.createControls() implementation.
			 * Creation order of SourceViewer is ancestor, left, and right.
			 * It's quite risky and need to fully implement entire control creation in the future.
			 */
			SourceViewer leftViewer = viewers.get(1);
			SourceViewer rightViewer = viewers.get(2);
			cesp.setViewers(new SourceViewer[] { leftViewer, rightViewer }, null);
		}
	}

	@Override
	protected SourceViewer createSourceViewer(Composite parent, int textOrientation) {
		SourceViewer viewer = new SourceViewer(parent, new CompositeRuler(), textOrientation | SWT.H_SCROLL | SWT.V_SCROLL);
		if(viewers == null)
			viewers = new ArrayList<SourceViewer>();
		viewers.add(viewer);
		return viewer;
	}

	@Override
	protected void handleDispose(DisposeEvent event) {
		super.handleDispose(event);
		viewers.clear();
	}


}
