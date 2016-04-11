package co.platto.note.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.estimote.sdk.Beacon;
import com.estimote.sdk.SystemRequirementsChecker;
import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.regex.Pattern;

import co.platto.note.R;
import co.platto.note.data.UserAccount;
import co.platto.note.domain.CustomUser;
import co.platto.note.domain.UserBeacon;
import co.platto.note.estimotesrc.BeaconDetailsDetector;
import co.platto.note.fragments.AllBeaconsFragment;
import co.platto.note.utils.ActionBarUtils;
import butterknife.Bind;
import butterknife.ButterKnife;
import co.platto.note.utils.Observer;
import co.platto.note.utils.RefreshingDialog;

public class CreateAccount extends AppCompatActivity implements Observer,DialogInterface.OnDismissListener {

    @Bind(R.id.create_account_name_text) EditText nameText;
    @Bind(R.id.create_account_email_text) EditText emailText;
    @Bind(R.id.create_account_beaconID_text) EditText beaconUUIDText;
    @Bind(R.id.create_account_beaconMajor_text) EditText beaconMajorText;
    @Bind(R.id.create_account_beaconMinor_text) EditText beaconMinorText;
    @Bind(R.id.create_account_detect_beacon_button)Button detectBeaconButton;

    private  AllBeaconsFragment dialog;
    private FragmentTransaction ft;
    private BeaconDetailsDetector searchForBeacon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_account);
        ButterKnife.bind(this);
        ActionBarUtils.flatten(this);
        ActionBarUtils.setTitle(this, "Create Account");
        setupDetectButton();
        dialog = AllBeaconsFragment.newInstance();
        //setupBeaconUUIDTextField();
        SystemRequirementsChecker.checkWithDefaultDialogs(this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_save, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_favorite) {
            verifyDetails();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupDetectButton(){
            detectBeaconButton.setOnClickListener(new View.OnClickListener(){
                @Override
                public void onClick(View view){
                    searchForBeacon = new BeaconDetailsDetector(CreateAccount.this);
                    searchForBeacon.addObserver(CreateAccount.this);
                    searchForBeacon.scan();
                    ft = getSupportFragmentManager().beginTransaction();
                    dialog.show(ft,"asdf");

                }
            });

    }

    private void verifyDetails(){
        final String nameTextValue = nameText.getText().toString().trim();
	    final String emailTextValue = emailText.getText().toString().trim();
        String beaconUUIDValue = beaconUUIDText.getText().toString().trim();
        int major = Integer.parseInt(beaconMajorText.getText().toString());
        int minor = Integer.parseInt(beaconMinorText.getText().toString());

       if(validateFields(nameTextValue, emailTextValue, beaconUUIDValue, major, minor)) {

           final RefreshingDialog rd = new RefreshingDialog(this);
           rd.createRefreshingDialog();
           final UserBeacon userBeacon = new UserBeacon(beaconUUIDValue, major, minor);
           userBeacon.saveInBackground(new SaveCallback() {
               @Override
               public void done(ParseException e) {
                   if (e == null) {
                       final CustomUser newUser = new CustomUser(nameTextValue, emailTextValue, userBeacon, UserAccount.getInstance().getOrganization());
                       newUser.saveInBackground(new SaveCallback() {
                           @Override
                           public void done(ParseException e) {
                               if (e == null) {
                                   //all good
                                   UserAccount.getInstance().saveUser(newUser);
                                   UserAccount.getInstance().saveBeacon(userBeacon);

                                   startActivity(new Intent(CreateAccount.this, HomeActivity.class));

                               } else {
                                   //error
                               }
                               rd.stop();
                           }
                       });
                   } else {
                       //beacon error
                   }
               }
           });
       }
    }

    @Override
    public void update(ArrayList<Beacon> beacons) {
        dialog.addBeacons(beacons);
        System.out.println("ADDED BEACONS");
    }

    @Override
    public void onDismiss(DialogInterface dialogInterface) {
        Beacon selected = AllBeaconsFragment.SELECTED_BEACON;
        searchForBeacon.stopScanning();
        dialog.dismiss();
        dialog = AllBeaconsFragment.newInstance();
        System.out.println("HERE");
        if(selected != null){
            beaconUUIDText.setText(selected.getProximityUUID().toString());
            beaconMajorText.setText(String.valueOf(selected.getMajor()));
            beaconMinorText.setText(String.valueOf(selected.getMinor()));
        }
    }
    private void success(){
        Intent i = new Intent(this, HomeActivity.class);
        startActivity(i);
    }

    private boolean validateFields(String name, String email, String beaconUUID, int major, int minor){
        boolean valid = true;
        nameText.setError(null);
        emailText.setError(null);
        beaconMajorText.setError(null);
        beaconMinorText.setError(null);
        beaconUUIDText.setError(null);
        if(name.trim().isEmpty()){
            valid = false;
            nameText.setError("Please enter a name");
        }
        if(!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()){
            valid = false;
            emailText.setError("Please enter a valid email");
        }
        if(major < 0 || major > 65535 ){
            valid = false;
            beaconMajorText.setError("Please enter a valid major number(0 to 65535)");
        }
        if(minor < 0 || minor > 65535){
            valid = false;
            beaconMinorText.setError("Please enter a valid minor number(0 to 65535)");
        }
        if(!beaconUUID.isEmpty()){

            String[] parts = beaconUUID.split("-");
            if(parts[0].length() != 8){
                valid = false;
                beaconUUIDText.setError("Invalid UUID");
            }else if(parts[1].length() != 4 || parts[2].length() != 4 || parts[3].length() != 4){
                valid = false;
                beaconUUIDText.setError("Invalid UUID");
            }else if(parts[4].length() != 12){
                valid = false;
                beaconUUIDText.setError("Invalid UUID");
            }
        }else{
            valid = false;
            beaconUUIDText.setError("Invalid UUID");
        }


        return valid;
    }
}
