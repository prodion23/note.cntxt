package co.platto.note.activities;

import android.content.Intent;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.AppCompatSpinner;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.SaveCallback;

import java.util.ArrayList;
import java.util.List;

import co.platto.note.R;
import co.platto.note.data.DataSync;
import co.platto.note.data.UserAccount;
import co.platto.note.domain.Note;
import co.platto.note.domain.CustomUser;
import butterknife.Bind;
import butterknife.ButterKnife;
import co.platto.note.utils.ActionBarUtils;
import co.platto.note.utils.RefreshingDialog;

public class CreateNote extends AppCompatActivity {

    private ArrayList<CustomUser> users;
    @Bind(R.id.create_note_user_spinner)AppCompatSpinner spinner;
    @Bind(R.id.create_note_send)RelativeLayout sendButton;
    @Bind(R.id.create_note_content)EditText contentTextField;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_note);
        users = DataSync.getInstance().getUsers();
        ButterKnife.bind(this);
        ActionBarUtils.setTitle(this, "Post a Note");
        setupSpinner();
        setupSendButton();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

    }
    private void setupSpinner(){
        List<String> names = new ArrayList<String>();
        //users.remove(UserAccount.getInstance().getUser());
        //UserAccount.getInstance().load(this);
        for(CustomUser u : users){
                names.add(u.getName());
        }
        ArrayAdapter<String> nameAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, names);
        nameAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(nameAdapter);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to the action bar's Up/Home button
            case android.R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void setupSendButton(){
        sendButton.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                postNote();
            }
        });
    }

    private void postNote(){
        String content = contentTextField.getText().toString();
        if(content.trim().isEmpty()){
            Toast.makeText(this, "This note appears blank, enter some text before sending", Toast.LENGTH_SHORT).show();
        }else {
            CustomUser selectedUser = users.get(spinner.getSelectedItemPosition());
            Note stickyNote = new Note();
            stickyNote.setToUser(selectedUser);
            stickyNote.setFromUserName(UserAccount.getInstance().getUser().getName());
            stickyNote.setContent(content);
            stickyNote.setViaSlack(false);
            stickyNote.setIsCached(false);
            stickyNote.setRead(false);
            final RefreshingDialog rd = new RefreshingDialog(this);
            rd.createRefreshingDialog();
            stickyNote.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e != null) {
                        Toast.makeText(CreateNote.this, "Error sending note", Toast.LENGTH_SHORT).show();
                        rd.stop();
                    } else {
                        Toast.makeText(CreateNote.this, "Note Sent", Toast.LENGTH_SHORT).show();
                        rd.stop();
                        startActivity(new Intent(CreateNote.this, HomeActivity.class));
                    }

                }
            });
        }
    }
}
