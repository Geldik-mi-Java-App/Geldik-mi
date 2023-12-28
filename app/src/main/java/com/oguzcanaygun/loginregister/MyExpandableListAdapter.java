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

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class MyExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private Map<String, List<Alarm>> alarmCollection;
    private List<String> groupList;

    public MyExpandableListAdapter(Context context, List<String> groupList, Map<String, List<Alarm>> alarmCollection) {
        this.context = context;
        this.groupList = groupList;
        this.alarmCollection = alarmCollection;
    }

    @Override
    public int getGroupCount() {
        return groupList != null ? groupList.size() : 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        List<Alarm> alarms = alarmCollection != null ? alarmCollection.get(groupList.get(groupPosition)) : null;
        return alarms != null ? alarms.size() : 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return groupList != null ? groupList.get(groupPosition) : null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<Alarm> alarms = alarmCollection != null ? alarmCollection.get(groupList.get(groupPosition)) : null;
        return alarms != null ? alarms.get(childPosition) : null;
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
        String mobileName = (String) getGroup(groupPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.group_item, null);
        }
        TextView item = convertView.findViewById(R.id.city);
        item.setTypeface(null, Typeface.BOLD);
        item.setText(mobileName);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        Alarm alarm = (Alarm) getChild(groupPosition, childPosition);
        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.child_item, null);
        }
        TextView item = convertView.findViewById(R.id.child_item);
        ImageView delete = convertView.findViewById(R.id.delete_image);

        // Display only the alarm name in the child view
        item.setText(alarm.getAlarmName());

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setMessage("Silmek istiyor musunuz?");
                builder.setCancelable(true);
                builder.setPositiveButton("Evet", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String groupKey = groupList.get(groupPosition);

                        List<Alarm> alarms = alarmCollection != null ? alarmCollection.get(groupKey) : null;
                        if (alarms != null && childPosition < alarms.size()) {
                            Alarm deletedAlarm = alarms.remove(childPosition);

                            // Notify the adapter about the data change
                            notifyDataSetChanged();

                            // Delete the alarm from Firebase Firestore
                            if (deletedAlarm != null) {
                                deleteAlarmFromFirestore(groupKey, deletedAlarm);
                            }
                        }
                    }
                });
                builder.setNegativeButton("Hayır", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                AlertDialog alertDialog = builder.create();
                alertDialog.show();
            }
        });
        return convertView;
    }
    private void deleteAlarmFromFirestore(String groupKey, Alarm deletedAlarm) {
        if (groupKey != null && deletedAlarm != null) {
            // Update the Firebase Firestore
            FirebaseFirestore firebaseFirestore = FirebaseFirestore.getInstance();
            FirebaseAuth auth= FirebaseAuth.getInstance();
            String userId = auth.getUid();

            DocumentReference alarmlarDocRef = firebaseFirestore.collection("Alarmlar").document(userId);
            CollectionReference alarmsCollectionRef = alarmlarDocRef.collection("Alarms");

            // Delete the alarm document from Firestore
            alarmsCollectionRef.document(deletedAlarm.getAlarmName())
                    .delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.d("MyExpandableListAdapter", "Alarm deleted from Firestore: " + deletedAlarm.getAlarmName());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("MyExpandableListAdapter", "Error deleting alarm from Firestore: " + e.getLocalizedMessage());
                    });
        }
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }
}