package eu.martinlange.launchpad.ui.views.actions;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import eu.martinlange.launchpad.Plugin;
import eu.martinlange.launchpad.internal.ISharedImages;
import eu.martinlange.launchpad.ui.views.LaunchpadView;

public class LaunchAction extends Action {

	private LaunchpadView fPart;
	private String fLaunchMode;


	public LaunchAction(LaunchpadView part, String mode) {
		super("", IAction.AS_PUSH_BUTTON);

		fPart = part;
		fLaunchMode = mode;

		if (fLaunchMode.equals(ILaunchManager.RUN_MODE)) {
			setText("Run");
			setImageDescriptor(Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_RUN));
			setDisabledImageDescriptor(Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_RUN_DISABLED));
		}

		else if (fLaunchMode.equals(ILaunchManager.DEBUG_MODE)) {
			setText("Debug");
			setImageDescriptor(Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_DEBUG));
			setDisabledImageDescriptor(Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_DEBUG_DISABLED));
		}

		else if (fLaunchMode.equals(ILaunchManager.PROFILE_MODE)) {
			setText("Profile");
			setImageDescriptor(Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_PROFILE));
			setDisabledImageDescriptor(Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_PROFILE_DISABLED));
		}

	}


	@Override
	public void run() {
		ISelection selection = fPart.getSelection();
		ILaunchConfiguration configuration = null;
		configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(selection, ILaunchConfiguration.class);
		if (configuration == null)
			return;

		try {
			DebugUITools.buildAndLaunch(configuration, fLaunchMode, new NullProgressMonitor());
		} catch (CoreException e) {
		}
	}


	@Override
	public boolean isEnabled() {
		ISelection selection = fPart.getSelection();
		ILaunchConfiguration configuration = null;
		configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(selection, ILaunchConfiguration.class);
		if (configuration == null)
			return false;

		try {
			return configuration.supportsMode(fLaunchMode);
		} catch (CoreException e) {
		}

		return false;
	}

}
