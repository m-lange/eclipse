package eu.martinlange.tools.eclipse;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import eu.martinlange.tools.eclipse.preferences.PreferenceStore;
import eu.martinlange.tools.eclipse.theme.ThemedRangeIndicatorInstaller;

public class Plugin extends AbstractUIPlugin implements IStartup {

	public static final String PLUGIN_ID = "eu.martinlange.tools.eclipse";

	private static Plugin plugin;
	private PreferenceStore preferenceStore;
	private static ISharedImages sharedImages;


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

		if (preferenceStore != null && preferenceStore.needsSaving())
			preferenceStore.save();
		preferenceStore = null;

		try {
			ThemedRangeIndicatorInstaller.INSTANCE.uninstall();
		} finally {
			super.stop(context);
		}
	}


	public static Plugin getDefault() {
		return plugin;
	}


	@Override
	public PreferenceStore getPreferenceStore() {
		if (preferenceStore == null) {
			preferenceStore = new PreferenceStore(InstanceScope.INSTANCE, getBundle().getSymbolicName());

		}
		return preferenceStore;
	}


	public static ISharedImages getSharedImages() {
		if (sharedImages == null) {
			sharedImages = new SharedImages();
		}
		return sharedImages;
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
