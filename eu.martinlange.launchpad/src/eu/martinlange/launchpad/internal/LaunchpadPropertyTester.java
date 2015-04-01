package eu.martinlange.launchpad.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;

public class LaunchpadPropertyTester extends PropertyTester {

	public static final String NAMESPACE = "eu.martinlange.launchpad";

	public static final String SUPPORTS_MODE = "supportsMode";


	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (SUPPORTS_MODE.equals(property))
			return supportsMode(receiver, (String) expectedValue);

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

}
