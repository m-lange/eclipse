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

		restoreState(getRoot(), memento, configurations);

		ElementTreeData uncategorized = new ElementTreeData(UNCATEGORIZED);
		uncategorized.setEditable(false);
		for (ILaunchConfiguration configuration : configurations) {
			uncategorized.add(new ElementTreeData(configuration));
		}

		fRootElement.add(uncategorized);
	}


	private void restoreState(ElementTreeData parent, IMemento memento, List<ILaunchConfiguration> configurations) {
		for (IMemento folder : memento.getChildren("folder")) {
			ElementTreeData element = new ElementTreeData(folder.getString("name"));
			parent.add(element);
			restoreState(element, folder, configurations);
		}

		for (IMemento launch : memento.getChildren("launch")) {
			ILaunchConfiguration configuration = getByName(configurations, launch.getString("name"));
			if (configuration == null)
				continue;

			ElementTreeData element = new ElementTreeData(configuration);
			parent.add(element);
		}
	}


	public void saveState(IMemento memento) {
		saveState(getRoot(), memento);
	}


	private void saveState(ElementTreeData parent, IMemento memento) {
		for (ElementTreeData element : parent.getChildren()) {
			if (!element.isEditable())
				continue;

			if (element.getData() instanceof String) {
				IMemento folder = memento.createChild("folder");
				folder.putString("name", (String) element.getData());
				saveState(element, folder);
			}

			else if (element.getData() instanceof ILaunchConfiguration) {
				IMemento launch = memento.createChild("launch");
				launch.putString("name", ((ILaunchConfiguration) element.getData()).getName());
			}
		}
	}


	public ElementTreeData getRoot() {
		return fRootElement;
	}


	public ElementTreeData getById(String id) {
		Queue<ElementTreeData> remain = new LinkedList<ElementTreeData>();
		remain.addAll(this);

		while (!remain.isEmpty()) {
			ElementTreeData e = remain.poll();
			if (e.getId().equals(id))
				return e;

			Collections.addAll(remain, e.getChildren());
		}

		return null;
	}


	private ILaunchConfiguration getByName(List<ILaunchConfiguration> configurations, String name) {
		for (ILaunchConfiguration e : configurations) {
			if (e.getName().equals(name)) {
				configurations.remove(e);
				return e;
			}
		}
		return null;
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
