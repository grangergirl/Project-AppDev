package com.example.abhinayas.muzimix;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.app.NotificationCompat;
import android.support.v7.view.menu.ExpandedMenuView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.example.abhinayas.muzimix.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.SequenceInputStream;
import java.net.URI;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.StreamHandler;


public class Dashboard extends AppCompatActivity {
    public MediaPlayer track = null;
    private static String mFileName = null;
    private MediaRecorder mRecorder = null;
    private static String trimFileName = null;
    private MediaPlayer mPlayer = null;
    public int mStartRecording = 1;
    public int mStartPlaying = 1;
    private static final String LOG_TAG = "AudioRecordTest";
    public static ArrayList<String> clips = new ArrayList<String>();
    public static MergerList adapter = null;
    public Button addnew, saveFile, preview;
    public static boolean flag;
    public static int previewCount;
    public int playNext;
    public EditText userInput;

    @Override
    public void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashboard);
        ListView list;
        adapter = new MergerList(Dashboard.this, clips);
        list = (ListView) findViewById(R.id.list);
        list.setAdapter(adapter);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                Intent i = new Intent(Dashboard.this, TrimMusic.class);
                i.putExtra("name", clips.get(arg2));
                startActivity(i);

            }
        });
        addnew = (Button) findViewById(R.id.addnew);
        addnew.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try {
                    flag = true;
                    Intent pickTrack = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
                    startActivityForResult(pickTrack, 1);//one can be replaced with any action code
                } catch (Exception e) {
                    Toast.makeText(Dashboard.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });
        preview = (Button) findViewById(R.id.preview);
        preview.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                previewCount = 0;
                playPreview();
            }

        });
        saveFile = (Button) findViewById(R.id.save);
        saveFile.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {
                try {
                    LayoutInflater li = LayoutInflater.from(Dashboard.this);
                    View promptsView = li.inflate(R.layout.audio_name, null);
                    AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Dashboard.this);
                    alertDialogBuilder.setView(promptsView);
                    userInput = (EditText) promptsView
                            .findViewById(R.id.editTextDialogUserInput);

                    alertDialogBuilder
                            .setCancelable(false)
                            .setPositiveButton("Confirm",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            //new Thread(saveFileRunnable).run();
                                            new fileSaver().execute("wow");


                                        }
                                    })
                            .setNegativeButton("Cancel",
                                    new DialogInterface.OnClickListener() {
                                        public void onClick(DialogInterface dialog, int id) {
                                            dialog.cancel();
                                            Toast.makeText(Dashboard.this, "Cancelled", Toast.LENGTH_SHORT).show();

                                        }
                                    });

                    // create alert dialog
                    AlertDialog alertDialog = alertDialogBuilder.create();

                    // show it
                    alertDialog.show();

                } catch (Exception e) {
                    Toast.makeText(Dashboard.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }

        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.dashboard_options, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        //When Product action item is clicked
        if (id == R.id.action_play) {
            if (track != null) {
                track.pause();
                track.release();
                track = null;
            }
            flag = false;
            Intent pickTrack = new Intent(Intent.ACTION_PICK, MediaStore.Audio.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(pickTrack, 1);//one can be replaced with any action code

            return true;
        } else if (id == R.id.action_rec) {
            if (mStartRecording == 1) {

                try {
                    onRecord(1);

                    mStartRecording = 0;
                } catch (Exception e) {
                    Toast.makeText(Dashboard.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            } else {

                try {
                    onRecord(0);
                    mStartRecording = 1;
                } catch (Exception e) {
                    Toast.makeText(Dashboard.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
                }
            }

            return true;
        } else if (id == R.id.action_playrec) {
            try {
                onPlay(mStartPlaying);
                if (mStartPlaying == 1) {

                    mStartPlaying = 0;

                } else {

                    mStartPlaying = 1;
                }

            } catch (Exception e) {
                Toast.makeText(Dashboard.this, e.getMessage().toString(), Toast.LENGTH_SHORT).show();
            }

            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent trackIntent) {
        super.onActivityResult(requestCode, resultCode, trackIntent);


        switch (requestCode) {
            case 0:
                if (resultCode == RESULT_OK) {
                    if (!flag) {
                        Uri trackUri = trackIntent.getData();
                        track = MediaPlayer.create(Dashboard.this, trackUri);
                        trimFileName = trackUri.toString();
                        track.start();
                    } else {
                        String fileName = trackIntent.getDataString();
                        adapter.notifyDataSetChanged();
                        clips.add(fileName);
                    }

                }

                break;
            case 1:
                if (resultCode == RESULT_OK) {
                    if (!flag) {
                        Uri trackUri = trackIntent.getData();
                        track = MediaPlayer.create(Dashboard.this, trackUri);
                        trimFileName = trackUri.toString();
                        track.start();
                    } else {
                        String fileName = trackIntent.getDataString();
                        adapter.notifyDataSetChanged();
                        clips.add(fileName);

                    }
                }
                break;
        }
    }

    public void onRecord(int start) {
        if (start == 1) {
            startRecording();
        } else {
            stopRecording();
        }
    }

    public void onPlay(int start) {
        if (start == 1) {
            startPlaying();
        } else {
            stopPlaying();
        }
    }

    public void startPlaying() {
        mPlayer = new MediaPlayer();
        try {


            mPlayer.setDataSource(mFileName);
            mPlayer.prepare();
            mPlayer.start();
        } catch (IOException e) {
            Log.e(LOG_TAG, "prepare() failed");
        }


    }

    public void stopPlaying() {
        mPlayer.release();
        mPlayer = null;
    }

    public void startRecording() {

        mRecorder = new MediaRecorder();
        mRecorder.reset();
        try {
            final Date createdTime = new Date();
            mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + createdTime + "_rec.mp3";
            Toast.makeText(Dashboard.this, mFileName.toString(), Toast.LENGTH_SHORT).show();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
            LayoutInflater li = LayoutInflater.from(Dashboard.this);
            View promptsView = li.inflate(R.layout.audio_name, null);
            AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(Dashboard.this);
            alertDialogBuilder.setView(promptsView);
            final EditText userInput = (EditText) promptsView
                    .findViewById(R.id.editTextDialogUserInput);

            alertDialogBuilder
                    .setCancelable(false)
                    .setPositiveButton("Confirm",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    // get user input and set it to result
                                    // edit text
                                    String fileName = userInput.getText().toString();
                                    mFileName = Environment.getExternalStorageDirectory().getAbsolutePath() + "/" + fileName + "_rec.mp3";
                                    mRecorder.setOutputFile(mFileName);
                                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                                    try {
                                        mRecorder.prepare();
                                    } catch (Exception e) {
                                        Toast.makeText(Dashboard.this, "what nonsense", Toast.LENGTH_SHORT).show();
                                    }
                                    mRecorder.start();
                                }
                            })
                    .setNegativeButton("Cancel",
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                    mRecorder.setOutputFile(mFileName);
                                    mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
                                    try {
                                        mRecorder.prepare();
                                    } catch (Exception e) {
                                        Toast.makeText(Dashboard.this, "what nonsense", Toast.LENGTH_SHORT).show();
                                    }

                                    mRecorder.start();
                                }
                            });

            // create alert dialog
            AlertDialog alertDialog = alertDialogBuilder.create();

            // show it
            alertDialog.show();


        } catch (Exception e) {
            //Log.e(LOG_TAG, "Voice record failure");
            Toast.makeText((Dashboard.this), e.getMessage().toString(), Toast.LENGTH_SHORT).show();
        }


    }

    public void stopRecording() {
        mRecorder.stop();
        mRecorder.release();
        mRecorder = null;
    }

    @Override
    public void onPause() {
        super.onPause();
        if (mRecorder != null) {
            mRecorder.release();
            mRecorder = null;
        }

        if (mPlayer != null) {
            mPlayer.release();
            mPlayer = null;
        }
    }

    public void playPreview() {
        Toast.makeText(Dashboard.this, String.format("%d", previewCount), Toast.LENGTH_SHORT).show();

        if (track != null) {
            track.pause();
            track.release();
            track = null;
        }
        Uri currentUri = Uri.parse(clips.get(previewCount));//(Uri)clips.get(previewCount);
        track = MediaPlayer.create(Dashboard.this, currentUri);
        previewCount++;
        track.start();
        track.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            public void onCompletion(MediaPlayer mp) {
                if (previewCount < clips.size())
                    playPreview();
                else {
                    previewCount = 0;
                    track.pause();
                    track.release();
                    track = null;
                }

            }
        });
    }


    public String getRealPathFromURI(Uri contentUri) {
        String[] proj = {MediaStore.Audio.Media.DATA};
        Cursor cursor = managedQuery(contentUri, proj, null, null, null);
        int column_index = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DATA);
        cursor.moveToFirst();
        return cursor.getString(column_index);
    }


    class fileSaver extends AsyncTask<String,Void,Boolean> {

        String name_file = userInput.getText().toString();
        Uri currentUri;
        String s;
        ProgressDialog pd;

        protected void onPreExecute() {
            super.onPreExecute();
            pd = new ProgressDialog(Dashboard.this);
            pd.setMessage("Saving...");
            pd.setTitle("Please wait");
            pd.show();
            pd.setCancelable(true);
            Intent intent = new Intent(getApplicationContext(),Dashboard.class);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext());

            b.setAutoCancel(true)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.create_music)
                    .setContentTitle("MuziMix")
                    .setContentText("File save in progress")
                    .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                    .setContentIntent(contentIntent)
                    .setContentInfo("Info");


            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, b.build());
        }

        @Override
        protected Boolean doInBackground(String... urls) {
// TODO Auto-generated method stub

            try {
                Vector<FileInputStream> allClips = new Vector<FileInputStream>();
                for (int i = 0; i < clips.size(); i++) {
                    currentUri = Uri.parse(clips.get(i));
                    s = getRealPathFromURI(currentUri);
                    allClips.add(new FileInputStream(s));
                }
                Enumeration<FileInputStream> enu = allClips.elements();
                SequenceInputStream sis = new SequenceInputStream(enu);
                File path = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_MUSIC);
                File f = new File(path.getCanonicalPath() + "/" + name_file + ".mp3");
                FileOutputStream fos = new FileOutputStream(f);
                int temp;

                try {
                    while ((temp = sis.read()) != -1) {

                        fos.write(temp);

                    }
                } catch (IOException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();

                }

            } catch (Exception e) {
                Toast.makeText(Dashboard.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }


            return false;
        }


        @Override
        protected void onPostExecute(Boolean result) {
// TODO Auto-generated method stub
            pd.cancel();
            Toast.makeText(Dashboard.this, name_file + "saved successfully!", Toast.LENGTH_SHORT).show();
            adapter.notifyDataSetChanged();
            clips.clear();
            clips = new ArrayList<String>();
            pd.cancel();
            Intent intent = new Intent(getApplicationContext(),Dashboard.class);
            PendingIntent contentIntent = PendingIntent.getActivity(getApplicationContext(), 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder b = new NotificationCompat.Builder(getApplicationContext());

            b.setAutoCancel(false)
                    .setDefaults(Notification.DEFAULT_ALL)
                    .setWhen(System.currentTimeMillis())
                    .setSmallIcon(R.drawable.play_record)
                    .setContentTitle("MuziMix")
                    .setContentText("File saved")
                    .setDefaults(Notification.DEFAULT_LIGHTS| Notification.DEFAULT_SOUND)
                    .setContentIntent(contentIntent)
                    .setContentInfo("Info");


            NotificationManager notificationManager = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(1, b.build());
        }
    }
}





