package eu.martinlange.launchpad.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchConfigurationListener;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.IMemento;

public class ElementTree extends ArrayList<ElementTreeData> implements ILaunchConfigurationListener {

	public static final ElementTree INSTANCE = new ElementTree();
	
	
	private static final long serialVersionUID = 3792280186446347626L;

	private static final String UNCATEGORIZED = "Uncategorized";

	private ElementTreeData fRootElement;
	

	private ElementTree() {
		fRootElement = new ElementTreeData("Launch Pad");
		add(fRootElement);
	}

	
	public void restoreState(IMemento memento) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		manager.addLaunchConfigurationListener(this);

		List<ILaunchConfiguration> configurations = new ArrayList<ILaunchConfiguration>();
		try {
			Collections.addAll(configurations, manager.getLaunchConfigurations());
		} catch (CoreException e) {
		}
		
		// restore folder structure
		// remove assigned configurations from list

		ElementTreeData uncategorized = new ElementTreeData(UNCATEGORIZED);
		for (ILaunchConfiguration configuration : configurations) {
			uncategorized.add(new ElementTreeData(configuration));
		}

		fRootElement.add(uncategorized);
	}
	

	public void saveState(IMemento memento) {

	}
	
	
	public ElementTreeData getRoot() {
		return fRootElement;
	}


	@Override
	public void launchConfigurationAdded(ILaunchConfiguration configuration) {
		for (ElementTreeData element : this) {
			if (element.getData() instanceof String && (String) element.getData() == UNCATEGORIZED) {
				element.add(new ElementTreeData(configuration));
			}
		}
	}


	@Override
	public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
		Queue<ElementTreeData> remain = new LinkedList<ElementTreeData>();
		remain.addAll(this);

		ElementTreeData old = new ElementTreeData(configuration);

		while (!remain.isEmpty()) {
			ElementTreeData e = remain.poll();
			e.remove(old);
			Collections.addAll(remain, e.getChildren());
		}
	}


	@Override
	public void launchConfigurationChanged(ILaunchConfiguration configuration) {
		// don't do anything.
	}

}
