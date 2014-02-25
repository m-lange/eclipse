package eu.martinlange.console.internal;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.graphics.Image;

public interface ISharedImages {

	public final static String IMG_OBJ_STYLE        = "IMG_OBJ_STYLE";
	public final static String IMG_OBJ_PATTERN      = "IMG_OBJ_PATTERN";
	public final static String IMG_OBJ_GROUP        = "IMG_OBJ_GROUP";

	public final static String IMG_ETOOL_HIGHLIGHT  = "IMG_ETOOL_HIGHLIGHT";


	public Image getImage(String symbolicName);


	public ImageDescriptor getImageDescriptor(String symbolicName);

}
