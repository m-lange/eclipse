package eu.martinlange.launchpad.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public interface ISharedImages {

	public final static String IMG_OBJ_FOLDER                 = "IMG_OBJ_FOLDER";
	public final static String IMG_OBJ_FOLDERS            = "IMG_OBJ_WORKINGSETS";
	
	public final static String IMG_ETOOL_RUN                  = "IMG_ETOOL_RUN";
	public final static String IMG_ETOOL_RUN_DISABLED         = "IMG_DTOOL_RUN_DISABLED ";
	
	public final static String IMG_ETOOL_DEBUG                = "IMG_ETOOL_DEBUG";
	public final static String IMG_ETOOL_DEBUG_DISABLED       = "IMG_DTOOL_DEBUG_DISABLED ";
	
	public final static String IMG_ETOOL_PROFILE              = "IMG_ETOOL_PROFILE";
	public final static String IMG_ETOOL_PROFILE_DISABLED     = "IMG_DTOOL_PROFILE_DISABLED ";
	
	public final static String IMG_ELCL_COLLAPSEALL           = "IMG_ELCL_COLLAPSEALL";
	public final static String IMG_ELCL_COLLAPSEALL_DISABLED  = "IMG_ELCL_COLLAPSEALL_DISABLED";
	

	public Image getImage(String symbolicName);


	public ImageDescriptor getImageDescriptor(String symbolicName);

}
