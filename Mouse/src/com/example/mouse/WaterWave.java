package com.example.mouse;

import java.util.ArrayList;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.RadialGradient;
import android.graphics.Shader;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.OnDoubleTapListener;
import android.view.GestureDetector.OnGestureListener;
import android.view.MotionEvent;
import android.widget.ImageView;





/**
 * Created by aplex on 16-6-15.
 */
public class WaterWave extends ImageView {
    Paint paint;
    GestureDetector mGestureDetector;
    ArrayList<Raindrop> raindrops;
 // 双点触控down坐标 用于计算每个event的偏移量
 	float start_x_1 = 0;
 	float start_y_1 = 0;
 	float start_x_2 = 0;
 	float start_y_2 = 0;

 	// 双点触控down坐标 用于计算离up的时候距离down的偏移量
 	float start_x_1_all = 0;
 	float start_y_1_all = 0;
 	float start_x_2_all = 0;
 	float start_y_2_all = 0;

 	// 双点触控end坐标
 	float end_x_1 = 0;
 	float end_x_2 = 0;
 	float end_y_1 = 0;
 	float end_y_2 = 0;
    public WaterWave(Context context) {
        this(context, null);
    }

    public WaterWave(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public WaterWave(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        paint = new Paint(Paint.ANTI_ALIAS_FLAG);

        raindrops = new ArrayList<Raindrop>();
        mGestureDetector = new GestureDetector(context,
				new MouseTapListener());
		mGestureDetector.setOnDoubleTapListener(new MouseDoubleTapListener());
        new Thread(new Runnable() {

            ArrayList<Raindrop> newRaindrops;

            @Override
            public void run() {
                while (true) {

                    newRaindrops = new ArrayList<Raindrop>();
                    for (int i = 0; i < raindrops.size(); i++) {
                        Raindrop raindrop = raindrops.get(i);
                        if (raindrop.isInRadius()) {
                            raindrop.increaseRadius(3);
                            newRaindrops.add(raindrop);
                        }
                    }
                    raindrops = newRaindrops;

                    postInvalidate();

                    try {
                        Thread.sleep(20);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        for (Raindrop raindrop : raindrops)
            raindrop.drawRaindrop(canvas, paint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
    	for (int i = 0; i < event.getPointerCount(); i++ )
            raindrops.add(new Raindrop((int) event.getX(i), (int) event.getY(i), 1));
    	
    	boolean consume = false;
		if (checkNetworkAvailable(this.getContext())) {
			Log.e("WaterWave", event.getPointerCount()+"");

			// 双点触控move坐标
			float x_1;
			float y_1;
			float x_2;
			float y_2;

			// 双点触控距离
			float distanceX_1;
			float distanceY_1;
			float distanceX_2;
			float distanceY_2;
//
//			// 获得屏幕触点数量
			int pointerCount = event.getPointerCount();			
			switch (pointerCount) {
			// 一点触控
			case 1:
				// 单点触控用GestureDetector
				consume = mGestureDetector.onTouchEvent(event);
				break;
			// 两点触控
			case 2:
				// 双点触控用
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_POINTER_DOWN:
					// 双指down 坐标 用于计算每个event的偏移量
					start_x_1 = event.getX(0);
					start_y_1 = event.getY(0);
					start_x_2 = event.getX(1);
					start_y_2 = event.getY(1);

					// 双指down 坐标 用于计算up的时候距离down的偏移量
					// 双点触控down坐标
					start_x_1_all = event.getX(0);
					start_y_1_all = event.getY(0);
					start_x_2_all = event.getX(1);
					start_y_2_all = event.getY(1);
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

					start_x_1 = x_1;
					start_y_1 = y_1;
					start_x_2 = x_2;
					start_y_2 = y_2;
					if (isScroll(distanceX_1, distanceY_1, distanceX_2,
							distanceY_2)) {
						// 如果1触点的滑动间距大于触点2
						if (Math.abs(distanceY_1) > Math.abs(distanceY_2)) {
							// 滑动y轴绝对值大于3才传值
							if (Math.abs(distanceY_1) >= 3) {
								//发送滚轮数据
								if (mMouseListener != null) {
									mMouseListener.onTwoPoitScroll(distanceY_1);;
								}
							}

						} else {
							// 滑动y轴绝对值大于3才传值
							if (Math.abs(distanceY_2) >= 3) {
								//发送滚轮数据
								if (mMouseListener != null) {
									mMouseListener.onTwoPoitScroll(distanceY_2);;
								}
							}
						}
					} else {

					}

					break;
				case MotionEvent.ACTION_POINTER_UP:
					float end_x_1_all = event.getX(0);
					float end_y_1_all = event.getY(0);
					float end_x_2_all = event.getX(1);
					float end_y_2_all = event.getY(1);
					float offsetx = (start_x_1_all - end_x_1_all)
							+ (start_x_2_all - end_x_2_all);
					float offsety = (start_y_1_all - end_y_1_all)
							+ (start_y_2_all - end_y_2_all);
					//双指左右滑动触发右击，改为双指点击触发鼠标右键
					if (Math.abs(offsetx)+Math.abs(offsety) < 6) {
						if (mMouseListener != null) {
							mMouseListener.onTwoPoitXMove();
						}
					}
					//双手左右滑动
//					if (Math.abs(offsetx) > Math.abs(offsety)) {
//						//发送右击事件
//						
//					}
					break;
				default:
					break;
				}
				consume = true;
				break;
			// 三点触控
			case 3:
				switch (event.getAction() & MotionEvent.ACTION_MASK) {
				case MotionEvent.ACTION_POINTER_UP:
					//启动配置信息
					//Intent intent = new Intent(this, SettingActivity.class);
					//startActivity(intent);
					if (mMouseListener != null) {
						mMouseListener.onThreeUp();
					}
				}
				consume = true;
				break;

			default:
//				break;
			}
		}
		return true;
    }

    /*
    @Override
    public boolean onTouch(View v, MotionEvent event) {

        //raindrops.add(new Raindrop((int)event.getX(), (int)event.getY(), 1));
        for (int i = 0; i < event.getPointerCount(); i++ ) {
            raindrops.add(new Raindrop((int) event.getX(i), (int) event.getY(i), 1));
        }
        Log.e("WaterWave", "" + event.getPointerCount());

        return false;
    }
    */
    private class MouseTapListener implements OnGestureListener {

		@Override
		public boolean onDown(MotionEvent e) {
			return false;
		}

		@Override
		public void onShowPress(MotionEvent e) {

		}

		@Override
		public boolean onSingleTapUp(MotionEvent e) {
			return false;
		}

		@Override
		public boolean onScroll(MotionEvent e1, MotionEvent e2,
				float distanceX, float distanceY) {
			if (mMouseListener != null) {
				mMouseListener.onPointMove(distanceX, distanceY);
			}
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

	private class MouseDoubleTapListener implements OnDoubleTapListener {

		@Override
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Log.e("MainActivity", "onSingleTapConfirmed");
			if (mMouseListener != null) {
				mMouseListener.onOnePointClick();
			}
			return false;
		}

		@Override
		public boolean onDoubleTap(MotionEvent e) {
			Log.e("MainActivity", "onDoubleTap");
			if (mMouseListener != null) {
				mMouseListener.onOnePointDoubleClick();
			}
			return false;
		}

		@Override
		public boolean onDoubleTapEvent(MotionEvent e) {
			return false;
		}

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
	private boolean isScroll(float distanceX_1, float distanceY_1,
			float distanceX_2, float distanceY_2) {
		if (Math.abs(distanceX_1) + Math.abs(distanceX_2) < Math
				.abs(distanceY_1) + Math.abs(distanceY_2)) {
			return true;
		}
		return false;
	}
	public MouseListener mMouseListener;
	public void setOnMouseListener(MouseListener mouseListener){
		if(mMouseListener == null){
			mMouseListener = mouseListener;
		}
	}
	public interface MouseListener{
		public void onOnePointClick();
		public void onOnePointDoubleClick();
		public void onPointMove(float distanceX,float distanceY);
		public void onTwoPoitScroll(float distanceY);
		public void onTwoPoitXMove();
		public void onThreeUp();
	}
}

class Raindrop {

    int x;
    int y;
    int currentRadius = 1;
    int maxRadius = 300;
    float rate = 0.03f;

    Raindrop(int x, int y, int radius) {
        this.x = x;
        this.y = y;
        this.currentRadius = radius;
    }

    public boolean isInRadius() {
        return maxRadius > currentRadius;
    }

    public void increaseRadius(int radius) {
        this.currentRadius = this.currentRadius + radius + (int)(this.currentRadius * rate);
    }

    public void drawRaindrop(Canvas canvas, Paint paint) {
        RadialGradient radialGradient = new RadialGradient(x, y, currentRadius, new int[]{0x00f5f5f5, 0x88dfdfdf, 0x00f5f5f5}, new float[]{0.4f, 0.7f, 1.0f }, Shader.TileMode.CLAMP);
        paint.setShader(radialGradient);
        canvas.drawCircle(x, y, currentRadius, paint);

        paint.reset();
    }
}
