package eu.martinlange.theme;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import eu.martinlange.theme.internal.ThemedRangeIndicatorInstaller;

public class Plugin extends AbstractUIPlugin implements IStartup {

	public static final String PLUGIN_ID = "eu.martinlange.theme"; //$NON-NLS-1$
	private static Plugin plugin;


	public Plugin() {
	}


	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		Display.getDefault().asyncExec(new Runnable() {
			public void run() {
				ThemedRangeIndicatorInstaller.INSTANCE.install();
			}
		});
	}


	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
		
		try {
			ThemedRangeIndicatorInstaller.INSTANCE.uninstall();
		} finally {
			super.stop(context);
		}
	}


	public static Plugin getDefault() {
		return plugin;
	}


	public static void log(IStatus status) {
		getDefault().getLog().log(status);
	}


	public static void log(Throwable t) {
		if (t instanceof CoreException)
			log(((CoreException) t).getStatus());
		else
			log(new Status(IStatus.ERROR, PLUGIN_ID, 120, "Problems occurred when invoking code from plug-in: \"" + PLUGIN_ID + "\".", t));
	}


	public static void log(String message) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, 120, message, null));
	}


	public static void log(String message, Throwable t) {
		log(new Status(IStatus.ERROR, PLUGIN_ID, 120, message, t));
	}


	@Override
	public void earlyStartup() {
		// nothing, I just want the plug-in to be loaded
	}
}
