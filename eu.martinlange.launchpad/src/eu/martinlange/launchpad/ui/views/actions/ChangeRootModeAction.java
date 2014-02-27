package eu.martinlange.launchpad.ui.views.actions;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import eu.martinlange.launchpad.Plugin;
import eu.martinlange.launchpad.internal.ISharedImages;
import eu.martinlange.launchpad.ui.views.LaunchpadView;

public class ChangeRootModeAction extends Action {

	private LaunchpadView fPart;


	public ChangeRootModeAction(LaunchpadView part) {
		super("Categorized", IAction.AS_CHECK_BOX);

		fPart = part;
		setImageDescriptor(Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_OBJ_FOLDERS));
		setChecked(fPart.getRootMode() == LaunchpadView.FOLDERS_AS_ROOTS);
	}


	@Override
	public void run() {
		int newMode = fPart.getRootMode() == LaunchpadView.FOLDERS_AS_ROOTS
				? LaunchpadView.GROUPS_AS_ROOTS
				: LaunchpadView.FOLDERS_AS_ROOTS;
		fPart.rootModeChanged(newMode);
	}

}