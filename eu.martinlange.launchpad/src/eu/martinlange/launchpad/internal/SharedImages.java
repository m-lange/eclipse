package eu.martinlange.launchpad.internal;

import java.net.URL;
import org.eclipse.core.runtime.FileLocator;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.resource.ImageRegistry;
import org.eclipse.swt.graphics.Image;
import org.osgi.framework.Bundle;
import eu.martinlange.launchpad.Plugin;

public class SharedImages implements ISharedImages {

	private final static String ICONS_PATH   = "icons/full/";
	private final static String PATH_ETOOL   = ICONS_PATH + "etool16/";
	private final static String PATH_DTOOL   = ICONS_PATH + "dtool16/";
	private final static String PATH_ELCL    = ICONS_PATH + "elcl16/";
	private final static String PATH_DLCL    = ICONS_PATH + "dlcl16/";
	private final static String PATH_OBJECT  = ICONS_PATH + "obj16/";
	private final static String PATH_WIZBAN  = ICONS_PATH + "wizban/";

	private static ImageRegistry imageRegistry;


	public SharedImages() {
	}


	public Image getImage(String symbolicName) {
		return getImageRegistry().get(symbolicName);
	}


	public ImageDescriptor getImageDescriptor(String symbolicName) {
		return getImageRegistry().getDescriptor(symbolicName);
	}


	public static ImageRegistry getImageRegistry() {
		if (imageRegistry == null) {
			initializeImageRegistry();
		}
		return imageRegistry;
	}


	private static void initializeImageRegistry() {
		imageRegistry = new ImageRegistry();
		declareImages();
	}


	private final static void declareImages() {

		declareImage(SharedImages.IMG_OBJ_FOLDER, PATH_OBJECT + "folder.gif");
		declareImage(SharedImages.IMG_OBJ_FOLDERS, PATH_OBJECT + "folders.gif");
		
		declareImage(SharedImages.IMG_ETOOL_RUN, PATH_ETOOL + "run.gif");
		declareImage(SharedImages.IMG_ETOOL_RUN_DISABLED, PATH_DTOOL + "run.gif");
		
		declareImage(SharedImages.IMG_ETOOL_DEBUG, PATH_ETOOL + "debug.gif");
		declareImage(SharedImages.IMG_ETOOL_DEBUG_DISABLED, PATH_DTOOL + "debug.gif");
		
		declareImage(SharedImages.IMG_ETOOL_PROFILE, PATH_ETOOL + "profile.gif");
		declareImage(SharedImages.IMG_ETOOL_PROFILE_DISABLED, PATH_DTOOL + "profile.gif");

		declareImage(SharedImages.IMG_ELCL_NEWFOLDER, PATH_ELCL + "newfolder.gif");
		declareImage(SharedImages.IMG_ELCL_NEWFOLDER_DISABLED, PATH_DLCL + "newfolder.gif");
		
		declareImage(SharedImages.IMG_ELCL_COLLAPSEALL, PATH_ELCL + "collapseall.gif");
		declareImage(SharedImages.IMG_ELCL_COLLAPSEALL_DISABLED, PATH_DLCL + "collapseall.gif");
		
		declareImage(SharedImages.IMG_WIZBAN_NEWFOLDER, PATH_WIZBAN + "newfolder_wiz.png");
	}


	private final static void declareImage(String key, String path) {
		Bundle bundle = Plugin.getDefault().getBundle();
		URL url = FileLocator.find(bundle, new Path(path), null);
		ImageDescriptor desc = ImageDescriptor.createFromURL(url);
		imageRegistry.put(key, desc);
	}

}
