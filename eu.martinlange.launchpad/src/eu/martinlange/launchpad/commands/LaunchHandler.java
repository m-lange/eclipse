package eu.martinlange.launchpad.commands;

import java.util.Hashtable;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IConfigurationElement;
import org.eclipse.core.runtime.IExecutableExtension;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.handlers.HandlerUtil;
import eu.martinlange.launchpad.Plugin;

public class LaunchHandler extends AbstractHandler implements IHandler, IExecutableExtension {

	private String fMode = null;


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try {
			ISelection selection = HandlerUtil.getCurrentSelection(event);

			ILaunchConfiguration configuration = null;
			configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(selection, ILaunchConfiguration.class);

			if (configuration != null && configuration.supportsMode(fMode))
				DebugUITools.buildAndLaunch(configuration, fMode, new NullProgressMonitor());
		} catch (Exception e) {
			Plugin.log(e);
		}

		return null;
	}


	@Override
	public void setInitializationData(IConfigurationElement config, String propertyName, Object data) throws CoreException {
		if (data instanceof Hashtable) {
			final Hashtable<?, ?> parameters = (Hashtable<?, ?>) data;
			final Object mode = parameters.get("mode");
			if (mode instanceof String)
				fMode = (String) mode;
		}
	}

}
