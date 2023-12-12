package com.oguzcanaygun.loginregister;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SettingsViewHolder extends RecyclerView.ViewHolder {

    ImageView symbolView;
    TextView titleView;

    public SettingsViewHolder(@NonNull View itemView) {
        super(itemView);
        symbolView= itemView.findViewById(R.id.symbolView);
        titleView=itemView.findViewById(R.id.textTitle);


    }
}
