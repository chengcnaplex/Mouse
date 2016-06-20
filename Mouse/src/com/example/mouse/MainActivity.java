package com.example.mouse;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;

import com.example.mouse.WaterWave.MouseListener;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Canvas;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;

public class MainActivity extends Activity {
	public static final int BROADCAST_PORT = 10006;
	public static final int BROADCAST_RES_PORT = 10007;
	public static final int MESSAGE_PORT = 5000;
	// 默认为255.255.255.255 知道远程被控制端的ip之后可以指定ip
	// 要知道指定ip参照 请访问https://github.com/chengcnaplex/AndroidUdpDemo
	public String ServiceIP = "255.255.255.255";

	public byte[] SingleTap = { 1 };
	public byte[] DoubleTap = { 2 };
	public byte[] RightSingleTap = { 5 };
	// 单点触控down坐标
	public WaterWave mWaterWave;
	public UdpUtils mUtils ;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		//初始化UdpUtils
		mUtils= UdpUtils.getInstence();
		mUtils.setmServiceIP(ServiceIP);
		mUtils.setPort(MESSAGE_PORT);
		setContentView(R.layout.activity_main);
		mWaterWave = (WaterWave)findViewById(R.id.waterWave);
		mWaterWave.setOnMouseListener(new MouseListener() {
			@Override
			public void onTwoPoitScroll(float distanceY) {
				mUtils.sendMessage(Arrays.toString(intToByteArray_ScrollY((int) distanceY)));		
			}
			
			@Override
			public void onThreeUp() {
				Intent intent = new Intent(MainActivity.this, SettingActivity.class);
				startActivity(intent);
			}
			
			@Override
			public void onPointMove(float distanceX, float distanceY) {
				mUtils.sendMessage(Arrays.toString(intToMoveByteArray(
						(int) distanceX, (int) distanceY)));	
			}
			
			@Override
			public void onOnePointDoubleClick() {
				mUtils.sendMessage(Arrays.toString(DoubleTap));		
			}
			
			@Override
			public void onOnePointClick() {
				mUtils.sendMessage(Arrays.toString(SingleTap));	
			}

			@Override
			public void onTwoPoitXMove() {
				mUtils.sendMessage(Arrays.toString(RightSingleTap));					
			}
		});
	}
	public static byte[] intToMoveByteArray(int x, int y) {
		// 必须把我们要的值弄到最低位去，有人说不移位这样做也可以， result[0] = (byte)(i & 0xFF000000);
		// ，这样虽然把第一个字节取出来了，但是若直接转换为byte类型，会超出byte的界限，出现error。再提下数
		// 之间转换的原则（不管两种类型的字节大小是否一样，原则是不改变值，内存内容可能会变，比如int转为
		// float肯定会变）所以此时的int转为byte会越界，只有int的前三个字节都为0的时候转byte才不会越界。
		// 虽然 result[0] = (byte)(i & 0xFF000000); 这样不行，但是我们可以这样 result[0] =
		// (byte)((i & //0xFF000000) >>24);
		byte[] result = new byte[9];
		result[0] = (byte) (3);
		result[1] = (byte) ((x >> 24) & 0xFF);
		result[2] = (byte) ((x >> 16) & 0xFF);
		result[3] = (byte) ((x >> 8) & 0xFF);
		result[4] = (byte) (x & 0xFF);
		result[5] = (byte) ((y >> 24) & 0xFF);
		result[6] = (byte) ((y >> 16) & 0xFF);
		result[7] = (byte) ((y >> 8) & 0xFF);
		result[8] = (byte) (y & 0xFF);
		return result;
	}
//git config --global push.default matching
	//git config --global push.default simple
	public static byte[] intToByteArray_ScrollY(int distanceY) {
		// 必须把我们要的值弄到最低位去，有人说不移位这样做也可以， result[0] = (byte)(i & 0xFF000000);
		// ，这样虽然把第一个字节取出来了，但是若直接转换为byte类型，会超出byte的界限，出现error。再提下数
		// 之间转换的原则（不管两种类型的字节大小是否一样，原则是不改变值，内存内容可能会变，比如int转为
		// float肯定会变）所以此时的int转为byte会越界，只有int的前三个字节都为0的时候转byte才不会越界。
		// 虽然 result[0] = (byte)(i & 0xFF000000); 这样不行，但是我们可以这样 result[0] =
		// (byte)((i & //0xFF000000) >>24);
		byte[] result = new byte[5];
		result[0] = (byte) (4);
		result[1] = (byte) ((distanceY >> 24) & 0xFF);
		result[2] = (byte) ((distanceY >> 16) & 0xFF);
		result[3] = (byte) ((distanceY >> 8) & 0xFF);
		result[4] = (byte) (distanceY & 0xFF);
		return result;
	}

	public class UdpThread implements Runnable {
		private String message;

		public UdpThread(String message) {
			this.message = message;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			byte[] b = message.getBytes();
			try {
				DatagramSocket client = new DatagramSocket();
				InetAddress addr = InetAddress.getByName(ServiceIP);
				DatagramPacket sendPacket = new DatagramPacket(b, b.length,
						addr, MESSAGE_PORT);
				client.send(sendPacket);
				client.close();
			} catch (Exception e) {
				Log.e("MainActivity", e.toString());
				// Toast.makeText(getBaseContext(), e.toString(),
				// Toast.LENGTH_SHORT).show();
			}
		}
	};
}
