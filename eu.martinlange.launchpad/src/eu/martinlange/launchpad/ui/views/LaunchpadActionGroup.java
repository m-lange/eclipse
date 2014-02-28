package eu.martinlange.launchpad.ui.views;

import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.GroupMarker;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.eclipse.ui.actions.ActionGroup;
import eu.martinlange.launchpad.ui.views.actions.ChangeLaunchModeAction;
import eu.martinlange.launchpad.ui.views.actions.ChangeRootModeAction;
import eu.martinlange.launchpad.ui.views.actions.CollapseAllAction;
import eu.martinlange.launchpad.ui.views.actions.EditAction;
import eu.martinlange.launchpad.ui.views.actions.LaunchAction;
import eu.martinlange.launchpad.ui.views.actions.MoveAction;
import eu.martinlange.launchpad.ui.views.actions.NewAction;
import eu.martinlange.launchpad.ui.views.actions.RenameAction;

public class LaunchpadActionGroup extends ActionGroup {

	private LaunchpadView fPart;

	private ChangeRootModeAction actChangeRootMode;
	private ChangeLaunchModeAction actChangeLaunchModeRun;
	private ChangeLaunchModeAction actChangeLaunchModeDebug;
	private CollapseAllAction actCollapseAll;

	private LaunchAction actRun;
	private LaunchAction actDebug;
	private LaunchAction actProfile;

	private NewAction actNew;
	private MoveAction actMove;
	private RenameAction actRename;
	private EditAction actEdit;


	public LaunchpadActionGroup(LaunchpadView part) {
		fPart = part;

		actChangeRootMode = new ChangeRootModeAction(fPart);
		actChangeLaunchModeRun = new ChangeLaunchModeAction(fPart, ILaunchManager.RUN_MODE);
		actChangeLaunchModeDebug = new ChangeLaunchModeAction(fPart, ILaunchManager.DEBUG_MODE);
		actCollapseAll = new CollapseAllAction(fPart.fViewer);

		actRun = new LaunchAction(fPart, ILaunchManager.RUN_MODE);
		actDebug = new LaunchAction(fPart, ILaunchManager.DEBUG_MODE);
		actProfile = new LaunchAction(fPart, ILaunchManager.PROFILE_MODE);

		actNew = new NewAction(fPart);
		actMove = new MoveAction(fPart);
		actRename = new RenameAction(fPart);
		actEdit = new EditAction(fPart);
	}


	@Override
	public void fillActionBars(IActionBars actionBars) {
		super.fillActionBars(actionBars);

		IToolBarManager toolBar = actionBars.getToolBarManager();
		toolBar.add(actCollapseAll);
		toolBar.add(new Separator());
		toolBar.add(actChangeRootMode);
		toolBar.add(new Separator());
		toolBar.add(actChangeLaunchModeRun);
		toolBar.add(actChangeLaunchModeDebug);
		toolBar.add(new Separator());
		toolBar.add(new GroupMarker(IWorkbenchActionConstants.MB_ADDITIONS));

		toolBar.update(true);
	}


	@Override
	public void fillContextMenu(IMenuManager menu) {
		switch (fPart.getRootMode()) {
		case LaunchpadView.GROUPS_AS_ROOTS:
			menu.add(actRun);
			menu.add(actDebug);
			menu.add(actProfile);
			menu.add(new Separator());
			if (actEdit.isEnabled()) menu.add(actEdit);
			break;
		case LaunchpadView.FOLDERS_AS_ROOTS:
			menu.add(actNew);
			menu.add(new Separator());
			menu.add(actRun);
			menu.add(actDebug);
			menu.add(actProfile);
			menu.add(new Separator());
			menu.add(actMove);
			if (actRename.isEnabled()) menu.add(actRename);
			if (actEdit.isEnabled()) menu.add(actEdit);
			break;
		}

		menu.update(true);
	}

}
