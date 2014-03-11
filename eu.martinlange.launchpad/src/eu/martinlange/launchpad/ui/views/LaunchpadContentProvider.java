package eu.martinlange.launchpad.ui.views;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchConfigurationType;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.viewers.ITreeContentProvider;
import org.eclipse.jface.viewers.StructuredViewer;
import org.eclipse.jface.viewers.Viewer;
import eu.martinlange.launchpad.model.TreeNode;

public class LaunchpadContentProvider implements ITreeContentProvider, ILaunchConfigurationListener, PropertyChangeListener {

	protected static final Object[] EMPTY = new Object[0];

	protected StructuredViewer fViewer;
	protected ILaunchManager fLaunchManager;
	protected TreeNode fRootNode;


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

		if (parentElement instanceof TreeNode) {
			TreeNode data = (TreeNode) parentElement;
			return data.getChildren();
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

		if (element instanceof TreeNode) {
			TreeNode data = (TreeNode) element;
			return data.hasChildren();
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

		if (element instanceof TreeNode) {
			TreeNode data = (TreeNode) element;
			return data.getParent();
		}

		return null;
	}


	@Override
	public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
		if (fLaunchManager != null)
			fLaunchManager.removeLaunchConfigurationListener(this);
		if (fRootNode != null)
			fRootNode.removePropertyChangeListener(this);

		fViewer = (StructuredViewer) viewer;
		fLaunchManager = null;
		fRootNode = null;

		if (newInput instanceof ILaunchManager) {
			fLaunchManager = (ILaunchManager) newInput;
			fLaunchManager.addLaunchConfigurationListener(this);
		}
		if (newInput instanceof TreeNode) {
			fRootNode = (TreeNode) newInput;
			fRootNode.addPropertyChangeListener(this);
		}
	}


	@Override
	public void dispose() {
		if (fLaunchManager != null)
			fLaunchManager.removeLaunchConfigurationListener(this);
		if (fRootNode != null)
			fRootNode.removePropertyChangeListener(this);
		
		fLaunchManager = null;
		fRootNode = null;
	}


	@Override
	public void launchConfigurationAdded(ILaunchConfiguration configuration) {
		if (fViewer != null) { 
			fViewer.refresh(true);
		}
	}


	@Override
	public void launchConfigurationChanged(ILaunchConfiguration configuration) {
		if (fViewer != null) {
			fViewer.refresh(true);
		}
	}


	@Override
	public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
		if (fViewer != null) {
			fViewer.refresh(true);
		}
	}


	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (fViewer != null) {
			fViewer.refresh(true);
		}
	}

}
