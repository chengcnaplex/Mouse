package com.example.mouse;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import android.util.Log;

public class UdpUtils {
	private static UdpUtils mUdpUtils;
	private String mServiceIP;
	private int mPort;
	
	public String getmServiceIP() {
		return mServiceIP;
	}

	public void setmServiceIP(String mServiceIP) {
		this.mServiceIP = mServiceIP;
	}

	public int getPort() {
		return mPort;
	}

	public void setPort(int port) {
		this.mPort = port;
	}

	public static ExecutorService mCachedThreadPool;
	static {
		if (mCachedThreadPool == null) {
			synchronized (UdpUtils.class) {
				if (mCachedThreadPool == null) {
					mCachedThreadPool = Executors.newCachedThreadPool();
				}
			}
		}
	}
	public static UdpUtils getInstence(){
		if (mUdpUtils == null) {
			synchronized (UdpUtils.class) {
				if (mUdpUtils == null) {
					mUdpUtils = new UdpUtils();
				}
			}
		}
		return mUdpUtils;
	}
	public void sendMessage(String serviceIP, int port, String message) {
		mCachedThreadPool.execute(new UdpThread(message, serviceIP, port));
	}
	public void sendMessage(String message){
		mCachedThreadPool.execute(new UdpThread(message, mServiceIP, mPort));
	}
	public static class UdpThread implements Runnable {
		private String message;
		private String serviceIP;
		private int port;
		public UdpThread(String message,String serviceIP,int port) {
			this.message = message;
			this.serviceIP =serviceIP;
			this.port = port;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			byte[] b = message.getBytes();
			try {
				DatagramSocket client = new DatagramSocket();
				InetAddress addr = InetAddress.getByName(serviceIP);
				DatagramPacket sendPacket = new DatagramPacket(b, b.length,
						addr, port);
				client.send(sendPacket);
				client.close();
			} catch (Exception e) {
				Log.e("MainActivity", e.toString());
			}
		}
	};
}
