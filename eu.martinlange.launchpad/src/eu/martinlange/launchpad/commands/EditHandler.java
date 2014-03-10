package eu.martinlange.launchpad.commands;

import java.util.Map;
import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.commands.IHandler;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.debug.ui.IDebugModelPresentation;
import org.eclipse.debug.ui.ILaunchGroup;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.graphics.Image;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.commands.IElementUpdater;
import org.eclipse.ui.handlers.HandlerUtil;
import org.eclipse.ui.menus.UIElement;
import eu.martinlange.launchpad.Plugin;

public class EditHandler extends AbstractHandler implements IHandler, IElementUpdater {

	private static IDebugModelPresentation fDelegate;

	static {
		fDelegate = DebugUITools.newDebugModelPresentation();
	}


	@Override
	public Object execute(ExecutionEvent event) throws ExecutionException {
		try
		{
			ISelection selection = HandlerUtil.getCurrentSelection(event);

			ILaunchConfiguration configuration = null;
			configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(selection, ILaunchConfiguration.class);

			if (configuration == null)
				return null;

			ILaunchGroup group = DebugUITools.getLaunchGroup(configuration, ILaunchManager.RUN_MODE);
			if (group == null)
				group = DebugUITools.getLaunchGroup(configuration, ILaunchManager.DEBUG_MODE);
			if (group == null)
				group = DebugUITools.getLaunchGroup(configuration, ILaunchManager.PROFILE_MODE);

			if (group == null)
				return null;

			DebugUITools.openLaunchConfigurationPropertiesDialog(
					PlatformUI.getWorkbench().getActiveWorkbenchWindow().getShell(),
					configuration,
					group.getIdentifier(),
					null);

		} catch (Exception e) {
			Plugin.log(e);
		}

		return null;
	}


	@Override
	public void updateElement(UIElement element, @SuppressWarnings("rawtypes") Map parameters) {
		ISelectionService service = PlatformUI.getWorkbench().getActiveWorkbenchWindow().getSelectionService();
		ISelection selection = service.getSelection();

		ILaunchConfiguration configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(selection, ILaunchConfiguration.class);
		if (configuration != null) {
			element.setText(String.format("Edit %s...", fDelegate.getText(configuration)));

			final Image img = fDelegate.getImage(configuration);
			final ImageDescriptor desc = ImageDescriptor.createFromImage(img);
			element.setIcon(desc);
			element.setDisabledIcon(desc);
			element.setHoverIcon(desc);
		}
	}

}
