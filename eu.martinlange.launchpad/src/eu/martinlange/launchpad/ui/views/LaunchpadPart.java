package eu.martinlange.launchpad.ui.views;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import org.eclipse.core.commands.Command;
import org.eclipse.core.commands.IStateListener;
import org.eclipse.core.commands.State;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.ISafeRunnable;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.core.runtime.Platform;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.Status;
import org.eclipse.debug.core.DebugPlugin;
import org.eclipse.debug.core.ILaunchConfiguration;
import org.eclipse.debug.core.ILaunchManager;
import org.eclipse.debug.ui.DebugUITools;
import org.eclipse.jface.dialogs.IDialogSettings;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.FocusAdapter;
import org.eclipse.swt.events.FocusEvent;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.MouseAdapter;
import org.eclipse.swt.events.MouseEvent;
import org.eclipse.swt.events.MouseMoveListener;
import org.eclipse.swt.events.MouseTrackAdapter;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.commands.ICommandService;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.handlers.RadioState;
import org.eclipse.ui.handlers.RegistryToggleState;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.progress.WorkbenchJob;
import eu.martinlange.launchpad.Plugin;
import eu.martinlange.launchpad.internal.ISharedImages;
import eu.martinlange.launchpad.model.TreeModel;

public class LaunchpadPart extends CommonNavigator {

	private static final long SOFT_MAX_EXPAND_TIME = 200;

	private static final String TAG_MEMENTO = "memento";

	private IDialogSettings fDialogSettings;
	protected IMemento fMemento;

	protected boolean fCategorized;
	protected String fLaunchMode;

	private Label fClearButton;
	private StyledText fFilterText;
	private String fInitialFilterText = "type filter text";
	private String fPreviousFilterText = "";

	private WorkbenchJob fRefreshJob;
	private PatternFilter fPatternFilter;
	private boolean fNarrowingDown;


	public LaunchpadPart() {
		fDialogSettings = Plugin.getDefault().getDialogSettings().getSection(getClass().getName());
		if (fDialogSettings == null)
			fDialogSettings = Plugin.getDefault().getDialogSettings().addNewSection(getClass().getName());

		fPatternFilter = new PatternFilter();
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

		ISafeRunnable code = new ISafeRunnable() {
			@Override
			public void run() throws Exception {
				ICommandService commandService = (ICommandService) getSite().getService(ICommandService.class);

				Command command = commandService.getCommand("eu.martinlange.launchpad.command.launchMode");
				State state = command.getState(RadioState.STATE_ID);
				state.addListener(new IStateListener() {
					@Override
					public void handleStateChange(State state, Object oldValue) {
						launchModeChanged((String) state.getValue());
					}
				});
				fLaunchMode = (String) state.getValue();

				command = commandService.getCommand("eu.martinlange.launchpad.command.rootMode");
				state = command.getState(RegistryToggleState.STATE_ID);
				state.addListener(new IStateListener() {
					@Override
					public void handleStateChange(State state, Object oldValue) {
						rootModeChanged(((Boolean) state.getValue()).booleanValue());
					}
				});
				fCategorized = ((Boolean) state.getValue()).booleanValue();
			}


			@Override
			public void handleException(Throwable exception) {
				Plugin.log(exception);
			}
		};
		SafeRunner.run(code);
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
		super.saveState(memento);

		if (this.fMemento == null || memento == null)
			return;

		if (getCommonViewer() == null && fMemento != null) {
			// part has not been created -> keep the old state
			memento.putMemento(fMemento);
			return;
		}

		TreeModel.INSTANCE.saveState(memento);
	}


