package jag.kumamoto.gotochi.counter;

import java.util.Date;

import jag.kumamoto.apps.gotochi.PrefecturesActivityBase;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.AbsoluteLayout;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;
import android.os.Handler; 
import android.os.Message;
import android.util.Log;
import android.widget.FrameLayout;
//テストのために追加　後で削除
/*
 * TODO:後で削除
 */
import android.app.DatePickerDialog;
import android.widget.DatePicker;
public class CounterActivity extends PrefecturesActivityBase {
	private TextView CntText;
	AbsoluteLayout.LayoutParams params2=null;
	Integer[][] StationXY;
	Integer Spoint = 25;
	Long TempD = 12L;
	float Lation = 1.1f;
	int mCount = 0;
	private Integer ScleWidth = 0;
	private Integer ScleHeight = 0;
	Integer Pointx =  (300/13)*2;
	//テスト用日付
	/*
	 * TODO:後で削除
	 */
	String SetToday = "2011/3/1" ;
	/*
	 * TODO:推奨されてないやり方
	 */
	
    private Long lngXday = new Date("2011/3/12").getTime();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    	Log.v("cnt","tes1");
        
		Toast.makeText(this, 
				"ご当地アプリは" + (isGotochiApp() ? "有効" : "無効") + "です。",
				Toast.LENGTH_SHORT).show();
		
        
        initializeComponents();
        loopcounter.start();
    }
    private void getLayoutScle(int w ,int h){ScleHeight = h;ScleWidth=w;}
	
   
    private void initializeComponents() {

		Context context = this;
		//StationXY[0][0to11] = X座標 StationXY[1][0to11]=Y座標　StationXY[1][12] = 画像累進倍率
		StationXY = MkStationXy();
		LinearLayout layouthrzntl1 = new LinearLayout(context);
		LinearLayout layouthrzntl2 = new LinearLayout(context);
		LinearLayout layoutvrtcl = new LinearLayout(context);
		layouthrzntl1.setOrientation(LinearLayout.HORIZONTAL);
		layouthrzntl2.setOrientation(LinearLayout.HORIZONTAL);
		layoutvrtcl.setOrientation(LinearLayout.VERTICAL);
		this.setContentView(layoutvrtcl);
		LinearLayout.LayoutParams params1;
		params1 = new LinearLayout.LayoutParams(
				LinearLayout.LayoutParams.WRAP_CONTENT,
				LinearLayout.LayoutParams.WRAP_CONTENT);
		//テストのため
		/*
    	 * TODO:後で削除
    	 */
		Button btnS = new Button(context);
		btnS.setText("日付をセット(テスト用");


		btnS.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				showDialog(1);

			}
		});
			
		layoutvrtcl.addView(btnS);
		
		layoutvrtcl.addView(layouthrzntl1);
		TextView TitleText = new TextView(context);
		TitleText.setText("九州新幹線全線開通まで");
		TitleText.setTextSize(40);

		layouthrzntl1.addView(TitleText,params1);
		CntText = new TextView(context);
		CntText.setTextSize(50);
		CntText.setBackgroundColor(Color.WHITE);
		CntText.setTextColor(Color.RED);
		layoutvrtcl.addView(layouthrzntl2);
		layouthrzntl2.addView(CntText,params1);

    	FrameLayout FlmLayout = new FrameLayout(context){
    		@Override
    		protected void  onSizeChanged(int w, int h, int oldw, int oldh) {
    	    	getLayoutScle(w,h);
    		}
    	};
    	layoutvrtcl.addView(FlmLayout);
    	/*
    	 * TODO:後でパラメータをクラス化
    	 */
    	ScleWidth = 320;
    	ScleHeight = 157;
    	if (params2==null){
	    	params2 = new AbsoluteLayout.LayoutParams(
	    			40,40,(ScleWidth*StationXY[0][0])/StationXY[0][12],(ScleHeight*StationXY[1][0])/StationXY[1][12]-Spoint);
    	}
    	LinearLayout.LayoutParams params3 = new LinearLayout.LayoutParams(
    			LinearLayout.LayoutParams.FILL_PARENT,
    			LinearLayout.LayoutParams.FILL_PARENT);
    	Log.v("CNT13",Integer.toString(ScleWidth));
    	Log.v("CNT14",Integer.toString(ScleHeight));

    	
    	ImageView TrainImg = new ImageView(context);
    	ImageView RailImg  = new ImageView(context);
    	AbsoluteLayout AbsLayout = new AbsoluteLayout(context);
    	TrainImg.setImageResource(R.drawable.train);
    	RailImg.setImageResource(R.drawable.rail);
    	FlmLayout.addView(RailImg,params1);
    	FlmLayout.addView(AbsLayout,params1);
    	AbsLayout.addView(TrainImg,params2);
    	Log.v("CNT15",Integer.toString(ScleWidth));
    	Log.v("CNT16",Integer.toString(ScleHeight));
    	//FlmLayout.addView(MkTrainAnime(context,FlmLayout.getWidth(),FlmLayout.getHeight()));
		//layout.addView(stopButton);

		

    }
    @Override protected boolean onLocationChange(Context context, Intent intent) {
    	++mCount;
    	
    	if(mCount == 1) {
	    	//Toast.makeText(this, "一度目のインテントでは終了させない" , Toast.LENGTH_SHORT).show();
	    	return true;
    	} else {
	    	//Toast.makeText(this, "二度目のインテントなので終了する" , Toast.LENGTH_SHORT).show();
    		return false;
    	}
    }
    //一定時間後にupdateを呼ぶためのオブジェクト
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
                Long[] TimeLeft = new Long[3];
                if(this.isUpdate){
                	TimeLeft = CalTimeLeft();
                	//TODO:拡張可能に書き換える
                	if (TimeLeft[3] == 1L ){
                	 
                	 if (TimeLeft[0] <= 10L){
	                	 if (TempD.equals(TimeLeft[0])){
	                		 AbstLayParamUpdate(TimeLeft[0]);
	                		 TempD = TimeLeft[0];
	                	 }else{
	                		 AbstLayParamUpdate(TimeLeft[0]);
	                		 TempD = TimeLeft[0];
	                	 }
	                }else{
	                		 AbstLayParamUpdate(11);
	                }
                	 
                	 CntText.setText(Long.toString(TimeLeft[0]) + "日" +
                			 Long.toString(TimeLeft[1]) + "時" +
                			 Long.toString(TimeLeft[2]) + "分" 
                			 // + Long.toString(TimeLeft[1]) + "秒"
                			 );
                	}else{
                		OpeningAfter();
                		this.stop();
                	}
                	 sendMessageDelayed(obtainMessage(0), 60000);//60000ミリ秒後にメッセージを出力
                }
 
        }
        private void OpeningAfter() {
        	
        	AbstLayParamUpdate(0L);
        	CntText.setText("00日00時00分" );
        }
        private Long[] CalTimeLeft(){
        	/*
        	 * 日：RtnTLefts[0]　時：RtnTLefts[1] 分：RtnTLefts[2]
        	 *  秒（未使用）：RtnTLefts[3]
        	 * をそれぞれ管理する
        	 */
        	Long[] RtnTLefts = new Long[4];
        	/*
        	 * TODO:後で削除
        	 */
        	//Long lngNow = new Date().getTime();
        	Long lngNow = new Date(SetToday).getTime();
        	//TODO 型の違いを学習
        	final Integer ONEDAY  = 1000 * 60 * 60 * 24;
        	final Integer ONEHOUR  = 1000 * 60 * 60;
        	final Integer ONEMIN  = 1000 * 60;
        	//未使用
        	final Integer ONESEC  = 1000;

        	for(int i = 0; i<=3;i++){
        		RtnTLefts[i] = 0L;
        	}
        	//残り時間をミリ秒で算出
        	if(lngXday - lngNow > 0L){
        		Log.v("CNTAA",lngXday + "," + lngNow+ "," + (lngXday - lngNow));
        		Long diffday = (lngXday - lngNow);
        		RtnTLefts[0] = diffday / ONEDAY;
            	RtnTLefts[1] = (diffday - RtnTLefts[0]*ONEDAY )/ ONEHOUR;
            	RtnTLefts[2] = (diffday - RtnTLefts[0]*ONEDAY - RtnTLefts[1]*ONEHOUR) / ONEMIN ;
            	//超過フラグ　0:超過 1:未到達
            	RtnTLefts[3] = 1L;
        	}

        	return RtnTLefts;
        }
    };
    private LoopCounter loopcounter = new LoopCounter();
    private void AbstLayParamUpdate(long OrderofDay){
    	int i = 11 - (int)OrderofDay;
    	float ThisLate = (float)Math.pow(Lation, i);
    	Log.v("TEST",Float.toString(ThisLate));
    	
    	params2.x = (ScleWidth*StationXY[0][i])/StationXY[0][12];
    	params2.y = (int)Math.round((ScleHeight*StationXY[1][i])/StationXY[1][12]- ThisLate*Spoint);

    	params2.width = (int)ThisLate*40;
    	params2.height = (int)ThisLate*40;
    	initializeComponents();
    	
    }
    /*
	 * TODO:後で削除
	 */
    //テストのために作成
    protected Dialog onCreateDialog(int id){
    	if(id == 1){
    		DatePickerDialog Dialog = new  DatePickerDialog(this ,
					 new DatePickerDialog.OnDateSetListener() {
						public void onDateSet(DatePicker view,int year ,int monthOfYear,int dayOfMonth){
							SetToday = Integer.toString(year) + "/" + Integer.toString(monthOfYear+1) + "/" +  Integer.toString(dayOfMonth);
							loopcounter.start();
						}
					},
					2011,
					2,
					1
			){};
			return Dialog;
    	}
    	return null;
    }
    /**
     * TODO :後でXML
     * より取得に修正
     * 倍率も包含
     */
    private Integer[][] MkStationXy(){
    	Integer[][] stationxy = new Integer[2][13];
    	stationxy[0][0]   = 452	;
    	stationxy[0][1]   = 414	;
    	stationxy[0][2]   = 377	;
    	stationxy[0][3]   = 339	;
    	stationxy[0][4]   = 302	;
    	stationxy[0][5]   = 264	;
    	stationxy[0][6]   = 226	;
    	stationxy[0][7]   = 189	;
    	stationxy[0][8]   = 151	;
    	stationxy[0][9]   = 114   ;
    	stationxy[0][10]  = 76	;
    	stationxy[0][11]  = 38	;
    	stationxy[0][12]  = 486;
    	stationxy[1][0]   =  18    ;
    	stationxy[1][1]   = 36     ;
    	stationxy[1][2]   = 54     ;
    	stationxy[1][3]   = 72     ;
    	stationxy[1][4]   = 90     ;
    	stationxy[1][5]   =     108;
    	stationxy[1][6]   = 126    ;
    	stationxy[1][7]   = 144    ;
    	stationxy[1][8]   = 162    ;
    	stationxy[1][9]   = 180    ;
    	stationxy[1][10]  =     198;
    	stationxy[1][11]  = 216    ;
    	stationxy[1][12]  =234;
    	return stationxy;
    	}
}