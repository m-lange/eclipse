package eu.martinlange.launchpad.views;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import eu.martinlange.launchpad.Plugin;
import eu.martinlange.launchpad.internal.ISharedImages;

public class ChangeLaunchModeAction extends Action {

	private LaunchpadView fPart;
	private String fLaunchMode;


	public ChangeLaunchModeAction(LaunchpadView part, String mode) {
		super("", IAction.AS_RADIO_BUTTON);

		fPart = part;
		fLaunchMode = mode;

		if (fLaunchMode.equals(ILaunchManager.RUN_MODE)) {
			setText("Run");
			setImageDescriptor(Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_RUN));
		} else if (fLaunchMode.equals(ILaunchManager.DEBUG_MODE)) {
			setText("Debug");
			setImageDescriptor(Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_DEBUG));
		} else if (fLaunchMode.equals(ILaunchManager.PROFILE_MODE)) {
			setText("Profile");
			setImageDescriptor(Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_PROFILE));
		}

		setChecked(fPart.getLaunchMode().equals(fLaunchMode));
	}


	@Override
	public void run() {
		if (!isChecked())
			fPart.launchModeChanged(fLaunchMode);
	}

}
