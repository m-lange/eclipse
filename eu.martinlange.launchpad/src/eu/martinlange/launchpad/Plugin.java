package eu.martinlange.launchpad;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;

import eu.martinlange.launchpad.internal.ISharedImages;
import eu.martinlange.launchpad.internal.LaunchpadAdapterFactory;
import eu.martinlange.launchpad.internal.SharedImages;

public class Plugin extends AbstractUIPlugin {

	public static final String PLUGIN_ID = "eu.martinlange.launchpad"; //$NON-NLS-1$

	private static Plugin plugin;
	private static ISharedImages sharedImages;


	public Plugin() {
	}


	public void start(BundleContext context) throws Exception {
		super.start(context);
		plugin = this;
		
		IAdapterFactory factory = new LaunchpadAdapterFactory();
		Platform.getAdapterManager().registerAdapters(factory, ISelection.class);
		Platform.getAdapterManager().registerAdapters(factory, IPath.class);
		Platform.getAdapterManager().registerAdapters(factory, IFile.class);
	}


	public void stop(BundleContext context) throws Exception {
		plugin = null;
		super.stop(context);
	}


	public static Plugin getDefault() {
		return plugin;
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
