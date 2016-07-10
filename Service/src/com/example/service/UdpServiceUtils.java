package com.example.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;





public class UdpServiceUtils {
	private String mServiceIP;
	private static int mPort = 9999;
	static DatagramSocket server;
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
		initServer();
		if (mUdpServiceUtils == null) {
			synchronized (UdpServiceUtils.class) {
				if (mUdpServiceUtils == null) {
					mUdpServiceUtils = new UdpServiceUtils();
				}
			}
		}
		return mUdpServiceUtils;
	}
	private static void initServer() {
		if (server == null) {
			synchronized (UdpServiceUtils.class) {
				if (mUdpServiceUtils == null) {
					mUdpServiceUtils = new UdpServiceUtils();
				}
			}
		}
		try {
			server = new DatagramSocket(mPort);
		} catch (SocketException e) {
			e.printStackTrace();
		}
		
	}

	@SuppressWarnings("finally")
	public String reciveUpdData() {
			String recvStr = "";
			try {
				
				byte[] recvBuf = new byte[100];
				DatagramPacket recvPacket = new DatagramPacket(recvBuf,
						recvBuf.length);
				server.receive(recvPacket);
				recvStr = new String(recvPacket.getData(), 0,
						recvPacket.getLength());
				// System.out.println(recvStr);
			} catch (Exception e) {
				e.printStackTrace();
			}finally{
				return recvStr;
			}
			
	}
}
