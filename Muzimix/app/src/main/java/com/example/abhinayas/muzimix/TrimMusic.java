package com.example.abhinayas.muzimix;

import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.concurrent.TimeUnit;

public class TrimMusic extends AppCompatActivity {
    public Button play, start, stop;
    public MediaPlayer trimTrack;
    public SeekBar mSeekBar;
    public String nameVal;
    private Handler myHandler = new Handler();


    private double startTime = 0;
    private double finalTime = 0;
    public static int oneTimeOnly = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trim_page);

        play = (Button) findViewById(R.id.play);
        start = (Button) findViewById(R.id.start);
        stop = (Button) findViewById(R.id.stop);

        final TextView tx1 = (TextView) findViewById(R.id.txt1);
        final TextView tx2 = (TextView) findViewById(R.id.txt2);
        TextView tx3 = (TextView) findViewById(R.id.name);
        Bundle data = getIntent().getExtras();
        nameVal = data.getString("name");
        if (nameVal != null) {
            tx3.setText(nameVal);
        }

        trimTrack = MediaPlayer.create(this, Uri.parse(nameVal));
        mSeekBar = (SeekBar) findViewById(R.id.seekBar);
        mSeekBar.setClickable(false);

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "Playing sound", Toast.LENGTH_SHORT).show();
                trimTrack.start();

                finalTime = trimTrack.getDuration();
                startTime = trimTrack.getCurrentPosition();

                if (oneTimeOnly == 0) {
                    mSeekBar.setMax((int) finalTime);
                    oneTimeOnly = 1;
                }
                tx2.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                );

                tx1.setText(String.format("%d min, %d sec",
                        TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                        TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
                );

                mSeekBar.setProgress((int) startTime);
                myHandler.postDelayed(UpdateSongTime, 100);
            }
        });





    }

    private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            startTime = trimTrack.getCurrentPosition();
            TextView tx1 = (TextView) findViewById(R.id.txt1);
            tx1.setText(String.format("%d min, %d sec",

                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            mSeekBar.setProgress((int) startTime);
            myHandler.postDelayed(this, 100);
        }
    };
}



 /*   @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trim_page);
        play = (Button) findViewById(R.id.play);
        Bundle data = getIntent().getExtras();
        TextView name = (TextView) findViewById(R.id.name);
        nameVal = data.getString("name");
        if (nameVal != null) {
            name.setText(nameVal);

            play.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try {
                        Uri currentUri = Uri.parse(nameVal);
                        trimTrack = new MediaPlayer();
                        trimTrack.setDataSource(getApplicationContext(), currentUri);
                        mSeekBar.setMax(trimTrack.getDuration()); // where mFileDuration is mMediaPlayer.getDuration();
                        trimTrack.prepare();
                        trimTrack.start();
                    } catch (Exception e) {
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_SHORT).show();
                    }


                    finalTime = trimTrack.getDuration();
                    startTime = trimTrack.getCurrentPosition();

                    if (oneTimeOnly == 0) {
                        mSeekBar.setMax((int) finalTime);
                        oneTimeOnly = 1;
                    }
                    TextView tx2 = (TextView) findViewById(R.id.txt2);
                    TextView tx1 = (TextView) findViewById(R.id.txt1);

                    tx2.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) finalTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) finalTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) finalTime)))
                    );

                    tx1.setText(String.format("%d min, %d sec",
                            TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                            TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                                    TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.toMinutes((long) startTime)))
                    );

                    mSeekBar.setProgress((int) startTime);
                    myHandler.postDelayed(UpdateSongTime, 100);

                }
            });


        }
    }
   private Runnable UpdateSongTime = new Runnable() {
        public void run() {
            TextView tx2=(TextView)findViewById(R.id.txt2);
            TextView tx1=(TextView)findViewById(R.id.txt1);
            startTime = trimTrack.getCurrentPosition();
            tx1.setText(String.format("%d min, %d sec",

                    TimeUnit.MILLISECONDS.toMinutes((long) startTime),
                    TimeUnit.MILLISECONDS.toSeconds((long) startTime) -
                            TimeUnit.MINUTES.toSeconds(TimeUnit.MILLISECONDS.
                                    toMinutes((long) startTime)))
            );
            mSeekBar.setProgress((int)startTime);
            myHandler.postDelayed(this, 100);
        }
    };


}*/
