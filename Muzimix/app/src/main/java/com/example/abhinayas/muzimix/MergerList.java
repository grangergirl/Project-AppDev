package com.example.abhinayas.muzimix;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MergerList extends ArrayAdapter<String> implements View.OnClickListener {

    private final Activity context;
    private ArrayList<String> clips = null;
    private final int imageId;

    public MergerList(Activity context,
                      ArrayList<String> clips) {
        super(context, R.layout.single_list, clips);
        this.context = context;
        this.clips = clips;
        this.imageId = R.drawable.trimmer_scissors;

    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView = inflater.inflate(R.layout.single_list, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.name);

        ImageView imageView = (ImageView) rowView.findViewById(R.id.trim);
        txtTitle.setText(clips.get(position).toString());

        imageView.setImageResource(imageId);
        imageView.setOnClickListener(this);
        return rowView;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {

            case R.id.imageView:
            {Toast.makeText(getContext(),"hey",Toast.LENGTH_SHORT).show();
               break;
            }
            default:
                break;
        }
    }
}
