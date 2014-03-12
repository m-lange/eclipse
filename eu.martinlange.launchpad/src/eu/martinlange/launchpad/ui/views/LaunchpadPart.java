package eu.martinlange.launchpad.ui.views;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.Map;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.ISourceProviderListener;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.services.ISourceProviderService;
import eu.martinlange.launchpad.Plugin;
import eu.martinlange.launchpad.internal.LaunchpadState;
import eu.martinlange.launchpad.model.TreeModel;

public class LaunchpadPart extends FilteredCommonNavigator implements ISourceProviderListener {

	public static final String VIEW_ID = "eu.martinlange.launchpad.ui.LaunchPad";
	
	private static final String TAG_MEMENTO = "memento";

	private IDialogSettings fDialogSettings;
	protected IMemento fMemento;

	protected LaunchpadState fState;


	public LaunchpadPart() {
		fDialogSettings = Plugin.getDefault().getDialogSettings().getSection(getClass().getName());
		if (fDialogSettings == null)
			fDialogSettings = Plugin.getDefault().getDialogSettings().addNewSection(getClass().getName());
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

		initConfigurationTree(memento);

		ISourceProviderService sourceProviderService = (ISourceProviderService) getSite().getService(ISourceProviderService.class);
		fState = (LaunchpadState) sourceProviderService.getSourceProvider(LaunchpadState.IS_CATEGORIZED);
		fState.addSourceProviderListener(this);
		
		fState.setCategorized(fState.isCategorized());
	}


	private void initConfigurationTree(final IMemento memento) {
		ISafeRunnable code = new ISafeRunnable() {
			@Override
			public void run() throws Exception {
				TreeModel.INSTANCE.restoreState(memento);
			}


			@Override
			public void handleException(Throwable exception) {
				TreeModel.INSTANCE.restoreState(null);
			}
		};
		SafeRunner.run(code);
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

		if (getCommonViewer() == null && fMemento != null) {
			// part has not been created -> keep the old state
			memento.putMemento(fMemento);
			return;
		}

		super.saveState(memento);

		TreeModel.INSTANCE.saveState(memento);
	}


	@Override
	protected Object getInitialInput() {
		if (fState.isCategorized())
			return TreeModel.INSTANCE.getRoot();
		else
			return DebugPlugin.getDefault().getLaunchManager();
	}
	
	
	@Override
	protected PatternFilter getPatternFilter() {
		return new PatternFilter();
	}


	@Override
	public void setFocus() {
		getCommonViewer().getTree().setFocus();
	}
	
	
	public void refresh() {
		if (getCommonViewer() == null || getCommonViewer().getTree().isDisposed())
			return;

		getCommonViewer().refresh(true);
	}


	@Override
	protected void handleDoubleClick(DoubleClickEvent anEvent) {

		ISelection selection = anEvent.getSelection();

		ILaunchConfiguration configuration = null;
		configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(selection, ILaunchConfiguration.class);

		if (configuration != null) {
			try {
				String mode = fState.getLaunchMode();
				if (configuration.supportsMode(mode)) {
					DebugUITools.buildAndLaunch(configuration, mode, new NullProgressMonitor());
					return;
				}

				mode = mode.equals(ILaunchManager.RUN_MODE) ? ILaunchManager.DEBUG_MODE : ILaunchManager.RUN_MODE;
				if (configuration.supportsMode(mode)) {
					DebugUITools.buildAndLaunch(configuration, mode, new NullProgressMonitor());
					return;
				}
			} catch (CoreException e)
			{
				Plugin.log(e);
			}
		}

		super.handleDoubleClick(anEvent);
	}


	@Override
	public void sourceChanged(int sourcePriority, @SuppressWarnings("rawtypes") Map sourceValuesByName) {

	}


	@Override
	public void sourceChanged(int sourcePriority, String sourceName, Object sourceValue) {
		if (getCommonViewer() == null || getCommonViewer().getTree().isDisposed())
			return;

		if (LaunchpadState.IS_CATEGORIZED.equals(sourceName))
			getCommonViewer().setInput(getInitialInput());
	}

}
