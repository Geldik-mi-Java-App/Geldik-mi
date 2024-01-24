package com.oguzcanaygun.loginregister;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class SettingsViewHolder extends RecyclerView.ViewHolder {

    ImageView symbolView;
    TextView titleView;

    public SettingsViewHolder(@NonNull View itemView, ItemClickInterface itemClickInterface) {
        super(itemView);
        symbolView= itemView.findViewById(R.id.symbolView);
        titleView=itemView.findViewById(R.id.textTitle);
        itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            if (itemClickInterface != null){
                int pos = getAdapterPosition();

                if (pos!=RecyclerView.NO_POSITION){
                    itemClickInterface.onItemClick(pos);
                }

            }
            }
        });

    }
}
