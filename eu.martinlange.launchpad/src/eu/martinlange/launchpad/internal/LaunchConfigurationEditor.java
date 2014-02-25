package eu.martinlange.launchpad.internal;

import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.ui.IEditorLauncher;
import org.eclipse.ui.PlatformUI;
import eu.martinlange.launchpad.Plugin;

public class LaunchConfigurationEditor implements IEditorLauncher {

	@Override
	public void open(IPath path) {
		try
		{
			ILaunchConfiguration configuration = null;
			configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(path, ILaunchConfiguration.class);

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

		} catch (Exception e) {
			Plugin.log(e);
		}
	}

}
