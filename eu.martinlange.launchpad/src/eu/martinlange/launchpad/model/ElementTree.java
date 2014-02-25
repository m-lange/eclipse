package eu.martinlange.launchpad.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.ui.IMemento;

public class ElementTree extends ArrayList<ElementTreeData> {

	private static final long serialVersionUID = 3792280186446347626L;

	public ElementTree(IMemento memento) {
		ILaunchManager manager = DebugPlugin.getDefault().getLaunchManager();
		
		List<ILaunchConfiguration> configurations = new ArrayList<ILaunchConfiguration>();
		try {
			Collections.addAll(configurations, manager.getLaunchConfigurations());
		} catch (CoreException e) {
		}
		
		// restore folder structure
		// remove assigned configurations from list
		
		ElementTreeData uncategorized = new ElementTreeData();
		uncategorized.setData("Uncategorized");
		
		for(ILaunchConfiguration configuration : configurations) {
			ElementTreeData e = new ElementTreeData();
			e.setData(configuration);
			uncategorized.add(e);
		}
		
		add(uncategorized);
	}


	public void saveState(IMemento memento) {

	}

}
