package hk.ust.cse.pishon.esgen.compare;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;

import org.eclipse.compare.CompareConfiguration;
import org.eclipse.compare.CompareEditorInput;
import org.eclipse.compare.contentmergeviewer.TextMergeViewer;
import org.eclipse.compare.internal.CompareEditorSelectionProvider;
import org.eclipse.compare.internal.MergeSourceViewer;
import org.eclipse.compare.internal.ViewerDescriptor;
import org.eclipse.compare.structuremergeviewer.DiffNode;
import org.eclipse.compare.structuremergeviewer.ICompareInput;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.internal.ui.compare.JavaMergeViewer;
import org.eclipse.jface.text.source.SourceViewer;
import org.eclipse.jface.viewers.ISelectionProvider;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IWorkbenchSite;

import hk.ust.cse.pishon.esgen.model.Change;

public class CompareInput extends CompareEditorInput {

	private Change change;
	private SourceViewer leftViewer;
	private SourceViewer rightViewer;

	public CompareInput(Change change){
		super(new CompareConfiguration());
		this.change = change;
		setTitle(change.getName());
		CompareConfiguration config = getCompareConfiguration();
		config.setLeftEditable(false);
		config.setLeftLabel("Old Version");
		config.setRightEditable(false);
		config.setRightLabel("New Version");
		leftViewer = null;
		rightViewer = null;
	}

	@Override
	protected Object prepareInput(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		return new DiffNode(change.getOldFile(), change.getNewFile());
	}



	@Override
	public void setContentViewerDescriptor(ViewerDescriptor vd) {
		// TODO Auto-generated method stub
		super.setContentViewerDescriptor(vd);
	}

	@Override
	public Viewer findContentViewer(Viewer oldViewer, ICompareInput input, Composite parent) {
		Viewer viewer = super.findContentViewer(oldViewer, input, parent);
		try {
			if(viewer instanceof JavaMergeViewer){
				Field fViewers = viewer.getClass().getDeclaredField("fSourceViewer");
				fViewers.setAccessible(true);
				ArrayList <SourceViewer> viewers = (ArrayList <SourceViewer>)fViewers.get(viewer);
				leftViewer = viewers.get(1);
				rightViewer = viewers.get(2);
			}else if(viewer instanceof TextMergeViewer){
				Field fLeft = viewer.getClass().getDeclaredField("fLeft");
				Field fRight = viewer.getClass().getDeclaredField("fRight");
				fLeft.setAccessible(true);
				fRight.setAccessible(true);
				leftViewer = ((MergeSourceViewer)fLeft.get(viewer)).getSourceViewer();
				rightViewer = ((MergeSourceViewer)fRight.get(viewer)).getSourceViewer();
			}
		} catch (NoSuchFieldException e) {
			e.printStackTrace();
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
		if (leftViewer != null && rightViewer != null) {
			IWorkbenchSite site = getWorkbenchPart().getSite();
			ISelectionProvider provider = site.getSelectionProvider();
			if (provider == null || provider instanceof CompareEditorSelectionProvider) {
				site.setSelectionProvider(new CompareSelectionProvider());
				provider = site.getSelectionProvider();
			}
			if (provider instanceof CompareSelectionProvider)
				((CompareSelectionProvider) provider).setViewers(new SourceViewer[] { leftViewer, rightViewer },
						null);
		}
		return viewer;
	}

	public Change getChange(){
		return this.change;
	}
}
