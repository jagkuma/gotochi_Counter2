
package jag.kumamoto.gotochi.counter;

import java.util.Date;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.*;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;

import android.graphics.Paint.FontMetrics;
import android.graphics.drawable.BitmapDrawable; 

public class CounterActivity extends Activity {
    /** Called when the activity is first created. */
    /** 画面描画用 View */
	static final  int DIALOG_PAUSED_ID = 1;
    android.os.Handler handler = new android.os.Handler();
    int WindowHeight = 0;
    int WindowWidth = 0;
	int ScleWidth = 0;
	int ScleHeight = 0;
	ImageView BackGroundImg;
	MyTrainView train ;
	int x1=0;
	int y1=0;
	FrameLayout FR;
	Float rate;

	LoopCounter loopcounter = new LoopCounter();
	Long lngXday = new Date("2011/3/12").getTime();
	Long TempD = 0L;
	Long[] TimeLeft = new Long[4];
	MyveilView veil;
	MyCeremonyBallView ball;
	MyCountView myimg;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        InitParam();
        Display display = getDisplay();
        WindowHeight = display.getHeight();
        WindowWidth = display.getWidth();
        
        FR = new FrameLayout(getApplication());        

        setContentView(FR);
        BackGroundImg = new ImageView(getApplication());
        BackGroundImg.setImageResource(R.drawable.counter_bg);
    	BackGroundImg.setScaleType(ImageView.ScaleType.FIT_CENTER);
        FR.addView(BackGroundImg);

    }
    private Display getDisplay(){
    	WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
    	Display display = wm.getDefaultDisplay();
    	return display;
    }
   @Override
	public void onWindowFocusChanged(boolean hasFocus){
		super.onWindowFocusChanged(hasFocus);
		scontinue();
	}
   private void scontinue() { 
	     FR.removeAllViews();//子ビューをすべて消去 

	    ScleWidth = BackGroundImg.getMeasuredWidth(); 
	    ScleHeight = BackGroundImg.getMeasuredHeight(); 
	    Rect rect = fitting(480,800,ScleWidth,ScleHeight); 
	    //背景の配置基準座標を取得 
	    x1 = rect.left; 
	    y1 = rect.top; 
	    //配置時の縮小倍率取得 
	    rate =  ((float)rect.right-rect.left)/480.0f; 
	    myimg = new MyCountView(getApplication(),x1,y1,rate); 
	    train = new MyTrainView(getApplication(),x1,y1,rate); 
	    ball= new MyCeremonyBallView(getApplication(),x1,y1,rate); 
	    veil = new MyveilView(getApplication(),x1,y1,WindowWidth,WindowHeight,rate); 
	    FR.addView(BackGroundImg);//追加しなおす 
	    FR.addView(myimg); 
	    FR.addView(train); 
	    FR.addView(ball); 
	    FR.addView(veil); 
	    Resources res = getResources();
	    //スレッドスタート 
	    loopcounter.start(); 
   } 
   class LoopCounter extends Handler {
    	
	    private boolean isUpdate;
	    public void start(){
            this.isUpdate = true;
            handleMessage(new Message());
	    }
	    public void stop(){
            this.isUpdate = false;
	    }
	    @Override
	    public void handleMessage(Message msg) {
            this.removeMessages(0);//既存のメッセージは削除
            if(this.isUpdate){
            	TimeLeft = CalTimeLeft();

            	if (TimeLeft[3] == 2L ){
            		myimg.invalidate();
            		/*
            		 * TODO:削除してよいかも
            		 */
	            	if (TimeLeft[0] <= 10L){
	                	 if (TempD.equals(TimeLeft[0])){
	                	 }else{
	                		 TempD = TimeLeft[0];
	                		 train.invalidate();
	                	 }
	                }else{
	                }
            	}else{
            		OpeningAfter();
            		this.stop();
            	}
            	 sendMessageDelayed(obtainMessage(0), 60000);//60000ミリ秒後にメッセージを出力
            }
	    }
        private Long[] CalTimeLeft(){
	    	/*
	    	 * 日：RtnTLefts[0]　時：RtnTLefts[1] 分：RtnTLefts[2]
	    	 *  秒（未使用）：RtnTLefts[3]
	    	 * をそれぞれ管理する
	    	 */
	    	Long[] RtnTLefts = new Long[4];
	    	Long lngNow = new Date().getTime();

	    	final int ONEDAY  = 1000 * 60 * 60 * 24;
	    	final int ONEHOUR  = 1000 * 60 * 60;
	    	final int ONEMIN  = 1000 * 60;
	    	//未使用　今後の拡張のため
	    	final int ONESEC  = 1000;

	    	for(int i = 0; i<=3;i++){
	    		RtnTLefts[i] = 0L;
	    	}
	    	
	    	//残り時間をミリ秒で算出
	    	if(lngXday - lngNow > 0L){
	    		Long diffday = (lngXday - lngNow);
	    		RtnTLefts[0] = diffday / ONEDAY;
	        	RtnTLefts[1] = (diffday - RtnTLefts[0]*ONEDAY )/ ONEHOUR;
	        	RtnTLefts[2] = (diffday - RtnTLefts[0]*ONEDAY - RtnTLefts[1]*ONEHOUR) / ONEMIN ;
	        	//超過フラグ　1:超過 2:未到達
	        	RtnTLefts[3] = 2L;
	    	}else{
	    		RtnTLefts[3] = 1L;
	    		RtnTLefts[0] = 0L;
	        	RtnTLefts[1] = 0L;
	        	RtnTLefts[2] = 0L ;

	    	}
	    	return RtnTLefts;
	    }
    }
    private void InitParam() {
    	TimeLeft[0] = 0L;
    	TimeLeft[1] = 0L;
    	TimeLeft[2] = 0L;
    	TimeLeft[3] = 0L;
    }
    private int Longtoint(Long Lv){
    	int rtnint = 0;
    	if (Lv == 0L){
    		rtnint  = 0;
    	}
    	if (Lv == 1L){
    		rtnint  = 1;
    	}
    	if (Lv == 2L){
    		rtnint  = 2;
    	}
    	if (Lv == 3L){
    		rtnint  = 3;
    	}
    	if (Lv == 4L){
    		rtnint  = 4;
    	}
    	if (Lv == 5L){
    		rtnint  = 5;
    	}
    	if (Lv == 6L){
    		rtnint  = 6;
    	}
    	if (Lv == 7L){
    		rtnint  = 7;
    	}
    	if (Lv == 8L){
    		rtnint  = 8;
    	}
    	if (Lv == 9L){
    		rtnint  = 9;
    	}

    	return rtnint;
    }
    private Rect fitting(int srcWidth, int srcHeight, int destWidth, int destHeight) { 
		int rectWidth, rectHeight; 
		int rectTop, rectLeft; 
		if (destWidth < srcWidth || destHeight < srcHeight) { 
			if (srcWidth < srcHeight) { 
				if (destWidth < destHeight) { 
					int height = (int) (destWidth * (srcHeight / (float) srcWidth)); 
					//大きさが小さいほうを出力
					rectHeight = (height > destHeight) ? destHeight : height; 
					rectWidth = (int) (rectHeight * (srcWidth / (float) srcHeight)); 
				} else { 

					rectWidth = (int) (destHeight * (srcWidth / (float) srcHeight)); 
					rectHeight = (int) (rectWidth * (srcHeight / (float) srcWidth)); 
				} 
			} else { 

				if (destWidth < destHeight) { 
					rectHeight = (int) (destWidth * (srcHeight / (float) srcWidth)); 
					rectWidth = (int) (rectHeight * (srcWidth / (float) srcHeight)); 
				} else { 

					int width = (int) (destHeight * (srcWidth / (float) srcHeight)); 
					rectWidth = (width > destWidth) ? destWidth : width; 
					rectHeight = (int) (rectWidth * (srcHeight / (float) srcWidth)); 
				} 
			} 

			rectLeft = (destWidth - rectWidth) / 2; 
			rectTop = (destHeight - rectHeight) / 2; 
		} else { 

			rectLeft = (destWidth - srcWidth) / 2; 
			rectTop = (destHeight - srcHeight) / 2; 
			rectWidth = srcWidth; 
			rectHeight = srcHeight; 
		} 

		return new Rect(rectLeft, rectTop, rectLeft + rectWidth, rectTop + rectHeight); 
    }
    class MyCountView extends View {
    	float BaseX;
    	float BaseY;
    	float BaseRate;
    	BitmapDrawable tempbitmap;
    	BitmapDrawable bitmapN0;
    	BitmapDrawable bitmapN1;
    	BitmapDrawable bitmapN2;
    	BitmapDrawable bitmapN3;
    	BitmapDrawable bitmapN4;
    	BitmapDrawable bitmapN5;
    	BitmapDrawable bitmapN6;
    	BitmapDrawable bitmapN7;
    	BitmapDrawable bitmapN8;
    	BitmapDrawable bitmapN9;
	    
    	public MyCountView(Context context,float x1,float y1,float rate) {  
	      super(context); 
	      BaseX = x1;
	      BaseY = y1;
	      BaseRate = rate;
	      
	      // BitmapDrawable を作成  
	      bitmapN0 = (BitmapDrawable) context.getResources().getDrawable(R.drawable.number0);
	      bitmapN1 = (BitmapDrawable) context.getResources().getDrawable(R.drawable.number1);
	      bitmapN2 = (BitmapDrawable) context.getResources().getDrawable(R.drawable.number2);
	      bitmapN3 = (BitmapDrawable) context.getResources().getDrawable(R.drawable.number3);
	      bitmapN4 = (BitmapDrawable) context.getResources().getDrawable(R.drawable.number4);
	      bitmapN5 = (BitmapDrawable) context.getResources().getDrawable(R.drawable.number5);
	      bitmapN6 = (BitmapDrawable) context.getResources().getDrawable(R.drawable.number6);
	      bitmapN7 = (BitmapDrawable) context.getResources().getDrawable(R.drawable.number7);
	      bitmapN8 = (BitmapDrawable) context.getResources().getDrawable(R.drawable.number8);
	      bitmapN9 = (BitmapDrawable) context.getResources().getDrawable(R.drawable.number9);
	    }  
	    @Override  
	    public void onDraw(Canvas canvas) {
	      Float[][] PointXY = MakeList();
	      Long[] buff = new Long[6];
	      for (int i=0;i<3;i++){
	    	  if (TimeLeft[i] < 10L){  
	    		  buff[2*i] = 0L;
	    		  buff[2*i+1]  =  TimeLeft[i];
	    	  } else{
	    		  buff[2*i] =  TimeLeft[i] / 10;
	    		  buff[2*i+1]  =  TimeLeft[i] - buff[2*i]*10;
	    	  }
	      }
	      for(int i=0;i<6;i++){
	      }
	      for(int i=0;i< PointXY[0].length;i++){
	    	  
	    	  switch(Longtoint(buff[i])){
	    	
				case 0:
					bitmapN0.setBounds(Math.round(PointXY[0][i]*BaseRate+x1), Math.round(PointXY[1][i]*BaseRate+y1),
						Math.round((PointXY[0][i]+ PointXY[2][i])*BaseRate+x1), Math.round((PointXY[1][i]+PointXY[3][i])*BaseRate+y1));
					bitmapN0.draw(canvas);
					break;
	    	  	case 1:
	    	  		bitmapN1.setBounds(Math.round(PointXY[0][i]*BaseRate+x1), Math.round(PointXY[1][i]*BaseRate+y1),
							Math.round((PointXY[0][i]+ PointXY[2][i])*BaseRate+x1), Math.round((PointXY[1][i]+PointXY[3][i])*BaseRate+y1));
					bitmapN1.draw(canvas);
					break;
	    	  	case 2:
	    	  		bitmapN2.setBounds(Math.round(PointXY[0][i]*BaseRate+x1), Math.round(PointXY[1][i]*BaseRate+y1),
							Math.round((PointXY[0][i]+ PointXY[2][i])*BaseRate+x1), Math.round((PointXY[1][i]+PointXY[3][i])*BaseRate+y1));
					bitmapN2.draw(canvas);
					break;
	    	  	case 3:
	    	  		bitmapN3.setBounds(Math.round(PointXY[0][i]*BaseRate+x1), Math.round(PointXY[1][i]*BaseRate+y1),
							Math.round((PointXY[0][i]+ PointXY[2][i])*BaseRate+x1), Math.round((PointXY[1][i]+PointXY[3][i])*BaseRate+y1));
					bitmapN3.draw(canvas);
					break;
	    	  	case 4:
	    	  		bitmapN4.setBounds(Math.round(PointXY[0][i]*BaseRate+x1), Math.round(PointXY[1][i]*BaseRate+y1),
							Math.round((PointXY[0][i]+ PointXY[2][i])*BaseRate+x1), Math.round((PointXY[1][i]+PointXY[3][i])*BaseRate+y1));
					bitmapN4.draw(canvas);
					break;
	    	  	case 5:
	    	  		bitmapN5.setBounds(Math.round(PointXY[0][i]*BaseRate+x1), Math.round(PointXY[1][i]*BaseRate+y1),
							Math.round((PointXY[0][i]+ PointXY[2][i])*BaseRate+x1), Math.round((PointXY[1][i]+PointXY[3][i])*BaseRate+y1));
					bitmapN5.draw(canvas);
					break;
	    	  	case 6:
	    	  		bitmapN6.setBounds(Math.round(PointXY[0][i]*BaseRate+x1), Math.round(PointXY[1][i]*BaseRate+y1),
							Math.round((PointXY[0][i]+ PointXY[2][i])*BaseRate+x1), Math.round((PointXY[1][i]+PointXY[3][i])*BaseRate+y1));
					bitmapN6.draw(canvas);
					break;
	    	  	case 7:
	    	  		bitmapN7.setBounds(Math.round(PointXY[0][i]*BaseRate+x1), Math.round(PointXY[1][i]*BaseRate+y1),
							Math.round((PointXY[0][i]+ PointXY[2][i])*BaseRate+x1), Math.round((PointXY[1][i]+PointXY[3][i])*BaseRate+y1));
					bitmapN7.draw(canvas);
					break;
	    	  	case 8:
	    	  		bitmapN8.setBounds(Math.round(PointXY[0][i]*BaseRate+x1), Math.round(PointXY[1][i]*BaseRate+y1),
							Math.round((PointXY[0][i]+ PointXY[2][i])*BaseRate+x1), Math.round((PointXY[1][i]+PointXY[3][i])*BaseRate+y1));
					bitmapN8.draw(canvas);

					break;
	    	  	default:
	    	  		bitmapN9.setBounds(Math.round(PointXY[0][i]*BaseRate+x1), Math.round(PointXY[1][i]*BaseRate+y1),
							Math.round((PointXY[0][i]+ PointXY[2][i])*BaseRate+x1), Math.round((PointXY[1][i]+PointXY[3][i])*BaseRate+y1));
					bitmapN9.draw(canvas);
					break;
				
	    	  		
	    	  }
	      }
	    }
	    private Float[][] MakeList(){
	    	Float[][] XY = new Float[4][6];
	    	XY[0][0] = 212F;XY[1][0] = 235F;XY[2][0] = 46F;XY[3][0] = 79F;
	    	XY[0][1] = 265F;XY[1][1] = 235F;XY[2][1] = 46F;XY[3][1] = 79F;
	    	XY[0][2] = 103F;XY[1][2] = 324F;XY[2][2] = 38F;XY[3][2] = 70F;
	    	XY[0][3] = 146F;XY[1][3] = 324F;XY[2][3] = 38F;XY[3][3] = 70F;
	    	XY[0][4] = 248F;XY[1][4] = 323F;XY[2][4] = 38F;XY[3][4] = 70F;
	    	XY[0][5] = 291F;XY[1][5] = 323F;XY[2][5] = 38F;XY[3][5] = 70F;
	    	return XY;
	    }
	    
  }  
       class MyTrainView extends View {
    	   float BaseX;
    	   float BaseY;
    	   float BaseRate;
    	   BitmapDrawable bitmapDrawable2; 
	    public MyTrainView(Context context,float x1,float y1,float rate) {  
	      super(context); 
	      BaseX = x1;
	      BaseY = y1;
	      BaseRate = rate;
	      
	      // BitmapDrawable を作成  
	      bitmapDrawable2 = (BitmapDrawable) context.getResources().getDrawable(R.drawable.counter_n700);
	      
	    }  
	    @Override  
	    public void onDraw(Canvas canvas) {
	    	Float[][] PointXY = MakeList();

	    	int i = 3;
	    	if(TimeLeft[3] == 1L){
	    		i=0;	
	    	}else if (TimeLeft[0] <=3L)
	    	{
	    		i=1;
	    	}else if (TimeLeft[0] <=5L)
	    	{
	    		i= 2;
	    	}

			bitmapDrawable2.setBounds(Math.round(PointXY[0][i]*BaseRate+x1), Math.round(PointXY[1][i]*BaseRate+y1),
			    		  		Math.round((PointXY[0][i]+ PointXY[2][i])*BaseRate+x1), Math.round((PointXY[1][i]+PointXY[3][i])*BaseRate+y1));  
	    	// BitmapDrawable の描画  
	    	bitmapDrawable2.draw(canvas);
	    	
	    }

	    private Float[][] MakeList(){
	    	Float[][] XY = new Float[4][4];
	    	XY[0][0] = 129F;XY[1][0] = 652F;XY[2][0] = 435F;XY[3][0] = 153F;
	    	XY[0][1] = 276F;XY[1][1] = 648F;XY[2][1] = 395F;XY[3][1] = 130F;
	    	XY[0][2] = 367F;XY[1][2] = 648F;XY[2][2] = 354F;XY[3][2] = 116F;
	    	XY[0][3] = 423F;XY[1][3] = 650F;XY[2][3] = 288F;XY[3][3] = 95F;	    	
	    	return XY;
	    }
	    
  }  
   class MyCeremonyBallView extends View {
	   float BaseX;
	   float BaseY;
	   float BaseRate;
	   BitmapDrawable bitmapDrawable;
	   Context context; 
	   Float PointXY[][];
	   public MyCeremonyBallView(Context context,float x1,float y1,float rate) {  
		      super(context); 
		      BaseX = x1;
		      BaseY = y1;
		      BaseRate = rate;
		      this.context = context;

	    }  
	    @Override  
	    public void onDraw(Canvas canvas) {
	    	PointXY = MakeList();
	    	//開通後クス玉が開いた状態になる
	    	if (TimeLeft[3].equals(1L)){
			      // BitmapDrawable を作成  
			      bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.counter_kusudama_2);
			      bitmapDrawable.setBounds(Math.round(PointXY[0][0]*BaseRate+x1), Math.round(PointXY[1][0]*BaseRate+y1),
		    		  		Math.round((PointXY[0][0]+ PointXY[2][0])*BaseRate+x1), Math.round((PointXY[1][0]+PointXY[3][0])*BaseRate+y1));
	    	}else{
			      bitmapDrawable = (BitmapDrawable) context.getResources().getDrawable(R.drawable.counter_kusudama_1);
			      bitmapDrawable.setBounds(Math.round(PointXY[0][1]*BaseRate+x1), Math.round(PointXY[1][1]*BaseRate+y1),
		    		  		Math.round((PointXY[0][1]+ PointXY[2][1])*BaseRate+x1), Math.round((PointXY[1][1]+PointXY[3][1])*BaseRate+y1));
	    	}
	    	
    		//BitmapDrawable の描画  
	    	bitmapDrawable.draw(canvas);
		    
        }  
	    private Float[][] MakeList(){
	    	Float[][] XY = new Float[4][2];
	    	//開いてるときの座標幅高さ
	    	XY[0][0] = 240F;XY[1][0] = 430F;XY[2][0] = 214F;XY[3][0] = 232F;
	    	//閉じてるときの座標幅高さ
	    	XY[0][1] = 292F;XY[1][1] = 440F;XY[2][1] = 113F;XY[3][1] = 112F;
	    	return XY;
	    }
   }
   public class MyveilView extends View{
	   float BaseX ;
	   float BaseY;
	   float winwidth;
	   float winheight;
	   float thisrate;
	   public MyveilView(Context context,float x1,float y1,float ww,float wh,float rate) {
			super(context);
			BaseX = x1;
			BaseY = y1;
			winwidth = ww;
			winheight = wh;
			thisrate = rate;
	   }

	   protected void  onDraw(Canvas canvas){
		   super.onDraw(canvas);
		
		   Paint mPaint = new Paint();
		   mPaint.setStyle(Paint.Style.FILL);
		   mPaint.setARGB(255, 0, 0, 0);
			
		   canvas.drawRect(x1+(480*thisrate), 0,winwidth , winheight, mPaint);
		   canvas.drawRect(0, 0,x1 , winheight, mPaint);
	   }
	
    }

   	private void OpeningAfter() {
   		myimg.invalidate();
   		ball.setOnClickListener(new View.OnClickListener(){
				@Override
				public void onClick(View v) {
				//クリックされたら、ダイアログ表示用のアクティビティへ移動
					startActivity(new Intent(CounterActivity.this,SecondActivity.class));
				}
   		});

   		ball.invalidate();
   		Resources res = getResources();
   		String[] recomtext = new String[3];
   		recomtext[0] = res.getString(R.string.recomenddwnloadL1);
   		recomtext[1] = res.getString(R.string.recomenddwnloadL2);
   		recomtext[2] = res.getString(R.string.recomenddwnloadL3);
   		
   		MyTextView recommend = new MyTextView(getApplication(),x1,y1,rate,recomtext);
   		FR.addView(recommend);
	}
    public class MyTextView extends View{
    	float BaseX ;
 	    float BaseY;
 	    float thisrate;
 	    String[] thisText;
 	    
 	    public MyTextView(Context context,float x1,float y1,float rate,String[] texts) {
	 		super(context);
	 		BaseX = x1;
	 		BaseY = y1;
	 		thisText = texts;
	 		thisrate = rate;
 	    }

 	protected void  onDraw(Canvas canvas){
 		  super.onDraw(canvas);
 		  // 文字列用ペイントの生成
 		  Paint textPaint = new Paint( Paint.ANTI_ALIAS_FLAG);
 		  textPaint.setTextSize( 27*thisrate);
 		  textPaint.setColor( Color.BLUE);
 		  FontMetrics fontMetrics = textPaint.getFontMetrics();

 		  // テキストの座標
 		  float baseLeft    = 100*thisrate + x1 ;
 		  float baseTop     = 233*thisrate + y1;
 		  float baseRight   = 401*thisrate + x1;
 		  float baseBottom  = 407*thisrate + y1;
 		  float patting     = 5*thisrate;
 		  float ruff        = 5*thisrate;
 		  String[] text1    = thisText;

 		  // 文字列の幅を取得
 		  float textWidth = textPaint.measureText( text1[0]);

 		  // 文字列の幅からX座標を計算
 		  float textX = baseLeft + patting ;
 		  // 文字列の高さからY座標を計算
 		  float textY = baseTop - fontMetrics.ascent + patting;

 		  // 角なし四角のペイントの生成
 		  Paint balloonPaint = new Paint( Paint.ANTI_ALIAS_FLAG);
 		  balloonPaint.setTextSize( 27*thisrate);
 		  balloonPaint.setColor( Color.argb(198,255,255,255));

  		  // 角なし四角の描画
 		  RectF balloonRectF = new RectF( baseLeft, baseTop, baseRight, baseBottom);
 		  canvas.drawRoundRect(balloonRectF, ruff, ruff, balloonPaint);

 		  // 文字列の描画
 		  canvas.drawText( text1[0], textX, textY, textPaint);
 		  canvas.drawText( text1[1], textX, textY + fontMetrics.bottom -fontMetrics.ascent, textPaint);
 		  canvas.drawText( text1[2], textX, textY + 2*(fontMetrics.bottom -fontMetrics.ascent), textPaint);
 		 
 	   }

    }   
 
}
