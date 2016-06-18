# Mouse
##v1.0
通过手机远程发送udp数据给远程服务端，包括：
* 发送单击数据（单指单击）  
* 发送双击数据（单指双击）  
* 发送鼠标移动数据（单指滑动）  
* 发送鼠标滚轮数据（双指上下滑动）  
* 发送鼠标右键数据（双指左右滑动）

##v2.0
* 使用曾剑锋（https://github.com/AplexOS）的WaterView添加触点水波纹效果。
* 重构WaterView的代码增加事件监听
  
  ````
      实例代码
      mWaterWave = (WaterWave)findViewById(R.id.waterWave);
		mWaterWave.setOnMouseListener(new MouseListener() {
			@Override
			public void onTwoPoitScroll(float distanceY) {
				new Thread(
						new UdpThread(
								Arrays.toString(intToByteArray_ScrollY((int) distanceY))))
						.start();
				
			}
			
			@Override
			public void onThreeUp() {
				Intent intent = new Intent(MainActivity.this, SettingActivity.class);
				startActivity(intent);
			}
			
			@Override
			public void onPointMove(float distanceX, float distanceY) {
				new Thread(new UdpThread(Arrays.toString(intToMoveByteArray(
						(int) distanceX, (int) distanceY)))).start();
			}
			
			@Override
			public void onOnePointDoubleClick() {
				new Thread(new UdpThread(Arrays.toString(DoubleTap))).start();
			}
			
			@Override
			public void onOnePointClick() {
				new Thread(new UdpThread(Arrays.toString(SingleTap))).start();
				
			}

			@Override
			public void onTwoPoitYMove() {
				new Thread(new UdpThread(Arrays.toString(RightSingleTap))).start();
				
			}
		});
