package eu.martinlange.tools.eclipse.console.preferences;

import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.layout.PixelConverter;
import org.eclipse.jface.layout.TableColumnLayout;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.jface.util.PropertyChangeEvent;
import org.eclipse.jface.viewers.ArrayContentProvider;
import org.eclipse.jface.viewers.ColumnPixelData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import eu.martinlange.tools.eclipse.Plugin;
import eu.martinlange.tools.eclipse.console.model.PatternDefinition;
import eu.martinlange.tools.eclipse.console.model.StyleDefinition;
import eu.martinlange.tools.eclipse.preferences.IPreferenceConstants;
import eu.martinlange.tools.eclipse.preferences.PreferenceStore;

public class PatternPreferencePage extends PreferencePage implements IWorkbenchPreferencePage, IPropertyChangeListener {

	private Table fTable;
	private TableViewer fTableViewer;
	private Composite fTableComposite;
	private Composite fButtonComposite;
	private Button fAddButton;
	private Button fEditButton;
	private Button fRemoveButton;
	private Button fUpButton;
	private Button fDownButton;

	private PreferenceStore fStore;
	private List<PatternDefinition> fPattern;


	@Override
	public void init(IWorkbench workbench) {
		fStore = Plugin.getDefault().getPreferenceStore();
		fStore.addPropertyChangeListener(this);
		setPreferenceStore(fStore);

		fPattern = fStore.getList(IPreferenceConstants.PATTERN, PatternDefinition.class);
	}


	@Override
	protected void performDefaults() {
		fStore.setToDefault(IPreferenceConstants.PATTERN);
		fPattern = fStore.getList(IPreferenceConstants.PATTERN, PatternDefinition.class);
		applyData();
		super.performDefaults();
	}


	@Override
	public boolean performOk() {
		fStore.setValue(IPreferenceConstants.PATTERN, fPattern, PatternDefinition.class);
		return super.performOk();
	}


