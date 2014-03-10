package eu.martinlange.launchpad.internal;

import org.eclipse.core.expressions.PropertyTester;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.Platform;
import org.eclipse.debug.core.ILaunchConfiguration;

public class ConfigurationPropertyTester extends PropertyTester {

	public static final String NAMESPACE = "eu.martinlange.launchpad";
	public static final String PROPERTY = "supportsMode";
	
	
	@Override
	public boolean test(Object receiver, String property, Object[] args, Object expectedValue) {
		if (!PROPERTY.equals(property))
			return false;
		
		ILaunchConfiguration configuration = null;
		configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(receiver, ILaunchConfiguration.class);
		
		if (configuration == null)
			return false;
		
		try {
			if (configuration.supportsMode((String) expectedValue))
				return true;
		} catch (CoreException e) {
		}
		
		return false;
	}

}
