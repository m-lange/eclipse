package eu.martinlange.launchpad.ui.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import eu.martinlange.launchpad.ui.views.LaunchpadView;

public class SortByNameAction extends Action {

	private LaunchpadView fPart;


	public SortByNameAction(LaunchpadView part) {
		super("Sort by Name", IAction.AS_CHECK_BOX);

		fPart = part;
		setChecked(fPart.getSortMode() == LaunchpadView.SORT_BY_NAME);
		setEnabled(fPart.getRootMode() == LaunchpadView.FOLDERS_AS_ROOTS);
	}


	@Override
	public void run() {
		int newMode = fPart.getSortMode() == LaunchpadView.SORT_BY_NAME
				? LaunchpadView.SORT_DEFAULT
				: LaunchpadView.SORT_BY_NAME;
		fPart.sortModeChanged(newMode);
	}

}
