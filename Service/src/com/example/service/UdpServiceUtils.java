package com.example.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;





public class UdpServiceUtils {
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
	private static UdpServiceUtils mUdpServiceUtils;
	public static UdpServiceUtils getInstence(){
		if (mUdpServiceUtils == null) {
			synchronized (UdpServiceUtils.class) {
				if (mUdpServiceUtils == null) {
					mUdpServiceUtils = new UdpServiceUtils();
				}
			}
		}
		return mUdpServiceUtils;
	}
	public String reciveUpdData() throws Exception {
			try {
				DatagramSocket server = new DatagramSocket(mPort);
				byte[] recvBuf = new byte[100];
				DatagramPacket recvPacket = new DatagramPacket(recvBuf,
						recvBuf.length);
				server.receive(recvPacket);
				String recvStr = new String(recvPacket.getData(), 0,
						recvPacket.getLength());
				// System.out.println(recvStr);
				server.close();
				return recvStr;
			} catch (Exception e) {
				throw e;
			}
	}
}
