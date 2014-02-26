package eu.martinlange.launchpad.views;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionGroup;

public class LaunchpadActionGroup extends ActionGroup {

	private LaunchpadView fPart;

	private ChangeRootModeAction fChangeRootMode;
	private ChangeLaunchModeAction fChangeLaunchModeRun;
	private ChangeLaunchModeAction fChangeLaunchModeDebug;
	private CollapseAllAction fCollapseAll;


	public LaunchpadActionGroup(LaunchpadView part) {
		fPart = part;

		fChangeRootMode = new ChangeRootModeAction(fPart);
		fChangeLaunchModeRun = new ChangeLaunchModeAction(fPart, ILaunchManager.RUN_MODE);
		fChangeLaunchModeDebug = new ChangeLaunchModeAction(fPart, ILaunchManager.DEBUG_MODE);
		fCollapseAll = new CollapseAllAction(fPart.fViewer);
	}


	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);

		IToolBarManager toolBar = actionBars.getToolBarManager();
		toolBar.add(fCollapseAll);
		toolBar.add(new Separator());
		toolBar.add(fChangeRootMode);
		toolBar.add(new Separator());
		toolBar.add(fChangeLaunchModeRun);
		toolBar.add(fChangeLaunchModeDebug);
		toolBar.add(new Separator());
		toolBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

		toolBar.update(true);
	}
	
	
	@Override
	public void fillContextMenu(IMenuManager menu) {
		super.fillContextMenu(menu);
	}

}
