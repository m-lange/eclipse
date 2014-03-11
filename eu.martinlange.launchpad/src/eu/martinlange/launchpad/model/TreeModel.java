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

public class TreeModel implements ILaunchConfigurationListener {

	public static final TreeModel INSTANCE = new TreeModel();

	private static final String UNCATEGORIZED = "Uncategorized";

	private TreeNode fRootElement;


	private TreeModel() {
		fRootElement = new TreeNode("Launch Pad");
	}


	public void restoreState(IMemento memento) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		manager.addLaunchConfigurationListener(this);

		List<ILaunchConfiguration> configurations = new ArrayList<ILaunchConfiguration>();
		try {
			Collections.addAll(configurations, manager.getLaunchConfigurations());
		} catch (CoreException e) {
		}

		if (memento != null)
			restoreState(getRoot(), memento, configurations);

		TreeNode uncategorized = new TreeNode(UNCATEGORIZED);
		uncategorized.setEditable(false);
		for (ILaunchConfiguration configuration : configurations) {
			uncategorized.add(new TreeNode(configuration));
		}

		fRootElement.add(uncategorized);
	}


	private void restoreState(TreeNode parent, IMemento memento, List<ILaunchConfiguration> configurations) {
		for (IMemento folder : memento.getChildren("folder")) {
			TreeNode element = new TreeNode(folder.getString("name"));
			parent.add(element);
			restoreState(element, folder, configurations);
		}

		for (IMemento launch : memento.getChildren("launch")) {
			ILaunchConfiguration configuration = getByName(configurations, launch.getString("name"));
			if (configuration == null)
				continue;

			TreeNode element = new TreeNode(configuration);
			parent.add(element);
		}
	}


	public void saveState(IMemento memento) {
		saveState(getRoot(), memento);
	}


	private void saveState(TreeNode parent, IMemento memento) {
		for (TreeNode element : parent.getChildren()) {
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


	public TreeNode getRoot() {
		return fRootElement;
	}


	public TreeNode getById(String id) {
		Queue<TreeNode> remain = new LinkedList<TreeNode>();
		remain.add(getRoot());

		while (!remain.isEmpty()) {
			TreeNode e = remain.poll();
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
		for (TreeNode element : getRoot().getChildren()) {
			if (element.getData() instanceof String && (String) element.getData() == UNCATEGORIZED) {
				element.add(new TreeNode(configuration));
			}
		}
	}


	@Override
	public void launchConfigurationRemoved(ILaunchConfiguration configuration) {
		Queue<TreeNode> remain = new LinkedList<TreeNode>();
		remain.add(getRoot());

		TreeNode old = new TreeNode(configuration);

		while (!remain.isEmpty()) {
			TreeNode e = remain.poll();
			e.remove(old);
			Collections.addAll(remain, e.getChildren());
		}
	}


	@Override
	public void launchConfigurationChanged(ILaunchConfiguration configuration) {
		// don't do anything.
	}

}
