package com.oguzcanaygun.loginregister;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Typeface;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.location.Priority;

import java.util.List;
import java.util.Map;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private Map<String, List<String>> mobileCollection;
    private List<String> groupList;

    public MyExpandableListAdapter(Context context, List<String> groupList, Map<String, List<String>> mobileCollection){
        this.context=context;
        this.groupList=groupList;
        this.mobileCollection=mobileCollection;


    }
    @Override
    public int getGroupCount() {
        return mobileCollection.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<String> children = mobileCollection.get(groupList.get(groupPosition));
        if (children == null) {
            Log.e("MyExpandableListAdapter", "Children list is null for group position: " + groupPosition);
            return 0;
        } else {
            Log.d("MyExpandableListAdapter", "Children list size for group position " + groupPosition + ": " + children.size());
            return children.size();
        }
        //return mobileCollection.get(groupList.get(groupPosition)).size();
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return mobileCollection.get(groupList.get(groupPosition)).get(childPosition);
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {

        return true;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {
        String mobileName= groupList.get(groupPosition);
        if (convertView==null){
            LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.group_item,null);
        }
        TextView item = convertView.findViewById(R.id.city);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(mobileName);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        String model= getChild(groupPosition,childPosition).toString();
        if (convertView==null){
            LayoutInflater inflater =(LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView=inflater.inflate(R.layout.child_item,null);
        }
        TextView item= convertView.findViewById(R.id.child_item);
        ImageView delete= convertView.findViewById(R.id.delete_image);
        item.setText(model);
        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Silmek istiyor musunuz?");
                builder.setCancelable(true);
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        List<String>child = mobileCollection.get(groupList.get(groupPosition));
                        child.remove(childPosition);
                        notifyDataSetChanged();

                    }
                });
                builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog =builder.create();
                alertDialog.show();
            }
        });
        return convertView;

    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}
