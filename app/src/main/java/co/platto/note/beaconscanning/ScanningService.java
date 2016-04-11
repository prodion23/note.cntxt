package co.platto.note.beaconscanning;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import co.platto.note.R;
import co.platto.note.activities.CreateOrganization;
import co.platto.note.data.DataSync;
import co.platto.note.data.UserAccount;
import co.platto.note.domain.Note;
import co.platto.note.domain.UserBeacon;

/**
 * Created by Donnie Propst on 3/31/2016.
 */
public class ScanningService extends Service {

    private BeaconManager beaconManager;
    public static boolean inRange = false;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
    private String NOTE = "NOTE";

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        scan();

        return START_STICKY;
    }
    public void scan(){

        if(UserAccount.getInstance().getUser() == null){
            UserAccount.getInstance().load();
        }
        UserBeacon userBeacon = UserAccount.getInstance().getUser().getBeacon();
        userBeacon.fetchIfNeededInBackground(new GetCallback<UserBeacon>() {
            @Override
            public void done(UserBeacon object, ParseException e) {
                System.out.println("BEACON WAS NOT NULL");
                final String uuid = object.getUUID();
                final int major = object.getMajor();
                final int minor = object.getMinor();
                beaconManager = new BeaconManager(ScanningService.this);
                beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
                    @Override
                    public void onServiceReady() {
                        beaconManager.startMonitoring(new Region(
                                "user beacon",
                                UUID.fromString(uuid), major, minor));
                    }
                });
                beaconManager.setMonitoringListener(new BeaconManager.MonitoringListener() {
                    @Override
                    public void onEnteredRegion(Region region, List<Beacon> list) {
                        inRange = true;
                        DataSync.getInstance().checkForNotes();
                        List<Note> notes = DataSync.getInstance().checkForNotes();
                        if (notes != null && !notes.isEmpty()) {
                            for (Note n : notes) {
                                n.setIsCached(true);
                                n.saveInBackground();
                                n.pinInBackground(NOTE);
                            }
                            showNotification("Note from " + notes.get(0).getFromUserName(), "Click to read");
                        }
                    }

                    @Override
                    public void onExitedRegion(Region region) {
                        inRange = false;
                    }
                });
            }
        });



    }

    public void showNotification( String title, String message) {
        Intent notifyIntent = new Intent(this, CreateOrganization.class);
        notifyIntent.setFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivities(this, 0,
                new Intent[] { notifyIntent }, PendingIntent.FLAG_UPDATE_CURRENT);
        Notification notification = new Notification.Builder(this)
                .setSmallIcon(R.drawable.white_note_notification_36)
                .setContentTitle(title)
                .setContentText(message)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .build();
        notification.defaults |= Notification.DEFAULT_SOUND;
        NotificationManager notificationManager =
                (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(1, notification);
    }


    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
