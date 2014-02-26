package eu.martinlange.launchpad.model;

import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.Viewer;

public class DefaultContentProvider implements ITreeContentProvider, ILaunchConfigurationListener {

	protected static final Object[] EMPTY = new Object[0];

	protected Viewer fViewer;
	protected ILaunchManager fLaunchManager;


	@Override
	public Object[] getChildren(Object parentElement) {
		if (parentElement instanceof ILaunchManager)
			return fLaunchManager.getLaunchConfigurationTypes();

		if (parentElement instanceof ILaunchConfigurationType) {
			ILaunchConfigurationType type = (ILaunchConfigurationType) parentElement;
			try {
				return fLaunchManager.getLaunchConfigurations(type);
			} catch (CoreException e) {
			}
		}
				
		return EMPTY;
	}


	@Override
	public Object[] getElements(Object inputElement) {
		return getChildren(inputElement);
	}


	@Override
	public boolean hasChildren(Object element) {
		if (element instanceof ILaunchManager)
			return true;

		if (element instanceof ILaunchConfigurationType) {
			ILaunchConfigurationType type = (ILaunchConfigurationType) element;
			try {
				return fLaunchManager.getLaunchConfigurations(type).length > 0;
			} catch (CoreException e) {
			}
		}
		
		return false;
	}


	@Override
	public Object getParent(Object element) {
		if (element instanceof ILaunchConfigurationType)
			return fLaunchManager;

		if (element instanceof ILaunchConfiguration) {
			ILaunchConfiguration configuration = (ILaunchConfiguration) element;
			try {
				return configuration.getType();
			} catch (CoreException e) {
			}
		}

		return null;
	}


	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {

		fViewer = viewer;
		if (fLaunchManager != null)
			fLaunchManager.removeLaunchConfigurationListener(this);

		if (newInput instanceof ILaunchManager) {
			fLaunchManager = (ILaunchManager) newInput;
			fLaunchManager.addLaunchConfigurationListener(this);
		}
	}


	@Override
	public void dispose() {
		if (fLaunchManager != null)
			fLaunchManager.removeLaunchConfigurationListener(this);
	}


	@Override
	public void launchConfigurationAdded(ILaunchConfiguration configuration) {
		if (fViewer != null) {
			fViewer.refresh();
		}
	}


	@Override
	public void launchConfigurationChanged(ILaunchConfiguration configuration) {
		if (fViewer != null) {
			fViewer.refresh();
		}
	}


	@Override
	public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
		if (fViewer != null) {
			fViewer.refresh();
		}
	}

}
