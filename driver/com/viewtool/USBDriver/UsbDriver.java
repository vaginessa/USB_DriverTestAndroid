package com.viewtool.USBDriver;

import java.util.HashMap;
import android.app.PendingIntent;
import android.hardware.usb.UsbConstants;
import android.hardware.usb.UsbDevice;
import android.hardware.usb.UsbDeviceConnection;
import android.hardware.usb.UsbEndpoint;
import android.hardware.usb.UsbInterface;
import android.hardware.usb.UsbManager;
import android.util.Log;


public class UsbDriver{
	private UsbManager usbManager;
	private UsbDevice usbDevice;
	private UsbInterface usbInterface;
	public UsbEndpoint[] BulkInEndpoint = new UsbEndpoint[2];
	public UsbEndpoint[] BulkOutEndpoint = new UsbEndpoint[2];
	private UsbDeviceConnection connection;
	private PendingIntent pendingIntent;
	public UsbDriver(UsbManager usbManager,PendingIntent pendingIntent) {
		super();
		this.usbManager = usbManager;
		this.pendingIntent = pendingIntent;
	}
	/**
	 * Scan Device
	 * @return
	 */
	public UsbDevice ScanDevices() {
		HashMap<String, UsbDevice> map = this.usbManager.getDeviceList();
		for(UsbDevice device : map.values()){
			Log.e("device", "vid:"+device.getVendorId()+"   pid:"+device.getProductId()+"   "+device.getDeviceName());
			if(1155 == device.getVendorId() && 22336 == device.getProductId()){
				usbDevice = device;
				System.out.println( "Start ScanDevices" );  
				usbManager.requestPermission(usbDevice, pendingIntent);
				return usbDevice;
			}
		}
		return null;
	}
	/**
	 * Open Device 
	 * @return
	 */
	public int OpenDevice(){
		this.usbInterface = usbDevice.getInterface(0);
		int i=0;
		int j=0;
		int k=0;
        for (i = 0; i < usbInterface.getEndpointCount(); i++) {  
            UsbEndpoint ep = usbInterface.getEndpoint(i);  
            // look for bulk endpoint
            if (ep.getType() == UsbConstants.USB_ENDPOINT_XFER_BULK) { 
                if (ep.getDirection() == UsbConstants.USB_DIR_OUT) {  
                	BulkOutEndpoint[j++] = ep;
                } else {  
                	BulkInEndpoint[k++] = ep;  
                }  
            }
        }
        if((BulkOutEndpoint[0]==null)||(BulkInEndpoint[0]==null)||(BulkOutEndpoint[1]==null)||(BulkInEndpoint[1]==null)){
        	return ErrorType.ERR_OPEN_DEVICE;
        }
		//判断是否有权限
		if(usbManager.hasPermission(usbDevice)){
			this.connection = usbManager.openDevice(usbDevice);
			if(this.connection == null){
				return ErrorType.ERR_OPEN_DEVICE;
			}else{
				this.connection.claimInterface(usbInterface, true);
			}
		}else{
			return  ErrorType.ERR_NO_PERMISSIONS;
		}
		return ErrorType.ERR_SUCCESS;
	}
	/**
	 * Write data to USB
	 * @param epNum Endpoint No.
	 * @param writebuffer Write Data buffer
	 * @param length Write Data Length
	 * @param timeout Time Out
	 * @return Write Data Length
	 */
	public int USBWriteData(int epNum,byte[] writebuffer,int length,int timeout){
		int count = 0;
		if(epNum==BulkOutEndpoint[0].getEndpointNumber()){
			count = connection.bulkTransfer(BulkOutEndpoint[0], writebuffer, length, timeout);
			if((length%64)==0){
				connection.bulkTransfer(BulkOutEndpoint[0], writebuffer, 0, timeout);
			}
		}
		if(epNum==BulkOutEndpoint[1].getEndpointNumber()){
			count = connection.bulkTransfer(BulkOutEndpoint[1], writebuffer, length, timeout);
			if((length%64)==0){
				connection.bulkTransfer(BulkOutEndpoint[0], writebuffer, 0, timeout);
			}
		}
		return count;
	}
	/**
	 * Read data From USB
	 * @param epNum Endpoint No.
	 * @param readbuffer Read Data Buffer
	 * @param length Read data length
	 * @param timeout Time out
	 * @return Read data length
	 */
	public int USBReadData(int epNum,byte[] readbuffer,int length,int timeout){
		if(epNum==(BulkInEndpoint[0].getEndpointNumber()|0x80)){
			return connection.bulkTransfer(BulkInEndpoint[0], readbuffer, length, timeout);
		}
		if(epNum==(BulkInEndpoint[1].getEndpointNumber()|0x80)){
			return connection.bulkTransfer(BulkInEndpoint[1], readbuffer, length, timeout);
		}
		return 0;
	}
}