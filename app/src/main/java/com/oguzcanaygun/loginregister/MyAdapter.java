package com.oguzcanaygun.loginregister;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {

    private List<String> dataList;
    private int selectedItem = RecyclerView.NO_POSITION;
    private OnItemClickListener listener;

    public MyAdapter(List<String> dataList, OnItemClickListener listener) {
        this.dataList = dataList;
        this.listener = listener;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_layout, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.bind(dataList.get(position), position, listener);
    }

    @Override
    public int getItemCount() {
        return dataList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        TextView textView;
        RelativeLayout itemRelativeLayout;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            textView = itemView.findViewById(R.id.textView);
            itemRelativeLayout = itemView.findViewById(R.id.itemRelativeLayout);
        }

        public void bind(String data, int position, OnItemClickListener listener) {
            textView.setText(data);

            // Set background color based on selection
            itemRelativeLayout.setBackgroundColor(itemView.getContext().getResources().getColor(
                    position == listener.getSelectedPosition() ? R.color.uygPembeSeffaf : android.R.color.transparent));

            itemView.setOnClickListener(view -> {
                if (listener != null) {
                    listener.onItemClick(position);
                }
            });
        }
    }

    public interface OnItemClickListener {
        void onItemClick(int position);

        int getSelectedPosition();
    }

    public void setSelectedItem(int position) {
        selectedItem = position;
        notifyDataSetChanged();
    }
}
