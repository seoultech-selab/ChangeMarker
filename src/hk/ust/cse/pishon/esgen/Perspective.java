package hk.ust.cse.pishon.esgen;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

public class Perspective implements IPerspectiveFactory {

	@Override
	public void createInitialLayout(IPageLayout layout) {
		layout.setEditorAreaVisible(true);
		layout.setFixed(true);
		IFolderLayout listFolder = layout.createFolder("ListFolder", IPageLayout.LEFT, 0.25f, IPageLayout.ID_EDITOR_AREA);
		listFolder.addView("hk.ust.cse.pishon.esgen.views.changeview");
		listFolder.addView("hk.ust.cse.pishon.esgen.views.scriptlist");
		IFolderLayout scriptFolder = layout.createFolder("ScriptFolder", 
				IPageLayout.BOTTOM, IPageLayout.RATIO_MAX, IPageLayout.ID_EDITOR_AREA);
		scriptFolder.addView("hk.ust.cse.pishon.esgen.views.scriptstatview");
		scriptFolder.addView("hk.ust.cse.pishon.esgen.views.multiscriptview");
	}

}