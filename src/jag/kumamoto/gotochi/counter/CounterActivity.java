package jag.kumamoto.gotochi.counter;

import java.util.Date;

import jag.kumamoto.apps.gotochi.PrefecturesActivityBase;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;
import android.os.Handler; 
import android.os.Message;
import android.util.Log;
public class CounterActivity extends PrefecturesActivityBase {
	private TextView CntText;
	int mCount = 0;
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
    }
    
    private void initializeComponents() {
		Context context = this;
		
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
		//layout.addView(stopButton);
		loopcounter.start();
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
        	CntText.setText("00日00時00分" );
        }
        private Long[] CalTimeLeft(){
        	/*
        	 * 日：RtnTLefts[0]　時：RtnTLefts[1] 分：RtnTLefts[2]
        	 *  秒（未使用）：RtnTLefts[3]
        	 * をそれぞれ管理する
        	 */
        	Long[] RtnTLefts = new Long[4];
        	Long lngNow = new Date().getTime();
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
        		Log.v("CNT",lngXday + "," + lngNow+ "," + (lngXday - lngNow));
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
}