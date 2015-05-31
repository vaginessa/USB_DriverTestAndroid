package com.viewtool.usb_drivertest;

import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;

import com.viewtool.USBDriver.ErrorType;
import com.viewtool.USBDriver.UsbDriver;

public class MainActivity extends Activity {
	StringBuffer mStringBuffer_Console_Text = new StringBuffer("Show Info:\n");

	// 权限
	private static final String ACTION_USB_PERMISSION = "com.viewtool.ginkgotest.USB_PERMISSION";
	// 接口类
	UsbDriver mUsbDriver;
	UsbManager mUsbManager;
	UsbDevice mUsbDevice;
	// 界面控件
	PendingIntent pendingIntent;
	Button mButtonScan;
	Button mButtonOpen;
	Button mButtonInit;
	Button mButtonFilter;
	Button mButtonStar;
	Button mButtonStar1;
	
	Button mButtonReset;
	TextView mTextView_ShowConsole;
	// usb监听
	MyHandler mHandler;
	Usb usbstates;

	class MyHandler extends Handler {
		public MyHandler() {
		};

		@Override
		public void handleMessage(Message msg) {
			super.handleMessage(msg);
			if (msg.arg1 == 0x21) {
				System.out.println("USB 连接");
			} else if (msg.arg1 == 0x22) {
				System.out.println("USB 断开");
			}
		}
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mHandler = new MyHandler();
		usbstates = new Usb(MainActivity.this);
		usbstates.registerReceiver();

		UsbManager mUsbManager = (UsbManager) getSystemService(Context.USB_SERVICE);

		pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);
		IntentFilter filter = new IntentFilter(ACTION_USB_PERMISSION);
		registerReceiver(mUsbReceiver, filter);
		mButtonStar = (Button) findViewById(R.id.btn_Star);
		mButtonStar1 = (Button) findViewById(R.id.btn_Sta2);
		mTextView_ShowConsole = (TextView) findViewById(R.id.ShowConsole);
		set_Listener();

	}

	private void set_Listener() {
		// TODO Auto-generated method stub
		mButtonStar.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				config_usb(1);
			}
		});
		mButtonStar1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                config_usb(2);
            }
        });
	}

	/**
	 * 配置usb
	 */
	private void config_usb(int ep) {
		// TODO Auto-generated method stub
		mUsbManager = (UsbManager) getSystemService(MainActivity.USB_SERVICE);
		pendingIntent = PendingIntent.getBroadcast(this, 0, new Intent(
				ACTION_USB_PERMISSION), 0);

		mUsbDriver = new UsbDriver(mUsbManager, pendingIntent);
		mUsbDevice = mUsbDriver.ScanDevices();

		if (mUsbDevice != null) {
			mStringBuffer_Console_Text.append("Find device sucessful.\n");
			mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
			run_set(ep);
		} else {
			mStringBuffer_Console_Text.append("No device connected.\n");
			mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
			return;
		}
		// run_set();
	}

	/**
	 * 运行加载
	 */
	private void run_set(int ep) {
		int ret;
		// TODO Auto-generated method stub

		ret = mUsbDriver.OpenDevice();
		if (ret != ErrorType.ERR_SUCCESS) {
			System.out.println("Open device error.");
			mStringBuffer_Console_Text.append("Open device error.\n");
			mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
			return;
		} else {
			System.out.println("Open device sucessful");
			mStringBuffer_Console_Text.append("BulkInEndpoint[0] "+mUsbDriver.BulkInEndpoint[0].getAddress()+"\n");
			mStringBuffer_Console_Text.append("BulkInEndpoint[1] "+mUsbDriver.BulkInEndpoint[1].getAddress()+"\n");
			mStringBuffer_Console_Text.append("BulkOutEndpoint[0] "+mUsbDriver.BulkOutEndpoint[0].getAddress()+"\n");
			mStringBuffer_Console_Text.append("BulkOutEndpoint[1] "+mUsbDriver.BulkOutEndpoint[1].getAddress()+"\n");
			
			mStringBuffer_Console_Text.append("Open device sucessful.\n");
			mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
			byte[] WriteData = new byte[64];
			for (int i = 0; i < WriteData.length; i++) {
				WriteData[i] = (byte) (i + 1);
			}
			byte[] ReadData = new byte[64];
			if(ep==1){
			    mStringBuffer_Console_Text.append("\n-------------EP1------------\n");
	            mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
	            
	            int i_USBWriteData_ep1 = mUsbDriver.USBWriteData(Config.EP1_OUT,
	                    WriteData, 64, 500);
	            if (i_USBWriteData_ep1 != 64) {
	                System.out.println("USBWriteData error");
	                mStringBuffer_Console_Text.append("USBWriteData error.\n");
	                mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
	            } else {
	                System.out.println("USBWriteData sucessful");
	                mStringBuffer_Console_Text.append("USBWriteData sucessful.\n");
	                mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
	            }
	            
	            int i_USBReadData_ep1 = mUsbDriver.USBReadData(Config.EP1_IN, ReadData,
	                    64, 500);
	            if (i_USBReadData_ep1 != 64) {
	                System.out.println("USBReadData error");
	                mStringBuffer_Console_Text.append("USBReadData error.\n");
	                mStringBuffer_Console_Text.append("error code = "
	                        + i_USBReadData_ep1 + ".\n");
	                mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
	            } else {
	                System.out.println("USBReadData sucessful");
	                mStringBuffer_Console_Text.append("USBReadData sucessful.\n");
	                mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
	                mStringBuffer_Console_Text.append("Data : \n");
	                for (int i = 0; i < ReadData.length; i++) {
	                    Integer.toHexString(ReadData[i]);
	                    mStringBuffer_Console_Text.append(Integer
	                            .toHexString(ReadData[i]) + " ");
	                    mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
	                }           
	            }
			}else {
			    mStringBuffer_Console_Text.append("\n-------------EP2------------\n");
	            mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
	            int i_USBWriteData_ep2 = mUsbDriver.USBWriteData(Config.EP2_OUT,
	                    WriteData, 64, 500);
	            if (i_USBWriteData_ep2 != 64) {
	                System.out.println("USBWriteData error");
	                mStringBuffer_Console_Text.append("USBWriteData error.\n");
	                mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
	            } else {
	                System.out.println("USBWriteData sucessful");
	                mStringBuffer_Console_Text.append("USBWriteData sucessful.\n");
	                mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
	            }
	            
	            int i_USBReadData_ep2 = mUsbDriver.USBReadData(Config.EP2_IN, ReadData,
	                    64, 500);
	            if (i_USBReadData_ep2 != 64) {
	                System.out.println("USBReadData error");
	                mStringBuffer_Console_Text.append("USBReadData error.\n");
	                mStringBuffer_Console_Text.append("error code = "
	                        + i_USBReadData_ep2 + ".\n");
	                mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
	            } else {
	                System.out.println("USBReadData sucessful");
	                mStringBuffer_Console_Text.append("USBReadData sucessful.\n");
	                mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
	                mStringBuffer_Console_Text.append("Data : \n");
	                for (int i = 0; i < ReadData.length; i++) {
	                    Integer.toHexString(ReadData[i]);
	                    mStringBuffer_Console_Text.append(Integer
	                            .toHexString(ReadData[i]) + " ");
	                    mTextView_ShowConsole.setText(mStringBuffer_Console_Text);
	                }           
	            }
            }
			
			
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	// private static final String ACTION_USB_PERMISSION =
	// "com.android.example.USB_PERMISSION";
	private final BroadcastReceiver mUsbReceiver = new BroadcastReceiver() {
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (ACTION_USB_PERMISSION.equals(action)) {
				synchronized (this) {
					UsbDevice device = (UsbDevice) intent
							.getParcelableExtra(UsbManager.EXTRA_DEVICE);
					if (intent.getBooleanExtra(
							UsbManager.EXTRA_PERMISSION_GRANTED, false)) {
						System.out.println("Authorization");
						if (mUsbDevice != null) {
							// run_set();
						}
					} else {
						System.out.println("不给权限");
						return;
					}
				}
			}
		}
	};
}
