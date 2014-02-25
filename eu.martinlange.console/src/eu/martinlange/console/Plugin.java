package eu.martinlange.console;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import eu.martinlange.console.internal.ISharedImages;
import eu.martinlange.console.internal.PreferenceStore;
import eu.martinlange.console.internal.SharedImages;

public class Plugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "eu.martinlange.console";

	private static Plugin plugin;
	private PreferenceStore preferenceStore;
	private static ISharedImages sharedImages;


	public Plugin() {
	}


	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
	}


	public void stop(BundleContext context) throws Exception {
		plugin = null;

		if (preferenceStore != null && preferenceStore.needsSaving())
			preferenceStore.save();
		preferenceStore = null;

		super.stop(context);
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

}
