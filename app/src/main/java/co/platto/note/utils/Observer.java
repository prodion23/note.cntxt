package co.platto.note.utils;

import com.estimote.sdk.Beacon;

import java.util.ArrayList;

/**
 * Created by Donnie Propst on 3/16/2016.
 */
public interface Observer {
    public void update(ArrayList<Beacon> beacons);
}
