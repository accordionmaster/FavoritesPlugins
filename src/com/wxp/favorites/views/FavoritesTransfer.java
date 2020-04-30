package com.wxp.favorites.views;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.management.InstanceAlreadyExistsException;

import org.eclipse.swt.dnd.ByteArrayTransfer;
import org.eclipse.swt.dnd.TransferData;
import org.eclipse.ui.actions.RetargetAction;

import com.wxp.favorites.model.FavoritesManager;
import com.wxp.favorites.model.IFavoriteItem;

public class FavoritesTransfer extends ByteArrayTransfer{
	
	private static final FavoritesTransfer INSTANCE = new FavoritesTransfer();
	
	public static FavoritesTransfer getInstance() {
		return INSTANCE;
	}
	
	public FavoritesTransfer() {
		super();
	}
	
	/**
	 * 每一个FavoritesTransfer类必须具有一个唯一的标识以保证不同的Eclipse程序使用
	 * 不同类型的FavoritesTransfer类。getTypeIds和getTypeNames方法返回平台特定的ID和
	 * 可以使用该传输代理进行转换的数据类型的名称。
	 */
	private static final String TYPE_NAME = 
			"favorites-transfer-format:"
			+ System.currentTimeMillis()
			+ ":"
			+ INSTANCE.hashCode();

	private static final int TYPEID = 
			registerType(TYPE_NAME);
	
	@Override
	protected int[] getTypeIds() {
		return new int[] {TYPEID};
	}

	@Override
	protected String[] getTypeNames() {
		return new String[] {TYPE_NAME};
	}
	
	protected void javaToNative(Object data, TransferData transferData) {
		
		if (!(data instanceof IFavoriteItem[])) {
			return;
		}
		
		IFavoriteItem[] items = (IFavoriteItem[]) data;
		
		try {
			ByteArrayOutputStream out = 
					new ByteArrayOutputStream();
			DataOutputStream dataOut = 
					new DataOutputStream(out);
			dataOut.writeInt(items.length);
			for (int i = 0; i < items.length; i++) {
				IFavoriteItem item = items[i];
				dataOut.writeUTF(item.getType().getId());
				dataOut.writeUTF(item.getInfo());
			}
			dataOut.close();
			out.close();
			super.javaToNative(out.toByteArray(), transferData);
		} catch (IOException e) {
			// Send nothing if there were problems.
		}
		
	}
	
	/**
	 * nativeToJava方法将平台相关的数据表示转换为java表示。
	 */
	protected Object nativeToJava(TransferData transferData) {
		byte[] bytes = (byte[]) super.nativeToJava(transferData);
		if (bytes == null) {
			return null;
		}
		DataInputStream in = 
				new DataInputStream(new ByteArrayInputStream(bytes));
		try {
			FavoritesManager mgr = 
					FavoritesManager.getManager();
			int count = in.readInt();
			List<IFavoriteItem> items = 
					new ArrayList<IFavoriteItem>(count);
			for (int i = 0; i < count; i++) {
				String typeId = in.readUTF();
				String info = in.readUTF();
				items.add(mgr.newFavoriteFor(typeId, info));
			}
			return items.toArray(new IFavoriteItem[items.size()]);
		} catch (IOException e) {
			return null;
		}
	}

}
