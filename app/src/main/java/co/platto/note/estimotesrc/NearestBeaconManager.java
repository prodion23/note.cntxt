package co.platto.note.estimotesrc;

import android.content.Context;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.estimote.sdk.Utils;

import java.util.ArrayList;
import java.util.List;

import co.platto.note.estimotesrc.BeaconID;

public class NearestBeaconManager {

    private static final String TAG = "NearestBeaconManager";

    private static final Region ALL_ESTIMOTE_BEACONS = new Region("all Estimote beacons", null, null, null);

    private List<BeaconID> beaconIDs;

    private Listener listener;

    private BeaconID currentlyNearestBeaconID;
    private boolean firstEventSent = false;

    private BeaconManager beaconManager;

    public NearestBeaconManager(Context context, List<BeaconID> beaconIDs) {
        this.beaconIDs = beaconIDs;

        beaconManager = new BeaconManager(context);
        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                checkForNearestBeacon(list);
            }
        });
    }

    public void setListener(Listener listener) {
        this.listener = listener;
    }

    public interface Listener {
        void onNearestBeaconChanged(BeaconID beaconID);
    }

    public void startNearestBeaconUpdates() {
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override
            public void onServiceReady() {
                beaconManager.startRanging(ALL_ESTIMOTE_BEACONS);
            }
        });
    }

    public void stopNearestBeaconUpdates() {
        beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS);
    }

    public void destroy() {
        beaconManager.disconnect();
    }

    private void checkForNearestBeacon(List<Beacon> allBeacons) {
        List<Beacon> beaconsOfInterest = filterOutBeaconsByIDs(allBeacons, beaconIDs);
        Beacon nearestBeacon = findNearestBeacon(beaconsOfInterest);
        if (nearestBeacon != null) {
            BeaconID nearestBeaconID = BeaconID.fromBeacon(nearestBeacon);
            if (!nearestBeaconID.equals(currentlyNearestBeaconID) || !firstEventSent) {
                updateNearestBeacon(nearestBeaconID);
            }
        } else if (currentlyNearestBeaconID != null || !firstEventSent) {
            updateNearestBeacon(null);
        }
    }

    private void updateNearestBeacon(BeaconID beaconID) {
        currentlyNearestBeaconID = beaconID;
        firstEventSent = true;
        if (listener != null) {
            listener.onNearestBeaconChanged(beaconID);
        }
    }

    private static List<Beacon> filterOutBeaconsByIDs(List<Beacon> beacons, List<BeaconID> beaconIDs) {
        List<Beacon> filteredBeacons = new ArrayList<>();
        for (Beacon beacon : beacons) {
            BeaconID beaconID = BeaconID.fromBeacon(beacon);
            if (beaconIDs.contains(beaconID)) {
                filteredBeacons.add(beacon);
            }
        }
        return filteredBeacons;
    }

    private static Beacon findNearestBeacon(List<Beacon> beacons) {
        Beacon nearestBeacon = null;
        double nearestBeaconsDistance = -1;
        for (Beacon beacon : beacons) {
            double distance = Utils.computeAccuracy(beacon);
            if (distance > -1 &&
                    (distance < nearestBeaconsDistance || nearestBeacon == null)) {
                nearestBeacon = beacon;
                nearestBeaconsDistance = distance;
            }
        }

        Log.d(TAG, "Nearest beacon: " + nearestBeacon + ", distance: " + nearestBeaconsDistance);
        return nearestBeacon;
    }
}
/*
 beaconManager = new BeaconManager(getApplicationContext());
    beaconManager = new BeaconManager(getApplicationContext());

    // add this below:
    beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
        @Override
        public void onServiceReady() {
            beaconManager.startMonitoring(new Region(
                    "monitored region",
                    UUID.fromString(icyMarshmallow),
                    873, 15551));
        }
    });
    beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
        @Override
        public void onEnteredRegion(Region region, List<Beacon> list) {
            showNotification(
                    "Beacon Detected",
                    "");
        }
        @Override
        public void onExitedRegion(Region region) {
            // could add an "exit" notification too if you want (-:
        }
    });
}
public void showNotification(String title, String message) {
    Intent notifyIntent = new Intent(this, MainActivity.class);
    notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
    PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
            new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
    Notification notification = new Notification.Builder(this)
            .setSmallIcon(android.R.drawable.ic_dialog_info)
            .setContentTitle(title)
            .setContentText(message)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build();
    notification.defaults |= Notification.DEFAULT_SOUND;
    NotificationManager notificationManager =
            (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(1, notification);
}

 */

