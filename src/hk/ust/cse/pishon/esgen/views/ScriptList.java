package hk.ust.cse.pishon.esgen.views;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.part.ViewPart;

import hk.ust.cse.pishon.esgen.model.ScriptItem;

public class ScriptList extends ViewPart {
	public static final String ID = "hk.ust.cse.pishon.esgen.views.scriptlist";
	public static final String METADATA_PATH = ".metadata" + File.separator + "changemarker";
	private TableViewer viewer;
	private List<ScriptItem> scripts;
	private ScriptItem opened = null;

	class ViewLabelProvider extends LabelProvider  {
		public String getColumnText(Object obj, int index) {
			return getText(obj);
		}

		public Image getColumnImage(Object obj, int index) {
			return getImage(obj);
		}

		@Override
		public Image getImage(Object obj) {
			return PlatformUI.getWorkbench().getSharedImages().getImage(
					ISharedImages.IMG_OBJ_ELEMENT);
		}

		@Override
		public String getText(Object element) {
			if(element instanceof ScriptItem){
				ScriptItem item = (ScriptItem)element;
				if(item == opened)
					return item.fileName + " - opened";
				return item.fileName;
			}
			return super.getText(element);
		}
	}

	/**
	 * This is a callback that will allow us to create the viewer and initialize
	 * it.
	 */
	@Override
	public void createPartControl(Composite parent) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		final IWorkbenchPage page = window.getActivePage();
		viewer = new TableViewer(parent, SWT.H_SCROLL | SWT.V_SCROLL);
		viewer.setContentProvider(ArrayContentProvider.getInstance());
		viewer.setLabelProvider(new ViewLabelProvider());
		// Provide the input to the ContentProvider
		scripts = new ArrayList<ScriptItem>();		
		page.addPartListener(new IPartListener2(){
		});
	}
	@Override
	public void saveState(IMemento memento) {
		super.saveState(memento);
	}

	/**
	 * Passing the focus request to the viewer's control.
	 */
	@Override
	public void setFocus() {
		viewer.getControl().setFocus();
	}

	public void setInput(){
		viewer.setInput(scripts);
	}

	public void setInput(List<ScriptItem> newInput){
		scripts.clear();
		if(newInput != null) {			
			scripts.addAll(newInput);
			viewer.setInput(scripts);
		}		
	}

	public List<ScriptItem> getInput(){
		List<ScriptItem> scripts = new ArrayList<ScriptItem>(this.scripts);
		return scripts;
	}
}