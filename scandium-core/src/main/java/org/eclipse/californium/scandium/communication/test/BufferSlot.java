package org.eclipse.californium.scandium.communication.test;


import java.util.Timer;
import java.util.TimerTask;

public class BufferSlot {
	public final static int EMPTY = 0;
	// sender
	public final static int SENT = 1; // �̹� �����: �������� ack�� ��ٸ��� ����
	public final static int ACKED = 2; // ��� �� ���� �� ��: ack �޾� ������ ����
	// receiver
	public final static int BUFFERED = 3; // Ȯ�� �����: �޾����� �� ���ȣ�� ��ٸ����� ���常 �Ǿ� ����
	public final static int RECEIVED = 4; // Ȯ�� �����: �����찡 �Ѿ�� ���� �Ϸ�Ǿ����� Ȯ���� ��
	
	private int mState;
	
	public final static int SEC_TIMEOUT = 10;
	public final static int SEC_DELIVERY = 3;
	public final static int MSEC_ANIM = 100;
	private Timer mTimerTimeout = null;	// sender only
	private Timer mTimerAnimation = null;
	
	public BufferSlot() { 
		mState = BufferSlot.EMPTY; 
	}
	
	public int getState() { return mState; }
	public void setState(int state) { mState = state; } 
	
	// ����: Ÿ�̸Ӵ� ��ȸ���̱� ������ �� �� ��ҵǸ� ���̻� �������� �� ���. ���� �Ұ����ϴ�.
	public void startTimerTimeout(TimerTask onTimeout) {
		if(mTimerTimeout != null) { cancelTimerTimeout(); }
		mTimerTimeout = new Timer();
		mTimerTimeout.schedule(onTimeout, SEC_TIMEOUT * 1000);
	}
	public void cancelTimerTimeout() { if(mTimerTimeout != null) { mTimerTimeout.cancel(); } }

	public void startTimerAnimation(TimerTask onTimeAnimate) {
		if(mTimerAnimation != null) { cancelTimerAnimation(); }
		mTimerAnimation = new Timer();
		mTimerAnimation.scheduleAtFixedRate(onTimeAnimate, 0, MSEC_ANIM);
	}
	public void cancelTimerAnimation() { if(mTimerAnimation != null) { mTimerAnimation.cancel(); } }
	
} // class

