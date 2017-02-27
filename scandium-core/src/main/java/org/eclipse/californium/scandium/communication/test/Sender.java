package org.eclipse.californium.scandium.communication.test;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.TimerTask;


public class Sender extends ConnectionThread implements Network {
	private InetAddress mAddrReciever;
	private DatagramSocket mSocket;
	private byte[] mQuickBuffer = new byte[SIZE_PACKET];
	private SR mUi = null;

	public Sender(SR parentContext) throws SocketException, UnknownHostException {
		// TODO Auto-generated constructor stub
		super();
		mUi = parentContext;
		mAddrReciever = InetAddress.getLocalHost();
		mSocket = new DatagramSocket(PORT_SENDER);
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
				if(packet.isAck()) {
					mUi.updateBufferSlotSender(packet.getSeq(), BufferSlot.ACKED);
					mUi.updateBaseSnd();
					mUi.getBufferSlotSender(packet.getSeq()).cancelTimerTimeout();
				}
			}
		});
	} // func
	
	// sender�� �ش� ���ȣ�� ��Ŷ�� �۽��ϸ� ����� Ÿ�̸� ������
	@Override
	protected void fakeSend(final int seq) throws IOException {		
		// ������ �����κ����� �ִϸ��̼� ���
		mUi.updateBufferSlotSender(seq, BufferSlot.SENT);
		mUi.updateNextSequenceSnd();
		mUi.getFlyingPiece(seq).setY(0);
		mUi.getFlyingPiece(seq).setAck(false);
		mUi.getFlyingPiece(seq).setVisible(true);
		mUi.getBufferSlotSender(seq).startTimerAnimation(new TimerTask() {
			int times = 0;
			private final int timesMax = (BufferSlot.SEC_DELIVERY * 1000) / BufferSlot.MSEC_ANIM;
			private final double distStep = SR.DISTANCE_BETWEEN_BUFFERS / timesMax;
			@Override
			public void run() { // Ÿ�̸� �ֱ⸶�� ����
				// TODO Auto-generated method stub
				++times;
				if(times > timesMax / 2) { // ��Ŷ�� ��ü �Ÿ��� ��ݸ� �����ϰ� �ս�
					mUi.getFlyingPiece(seq).setVisible(false);
					mUi.repaint();
					mUi.getBufferSlotSender(seq).cancelTimerAnimation();
					// System.out.println("test");
					return;
				} // if
				mUi.getFlyingPiece(seq).setY((int)(distStep * times)); // �� ���� ����
				mUi.repaint();
			}
		});
		// Ÿ�Ӿƿ� ����� Ÿ�̸� ����
		mUi.getBufferSlotSender(seq).startTimerTimeout(new TimerTask() {
			@Override
			public void run() { // retransmit
				// TODO Auto-generated method stub
				try {
					mUi.retransmit();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	} // func

	// sender�� �ش� ���ȣ�� ��Ŷ�� �۽��ϸ� ����� Ÿ�̸� ������
	@Override
	protected void niceSend(final int seq) throws IOException {		
		// ������ �����κ����� �ִϸ��̼� ���
		mUi.updateBufferSlotSender(seq, BufferSlot.SENT);
		mUi.updateNextSequenceSnd();
		mUi.getFlyingPiece(seq).setY(0);
		mUi.getFlyingPiece(seq).setAck(false);
		mUi.getFlyingPiece(seq).setVisible(true);
		mUi.getBufferSlotSender(seq).startTimerAnimation(new TimerTask() {
			int times = 0;
			private final int timesMax = (BufferSlot.SEC_DELIVERY * 1000) / BufferSlot.MSEC_ANIM;
			private final double distStep = SR.DISTANCE_BETWEEN_BUFFERS / timesMax;
			@Override
			public void run() { // Ÿ�̸� �ֱ⸶�� ����
				// TODO Auto-generated method stub
				++times;
				if(times > timesMax) { // �ִϸ��̼��� ��
					try { // ������ �ִϸ��̼��� ������ ������ ��Ŷ�� ������
						byte[] serializedMessage = serialization(new Packet(false, seq));
						mSocket.send(
								new DatagramPacket(
									serializedMessage, serializedMessage.length, 
									mAddrReciever, PORT_RECEIVER
								)
							);
						// System.out.println("sent");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					mUi.getFlyingPiece(seq).setVisible(false);
					mUi.repaint();
					mUi.getBufferSlotSender(seq).cancelTimerAnimation();
					// System.out.println("test");
					return;
				} // if
				mUi.getFlyingPiece(seq).setY((int)(distStep * times)); // �� ���� ����
				mUi.repaint();
			}
		});
		// Ÿ�Ӿƿ� ����� Ÿ�̸� ����
		mUi.getBufferSlotSender(seq).startTimerTimeout(new TimerTask() {
			@Override
			public void run() { // retransmit
				// TODO Auto-generated method stub
				try {
					mUi.retransmit();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
	} // func
	
} // public class
