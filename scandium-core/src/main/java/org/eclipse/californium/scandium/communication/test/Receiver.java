package org.eclipse.californium.scandium.communication.test;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.TimerTask;


public class Receiver extends ConnectionThread implements Network {
	private InetAddress mAddrSender;
	private DatagramSocket mSocket;
	private byte[] mQuickBuffer = new byte[SIZE_PACKET];
	private SR mUi = null;
	
	public Receiver(SR parentContext) throws UnknownHostException, SocketException {
		// TODO Auto-generated constructor stub
		super();
		mUi = parentContext;
		mAddrSender = InetAddress.getLocalHost();
		mSocket = new DatagramSocket(PORT_RECEIVER);
	} // func
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		initListen(new OnReceivedListener() {
			@Override
			public void onReceived() throws Exception {
				// TODO Auto-generated method stub
				mSocket.receive(new DatagramPacket(mQuickBuffer, mQuickBuffer.length));
				Packet packet = (Packet) deserialization(mQuickBuffer);
				if(!packet.isAck()) {
					mUi.updateBufferSlotReceiver(packet.getSeq(), BufferSlot.BUFFERED);
					mUi.updateBaseRcv();
					// sender���� ��Ŷ�� ������ ack�� �����Ѵ�
					send(packet.getSeq());
				} // if
			} // func
		}); // initListen()
	} // func
	
	// �������� ack�� �۽������� ��޵Ǵ� �߰��� ��Ŷ�� �սǵȴ�
	@Override
	protected void fakeSend(final int seq) {
		// ������ �����κ����� �ִϸ��̼� ���
		mUi.getFlyingPiece(seq).setY(SR.DISTANCE_BETWEEN_BUFFERS);
		mUi.getFlyingPiece(seq).setAck(true);
		mUi.getFlyingPiece(seq).setVisible(true);
		mUi.getBufferSlotReceiver(seq).startTimerAnimation(new TimerTask() {
			int times = 0;
			private final int timesMax = (BufferSlot.SEC_DELIVERY * 1000) / BufferSlot.MSEC_ANIM;
			private final double distStep = SR.DISTANCE_BETWEEN_BUFFERS / timesMax;
			@Override
			public void run() { // Ÿ�̸� �ֱ⸶�� ����
				// TODO Auto-generated method stub
				++times;
				if(times > timesMax / 2) { // �츮�� ��Ŷ�� �� �Ÿ��� ��� ��ŭ�� �����Ѵ�
					mUi.getFlyingPiece(seq).setVisible(false);
					mUi.repaint();
					mUi.getBufferSlotReceiver(seq).cancelTimerAnimation();
					// System.out.println("test");
					return;
				} // if
				// �� ���� ����
				mUi.getFlyingPiece(seq).setY(
						(int)(SR.DISTANCE_BETWEEN_BUFFERS - (distStep * times))
						);
				mUi.repaint();
			}
		}); // startTimerAnimation()
	} // func
	
	// receiver�� Ȯ������ ��Ŷ�� �۽���
	@Override
	protected void niceSend(final int seq) throws IOException {	
		// �޴� �����κ����� ack �۽� �ִϸ��̼� ���
		mUi.getFlyingPiece(seq).setY(SR.DISTANCE_BETWEEN_BUFFERS);
		mUi.getFlyingPiece(seq).setAck(true);
		mUi.getFlyingPiece(seq).setVisible(true);
		mUi.getBufferSlotReceiver(seq).startTimerAnimation(new TimerTask() {
			int times = 0;
			private final int timesMax = (BufferSlot.SEC_DELIVERY * 1000) / BufferSlot.MSEC_ANIM;
			private final double distStep = SR.DISTANCE_BETWEEN_BUFFERS / timesMax;
			@Override
			public void run() { // Ÿ�̸� �ֱ⸶�� ����
				// TODO Auto-generated method stub
				++times;
				if(times > timesMax) { // �ִϸ��̼��� ��
					try { // ������ �ִϸ��̼��� ������ ������ ��Ŷ�� ������
						byte[] serializedMessage = serialization(new Packet(true, seq));
						mSocket.send(
								new DatagramPacket(
									serializedMessage, serializedMessage.length, 
									mAddrSender, PORT_SENDER
								)
							);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mUi.getFlyingPiece(seq).setVisible(false);
					mUi.repaint();
					mUi.getBufferSlotReceiver(seq).cancelTimerAnimation();
					// System.out.println("test");
					return;
				} // if
				// �� ���� ����
				mUi.getFlyingPiece(seq).setY(
						(int)(SR.DISTANCE_BETWEEN_BUFFERS - (distStep * times))
						);
				mUi.repaint();
			}
		}); // startTimerAnimation()
	} // func
	
} // public class
