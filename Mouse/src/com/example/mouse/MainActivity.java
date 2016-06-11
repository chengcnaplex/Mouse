package com.example.mouse;


import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.Arrays;



import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;

public class MainActivity extends Activity {
	public static final int BROADCAST_PORT = 10006; 
	public static final int BROADCAST_RES_PORT = 10007;
	public static final int MESSAGE_PORT = 5000;
	//默认为255.255.255.255 知道远程被控制端的ip之后可以指定ip
	//要知道指定ip参照 请访问https://github.com/chengcnaplex/AndroidUdpDemo 
	public String ServiceIP = "255.255.255.255";
	GestureDetector mGestureDetector;
	public byte[] SingleTap = {1};
	public byte[] DoubleTap = {2};
	//单点触控down坐标
	float start_x = 0;
	float start_y = 0;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		mGestureDetector = new GestureDetector(MainActivity.this,new MouseTapListener());
		mGestureDetector.setOnDoubleTapListener(new MouseDoubleTapListener());
	}
	private class MouseTapListener implements OnGestureListener{

		@Override
		public boolean onDown(MotionEvent e) {
			start_x = e.getX();
			start_y = e.getY();
			Log.d("tag", " startx"+ start_x );
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {
			
		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			//new Thread(new UdpThread(SingleTap.toString())).start();
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			//Log.e("MainActivity",distanceX + "  " + distanceY);
			
			//new Thread(new UdpThread(Arrays.toString(intToMoveByteArray((int)distanceX,(int)distanceY)))).start();
			return false;
		}

		@Override
		public void onLongPress(MotionEvent e) {
			
		}

		@Override
		public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX,
				float velocityY) {
			return false;
		}
		
	}
	private class MouseDoubleTapListener implements OnDoubleTapListener{

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Log.e("MainActivity","onSingleTapConfirmed");
			new Thread(new UdpThread(Arrays.toString(SingleTap))).start();
			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Log.e("MainActivity","onDoubleTap");
			new Thread(new UdpThread(Arrays.toString(DoubleTap))).start();
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}
		
	}
	public static byte[] intToMoveByteArray(int x ,int y) {   
		//必须把我们要的值弄到最低位去，有人说不移位这样做也可以， result[0] = (byte)(i  & 0xFF000000);
		//，这样虽然把第一个字节取出来了，但是若直接转换为byte类型，会超出byte的界限，出现error。再提下数
		//之间转换的原则（不管两种类型的字节大小是否一样，原则是不改变值，内存内容可能会变，比如int转为
		//float肯定会变）所以此时的int转为byte会越界，只有int的前三个字节都为0的时候转byte才不会越界。
		//虽然 result[0] = (byte)(i  & 0xFF000000); 这样不行，但是我们可以这样 result[0] = (byte)((i  & //0xFF000000) >>24);
		byte[] result = new byte[9];   
		result[0] = (byte)(3);
		result[1] = (byte)((x >> 24) & 0xFF);
		result[2] = (byte)((x >> 16) & 0xFF);
		result[3] = (byte)((x >> 8) & 0xFF); 
		result[4] = (byte)(x & 0xFF);
		result[5] = (byte)((y >> 24) & 0xFF);
		result[6] = (byte)((y >> 16) & 0xFF);
		result[7] = (byte)((y >> 8) & 0xFF); 
		result[8] = (byte)(y & 0xFF);
		return result;
	}
	public static byte[] intToByteArray_ScrollY(int distanceY) {   
		//必须把我们要的值弄到最低位去，有人说不移位这样做也可以， result[0] = (byte)(i  & 0xFF000000);
		//，这样虽然把第一个字节取出来了，但是若直接转换为byte类型，会超出byte的界限，出现error。再提下数
		//之间转换的原则（不管两种类型的字节大小是否一样，原则是不改变值，内存内容可能会变，比如int转为
		//float肯定会变）所以此时的int转为byte会越界，只有int的前三个字节都为0的时候转byte才不会越界。
		//虽然 result[0] = (byte)(i  & 0xFF000000); 这样不行，但是我们可以这样 result[0] = (byte)((i  & //0xFF000000) >>24);
		byte[] result = new byte[5];   
		result[0] = (byte)(4);
		result[1] = (byte)((distanceY >> 24) & 0xFF);
		result[2] = (byte)((distanceY >> 16) & 0xFF);
		result[3] = (byte)((distanceY >> 8) & 0xFF); 
		result[4] = (byte)(distanceY & 0xFF);
		return result;
	}
	public class UdpThread implements Runnable{
		private String message;
		public UdpThread(String message){
			this.message = message;
		}
		@Override
		public void run() {
			// TODO Auto-generated method stub     
			byte[] b=message.getBytes();     
			try{
				DatagramSocket client = new DatagramSocket();
		        InetAddress addr = InetAddress.getByName(ServiceIP);
		        DatagramPacket sendPacket 
		            = new DatagramPacket(b , b.length , addr , MESSAGE_PORT);
		        client.send(sendPacket);
		        client.close();      
			}catch(Exception e){       
				Log.e("MainActivity",e.toString());
				//Toast.makeText(getBaseContext(), e.toString(),  Toast.LENGTH_SHORT).show();       
			}     
		} 	      
	};
	/*  
     * 处理触屏事件  
     */  
    @Override  
    public boolean onTouchEvent(MotionEvent event) {
    	boolean consume = false;
    	if(checkNetworkAvailable(this)){
    		//单点触控down坐标
        	
        	
    		//单点触控move坐标
        	float x;
        	float y;
        	
        	//双点触控距离
        	float distanceX;
        	float distanceY;
        	
        	//双点触控down坐标
        	float start_x_1 = 0;
        	float start_y_1 = 0;
        	float start_x_2 = 0;
        	float start_y_2 = 0;
        	
        	//双点触控move坐标
        	float x_1;
        	float y_1;
        	float x_2;
        	float y_2;
        	
        	//双点触控距离
        	float distanceX_1;
        	float distanceY_1;
        	float distanceX_2;
        	float distanceY_2;
        	
        	// 获得屏幕触点数量  
            int pointerCount = event.getPointerCount(); 
            switch (pointerCount) {
    	        //一点触控
    			case 1:
    				if(!checkNetworkAvailable(this.getApplicationContext())){
                    	break;
                    }
    				consume = mGestureDetector.onTouchEvent(event);
    				switch (event.getAction()){
    					case MotionEvent.ACTION_UP:
    						start_x = 0;
    			        	start_y = 0;
    						break;
    					case MotionEvent.ACTION_MOVE:
    						x = event.getX();
    						y = event.getY();
    						distanceX = x - start_x;
    						distanceY = y - start_y;
    						new Thread(new UdpThread(Arrays.toString(intToMoveByteArray((int)x, (int)y)))).start();
    						Log.d("tag", "ACTION_MOVE x = " + distanceX +" startx"+ start_x + ", y = " + distanceY);
    						break;
    				}
    			break;
    			//两点触控
    			case 2:
    				
    				if(!checkNetworkAvailable(this.getApplicationContext())){
                    	break;
                    }
    				switch (event.getAction()&MotionEvent.ACTION_MASK) {
    					case MotionEvent.ACTION_POINTER_DOWN:
    						
    						start_x_1 = event.getX(0);
    	    	        	start_y_1 = event.getY(0);
    	    	        	start_x_2 = event.getX(1);
    	    	        	start_y_2 = event.getY(1);
    						//new Thread(new UdpThread(point.toString())).start();
    						break;
    					case MotionEvent.ACTION_MOVE:
    				
    						x_1 = event.getX(0);
    	    				y_1 = event.getY(0);
    	    				x_2 = event.getX(1);
    	    				y_2 = event.getY(1);
    	    				
    	    				distanceX_1 = x_1 - start_x_1;
    	    				distanceY_1 = y_1 - start_y_1;
    	    				distanceX_2 = x_2 - start_x_2;
    	    				distanceY_2 = y_2 - start_y_2;
    	    				//如果1触点的滑动间距大于触点2
    	    				if(Math.abs(distanceY_1)>Math.abs(distanceY_2)){
    	    					new Thread(new UdpThread(Arrays.toString(intToByteArray_ScrollY((int)distanceY_1)))).start();
    	    				}
    	    				
    						
    						break;
    					case MotionEvent.ACTION_POINTER_UP:
//    						Log.d("tag", "ACTION_MOVE x_1 = " + x_1 + ", y_1 = " + y_1);
//    						Log.d("tag", "ACTION_MOVE x_2 = " + x_2 + ", y_2 = " + y_2);
//    						point.x_1 = x_1;
//    						point.y_1 = y_1;
//    						point.x_2 = x_2;
//    						point.y_2 = y_2;
    						//new Thread(new UdpThread(point.toString())).start();
    						break;
    					default:
    						break;
    				}
    				consume = true;
    				break;
    			//三点触控		
    			case 3:
    				switch(event.getAction()&MotionEvent.ACTION_MASK){
    					case MotionEvent.ACTION_POINTER_UP:
    						Intent intent = new Intent(this, SettingActivity.class);
    						startActivity(intent);
    				}
    				consume = true;
    				break;
    	
    			default:
    				break;
    		}
    	}
        return consume;  
    }
    public boolean checkNetworkAvailable(Context context) {
		ConnectivityManager connectivity = (ConnectivityManager) context
				.getSystemService(Context.CONNECTIVITY_SERVICE);
		if (connectivity == null) {
			return false;
		} else {
			NetworkInfo[] info = connectivity.getAllNetworkInfo();
			if (info != null) {
				for (int i = 0; i < info.length; i++) {
					if (info[i].getState() == NetworkInfo.State.CONNECTED) {
						NetworkInfo netWorkInfo = info[i];
						if (netWorkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
							return true;
						} else if (netWorkInfo.getType() == ConnectivityManager.TYPE_MOBILE) {
							return true;
						}
					}
				}
			}
		}
		return false;
    }
}
