package hk.ust.cse.pishon.esgen.compare;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.IViewerCreator;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;

public class JavaTextMergeViewerCreator implements IViewerCreator {

	public JavaTextMergeViewerCreator() {
	}

	@Override
	public Viewer createViewer(Composite parent, CompareConfiguration config) {
		return new JavaTextMergeViewer(parent, config);
	}

}
