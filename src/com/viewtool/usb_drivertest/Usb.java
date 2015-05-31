package com.viewtool.usb_drivertest;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;
public class Usb extends BroadcastReceiver {
	MainActivity execactivity;
	public static final int USB_STATE_ON = 0x21;
	public static final int USB_STATE_OFF = 0x22;
	public IntentFilter filter = new IntentFilter();
	public Usb(Context context) {
		execactivity = (MainActivity) context;
		filter.addAction(Intent.ACTION_MEDIA_CHECKING);
		filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
		filter.addAction(Intent.ACTION_MEDIA_EJECT);
		filter.addAction(Intent.ACTION_MEDIA_REMOVED);
		filter.addDataScheme("file");
	}
	public Intent registerReceiver() {
		return execactivity.registerReceiver(this, filter);
	}
	public void unregisterReceiver() {
		execactivity.unregisterReceiver(this);
	}
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		if (execactivity.mHandler == null) {
			return;
		}
		Message msg = new Message();
		if (intent.getAction().equals(Intent.ACTION_MEDIA_MOUNTED)
				|| intent.getAction().equals(Intent.ACTION_MEDIA_CHECKING)) {
			msg.arg1 = USB_STATE_ON;
		} else {
			msg.arg1 = USB_STATE_OFF;
		}
		execactivity.mHandler.sendMessage(msg);
	};
}