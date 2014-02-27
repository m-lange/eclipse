package eu.martinlange.launchpad.ui.views;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.IBaseLabelProvider;
import org.eclipse.jface.viewers.IContentProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ViewPart;
import eu.martinlange.launchpad.Plugin;
import eu.martinlange.launchpad.model.DefaultContentProvider;
import eu.martinlange.launchpad.model.DefaultLabelProvider;
import eu.martinlange.launchpad.model.ElementTree;

public class LaunchpadView extends ViewPart {

	private static final String TAG_ROOT_MODE = "rootMode";
	private static final String TAG_LAUNCH_MODE = "launchMode";
	private static final String TAG_MEMENTO = "memento";

	public static final int GROUPS_AS_ROOTS = 1;
	public static final int FOLDERS_AS_ROOTS = 2;

	private IDialogSettings fDialogSettings;
	protected IMemento fMemento;

	private int fRootMode;
	private String fLaunchMode;

	protected LaunchpadActionGroup fActionSet;
	protected FilteredTree fTree;
	protected TreeViewer fViewer;
	protected IContentProvider fContentProvider;
	protected IBaseLabelProvider fLabelProvider;


	public LaunchpadView() {
		fDialogSettings = Plugin.getDefault().getDialogSettings().addNewSection(getClass().getName());

		fLaunchMode = fDialogSettings.get(TAG_LAUNCH_MODE);
		try {
			fRootMode = fDialogSettings.getInt(TAG_ROOT_MODE);
		} catch (NumberFormatException e) {
			fRootMode = GROUPS_AS_ROOTS;
		}
	}


	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);

		if (memento == null) {
			String persistedMemento = fDialogSettings.get(TAG_MEMENTO);
			if (persistedMemento != null) {
				try {
					memento = XMLMemento.createReadRoot(new StringReader(persistedMemento));
				} catch (WorkbenchException e) {
					// don't do anything. Simply don't restore the settings
				}
			}
		}

		fMemento = memento;

		if (memento != null) {
			restoreRootMode(memento);
			restoreLaunchMode(memento);
		}

		createElementTree();
	}


	@Override
	public void dispose() {
		XMLMemento memento = XMLMemento.createWriteRoot("LAUNCHPAD");
		saveState(memento);

		StringWriter writer = new StringWriter();
		try {
			memento.save(writer);
			fDialogSettings.put(TAG_MEMENTO, writer.getBuffer().toString());
		} catch (IOException e) {
			// don't do anything. Simple don't store the settings
		}

		super.dispose();
	}


	@Override
	public void saveState(IMemento memento) {
		if (this.fMemento == null || memento == null)
			return;

		if (fTree == null && fMemento != null) {
			// part has not been created -> keep the old state
			memento.putMemento(fMemento);
			return;
		}

		memento.putInteger(TAG_ROOT_MODE, fRootMode);
		memento.putString(TAG_LAUNCH_MODE, fLaunchMode);

		ElementTree.INSTANCE.saveState(memento);
	}


	private void saveDialogSettings() {
		fDialogSettings.put(TAG_ROOT_MODE, fRootMode);
		fDialogSettings.put(TAG_LAUNCH_MODE, fLaunchMode);
	}


	private void restoreRootMode(IMemento memento) {
		Integer value = memento.getInteger(TAG_ROOT_MODE);
		fRootMode = value == null ? GROUPS_AS_ROOTS : value.intValue();
		if (fRootMode != FOLDERS_AS_ROOTS && fRootMode != GROUPS_AS_ROOTS)
			fRootMode = GROUPS_AS_ROOTS;
	}


	public int getRootMode() {
		return fRootMode;
	}


	public void rootModeChanged(int newMode) {
		fRootMode = newMode;
		saveDialogSettings();
		setProviders();
	}


	private void restoreLaunchMode(IMemento memento) {
		String value = memento.getString(TAG_LAUNCH_MODE);
		fLaunchMode = value == null ? ILaunchManager.RUN_MODE : value;
		if (!fLaunchMode.equals(ILaunchManager.RUN_MODE) || !fLaunchMode.equals(ILaunchManager.DEBUG_MODE))
			fLaunchMode = ILaunchManager.RUN_MODE;
	}


	public String getLaunchMode() {
		return fLaunchMode;
	}


	public void launchModeChanged(String newMode) {
		fLaunchMode = newMode;
		saveDialogSettings();
	}


	public ISelection getSelection() {
		return fViewer.getSelection();
	}


	private void createElementTree() {
		SafeRunner.run(new ISafeRunnable() {

			@Override
			public void run() throws Exception {
				ElementTree.INSTANCE.restoreState(fMemento);
			}


			@Override
			public void handleException(Throwable exception) {
				ElementTree.INSTANCE.restoreState(null);
			}
		});
	}


	@Override
	public void createPartControl(Composite parent) {
		fTree = new FilteredTree(parent, SWT.FULL_SELECTION, new PatternFilter(), true);
		fViewer = fTree.getViewer();

		setProviders();
		getSite().setSelectionProvider(fViewer);

		IActionBars actionBars = getViewSite().getActionBars();
		fActionSet = new LaunchpadActionGroup(this);
		fActionSet.fillActionBars(actionBars);

		MenuManager manager = new MenuManager();
		manager.setRemoveAllWhenShown(true);
		manager.addMenuListener(new IMenuListener() {
			@Override
			public void menuAboutToShow(IMenuManager manager) {
				fActionSet.fillContextMenu(manager);
			}
		});
		getSite().registerContextMenu(manager, getSite().getSelectionProvider());
		Menu menu = manager.createContextMenu(fViewer.getTree());
		fViewer.getTree().setMenu(menu);
	}


	private void setProviders() {
		if (fViewer == null)
			return;

		if (fContentProvider == null)
			fContentProvider = new DefaultContentProvider();
		if (fLabelProvider == null)
			fLabelProvider = new DefaultLabelProvider();

		switch (getRootMode()) {
		case GROUPS_AS_ROOTS:
			fViewer.setContentProvider(fContentProvider);
			fViewer.setLabelProvider(fLabelProvider);
			fViewer.setInput(DebugPlugin.getDefault().getLaunchManager());
			break;
		case FOLDERS_AS_ROOTS:
			fViewer.setContentProvider(fContentProvider);
			fViewer.setLabelProvider(fLabelProvider);
			fViewer.setInput(ElementTree.INSTANCE.getRoot());
			break;
		}
	}


	@Override
	public void setFocus() {
		fTree.setFocus();
	}

}
