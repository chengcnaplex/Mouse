package com.example.service;

import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class RemoteService {
	public static final int MESSAGE_PORT = 5000;
	public static void main(String[] args) {
		reciveUpdData();
	}
	public static void reciveUpdData(){
		while (true) {
			try {
				DatagramSocket server = new DatagramSocket(MESSAGE_PORT);
		        byte[] recvBuf = new byte[100];
		        DatagramPacket recvPacket
		            = new DatagramPacket(recvBuf,recvBuf.length);
		        server.receive(recvPacket);
		        String recvStr = new String(recvPacket.getData() , 0 , recvPacket.getLength());
		        System.out.println(recvStr);
		        server.close();
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}
}
