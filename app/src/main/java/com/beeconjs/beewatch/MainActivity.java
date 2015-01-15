package com.beeconjs.beewatch;

import android.app.Activity;
import android.os.Bundle;
import android.support.wearable.view.WatchViewStub;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;

import android.os.Handler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import beeconjar.BEECODE;
import beeconjar.Bee;
import beeconjar.BeeButton;
import beeconjar.BeeCallback;
import beeconjar.HoneyComb;

/*
android original article url : http://arabiannight.tistory.com/32
 */

public class MainActivity extends Activity {

    private TextView mTextView;
    private Handler mHandler;
    private NumberThread mNumberThread;
    private Switch mSwitch;
    private int healthData = 0;

    int uniqueKey = 2015011111;
    Bee watchBee;
    HoneyComb honeyComb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final WatchViewStub stub = (WatchViewStub) findViewById(R.id.watch_view_stub);
        stub.setOnLayoutInflatedListener(new WatchViewStub.OnLayoutInflatedListener() {
            @Override
            public void onLayoutInflated(WatchViewStub stub) {
                mHandler= new Handler();

                mTextView = (TextView) stub.findViewById(R.id.text);
                mSwitch = (Switch)stub.findViewById(R.id.switch1);

                mSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                    @Override
                    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                        if(isChecked)
                        {
                            mSwitch.setText("ON");
                            mNumberThread = new NumberThread(true);
                            mNumberThread.start();
                        }
                        else
                        {
                            mSwitch.setText("OFF");
                            if(mNumberThread!=null)
                                mNumberThread.stopThread();

                            mNumberThread = null;
                        }
                    }
                });
            }
        });

        honeyComb = new HoneyComb();

        honeyComb.setRegion(BEECODE.DEFAULT);

        BeeCallback beeCallback = new BeeCallback() {
            @Override
            public void onFindBee(Bee bee) {
                // TODO Auto-generated method stub
                super.onFindBee(bee);

                if(bee.key==uniqueKey)
                {
                    watchBee = bee;
                }
            }

            @Override
            public void onClickBee(JSONObject jsonObject) {
                // TODO Auto-generated method stub
                super.onClickBee(jsonObject);

                try {
                    String BEECODE = jsonObject.getString("BEECODE");
                    if(BEECODE.equals(watchBee.BEECODE))
                    {
                        JSONObject hey = new JSONObject();
                        hey.put("lowData", healthData);
                        honeyComb.heyBee(watchBee.BEECODE, hey);

                        mHandler.post(new Runnable() {
                            @Override
                            public void run() {
                                healthData=0;
                            }
                        });
                    }
                } catch (JSONException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };

        honeyComb.setBeeCallback(beeCallback);
        honeyComb.hangOn();

    }

    class NumberThread extends Thread {

        private boolean isPlay = false;

        public NumberThread(boolean isPlay){
            this.isPlay = isPlay;
        }

        public void stopThread(){
            isPlay = !isPlay;
        }
        @Override
        public void run() {
            super.run();
            while (isPlay) {

                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mTextView.setText(""+healthData++);
                    }
                });

                try { Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }



}
