package hk.ust.cse.pishon.esgen.sourceprovider;

import java.util.HashMap;
import java.util.Map;

import org.eclipse.ui.AbstractSourceProvider;
import org.eclipse.ui.ISources;

public class SelectionState extends AbstractSourceProvider {

	public final static String OLD_SELECTED = "hk.ust.cse.pishon.esgen.sourceprovider.oldselected";
	public final static String NEW_SELECTED = "hk.ust.cse.pishon.esgen.sourceprovider.newselected";
	public final static String FOCUS = "hk.ust.cse.pishon.esgen.sourceprovider.focus";
	public final static String SELECTED = "SELECTED";
	public final static String NOT_SELECTED = "NOTSELECTED";
	public final static String OLD = "OLD";
	public final static String NEW = "NEW";
	public final static String NONE = "NONE";
	private boolean oldSelected = false;
	private boolean newSelected = false;
	private String focus = null;

	@Override
	public void dispose() {
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map getCurrentState() {
		Map map = new HashMap<String, String>(2);
		map.put(OLD_SELECTED, oldSelected ? SELECTED : NOT_SELECTED);
		map.put(NEW_SELECTED, newSelected ? SELECTED : NOT_SELECTED);
		map.put(FOCUS, focus);
		return map;
	}

	@Override
	public String[] getProvidedSourceNames() {
		return new String[] { OLD_SELECTED, NEW_SELECTED, FOCUS };
	}

	public void setOldSelected(boolean selected){
		oldSelected = selected;
		fireSourceChanged(ISources.ACTIVE_EDITOR, OLD_SELECTED, oldSelected ? SELECTED : NOT_SELECTED);
	}

	public void setNewSelected(boolean selected){
		newSelected = selected;
		fireSourceChanged(ISources.ACTIVE_EDITOR, NEW_SELECTED, newSelected ? SELECTED : NOT_SELECTED);
	}

	public void setFocus(String focusedView){
		focus = focusedView;
		fireSourceChanged(ISources.ACTIVE_EDITOR, FOCUS, focus);
	}
}
