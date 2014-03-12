package eu.martinlange.launchpad.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;
import eu.martinlange.launchpad.model.TreeNode;

public class LaunchpadPropertyTester extends PropertyTester {

	public static final String NAMESPACE = "eu.martinlange.launchpad";

	public static final String SUPPORTS_MODE = "supportsMode";
	public static final String IS_FOLDER = "isFolder";
	public static final String IS_LAUNCH_CONFIGURATION = "isLaunchConfigguration";
	public static final String IS_EDITABLE = "isEditable";


	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (SUPPORTS_MODE.equals(property))
			return supportsMode(receiver, (String) expectedValue);

		if (IS_FOLDER.equals(property))
			return isFolder(receiver);

		if (IS_LAUNCH_CONFIGURATION.equals(property))
			return isLaunchConfiguration(receiver);

		if (IS_EDITABLE.equals(property))
			return isEditable(receiver);

		return false;
	}


	private boolean supportsMode(Object receiver, String mode) {
		ILaunchConfiguration configuration = null;
		configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(receiver, ILaunchConfiguration.class);

		if (configuration == null)
			return false;

		try {
			if (configuration.supportsMode(mode))
				return true;
		} catch (CoreException e) {
		}

		return false;
	}


	private boolean isFolder(Object receiver) {
		TreeNode node = null;
		node = (TreeNode) Platform.getAdapterManager().getAdapter(receiver, TreeNode.class);

		if (node == null)
			return false;

		return node.getData() instanceof String;
	}


	private boolean isLaunchConfiguration(Object receiver) {
		ILaunchConfiguration configuration = null;
		configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(receiver, ILaunchConfiguration.class);
		return configuration != null;
	}


	private boolean isEditable(Object receiver) {
		TreeNode node = null;
		node = (TreeNode) Platform.getAdapterManager().getAdapter(receiver, TreeNode.class);

		if (node == null)
			return false;

		return node.isEditable();
	}

}
