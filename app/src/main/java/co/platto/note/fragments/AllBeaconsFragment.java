package co.platto.note.fragments;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface;
import android.support.v4.app.DialogFragment;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ListView;

import com.estimote.sdk.Beacon;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import co.platto.note.R;
import co.platto.note.estimotesrc.BeaconListAdapter;

/**
 * Created by Donnie Propst on 3/16/2016.
 */
public class AllBeaconsFragment extends DialogFragment implements AdapterView.OnItemClickListener {
    private ArrayList<Beacon> beaconCollection;
    private ListView beaconList;
    public static Beacon SELECTED_BEACON;
    private BeaconListAdapter adapter;


    public AllBeaconsFragment(){
        SELECTED_BEACON = null;
    }
    public static AllBeaconsFragment newInstance() {
        AllBeaconsFragment frag = new AllBeaconsFragment();
        return frag;
    }
    public void addBeacons(ArrayList<Beacon> beaconCollection){
        this.beaconCollection = beaconCollection;
        adapter.replaceWith(beaconCollection);
        adapter.notifyDataSetChanged();
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setMessage("Scanning...");
        builder.setTitle("Select Your Beacon");
        builder.setNegativeButton("Cancel", null);


        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.fragment_beacon_list, null);

        beaconList = (ListView)view.findViewById(R.id.fragment_beacon_listview);
        adapter = new BeaconListAdapter(view.getContext());
        beaconList.setOnItemClickListener(this);
        adapter.replaceWith((Collection)beaconCollection);
        beaconList.setAdapter(adapter);

        builder.setView(view);
        // Create the AlertDialog object and return it
        return builder.create();


    }

    @Override
    public void onDismiss(final DialogInterface dialog) {
        super.onDismiss(dialog);
        final Activity activity = getActivity();
        if (activity instanceof DialogInterface.OnDismissListener) {
            ((DialogInterface.OnDismissListener) activity).onDismiss(dialog);
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    public Beacon getSelectedBeacon(){
        return SELECTED_BEACON;
    }
    private void setSelectedBeacon(int i){
        System.out.println("SET SELECTED");
        AllBeaconsFragment.SELECTED_BEACON = beaconCollection.get(i);
        dismiss();
    }
    @Override
    public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
        System.out.println("I: " + i);
       setSelectedBeacon(i);
    }
}
