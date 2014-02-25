package eu.martinlange.launchpad.commands;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import eu.martinlange.launchpad.Plugin;

public class LaunchCommandHandler extends AbstractHandler implements IHandler {

	public static final String COMMAND_ID = "eu.martinlange.launchpad.command.launch";
	public static final String PARAMETER_ID = "eu.martinlange.launchpad.command.launch.mode";


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			String mode = event.getParameter(PARAMETER_ID);

			ISelection selection = HandlerUtil.getCurrentSelection(event);

			ILaunchConfiguration configuration = null;
			configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(selection, ILaunchConfiguration.class);

			if (configuration != null && configuration.supportsMode(mode))
				DebugUITools.buildAndLaunch(configuration, mode, new NullProgressMonitor());
		} catch (Exception e) {
			Plugin.log(e);
		}

		return null;
	}

}
