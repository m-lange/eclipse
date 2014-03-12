package eu.martinlange.launchpad.ui.views;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.action.ToolBarManager;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.accessibility.ACC;
import org.eclipse.swt.accessibility.AccessibleAdapter;
import org.eclipse.swt.accessibility.AccessibleControlAdapter;
import org.eclipse.swt.accessibility.AccessibleControlEvent;
import org.eclipse.swt.accessibility.AccessibleEvent;
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
import org.eclipse.swt.events.MouseTrackListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.TraverseEvent;
import org.eclipse.swt.events.TraverseListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.ISharedImages;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.navigator.CommonNavigator;
import org.eclipse.ui.progress.WorkbenchJob;

public abstract class FilteredCommonNavigator extends CommonNavigator {

	private static final long SOFT_MAX_EXPAND_TIME = 200;

	private static Boolean useNativeSearchField;

	protected Composite parent;
	protected Composite treeComposite;
	protected Composite filterComposite;
	protected Text filterText;

	protected ToolBarManager filterToolBar;
	protected Control clearButtonControl;

	protected TreeViewer treeViewer;

	private PatternFilter patternFilter;
	protected String initialText;
	private Job refreshJob;
	private Method isLeafMatch;

	private String previousFilterText;
	private boolean narrowingDown;

	private boolean showFilterControls = true;
	private boolean quickSelectionMode = false;
	private boolean useNewLook = true;


	public FilteredCommonNavigator() {
		super();
		this.patternFilter = getPatternFilter();
	}


	protected abstract PatternFilter getPatternFilter();