	@Override
	public void createPartControl(Composite parent) {
		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		layout.horizontalSpacing = 0;
		layout.verticalSpacing = 3;
		parent.setLayout(layout);
		parent.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		Composite filterComposite = new Composite(parent, SWT.BORDER);
		filterComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		layout = new GridLayout(2, false);
		layout.marginHeight = 0;
		layout.marginWidth = 0;
		filterComposite.setLayout(layout);
		filterComposite.setFont(parent.getFont());

		createFilterText(filterComposite);
		filterComposite.setLayoutData(new GridData(SWT.FILL, SWT.BEGINNING, true, false));

		Composite treeComposite = new Composite(parent, SWT.NONE);
		treeComposite.setLayout(new FillLayout());
		treeComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));

		super.createPartControl(treeComposite);

		createRefreshJob();
		getCommonViewer().addFilter(fPatternFilter);
	}


	protected void createFilterText(Composite parent) {
		fFilterText = new StyledText(parent, SWT.SINGLE);
		fFilterText.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		fFilterText.setMargins(1, 2, 1, 2);
		setFilterText(fInitialFilterText);
		textChanged();

		fFilterText.addFocusListener(new FocusAdapter() {
			public void focusLost(FocusEvent e) {
				if (fFilterText.getText().equals(fInitialFilterText)) {
					setFilterText("");
					textChanged();
				}
			};
		});

		fFilterText.addMouseListener(new MouseAdapter() {
			public void mouseDown(MouseEvent e) {
				if (fFilterText.getText().equals(fInitialFilterText)) {
					setFilterText("");
					textChanged();
				}
			};
		});

		fFilterText.addKeyListener(new KeyAdapter() {
			@Override
			public void keyPressed(KeyEvent e) {
				boolean hasItems = getCommonViewer().getTree().getItemCount() > 0;
				if (hasItems && e.keyCode == SWT.ARROW_DOWN) {
					getCommonViewer().getTree().setFocus();
					return;
				}
			}
		});

		fFilterText.addModifyListener(new ModifyListener() {
			@Override
			public void modifyText(ModifyEvent e) {
				textChanged();
			}
		});

		if ((fFilterText.getStyle() & SWT.ICON_CANCEL) == 0) {
			final Image inactive = Plugin.getSharedImages().getImage(ISharedImages.DISABLED_CLEAR_ICON);
			final Image active = Plugin.getSharedImages().getImage(ISharedImages.CLEAR_ICON);
			final Image pressed = new Image(parent.getDisplay(), active, SWT.IMAGE_GRAY);

			fClearButton = new Label(parent, SWT.NONE);
			fClearButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			fClearButton.setImage(inactive);
			fClearButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

			fClearButton.addMouseListener(new MouseAdapter() {

				private MouseMoveListener fListener;


				@Override
				public void mouseDown(MouseEvent e) {
					fClearButton.setImage(pressed);
					fListener = new MouseMoveListener() {

						private boolean fMouseInButton = true;


						@Override
						public void mouseMove(MouseEvent e) {
							boolean hot = isMouseInButton(e);
							if (hot != fMouseInButton) {
								fMouseInButton = hot;
								fClearButton.setImage(hot ? pressed : inactive);
							}
						}

					};

					fClearButton.addMouseMoveListener(fListener);
				}


				@Override
				public void mouseUp(MouseEvent e) {
					if (fListener != null) {
						fClearButton.removeMouseMoveListener(fListener);
						fListener = null;
						boolean hot = isMouseInButton(e);
						fClearButton.setImage(hot ? active : inactive);
						if (hot) {
							setFilterText("");
							textChanged();
							fFilterText.setFocus();
						}
					}
				}


				private boolean isMouseInButton(MouseEvent e) {
					Point buttonSize = fClearButton.getSize();
					return 0 <= e.x && e.x < buttonSize.x && 0 <= e.y && e.y < buttonSize.y;
				}
			});

			fClearButton.addMouseTrackListener(new MouseTrackAdapter() {
				@Override
				public void mouseExit(MouseEvent e) {
					fClearButton.setImage(inactive);
				}


				@Override
				public void mouseEnter(MouseEvent e) {
					fClearButton.setImage(active);
				}
			});

			fClearButton.addDisposeListener(new DisposeListener() {
				@Override
				public void widgetDisposed(DisposeEvent e) {
					inactive.dispose();
					active.dispose();
					pressed.dispose();
				}
			});
		}
	}


	private void createRefreshJob() {
		fRefreshJob = new WorkbenchJob("Refresh Filter") {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (getCommonViewer().getControl().isDisposed()) {
				return Status.CANCEL_STATUS;
				} 

				String text = getFilterString();
				if (text == null) return Status.OK_STATUS;

				if (fInitialFilterText.equals(text))
					fPatternFilter.setPattern(null);
				else if (text != null)
					fPatternFilter.setPattern(text);

				try {
					getCommonViewer().getControl().setRedraw(false);

					if (!fNarrowingDown) {
						for (TreeItem item : getCommonViewer().getTree().getItems()) {
							if (item.getExpanded()) {
								getCommonViewer().setExpandedState(item.getData(), false);
							}
						}
					}
					getCommonViewer().refresh(true);

					if (text.length() > 0 && !text.equals(fInitialFilterText)) {
						TreeItem[] items = getCommonViewer().getTree().getItems();
						int treeHeight = getCommonViewer().getTree().getBounds().height;
						int numVisibleItems = treeHeight / getCommonViewer().getTree().getItemHeight();
						long stopTime = SOFT_MAX_EXPAND_TIME + System.currentTimeMillis();
						boolean cancel = false;

						if (items.length > 0 && recursiveExpand(items, monitor, stopTime, new int[] { numVisibleItems })) {
							cancel = true;
						}

						fClearButton.setVisible(true);

						if (cancel)
							return Status.CANCEL_STATUS;
					} else {
						fClearButton.setVisible(false);
					}
				}
				finally {
					TreeItem[] items = getCommonViewer().getTree().getItems();
					if (items.length > 0 && getCommonViewer().getTree().getSelectionCount() == 0)
						getCommonViewer().getTree().setTopItem(items[0]);

					getCommonViewer().getControl().setRedraw(true);
				}
				return Status.OK_STATUS;
			}


			private boolean recursiveExpand(TreeItem[] items, IProgressMonitor monitor, long cancelTime, int[] numItemsLeft) {
				boolean canceled = false;
				for (int i = 0; !canceled && i < items.length; i++) {
					TreeItem item = items[i];
					boolean visible = numItemsLeft[0]-- >= 0;
					if (monitor.isCanceled() || (!visible && System.currentTimeMillis() > cancelTime)) {
						canceled = true;
					} else {
						Object itemData = item.getData();
						if (itemData != null) {
							if (!item.getExpanded()) {
								getCommonViewer().setExpandedState(itemData, true);
							}
							TreeItem[] children = item.getItems();
							if (items.length > 0) {
								canceled = recursiveExpand(children, monitor, cancelTime, numItemsLeft);
							}
						}
					}
				}
				return canceled;
			}
		};

		fRefreshJob.setSystem(true);
	}


	protected void setFilterText(String string) {
		if (fFilterText != null) {
			fFilterText.setText(string);
			if (fFilterText != null) {
				fFilterText.selectAll();
			}
		}
	}


	protected String getFilterString() {
		return fFilterText != null ? fFilterText.getText() : null;
	}


	@Override
	protected Object getInitialInput() {
		if (fCategorized)
			return TreeModel.INSTANCE.getRoot();
		else
			return DebugPlugin.getDefault().getLaunchManager();
	}
	
	
	@Override
	public void setFocus() {
		getCommonViewer().getTree().setFocus();
	}


	protected void textChanged() {
		if (fRefreshJob == null) return;
		fNarrowingDown = fPreviousFilterText == null || fPreviousFilterText.equals(fInitialFilterText) || getFilterString().startsWith(fPreviousFilterText);
		fPreviousFilterText = getFilterString();
		fRefreshJob.cancel();
		fRefreshJob.schedule(200);
	}


	@Override
	protected void handleDoubleClick(DoubleClickEvent anEvent) {

		ISelection selection = anEvent.getSelection();

		ILaunchConfiguration configuration = null;
		configuration = (ILaunchConfiguration) Platform.getAdapterManager().getAdapter(selection, ILaunchConfiguration.class);

		if (configuration != null) {
			try {
				String mode = fLaunchMode;
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


	public void rootModeChanged(boolean newMode) {
		fCategorized = newMode;
		getCommonViewer().setInput(getInitialInput());
	}


	public void launchModeChanged(String newMode) {
		fLaunchMode = newMode;
	}

}
