package com.example.service;

import java.awt.AWTException;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.Arrays;

public class RemoteService {
	static Robot robot;
	static Point point;
	public static final int MESSAGE_PORT = 5000;

	public static void main(String[] args) throws AWTException,
			InterruptedException, IOException {
		// Thread.sleep(3000);
		// point = MouseInfo.getPointerInfo().getLocation();
		// System.out.println("pointx  " + point.getX() + " pointy " +
		// point.getY());
		robot = new Robot();
		reciveUpdData();
	}

	public static void reciveUpdData() {
		while (true) {
			try {
				DatagramSocket server = new DatagramSocket(MESSAGE_PORT);
				byte[] recvBuf = new byte[100];
				DatagramPacket recvPacket = new DatagramPacket(recvBuf,
						recvBuf.length);
				server.receive(recvPacket);
				String recvStr = new String(recvPacket.getData(), 0,
						recvPacket.getLength());
				// System.out.println(recvStr);
				server.close();
				byte[] recvData = getByteArraysByString(recvStr);
				System.out.println(recvData.length + "         "
						+ Arrays.toString(recvData));
				 controlMouseByDate(recvData);
			} catch (Exception e) {
				// TODO: handle exception
			}
		}
	}

	public static byte[] getByteArraysByString(String data) {
		String revc = data.replace("[", "");
		revc = revc.replace("]", "");
		if (revc.contains(",")) {
			String[] datas = revc.split(",");

			byte[] byteDatas = new byte[datas.length];

			for (int i = 0; i < datas.length; i++) {
				byteDatas[i] = Byte.valueOf(datas[i].trim());
			}
			return byteDatas;
		} else {
			byte[] byteData = { Byte.valueOf(revc) };
			return byteData;
		}
	}

	public static void controlMouseByDate(byte[] datas) {
		if (datas == null || datas.length == 0) {
			return;
		}
		switch (datas[0]) {
		// 左键单击
		case 1:
			robot.mousePress(InputEvent.BUTTON1_MASK);// 按下左键
			robot.mouseRelease(InputEvent.BUTTON1_MASK);// 释放左键
			break;

		// 左键双击
		case 2:
			robot.mousePress(InputEvent.BUTTON1_MASK);// 按下左键
			robot.mouseRelease(InputEvent.BUTTON1_MASK);// 释放左键
			robot.delay(100);// 停顿100毫秒,即0.1秒
			robot.mousePress(InputEvent.BUTTON1_MASK);// 按下左键
			robot.mouseRelease(InputEvent.BUTTON1_MASK);// 释放左键
			break;

		// 鼠标滑动;
		case 3:
			// 获得当前的鼠标坐标
			point = MouseInfo.getPointerInfo().getLocation();
			int posX = (int) point.getX();
			int posY = (int) point.getY();
			
			int offset_x = byteArrayToInt_x(datas);
			int offset_y = byteArrayToInt_y(datas);
			
			robot.mouseMove(posX - offset_x, posY - offset_y);
			break;

		// 滚轮
		case 4:
			
			int wheelAmt = byteArrayToInt_scroll(datas);
			if (wheelAmt > 0) {
				wheelAmt = 1;
			}else if(wheelAmt < 0){
				wheelAmt = -1;
			}else {
				wheelAmt = 0;
			}
			
			System.out.println(wheelAmt);
			robot.mouseWheel(wheelAmt);
			break;
		case 5:
			//右键单击
			robot.mousePress(InputEvent.BUTTON3_MASK);
            robot.mouseRelease(InputEvent.BUTTON3_MASK);
			break;
		default:
			break;
		}
	}

	public static int byteArrayToInt_x(byte[] data) {
		byte[] byte_x = new byte[4];
		byte_x[0] = data[1];
		byte_x[1] = data[2];
		byte_x[2] = data[3];
		byte_x[3] = data[4];
		
		int value;
		value = (int) ((byte_x[3] & 0xFF) | ((byte_x[2] & 0xFF) << 8)
				| ((byte_x[1] & 0xFF) << 16) | ((byte_x[0] & 0xFF) << 24));
		return value;
	}

	public static int byteArrayToInt_y(byte[] data) {
		byte[] byte_y = new byte[4];
		byte_y[0] = data[5];
		byte_y[1] = data[6];
		byte_y[2] = data[7];
		byte_y[3] = data[8];
		int value;
		value = (int) ((byte_y[3] & 0xFF) | ((byte_y[2] & 0xFF) << 8)
				| ((byte_y[1] & 0xFF) << 16) | ((byte_y[0] & 0xFF) << 24));
		return value;
	}

	public static int byteArrayToInt_scroll(byte[] data) {
		byte[] byte_scroll = new byte[4];
		byte_scroll[0] = data[1];
		byte_scroll[1] = data[2];
		byte_scroll[2] = data[3];
		byte_scroll[3] = data[4];
		int value;
		value = (int) ((byte_scroll[3] & 0xFF) | ((byte_scroll[2] & 0xFF) << 8)
				| ((byte_scroll[1] & 0xFF) << 16) | ((byte_scroll[0] & 0xFF) << 24));
		return value;
	}
}
