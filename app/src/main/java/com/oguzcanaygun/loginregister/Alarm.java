package com.oguzcanaygun.loginregister;

import java.util.HashMap;
import java.util.Map;

public class Alarm {
    private String alarmName;
    private double latitude;
    private double longitude;
    private double radius;

    public Alarm(String alarmName, double latitude, double longitude, double radius) {
        this.alarmName = alarmName;
        this.latitude = latitude;
        this.longitude = longitude;
        this.radius = radius;
    }


    public String getAlarmName() {
        return alarmName;
    }

    public void setAlarmName(String alarmName) {
        this.alarmName = alarmName;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getRadius() {
        return radius;
    }

    public void setRadius(double radius) {
        this.radius = radius;
    }
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        map.put("alarmName", alarmName);
        map.put("latitude", latitude);
        map.put("longitude", longitude);
        map.put("radius", radius);
        return map;
    }
}
