package eu.martinlange.launchpad.ui.views.actions;

import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.PlatformUI;
import eu.martinlange.launchpad.ui.views.LaunchpadView;

public class EditAction extends Action {

	private static IDebugModelPresentation fDelegate;

	static {
		fDelegate = DebugUITools.newDebugModelPresentation();
	}

	private LaunchpadView fPart;


	public EditAction(LaunchpadView part) {
		super("Edit...", IAction.AS_PUSH_BUTTON);

		fPart = part;
	}


	@Override
	public void run() {
		ISelection selection = fPart.getSelection();
		ILaunchConfiguration configuration = null;
		configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(selection, ILaunchConfiguration.class);
		if (configuration == null)
			return;

		ILaunchGroup group = DebugUITools.getLaunchGroup(configuration, ILaunchManager.RUN_MODE);
		if (group == null)
			group = DebugUITools.getLaunchGroup(configuration, ILaunchManager.DEBUG_MODE);
		if (group == null)
			group = DebugUITools.getLaunchGroup(configuration, ILaunchManager.PROFILE_MODE);

		if (group == null)
			return;

		DebugUITools.openLaunchConfigurationPropertiesDialog(
				PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
				configuration,
				group.getIdentifier(),
				null);
	}


	@Override
	public String getText() {
		ISelection selection = fPart.getSelection();
		ILaunchConfiguration configuration = null;
		configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(selection, ILaunchConfiguration.class);
		if (configuration != null)
			return String.format("Edit %s...", fDelegate.getText(configuration));
		return super.getText();
	}


	@Override
	public ImageDescriptor getImageDescriptor() {
		ISelection selection = fPart.getSelection();
		ILaunchConfiguration configuration = null;
		configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(selection, ILaunchConfiguration.class);
		if (configuration != null)
			return ImageDescriptor.createFromImage(fDelegate.getImage(configuration));
		return null;
	}


	@Override
	public boolean isEnabled() {
		ISelection selection = fPart.getSelection();
		ILaunchConfiguration configuration = null;
		configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(selection, ILaunchConfiguration.class);
		return configuration != null;
	}

}
