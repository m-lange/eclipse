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
import eu.martinlange.tools.eclipse.console.model.StyleDefinition;
import eu.martinlange.tools.eclipse.preferences.IPreferenceConstants;
import eu.martinlange.tools.eclipse.preferences.PreferenceStore;

public class StylePreferencePage extends PreferencePage implements IWorkbenchPreferencePage, IPropertyChangeListener {

	private Table fTable;
	private TableViewer fTableViewer;
	private Composite fTableComposite;
	private Composite fButtonComposite;
	private Button fAddButton;
	private Button fEditButton;
	private Button fRemoveButton;

	private PreferenceStore fStore;
	private List<StyleDefinition> fStyles;


	@Override
	public void init(IWorkbench workbench) {
		fStore = Plugin.getDefault().getPreferenceStore();
		fStore.addPropertyChangeListener(this);
		setPreferenceStore(fStore);

		fStyles = fStore.getList(IPreferenceConstants.STYLES, StyleDefinition.class);
	}


	@Override
	protected void performDefaults() {
		fStore.setToDefault(IPreferenceConstants.STYLES);
		fStyles = fStore.getList(IPreferenceConstants.STYLES, StyleDefinition.class);
		applyData();
		super.performDefaults();
	}


	@Override
	public boolean performOk() {
		fStore.setValue(IPreferenceConstants.STYLES, fStyles, StyleDefinition.class);
		return super.performOk();
	}


	@Override
	public Control createContents(Composite parent) {

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
		fTable = fTableViewer.getTable();
		fTable.setHeaderVisible(true);

		TableColumn fNameColumn = new TableColumn(fTable, SWT.NONE);
		fNameColumn.setResizable(false);
		layout.setColumnData(fNameColumn, new ColumnWeightData(1, 100, true));
		fNameColumn.setText("Name");

		TableColumn fPreviewColumn = new TableColumn(fTable, SWT.CENTER);
		fPreviewColumn.setResizable(false);
		int widthInPixel = converter.convertWidthInCharsToPixels(DefaultLabelProvider.PREVIEW_TEXT.length());
		layout.setColumnData(fPreviewColumn, new ColumnPixelData(widthInPixel, true, true));
		fPreviewColumn.setText("Preview");
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

		applyData();

		return container;
	}


	protected StyleDefinition getSelection() {
		IStructuredSelection selection = (IStructuredSelection) fTableViewer.getSelection();
		if (selection != null && selection.size() > 0)
			return (StyleDefinition) selection.getFirstElement();

		return null;
	}


	public void applyData() {
		if (fTableViewer == null || fTableViewer.getControl().isDisposed())
			return;

		Collections.sort(fStyles, new Comparator<StyleDefinition>() {
			@Override
			public int compare(StyleDefinition o1, StyleDefinition o2) {
				return o1.name.compareTo(o2.name);
			}
		});

		fTableViewer.setInput(fStyles.toArray(new StyleDefinition[0]));
		fTableViewer.refresh(true);
		updateEnableState();
	}


	protected void addPressed() {
		StyleDefinition e = new StyleDefinition();
		StyleDefinitionDialog dialog = new StyleDefinitionDialog(getShell());
		dialog.setText("New Style Definition");
		dialog.setStyle(e);
		if (dialog.open() == Dialog.OK) {
			fStyles.add(e);
			applyData();
		}
	}


	protected void editPressed() {
		StyleDefinition e = getSelection();
		if (e == null)
			return;

		StyleDefinitionDialog dialog = new StyleDefinitionDialog(getShell());
		dialog.setText("Edit Style Definition");
		dialog.setStyle(e);
		if (dialog.open() == Dialog.OK) {
			applyData();
		}
	}


	protected void removePressed() {
		IStructuredSelection selection = (IStructuredSelection) fTableViewer.getSelection();
		if (selection != null && selection.size() > 0) {
			Iterator<?> itr = selection.iterator();
			while (itr.hasNext()) {
				fStyles.remove(itr.next());
			}
			applyData();
		}
	}


	protected void updateEnableState() {
		IStructuredSelection selection = (IStructuredSelection) fTableViewer.getSelection();
		fEditButton.setEnabled(selection.size() == 1);
		fRemoveButton.setEnabled(selection.size() > 0);
	}


	@Override
	public void propertyChange(PropertyChangeEvent event) {
		if (event.getProperty().equals(IPreferenceConstants.STYLES)) {
			fStyles = fStore.getList(IPreferenceConstants.STYLES, StyleDefinition.class);
			applyData();
		}
	}

}