	@Override
	protected Control createContents(Composite parent) {
		PixelConverter converter = new PixelConverter(StyleDefinition.DEFAULT_FONT);
		
		Composite container = new Composite(parent, SWT.NULL);
		container.setLayout(new GridLayout(2, false));

		fTableComposite = new Composite(container, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true, 1, 1);
		gd.widthHint = convertWidthInCharsToPixels(75);
		gd.heightHint = convertHeightInCharsToPixels(6);
		fTableComposite.setLayoutData(gd);
		TableColumnLayout layout = new TableColumnLayout();
		fTableComposite.setLayout(layout);

		fTableViewer = new TableViewer(fTableComposite, SWT.BORDER | SWT.FULL_SELECTION | SWT.MULTI);
		fTable = fTableViewer.getTable();
		fTable.setHeaderVisible(true);

		fTableViewer.addDoubleClickListener(new IDoubleClickListener() {
			public void doubleClick(DoubleClickEvent event) {
				if (getSelection() != null)
					editPressed();
			}
		});
		fTableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
			public void selectionChanged(SelectionChangedEvent event) {
				updateEnableState();
			}
		});

		TableColumn fNameColumn = new TableColumn(fTable, SWT.NONE);
		fNameColumn.setResizable(false);
		layout.setColumnData(fNameColumn, new ColumnWeightData(1, 100, true));
		fNameColumn.setText("Name");

		TableColumn fPreviewColumn = new TableColumn(fTable, SWT.NONE);
		fPreviewColumn.setResizable(false);
		int widthInPixel = converter.convertWidthInCharsToPixels(50);
		layout.setColumnData(fPreviewColumn, new ColumnPixelData(widthInPixel, true, true));
		fPreviewColumn.setText("Pattern");

		fTableViewer.setLabelProvider(DefaultLabelProvider.getInstance());
		fTableViewer.setContentProvider(ArrayContentProvider.getInstance());
		fTableViewer.setInput(new Object());

		fButtonComposite = new Composite(container, SWT.NONE);
		fButtonComposite.setLayout(new GridLayout(1, false));
		fButtonComposite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, false, true, 1, 1));

		fAddButton = new Button(fButtonComposite, SWT.NONE);
		fAddButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				addPressed();
			}
		});
		setButtonLayoutData(fAddButton);
		fAddButton.setText("Add...");

		fEditButton = new Button(fButtonComposite, SWT.NONE);
		fEditButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				editPressed();
			}
		});
		setButtonLayoutData(fEditButton);
		fEditButton.setText("Edit...");

		fRemoveButton = new Button(fButtonComposite, SWT.NONE);
		fRemoveButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				removePressed();
			}
		});
		setButtonLayoutData(fRemoveButton);
		fRemoveButton.setText("Remove");

		fUpButton = new Button(fButtonComposite, SWT.NONE);
		fUpButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				upPressed();
			}
		});
		setButtonLayoutData(fUpButton);
		((GridData) fUpButton.getLayoutData()).verticalIndent = 10;
		fUpButton.setText("Up");

		fDownButton = new Button(fButtonComposite, SWT.NONE);
		fDownButton.addSelectionListener(new SelectionAdapter() {
			@Override
			public void widgetSelected(SelectionEvent e) {
				downPressed();
			}
		});
		setButtonLayoutData(fDownButton);
		fDownButton.setText("Down");

		applyData();
		updateEnableState();

		return container;
	}


	protected PatternDefinition getSelection() {
		IStructuredSelection selection = (IStructuredSelection) fTableViewer.getSelection();
		if (selection != null && selection.size() > 0)
			return (PatternDefinition) selection.getFirstElement();
		return null;
	}


	public void applyData() {
		if (fTableViewer == null || fTableViewer.getControl().isDisposed())
			return;

		Collections.sort(fPattern, new Comparator<PatternDefinition>() {
			@Override
			public int compare(PatternDefinition o1, PatternDefinition o2) {
				return o1.index - o2.index;
			}
		});

		fTableViewer.setInput(fPattern.toArray(new PatternDefinition[0]));
		fTableViewer.refresh(true);
		updateEnableState();
	}


	protected void addPressed() {
		PatternDefinition e = new PatternDefinition();
		e.index = fTable.getItemCount();
		PatternDefinitionDialog dialog = new PatternDefinitionDialog(getShell());
		dialog.setText("New Pattern Definition");
		dialog.setPattern(e);
		if (dialog.open() == Dialog.OK) {
			fPattern.add(e);
			applyData();
		}
	}


	protected void editPressed() {
		PatternDefinition e = getSelection();
		if (e == null)
			return;

		PatternDefinitionDialog dialog = new PatternDefinitionDialog(getShell());
		dialog.setText("Edit Pattern Definition");
		dialog.setPattern(e);
		if (dialog.open() == Dialog.OK) {
			applyData();
		}
	}


	protected void removePressed() {
		IStructuredSelection selection = (IStructuredSelection) fTableViewer.getSelection();
		if (selection != null && selection.size() > 0) {
			Iterator<?> itr = selection.iterator();
			while (itr.hasNext()) {
				fPattern.remove(itr.next());
			}
			applyData();
		}
	}


	protected void upPressed() {
		int idx = fTable.getSelectionIndex();
		if (idx > 0) {
			PatternDefinition[] node = {
					(PatternDefinition) fTableViewer.getElementAt(idx),
					(PatternDefinition) fTableViewer.getElementAt(idx - 1)
			};
			node[0].index = idx - 1;
			node[1].index = idx;
			applyData();
		}
	}


	protected void downPressed() {
		int idx = fTable.getSelectionIndex();
		if (idx < (fTable.getItemCount() - 1)) {
			PatternDefinition[] node = {
					(PatternDefinition) fTableViewer.getElementAt(idx),
					(PatternDefinition) fTableViewer.getElementAt(idx + 1)
			};
			node[0].index = idx + 1;
			node[1].index = idx;
			applyData();
		}
	}


	protected void updateEnableState() {
		IStructuredSelection selection = (IStructuredSelection) fTableViewer.getSelection();
		fEditButton.setEnabled(selection.size() == 1);
		fRemoveButton.setEnabled(selection.size() > 0);
		fUpButton.setEnabled(selection.size() == 1 && fTable.getSelectionIndex() > 0);
		fDownButton.setEnabled(selection.size() == 1 && fTable.getSelectionIndex() < (fTable.getItemCount() - 1));
	}


	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(IPreferenceConstants.PATTERN)) {
			fPattern = fStore.getList(IPreferenceConstants.PATTERN, PatternDefinition.class);
			applyData();
		}
	}

}
