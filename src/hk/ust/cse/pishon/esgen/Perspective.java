package hk.ust.cse.pishon.esgen;

import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
		layout.setFixed(true);
		layout.addStandaloneView("hk.ust.cse.pishon.esgen.views.changeview", true, IPageLayout.LEFT, 0.2f,
				IPageLayout.ID_EDITOR_AREA);
		layout.addStandaloneView("hk.ust.cse.pishon.esgen.views.scriptview", true,
				IPageLayout.BOTTOM, IPageLayout.RATIO_MAX, IPageLayout.ID_EDITOR_AREA);
	}

}