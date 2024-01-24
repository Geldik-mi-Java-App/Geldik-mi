package com.oguzcanaygun.loginregister;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsViewHolder> {

    public final ItemClickInterface itemClickInterface;
    Context context;
    List<Item> items;

    public SettingsAdapter(ItemClickInterface itemClickInterface, Context context, List<Item> items) {
        this.itemClickInterface = itemClickInterface;
        this.context = context;
        this.items = items;
    }

    @NonNull
    @Override
    public SettingsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new SettingsViewHolder(LayoutInflater.from(context).inflate(R.layout.item_view, parent,false), itemClickInterface);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingsViewHolder holder, int position) {
    holder.titleView.setText(items.get(position).getTitle());
    holder.symbolView.setImageResource(items.get(position).getImage());


    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
