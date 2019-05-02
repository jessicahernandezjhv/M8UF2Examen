package com.dam.jessicahv.pruebamultimedia;


import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;


public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.RecyclerViewHolder>{
    ArrayList<ImageUploadInfo> myList;
    Bitmap image;

    public RecyclerAdapter(ArrayList<ImageUploadInfo> myList) {
        this.myList = myList;
    }

    @NonNull
    @Override
    public RecyclerViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, null, false);
        return new RecyclerViewHolder(view);
    }

    public static Bitmap getBitmapFromURL(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            // Log exception
            return null;
        }
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerViewHolder holder, final int position) {
        holder.txtName.setText(myList.get(position).getImageName());
        holder.txtInformacion.setText(myList.get(position).getAula());
        holder.myCheckBox.setChecked(myList.get(position).isStatus());

        String imageURL = myList.get(position).getImageURL();

        try {
            holder.foto.setImageBitmap(getBitmapFromURL(imageURL));
        } catch (Exception e) {
        }

        if (holder.myCheckBox.isChecked()) {
            holder.cross.setVisibility(View.VISIBLE);
        } else {
            holder.cross.setVisibility(View.INVISIBLE);
        }

        holder.myCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setFadeAnimation(holder.cross);
                } else {
                    setFadeOutAnimation(holder.cross);
                }
            }
        });
    }

    private void setFadeAnimation(View view){
        AlphaAnimation anim = new AlphaAnimation(0.0f, 1.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);
        view.setVisibility(View.VISIBLE);
    }

    private void setFadeOutAnimation(View view){
        AlphaAnimation anim = new AlphaAnimation(1.0f, 0.0f);
        anim.setDuration(1000);
        view.startAnimation(anim);
        view.setVisibility(View.INVISIBLE);
    }

    @Override
    public int getItemCount() {
        return myList.size();
    }

    public class RecyclerViewHolder extends RecyclerView.ViewHolder {
        TextView txtName, txtInformacion;
        CheckBox myCheckBox;
        ImageView foto, cross;


        public RecyclerViewHolder(@NonNull final View itemView) {
            super(itemView);
            txtName = (TextView) itemView.findViewById(R.id.idName);
            txtInformacion = (TextView) itemView.findViewById(R.id.idInformation);
            foto = (ImageView) itemView.findViewById(R.id.idImage);
            myCheckBox = (CheckBox) itemView.findViewById(R.id.idCheckBox);
            cross = (ImageView) itemView.findViewById(R.id.idDoneCross);
        }
    }
}
