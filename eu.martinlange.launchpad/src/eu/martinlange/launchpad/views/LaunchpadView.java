package eu.martinlange.launchpad.views;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.handlers.CollapseAllHandler;
import org.eclipse.ui.handlers.IHandlerService;
import org.eclipse.ui.part.ViewPart;
import eu.martinlange.launchpad.Plugin;
import eu.martinlange.launchpad.model.DefaultContentProvider;
import eu.martinlange.launchpad.model.DefaultLabelProvider;
import eu.martinlange.launchpad.model.ElementTree;
import eu.martinlange.launchpad.model.FolderAwareContentProvider;

public class LaunchpadView extends ViewPart {

	private static final String TAG_ROOT_MODE = "rootMode";
	private static final String TAG_MEMENTO = "memento";

	public static final int GROUPS_AS_ROOTS = 1;
	public static final int FOLDERS_AS_ROOTS = 2;

	private IDialogSettings fDialogSettings;
	protected IMemento fMemento;

	private ElementTree fElementTree;
	private int fRootMode;

	protected FilteredTree fTree;
	private TreeViewer fViewer;


	public LaunchpadView() {
		fDialogSettings = Plugin.getDefault().getDialogSettings().addNewSection(getClass().getName());

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

		if (fElementTree != null)
			fElementTree.saveState(memento);
	}


	private void saveDialogSettings() {
		fDialogSettings.put(TAG_ROOT_MODE, fRootMode);
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


	private void createElementTree() {
		SafeRunner.run(new ISafeRunnable() {

			@Override
			public void run() throws Exception {
				fElementTree = new ElementTree(fMemento);
			}


			@Override
			public void handleException(Throwable exception) {
				fElementTree = new ElementTree(null);
			}
		});
	}


	@Override
	public void createPartControl(Composite parent) {
		fTree = new FilteredTree(parent, SWT.FULL_SELECTION, new PatternFilter(), true);
		fViewer = fTree.getViewer();

		fViewer.collapseAll();
		
		setProviders();
		
		IHandlerService hs = (IHandlerService) getSite().getService(IHandlerService.class);
		hs.activateHandler(CollapseAllHandler.COMMAND_ID, new CollapseAllHandler(fViewer));
	}

	private void setProviders() {
		if (fViewer == null)
			return;

		if (getRootMode() == FOLDERS_AS_ROOTS) {
			fViewer.setContentProvider(new FolderAwareContentProvider(fElementTree));
			fViewer.setLabelProvider(new DefaultLabelProvider());
		} else {
			fViewer.setContentProvider(new DefaultContentProvider());
			fViewer.setLabelProvider(new DefaultLabelProvider());
		}

		fViewer.setInput(DebugPlugin.getDefault().getLaunchManager());
	}


	@Override
	public void setFocus() {
		fTree.setFocus();
	}

}
