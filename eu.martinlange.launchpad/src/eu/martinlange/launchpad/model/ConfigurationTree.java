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

public class ConfigurationTree extends ArrayList<ConfigurationTreeNode> implements ILaunchConfigurationListener {

	public static final ConfigurationTree INSTANCE = new ConfigurationTree();

	private static final long serialVersionUID = 3792280186446347626L;

	private static final String UNCATEGORIZED = "Uncategorized";

	private ConfigurationTreeNode fRootElement;


	private ConfigurationTree() {
		fRootElement = new ConfigurationTreeNode("Launch Pad");
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

		ConfigurationTreeNode uncategorized = new ConfigurationTreeNode(UNCATEGORIZED);
		uncategorized.setEditable(false);
		for (ILaunchConfiguration configuration : configurations) {
			uncategorized.add(new ConfigurationTreeNode(configuration));
		}

		fRootElement.add(uncategorized);
	}


	private void restoreState(ConfigurationTreeNode parent, IMemento memento, List<ILaunchConfiguration> configurations) {
		for (IMemento folder : memento.getChildren("folder")) {
			ConfigurationTreeNode element = new ConfigurationTreeNode(folder.getString("name"));
			parent.add(element);
			restoreState(element, folder, configurations);
		}

		for (IMemento launch : memento.getChildren("launch")) {
			ILaunchConfiguration configuration = getByName(configurations, launch.getString("name"));
			if (configuration == null)
				continue;

			ConfigurationTreeNode element = new ConfigurationTreeNode(configuration);
			parent.add(element);
		}
	}


	public void saveState(IMemento memento) {
		saveState(getRoot(), memento);
	}


	private void saveState(ConfigurationTreeNode parent, IMemento memento) {
		for (ConfigurationTreeNode element : parent.getChildren()) {
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


	public ConfigurationTreeNode getRoot() {
		return fRootElement;
	}


	public ConfigurationTreeNode getById(String id) {
		Queue<ConfigurationTreeNode> remain = new LinkedList<ConfigurationTreeNode>();
		remain.addAll(this);

		while (!remain.isEmpty()) {
			ConfigurationTreeNode e = remain.poll();
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
		for (ConfigurationTreeNode element : this) {
			if (element.getData() instanceof String && (String) element.getData() == UNCATEGORIZED) {
				element.add(new ConfigurationTreeNode(configuration));
			}
		}
	}


	@Override
	public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
		Queue<ConfigurationTreeNode> remain = new LinkedList<ConfigurationTreeNode>();
		remain.addAll(this);

		ConfigurationTreeNode old = new ConfigurationTreeNode(configuration);

		while (!remain.isEmpty()) {
			ConfigurationTreeNode e = remain.poll();
			e.remove(old);
			Collections.addAll(remain, e.getChildren());
		}
	}


	@Override
	public void launchConfigurationChanged(ILaunchConfiguration configuration) {
		// don't do anything.
	}

}
