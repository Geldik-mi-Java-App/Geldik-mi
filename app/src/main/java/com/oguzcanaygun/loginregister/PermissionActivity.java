package com.oguzcanaygun.loginregister;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



public class PermissionActivity extends AppCompatActivity {

    private static final int PERMISSION_REQUEST_CODE = 123;

    private String[] permissions = {
            "Bildirim Gönderme İzni",
            "Galeriye Giriş izni",
            "Yaklaşık Konuma Erişim İzni",
            "Net Konuma Erişim İzni",
            "Internet Erişim İzni",
            "Alarm Çalıştırma İzni",
            "Arkaplanda Çalışma İzni",
            "Arkaplanda Konum Takibi İzni"
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_permission);

        for (String permission : permissions) {
            requestPermissionIfNotGranted(permission);
        }

        // Initialize switches dynamically
        setupPermissionSwitches();
        setupMainSwitch();
    }

    private void requestPermissionIfNotGranted(String permission) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(this, permission) !=
                        PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{permission}, PERMISSION_REQUEST_CODE);
        }
    }

    private void setupPermissionSwitches() {
        for (String permission : permissions) {
            addSwitchForPermission(permission);
        }
    }

    private void setupMainSwitch() {
        final Switch mainSwitch = findViewById(R.id.permissionSwitch);
        final LinearLayout permissionLayout = findViewById(R.id.permissionLayout);

        mainSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // Update all permission switches according to the state of the main switch
                for (int i = 0; i < permissionLayout.getChildCount(); i++) {
                    View childView = permissionLayout.getChildAt(i);
                    if (childView instanceof Switch) {
                        Switch permissionSwitch = (Switch) childView;
                        permissionSwitch.setChecked(isChecked);
                        updateSwitchText(permissionSwitch);
                    }
                }
            }
        });
    }

    private void addSwitchForPermission(final String permission) {
        // Check if the switch for this permission is already added
        if (findViewById(permission.hashCode()) != null) {
            return;
        }

        final Switch permissionSwitch = new Switch(this);
        permissionSwitch.setId(permission.hashCode()); // Use permission hash as the ID
        permissionSwitch.setText(permission);
        permissionSwitch.setChecked(ActivityCompat.checkSelfPermission(this, permission) ==
                PackageManager.PERMISSION_GRANTED);

        // Set switch thumb and track colors using getColorStateList
        permissionSwitch.getThumbDrawable().setTintList(
                ContextCompat.getColorStateList(this, R.color.switchOn));
        permissionSwitch.getTrackDrawable().setTintList(
                ContextCompat.getColorStateList(this, R.color.switchToggle));

        permissionSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                updateSwitchText(permissionSwitch);
                if (isChecked) {
                    requestPermissionIfNotGranted(permission);
                } else {
                    ActivityCompat.requestPermissions(PermissionActivity.this,
                            new String[]{permission}, PERMISSION_REQUEST_CODE);
                }
            }
        });

        // Add the switch to your layout
        LinearLayout permissionLayout = findViewById(R.id.permissionLayout);
        permissionLayout.addView(permissionSwitch);
    }

    private void updateSwitchText(Switch permissionSwitch) {
        TextView switchText = findViewById(R.id.switchText);

        if (permissionSwitch.isChecked()) {
            switchText.setText("Açık");
        } else {
            switchText.setText("Kapalı");
        }
    }
    public void returnUserActivity(View view){
        Intent intent = new Intent(PermissionActivity.this, UserActivity.class);
        startActivity(intent);
        finish();
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == PERMISSION_REQUEST_CODE) {
            // Check if all requested permissions are granted
            for (int grantResult : grantResults) {
                if (grantResult != PackageManager.PERMISSION_GRANTED) {
                    // Permission denied, ask again
                    // You can implement a more sophisticated retry logic here
                    // For simplicity, I'm just requesting the permissions again
                    setupPermissionSwitches();
                    break;
                }
            }
        }
    }
}