	@Override
	public void createPartControl(Composite aParent) {
		this.parent = aParent;

		GridLayout layout = new GridLayout();
		layout.marginHeight = 0;
		layout.marginWidth = 0;

		Composite composite = new Composite(parent, SWT.NONE);
		composite.setLayout(layout);
		composite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));

		createControl(composite);
		createRefreshJob();
		setInitialText("type filter text");
	}


	protected void createControl(Composite parent) {
		if (showFilterControls) {
			if (!useNewLook || useNativeSearchField(parent)) {
				filterComposite = new Composite(parent, SWT.NONE);
			} else {
				filterComposite = new Composite(parent, SWT.BORDER);
				filterComposite.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			}

			GridLayout layout = new GridLayout(2, false);
			layout.marginHeight = 0;
			layout.marginWidth = 0;
			filterComposite.setLayout(layout);
			filterComposite.setFont(parent.getFont());
			
			createFilterControls(filterComposite);

			GridData gd = new GridData(SWT.FILL, SWT.BEGINNING, true, false);
			filterComposite.setLayoutData(gd);
		}

		treeComposite = new Composite(parent, SWT.NONE);
		treeComposite.setLayout(new FillLayout());
		
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
		treeComposite.setLayoutData(gd);

		super.createPartControl(treeComposite);

		getCommonViewer().getControl().addDisposeListener(new DisposeListener() {
			@Override
			public void widgetDisposed(DisposeEvent e) {
				refreshJob.cancel();
			}
		});
		getCommonViewer().addFilter(patternFilter);
		treeViewer = getCommonViewer();
	}


	private static boolean useNativeSearchField(Composite composite) {
		if (useNativeSearchField == null) {
			useNativeSearchField = Boolean.FALSE;
			Text text = null;
			try {
				text = new Text(composite, SWT.SEARCH | SWT.ICON_CANCEL);
				useNativeSearchField = new Boolean((text.getStyle() & SWT.ICON_CANCEL) != 0);
			} finally {
				if (text != null) {
					text.dispose();
				}
			}
		}
		return useNativeSearchField.booleanValue();
	}


	protected Composite createFilterControls(Composite parent) {
		createFilterText(parent);
		if (useNewLook)
			createClearTextNew(parent);
		else
			createClearTextOld(parent);
		if (clearButtonControl != null)
			clearButtonControl.setVisible(false);

		if (filterToolBar != null) {
			filterToolBar.update(false);
			filterToolBar.getControl().setVisible(false);
		}
		return parent;
	}


	private TreeItem getFirstMatchingItem(TreeItem[] items) {
		for (int i = 0; i < items.length; i++) {
			if (isLeafMatch(patternFilter, treeViewer, items[i].getData()) && patternFilter.isElementSelectable(items[i].getData())) { return items[i]; }
			TreeItem treeItem = getFirstMatchingItem(items[i].getItems());
			if (treeItem != null) { return treeItem; }
		}
		return null;
	}


	private boolean isLeafMatch(PatternFilter filter, Viewer viewer, Object element) {
		try {
			if (isLeafMatch == null) {
				isLeafMatch = filter.getClass().getMethod("isLeafMatch", Viewer.class, Object.class);
				isLeafMatch.setAccessible(true);
			}

			Object result = isLeafMatch.invoke(filter, viewer, element);
			if (result != null && result instanceof Boolean)
				return ((Boolean) result).booleanValue();
		} catch (NoSuchMethodException e) {
		} catch (SecurityException e) {
		} catch (IllegalAccessException e) {
		} catch (IllegalArgumentException e) {
		} catch (InvocationTargetException e) {
		}

		return false;
	}


	private void createRefreshJob() {
		refreshJob = doCreateRefreshJob();
		refreshJob.setSystem(true);
	}


	protected WorkbenchJob doCreateRefreshJob() {
		return new WorkbenchJob("Refresh Filter") {
			public IStatus runInUIThread(IProgressMonitor monitor) {
				if (treeViewer.getControl().isDisposed())
					return Status.CANCEL_STATUS;

				String text = getFilterString();
				if (text == null)
					return Status.OK_STATUS;

				boolean initial = initialText != null && initialText.equals(text);
				if (initial)
					patternFilter.setPattern(null);
				else if (text != null)
					patternFilter.setPattern(text);

				Control redrawFalseControl = treeComposite != null ? treeComposite : treeViewer.getControl();
				try {
					redrawFalseControl.setRedraw(false);
					if (!narrowingDown) {
						TreeItem[] is = treeViewer.getTree().getItems();
						for (int i = 0; i < is.length; i++) {
							TreeItem item = is[i];
							if (item.getExpanded()) {
								treeViewer.setExpandedState(item.getData(), false);
							}
						}
					}
					treeViewer.refresh(true);

					if (text.length() > 0 && !initial) {
						TreeItem[] items = getCommonViewer().getTree().getItems();
						int treeHeight = getCommonViewer().getTree().getBounds().height;
						int numVisibleItems = treeHeight / getCommonViewer().getTree().getItemHeight();
						long stopTime = SOFT_MAX_EXPAND_TIME + System.currentTimeMillis();
						boolean cancel = false;
						if (items.length > 0 && recursiveExpand(items, monitor, stopTime, new int[] { numVisibleItems })) {
							cancel = true;
						}

						updateToolbar(true);

						if (cancel)
							return Status.CANCEL_STATUS;
					} else {
						updateToolbar(false);
					}
				} finally {
					TreeItem[] items = getCommonViewer().getTree().getItems();
					if (items.length > 0 && getCommonViewer().getTree().getSelectionCount() == 0) {
						treeViewer.getTree().setTopItem(items[0]);
					}
					if (quickSelectionMode)
						updateTreeSelection(false);
					redrawFalseControl.setRedraw(true);
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
								treeViewer.setExpandedState(itemData, true);
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
	}


	protected void updateToolbar(boolean visible) {
		if (clearButtonControl != null) {
			clearButtonControl.setVisible(visible);
		}
		if (filterToolBar != null) {
			filterToolBar.getControl().setVisible(visible);
		}
	}


	protected void createFilterText(Composite parent) {
		filterText = doCreateFilterText(parent);
		filterText.getAccessible().addAccessibleListener(
				new AccessibleAdapter() {
					public void getName(AccessibleEvent e) {
						String filterTextString = filterText.getText();
						if (filterTextString.length() == 0 || filterTextString.equals(initialText))
							e.result = initialText;
						else
							e.result = String.format("%s: %d matches", filterTextString, getFilteredItemsCount());
					}


					private int getFilteredItemsCount() {
						int total = 0;
						TreeItem[] items = getCommonViewer().getTree().getItems();
						for (int i = 0; i < items.length; i++)
							total += itemCount(items[i]);
						return total;
					}


					private int itemCount(TreeItem treeItem) {
						int count = 1;
						TreeItem[] children = treeItem.getItems();
						for (int i = 0; i < children.length; i++)
							count += itemCount(children[i]);
						return count;
					}
				});

		filterText.addFocusListener(new FocusAdapter() {
			public void focusGained(FocusEvent e) {
				if (!useNewLook) {
					Display display = filterText.getDisplay();
					display.asyncExec(new Runnable() {
						public void run() {
							if (!filterText.isDisposed()) {
								if (getInitialText().equals(filterText.getText().trim())) {
									filterText.selectAll();
								}
							}
						}
					});
					return;
				}
			}


			public void focusLost(FocusEvent e) {
				if (!useNewLook) {
				return;
				}
				if (filterText.getText().equals(initialText)) {
					setFilterText("");
					textChanged();
				}
			}
		});

		if (useNewLook) {
			filterText.addMouseListener(new MouseAdapter() {
				public void mouseDown(MouseEvent e) {
					if (filterText.getText().equals(initialText)) {
						setFilterText("");
						textChanged();
					}
				}
			});
		}

		filterText.addKeyListener(new KeyAdapter() {
			public void keyPressed(KeyEvent e) {
				boolean hasItems = getCommonViewer().getTree().getItemCount() > 0;
				if (hasItems && e.keyCode == SWT.ARROW_DOWN) {
					treeViewer.getTree().setFocus();
					return;
				}
			}
		});

		filterText.addTraverseListener(new TraverseListener() {
			public void keyTraversed(TraverseEvent e) {
				if (quickSelectionMode) {
				return;
				}
				if (e.detail == SWT.TRAVERSE_RETURN) {
					e.doit = false;
					updateTreeSelection(true);
				}
			}
		});

		filterText.addModifyListener(new ModifyListener() {
			public void modifyText(ModifyEvent e) {
				textChanged();
			}
		});

		if ((filterText.getStyle() & SWT.ICON_CANCEL) != 0) {
			filterText.addSelectionListener(new SelectionAdapter() {
				public void widgetDefaultSelected(SelectionEvent e) {
					if (e.detail == SWT.ICON_CANCEL)
						clearText();
				}
			});
		}

		GridData gd = new GridData(SWT.FILL, SWT.CENTER, true, false);
		if ((filterText.getStyle() & SWT.ICON_CANCEL) != 0)
			gd.horizontalSpan = 2;
		filterText.setLayoutData(gd);
	}


	protected void updateTreeSelection(boolean setFocus) {
		Tree tree = getCommonViewer().getTree();
		if (tree.getItemCount() == 0) {
			if (setFocus)
				Display.getCurrent().beep();
		} else {
			boolean hasFocus = setFocus ? tree.setFocus() : true;
			boolean textChanged = !getInitialText().equals(filterText.getText().trim());
			if (hasFocus && textChanged && filterText.getText().trim().length() > 0) {
				TreeItem item;
				if (tree.getSelectionCount() > 0)
					item = getFirstMatchingItem(tree.getSelection());
				else
					item = getFirstMatchingItem(tree.getItems());
				if (item != null) {
					tree.setSelection(new TreeItem[] { item });
					ISelection sel = getCommonViewer().getSelection();
					getCommonViewer().setSelection(sel, true);
				}
			}
		}
	}


	protected Text doCreateFilterText(Composite parent) {
		if (!useNewLook || useNativeSearchField(parent)) { 
			return new Text(parent, SWT.SINGLE | SWT.BORDER | SWT.SEARCH | SWT.ICON_CANCEL); 
		}
		return new Text(parent, SWT.SINGLE);
	}


	protected void textChanged() {
		narrowingDown = previousFilterText == null
				|| previousFilterText.equals("type filter text")
				|| getFilterString().startsWith(previousFilterText);

		previousFilterText = getFilterString();
		refreshJob.cancel();
		refreshJob.schedule(getRefreshJobDelay());
	}


	protected long getRefreshJobDelay() {
		return 200;
	}


	private void createClearTextOld(Composite parent) {
		if ((filterText.getStyle() & SWT.ICON_CANCEL) == 0) {
			filterToolBar = new ToolBarManager(SWT.FLAT | SWT.HORIZONTAL);
			filterToolBar.createControl(parent);

			IAction clearTextAction = new Action("", IAction.AS_PUSH_BUTTON) {
				public void run() {
					clearText();
				}
			};

			clearTextAction.setToolTipText("Clear");
			clearTextAction.setImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR));
			clearTextAction.setDisabledImageDescriptor(PlatformUI.getWorkbench().getSharedImages().getImageDescriptor(ISharedImages.IMG_ETOOL_CLEAR_DISABLED));
			filterToolBar.add(clearTextAction);
		}
	}


	private void createClearTextNew(Composite parent) {
		if ((filterText.getStyle() & SWT.ICON_CANCEL) == 0) {
			final Image inactiveImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_CLEAR);
			final Image activeImage = PlatformUI.getWorkbench().getSharedImages().getImage(ISharedImages.IMG_ETOOL_CLEAR_DISABLED);
			final Image pressedImage = new Image(parent.getDisplay(), activeImage, SWT.IMAGE_GRAY);

			final Label clearButton = new Label(parent, SWT.NONE);
			clearButton.setLayoutData(new GridData(SWT.BEGINNING, SWT.CENTER, false, false));
			clearButton.setImage(inactiveImage);
			clearButton.setBackground(parent.getDisplay().getSystemColor(SWT.COLOR_LIST_BACKGROUND));
			clearButton.setToolTipText("Clear");

			clearButton.addMouseListener(new MouseAdapter() {
				private MouseMoveListener fMoveListener;


				public void mouseDown(MouseEvent e) {
					clearButton.setImage(pressedImage);
					fMoveListener = new MouseMoveListener() {
						private boolean fMouseInButton = true;


						public void mouseMove(MouseEvent e) {
							boolean mouseInButton = isMouseInButton(e);
							if (mouseInButton != fMouseInButton) {
								fMouseInButton = mouseInButton;
								clearButton.setImage(mouseInButton ? pressedImage : inactiveImage);
							}
						}
					};
					clearButton.addMouseMoveListener(fMoveListener);
				}


				public void mouseUp(MouseEvent e) {
					if (fMoveListener != null) {
						clearButton.removeMouseMoveListener(fMoveListener);
						fMoveListener = null;
						boolean mouseInButton = isMouseInButton(e);
						clearButton.setImage(mouseInButton ? activeImage : inactiveImage);
						if (mouseInButton) {
							clearText();
							filterText.setFocus();
						}
					}
				}


				private boolean isMouseInButton(MouseEvent e) {
					Point buttonSize = clearButton.getSize();
					return 0 <= e.x && e.x < buttonSize.x && 0 <= e.y && e.y < buttonSize.y;
				}
			});

			clearButton.addMouseTrackListener(new MouseTrackListener() {
				public void mouseEnter(MouseEvent e) {
					clearButton.setImage(activeImage);
				}


				public void mouseExit(MouseEvent e) {
					clearButton.setImage(inactiveImage);
				}


				public void mouseHover(MouseEvent e) {
				}
			});

			clearButton.addDisposeListener(new DisposeListener() {
				public void widgetDisposed(DisposeEvent e) {
					pressedImage.dispose();
				}
			});

			clearButton.getAccessible().addAccessibleListener(
					new AccessibleAdapter() {
						public void getName(AccessibleEvent e) {
							e.result = "Clear filter field";
						}
					});

			clearButton.getAccessible().addAccessibleControlListener(
					new AccessibleControlAdapter() {
						public void getRole(AccessibleControlEvent e) {
							e.detail = ACC.ROLE_PUSHBUTTON;
						}
					});

			this.clearButtonControl = clearButton;
		}
	}


	protected void clearText() {
		setFilterText("");
		textChanged();
	}


	protected void setFilterText(String string) {
		if (filterText != null) {
			filterText.setText(string);
			selectAll();
		}
	}


	protected String getFilterString() {
		return filterText != null ? filterText.getText() : null;
	}


	public void setInitialText(String text) {
		initialText = text;
		if (useNewLook && filterText != null) {
			filterText.setMessage(text);
			if (filterText.isFocusControl()) {
				setFilterText(initialText);
				textChanged();
			} else {
				parent.getDisplay().asyncExec(new Runnable() {
					public void run() {
						if (!filterText.isDisposed() && filterText.isFocusControl()) {
							setFilterText(initialText);
							textChanged();
						}
					}
				});
			}
		} else {
			setFilterText(initialText);
			textChanged();
		}
	}


	public void setQuickSelectionMode(boolean enabled) {
		this.quickSelectionMode = enabled;
	}


	protected void selectAll() {
		if (filterText != null) {
			filterText.selectAll();
		}
	}


	protected String getInitialText() {
		return initialText;
	}

}
