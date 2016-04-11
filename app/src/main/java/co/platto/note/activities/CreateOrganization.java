package co.platto.note.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import java.util.List;

import co.platto.note.R;
import butterknife.Bind;
import butterknife.ButterKnife;
import co.platto.note.data.UserAccount;
import co.platto.note.domain.Organization;
import co.platto.note.utils.ActionBarUtils;
import co.platto.note.utils.RefreshingDialog;

public class CreateOrganization extends AppCompatActivity{

    @Bind(R.id.add_org_text_field)EditText orgName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        UserAccount.getInstance().load();
        if(UserAccount.getInstance().getUser() != null){
            Intent intent = new Intent(this, HomeActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }else if(!IntroActivity.visited){
            Intent intent = new Intent(this, IntroActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        }

        setContentView(R.layout.activity_create_organization);
        ButterKnife.bind(this);
        ActionBarUtils.flatten(this);
        ActionBarUtils.setTitle(this, "Organization Setup");

        setupOrgNameKeyboardAction();
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
            verifyOrganization();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void verifyOrganization(){
        final String enteredText = orgName.getText().toString().trim();

        final RefreshingDialog rd = new RefreshingDialog(this);
        rd.createRefreshingDialog();
        ParseQuery<Organization> query = ParseQuery.getQuery(Organization.class);
        query.whereEqualTo("name", enteredText);
        query.findInBackground(new FindCallback<Organization>() {
            @Override
            public void done(List<Organization> objects, ParseException e) {
               if(objects.isEmpty()){//new group
                   createGroupAlert(enteredText);
                   rd.stop();
               }else{
                   joinGroupAlert(objects.get(0));
                   rd.stop();
               }
            }
        });

    }


    private void createGroupAlert(final String groupName){
       AlertDialog ad =  new AlertDialog.Builder(this)
                .setTitle("Create Group")
                .setMessage("Are you sure you want to create the group: " + groupName + "?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                       userAcceptedGroup(groupName);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void joinGroupAlert(final Organization org){
        AlertDialog ad =  new AlertDialog.Builder(this)
                .setTitle("Join Group")
                .setMessage("Are you sure you want to join the group: " + org.getName() + "?")
                .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        userAcceptedGroup(org);
                    }
                })
                .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {

                    }
                })
                .show();
    }

    private void userAcceptedGroup(Object org){
       if(org instanceof String){
           final Organization newOrg = new Organization((String)org);
           newOrg.saveInBackground(new SaveCallback() {
               @Override
               public void done(ParseException e) {
                   if(e == null){
                       //save worked
                       saveAndContinue(newOrg);
                   }else{
                       Toast.makeText(CreateOrganization.this, "Error creating organization, please try again", Toast.LENGTH_SHORT).show();
                   }
               }
           });
       }else{
           saveAndContinue((Organization)org);
       }

   }

    private void saveAndContinue(Organization newOrg){
        UserAccount.getInstance().saveOrganization(newOrg);
        if(UserAccount.getInstance().getOrganization() != null){
            startActivity(new Intent(CreateOrganization.this, CreateAccount.class));
            finish();
        }
    }

    private void setupOrgNameKeyboardAction(){
        //this makes the return key on keyboard submit data
        orgName.setOnEditorActionListener(new EditText.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if(i == EditorInfo.IME_NULL && keyEvent.getAction() == KeyEvent.ACTION_DOWN){
                    verifyOrganization();;
                }
                return true;
            }
        });

    }
}
