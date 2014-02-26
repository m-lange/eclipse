package eu.martinlange.launchpad.internal;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IAdapterFactory;
import org.eclipse.core.runtime.IPath;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import eu.martinlange.launchpad.Plugin;
import eu.martinlange.launchpad.model.ElementTreeData;

@SuppressWarnings("rawtypes")
public class LaunchConfigurationAdapterFactory implements IAdapterFactory {

	private static ILaunchManager fLaunchManager;

	static {
		fLaunchManager = DebugPlugin.getDefault().getLaunchManager();
	}


	@Override
	public Object getAdapter(Object adaptableObject, Class adapterType) {
		if (adapterType != ILaunchConfiguration.class)
			return null;

		if (adaptableObject instanceof IPath)
			return getAdapter((IPath) adaptableObject);

		else if (adaptableObject instanceof IFile)
			return getAdapter((IFile) adaptableObject);

		else if (adaptableObject instanceof ISelection)
			return getAdapter((ISelection) adaptableObject);
		
		else if (adaptableObject instanceof ElementTreeData)
			return getAdapter((ElementTreeData) adaptableObject);

		return null;
	}


	@Override
	public Class[] getAdapterList() {
		return new Class[] { ILaunchConfiguration.class };
	}


	protected Object getAdapter(IPath path) {
		IFile file = ResourcesPlugin.getWorkspace().getRoot().getFile(path);
		return getAdapter(file);
	}


	protected Object getAdapter(IFile file) {
		if (!file.getFileExtension().equals("launch"))
			return null;

		try {
			ILaunchConfiguration configuration = fLaunchManager.getLaunchConfiguration(file);
			if (configuration != null) {
				String name = configuration.getName();
				for (ILaunchConfiguration e : fLaunchManager.getLaunchConfigurations()) {
					if (name.equals(e.getName())) {
						configuration = e;
						break;
					}
				}
				return configuration;
			}
		} catch (CoreException e) {
			Plugin.log(e);
		}
		return null;
	}


	protected Object getAdapter(ISelection selection) {
		Object obj = null;
		if (selection instanceof IStructuredSelection && ((IStructuredSelection) selection).size() == 1)
			obj = ((IStructuredSelection) selection).getFirstElement();

		if (obj instanceof ILaunchConfiguration)
			return obj;
		else if (obj instanceof IFile)
			return getAdapter((IFile) obj);
		else if (obj instanceof ElementTreeData)
			return getAdapter((ElementTreeData) obj);

		return null;
	}
	
	
	protected Object getAdapter(ElementTreeData selection) {
		if (selection.getData() instanceof ILaunchConfiguration)
			return selection.getData();

		return null;
	}

}
