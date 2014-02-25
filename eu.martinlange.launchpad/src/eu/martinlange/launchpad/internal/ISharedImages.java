package eu.martinlange.launchpad.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public interface ISharedImages {

	public final static String IMG_OBJ_FOLDER      = "IMG_OBJ_FOLDER";
	
	public final static String IMG_ETOOL_RUN       = "IMG_ETOOL_RUN";
	public final static String IMG_ETOOL_DEBUG     = "IMG_ETOOL_DEBUG";
	public final static String IMG_ETOOL_PROFILE   = "IMG_ETOOL_PROFILE";

	public final static String IMG_DTOOL_RUN       = "IMG_DTOOL_RUN";
	public final static String IMG_DTOOL_DEBUG     = "IMG_DTOOL_DEBUG";
	public final static String IMG_DTOOL_PROFILE   = "IMG_DTOOL_PROFILE";


	public Image getImage(String symbolicName);


	public ImageDescriptor getImageDescriptor(String symbolicName);

}
