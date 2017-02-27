package org.eclipse.californium.scandium.communication.test;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public abstract class ConnectionThread extends Thread {
	public static interface OnReceivedListener {
		void onReceived() throws Exception;
	}
	
	private boolean mbListening = false;
	
	protected void initListen(final OnReceivedListener listener) {
		if(!mbListening) {
			mbListening = true;
			new Thread() { // ��� ��
				public void run() {
					for (;;) {
						try {
							listener.onReceived();
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
					} // for(;;)
				};
			}.start();
		} // if
	} // func
	
	public void send(int seq) throws IOException {
		if(Math.random() < 0.8) { // 80% Ȯ��� ������ ��Ŷ�� ������
			niceSend(seq);
		} else { // 20% Ȯ��� ��Ŷ�� �߰��� �Ҵ´�
			fakeSend(seq);
		}
	} // func
	protected abstract void niceSend(int seq) throws IOException;
	protected abstract void fakeSend(int seq) throws IOException;
	
	// Serializable ��ü�� ����Ʈ�� ������ ��ȯ�Ͽ� ��ȯ
	protected byte[] serialization(Serializable serializable) throws IOException {
		ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
		ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream); 
		objectOutputStream.writeObject(serializable);
		byte[] serializedMessage = byteArrayOutputStream.toByteArray();
		objectOutputStream.close();
		byteArrayOutputStream.close();
		return serializedMessage;
	}
	
	// ����Ʈ�� ������ Serializable ��ü�� ��ȯ�Ͽ� ��ȯ (���� ���� ĳ�����ؼ� �� ��)
	protected Object deserialization(byte[] recvBytes) throws IOException, ClassNotFoundException {
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(recvBytes);
		ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);
		Object deserializedObject = objectInputStream.readObject();
		objectInputStream.close();
		byteArrayInputStream.close();
		return deserializedObject;
	}
	
} // public class
