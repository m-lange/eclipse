package eu.martinlange.launchpad.ui.views;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;
import eu.martinlange.launchpad.model.ElementTree;
import eu.martinlange.launchpad.model.ElementTreeData;

public class LaunchpadTransfer extends ByteArrayTransfer {

	private static final String TYPE_NAME = "launchpad-transfer-format";
	private static final int TYPE_ID = registerType(TYPE_NAME);

	private static LaunchpadTransfer fInstance = new LaunchpadTransfer();


	private LaunchpadTransfer() {
	}


	public static LaunchpadTransfer getInstance() {
		return fInstance;
	}


	@Override
	protected int[] getTypeIds() {
		return new int[] { TYPE_ID };
	}


	@Override
	protected String[] getTypeNames() {
		return new String[] { TYPE_NAME };
	}


	protected void javaToNative(Object data, TransferData transferData) {
		ElementTreeData realData = (ElementTreeData) data;
		if (data == null) return;

		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DataOutputStream dataOut = new DataOutputStream(out);
			dataOut.writeUTF(realData.getId());
			dataOut.close();
			super.javaToNative(out.toByteArray(), transferData);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}


	protected Object nativeToJava(TransferData transferData) {
		try {
			byte[] bytes = (byte[]) super.nativeToJava(transferData);
			ByteArrayInputStream in = new ByteArrayInputStream(bytes);
			DataInputStream dataIn = new DataInputStream(in);
			String id = dataIn.readUTF();
			dataIn.close();
			return ElementTree.INSTANCE.getById(id);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}

}
