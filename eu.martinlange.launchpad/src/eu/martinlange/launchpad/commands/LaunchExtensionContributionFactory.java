package eu.martinlange.launchpad.commands;

import java.util.HashMap;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.menus.CommandContributionItem;
import org.eclipse.ui.menus.CommandContributionItemParameter;
import org.eclipse.ui.menus.ExtensionContributionFactory;
import org.eclipse.ui.menus.IContributionRoot;
import org.eclipse.ui.services.IServiceLocator;
import eu.martinlange.launchpad.Plugin;
import eu.martinlange.launchpad.internal.ISharedImages;

public class LaunchExtensionContributionFactory extends ExtensionContributionFactory {

	public LaunchExtensionContributionFactory() {
	}


	@SuppressWarnings("unchecked")
	@Override
	public void createContributionItems(IServiceLocator serviceLocator, IContributionRoot additions) {
		IWorkbenchWindow window = PlatformUI.getWorkbench().getActiveWorkbenchWindow();
		ISelection selection = window.getSelectionService().getSelection();

		ILaunchConfiguration configuration = null;
		configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(selection, ILaunchConfiguration.class);

		if (configuration == null)
			return;

		try {
			if (configuration.supportsMode(ILaunchManager.RUN_MODE)) {
				CommandContributionItemParameter p = new CommandContributionItemParameter(
						serviceLocator,
						"",
						LaunchCommandHandler.COMMAND_ID,
						SWT.PUSH);

				p.label = "Run";
				p.icon = Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_RUN);
				p.disabledIcon = Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_DTOOL_RUN);
				p.parameters = new HashMap<String, String>();
				p.parameters.put(LaunchCommandHandler.PARAMETER_ID, ILaunchManager.RUN_MODE);

				CommandContributionItem item = new CommandContributionItem(p);
				item.setVisible(true);
				additions.addContributionItem(item, null);
			}

			if (configuration.supportsMode(ILaunchManager.DEBUG_MODE)) {
				CommandContributionItemParameter p = new CommandContributionItemParameter(
						serviceLocator,
						"",
						LaunchCommandHandler.COMMAND_ID,
						SWT.PUSH);

				p.label = "Debug";
				p.icon = Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_DEBUG);
				p.disabledIcon = Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_DTOOL_DEBUG);
				p.parameters = new HashMap<String, String>();
				p.parameters.put(LaunchCommandHandler.PARAMETER_ID, ILaunchManager.DEBUG_MODE);

				CommandContributionItem item = new CommandContributionItem(p);
				item.setVisible(true);
				additions.addContributionItem(item, null);
			}

			if (configuration.supportsMode(ILaunchManager.PROFILE_MODE)) {
				CommandContributionItemParameter p = new CommandContributionItemParameter(
						serviceLocator,
						"",
						LaunchCommandHandler.COMMAND_ID,
						SWT.PUSH);

				p.label = "Profile";
				p.icon = Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_PROFILE);
				p.disabledIcon = Plugin.getSharedImages().getImageDescriptor(ISharedImages.IMG_DTOOL_PROFILE);
				p.parameters = new HashMap<String, String>();
				p.parameters.put(LaunchCommandHandler.PARAMETER_ID, ILaunchManager.PROFILE_MODE);

				CommandContributionItem item = new CommandContributionItem(p);
				item.setVisible(true);
				additions.addContributionItem(item, null);
			}

			additions.addContributionItem(new Separator(), null);

		} catch (CoreException e) {
			Plugin.log(e);
		}
	}
}
