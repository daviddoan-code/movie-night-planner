package com.example.assignment_1.restapi;

import android.Manifest;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.PowerManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.NotificationCompat;

import com.example.assignment_1.R;
import com.example.assignment_1.model.EventImpl;

import static com.example.assignment_1.model.EventModel.events;

public class LocationService extends IntentService {

    public static final String SERVICE_CHANNEL = "serviceChannel";
    private PowerManager.WakeLock wakeLock;
    int notificationPeriod;

    private static final String TAG = "LocationService";

    public LocationService() {
        super("LocationService");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onCreate() {
        super.onCreate();

        PowerManager powerManager = (PowerManager) getSystemService(POWER_SERVICE);
        wakeLock = powerManager.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK,
                "Assignment:WakeLock");
        wakeLock.acquire(600000);
        createNotificationChannels();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            Notification notification = new Notification.Builder(this, SERVICE_CHANNEL)
                    .setContentTitle("Title")
                    .setContentText("Running...")
                    .setSmallIcon(R.drawable.ic_explore_white)
                    .build();

            startForeground(1, notification);
        }

        LocationManager locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        LocationListener locationListener = new LocationListener() {

            @Override
            public void onLocationChanged(Location location) {
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        locationManager.requestLocationUpdates("gps", 5000, 0, locationListener);

        Location currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
        for (EventImpl event : events) {
            HttpURLConnectionThread connectThread = new HttpURLConnectionThread(currentLocation, event);
            connectThread.start();
        }
//        final Location currentLocation = locationManager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
//
//        Thread thread = new Thread(new Runnable() {
//            @Override
//            public void run() {
//                try {
//                    LocalDateTime currentTime = LocalDateTime.now();
//                    for(EventImpl element: events) {
//                        if(element.getStartDate().isAfter(currentTime)) {
////                        new HttpURLConnectionAsyncTask(currentLocation, element.getLatitude(), element.getLongitude()).execute();
////                        HttpURLConnectionAsyncTask connect = (HttpURLConnectionAsyncTask) new HttpURLConnectionAsyncTask(currentLocation, element.getLatitude(), element.getLongitude()).executeOnExecutor(AsyncTask.THREAD_POOL_EXECUTOR);
////                        String ds = connect.getDuration();
////                        long travelTime = connect.getTravelTimeSeconds();
////                            long travelTime = HttpURLConnectionAsyncTask.getTravelTimeSeconds(currentLocation, element.getLatitude(), element.getLongitude());
//                            long travelTime = HttpURLConnectionAsyncTask.getTravelTimeSeconds(currentLocation, element.getLatitude(), element.getLongitude());
//                            long minutesUntilEvent = ChronoUnit.MINUTES.between(currentTime, element.getStartDate());
//
//                            if(minutesUntilEvent - travelTime < notificationPeriod) {
//                                displayNotification(getApplicationContext(), element.getTitle(), "is starting in " + minutesUntilEvent + " minutes. Approximately " + travelTime + "minutes travel time.", Integer.parseInt(element.getID()));
//                            }
//                        }
//
//
//
//                    }
//                } catch(Exception e) {
//                    e.printStackTrace();
//                }
//
//            }
//        });
//        thread.start();
    }

    private void createNotificationChannels() {
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel serviceChannel = new NotificationChannel(
                    SERVICE_CHANNEL,
                    "Service Channel",
                    NotificationManager.IMPORTANCE_DEFAULT
            );

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(serviceChannel);
        }
    }
    private void displayNotification(Context context, String title, String message, int id) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, SERVICE_CHANNEL)
                .setSmallIcon(R.drawable.ic_explore_white)
                .setContentTitle(title)
                .setContentText(message)
                .setPriority(NotificationCompat.PRIORITY_HIGH);

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        builder.setChannelId(SERVICE_CHANNEL);

        notificationManager.notify(id, builder.build());
    }

    @Override
    protected void onHandleIntent(Intent intent) {
         int notificationPeriodMilliseconds = intent.getIntExtra("notificationPeriod", 1000);
         notificationPeriod = notificationPeriodMilliseconds / 60000;
    }
}