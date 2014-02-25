package eu.martinlange.launchpad.views;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.IMemento;
import org.eclipse.ui.IViewSite;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.XMLMemento;
import org.eclipse.ui.dialogs.FilteredTree;
import org.eclipse.ui.dialogs.PatternFilter;
import org.eclipse.ui.part.ViewPart;

public class LaunchpadView extends ViewPart {

	protected IMemento fMemento;
	
	protected FilteredTree fTree;
	
	
	public LaunchpadView() {
	}

	
	@Override
	public void init(IViewSite site, IMemento memento) throws PartInitException {
		super.init(site, memento);
		
		if (memento == null)
			this.fMemento = XMLMemento.createWriteRoot("LAUNCHPAD");
		else
			this.fMemento = memento;
	}

	
	@Override
	public void saveState(IMemento memento) {
		if (this.fMemento == null || memento == null)
			return;
		
		memento.putMemento(fMemento);
	}
	

	@Override
	public void createPartControl(Composite parent) {
		fTree = new FilteredTree(parent, SWT.FULL_SELECTION, new PatternFilter(), true);
	}


	@Override
	public void setFocus() {

	}

}
