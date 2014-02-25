package eu.martinlange.launchpad.decorators;

import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.jface.viewers.BaseLabelProvider;
import org.eclipse.jface.viewers.ILabelDecorator;
import org.eclipse.swt.graphics.Image;

public class LaunchConfigurationDecorator extends BaseLabelProvider implements ILabelDecorator {

	private static IDebugModelPresentation fDelegate;

	static {
		fDelegate = DebugUITools.newDebugModelPresentation();
	}


	@Override
	public Image decorateImage(Image image, Object element) {
		ILaunchConfiguration configuration = null;
		configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(element, ILaunchConfiguration.class);

		if (configuration != null)
			return fDelegate.getImage(configuration);

		return image;
	}


	@Override
	public String decorateText(String text, Object element) {
		ILaunchConfiguration configuration = null;
		configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(element, ILaunchConfiguration.class);

		if (configuration != null)
			return fDelegate.getText(configuration);

		return text;
	}

}
