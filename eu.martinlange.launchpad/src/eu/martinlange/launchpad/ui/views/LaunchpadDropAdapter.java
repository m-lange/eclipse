package eu.martinlange.launchpad.ui.views;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.navigator.CommonDropAdapter;
import org.eclipse.ui.navigator.CommonDropAdapterAssistant;
import eu.martinlange.launchpad.Plugin;

public class LaunchpadDropAdapter extends CommonDropAdapterAssistant {

	@Override
	public IStatus validateDrop(Object target, int operation, TransferData transferType) {
		return new Status(IStatus.ERROR, Plugin.PLUGIN_ID, "");
	}


	@Override
	public IStatus handleDrop(CommonDropAdapter aDropAdapter, DropTargetEvent aDropTargetEvent, Object aTarget) {
//		TransferData transferData = aDropAdapter.getCurrentTransfer();
//		
//		if (LocalSelectionTransfer.getTransfer().isSupportedType(transferData)) {
//			Object selection = LocalSelectionTransfer.getTransfer().nativeToJava(transferData);
//			
//			
//		}
		
		return null;
	}

}
