package co.platto.note.estimotesrc;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.BeaconManager;
import com.estimote.sdk.Region;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import co.platto.note.utils.Observable;
import co.platto.note.utils.Observer;
import co.platto.note.utils.RefreshingDialog;

/**
 * Created by Donnie Propst on 3/16/2016.
 */
public class BeaconDetailsDetector implements Observable{
    private Context c;
    private ArrayList<Beacon> detectedBeacons;
    Observer activityToNotify;
    private BeaconManager beaconManager;

    private static final Region ALL_ESTIMOTE_BEACONS_REGION = new Region("rid", null, null, null);

    public BeaconDetailsDetector(Context c){
        this.c = c;
        detectedBeacons = new ArrayList<>();
    }

    public void scan(){
        beaconManager = new BeaconManager(c);
        //final RefreshingDialog dialog = new RefreshingDialog(c);
        //dialog.createRefreshingDialog();
        startScanning();

        beaconManager.setRangingListener(new BeaconManager.RangingListener() {
            @Override
            public void onBeaconsDiscovered(Region region, List<Beacon> list) {
                for(Beacon b : list){
                    addBeacon(b);
                }
            }
        });

    }

    private void addBeacon(Beacon b){
        if(!detectedBeacons.contains(b)) {
            detectedBeacons.add(b);
            notifyObserver();
        }
    }

    public ArrayList<Beacon> getAllBeacons(){
        return detectedBeacons;
    }


    private void startScanning() {
        beaconManager.connect(new BeaconManager.ServiceReadyCallback() {
            @Override public void onServiceReady() {
                beaconManager.startRanging(ALL_ESTIMOTE_BEACONS_REGION);
            }
        });
    }

    public void stopScanning(){
        beaconManager.stopRanging(ALL_ESTIMOTE_BEACONS_REGION);
    }

    @Override
    public void notifyObserver() {
        activityToNotify.update(detectedBeacons);
    }

    @Override
    public void addObserver(Observer o) {
        activityToNotify = o;
    }
}
