package co.platto.note.data;

import android.util.Log;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseQuery;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import co.platto.note.adapter.NoteAdapter;
import co.platto.note.domain.Note;
import co.platto.note.domain.CustomUser;
import co.platto.note.utils.Observable;

/**
 * Created by Donnie Propst on 3/17/2016.
 */
public class DataSync {
    private static DataSync ourInstance = new DataSync();
    private static String NOTE = "NOTE";

    private ArrayList<CustomUser> users;

    public List<Note> newNotes;

    private DataSync() {
        users = new ArrayList<>();
    }
    public static DataSync getInstance(){
        return ourInstance;
    }

    public void getAllGroupUsers() {
        ParseQuery<CustomUser> query = ParseQuery.getQuery(CustomUser.class);
        query.whereEqualTo("organization", UserAccount.getInstance().getUser().getOrganization());
        query.findInBackground(new FindCallback<CustomUser>() {
            @Override
            public void done(List<CustomUser> objects, ParseException e) {
                if (e == null) {
                    for (CustomUser u : objects) {
                        System.out.println(u.getName());
                    }
                    users = (ArrayList) objects;
                }
            }
        });
    }
    public ArrayList<CustomUser> getUsers(){
        return users;
    }

    public List<Note> refreshAllNotes(){
        ParseQuery<Note> query = ParseQuery.getQuery(Note.class);
        query.whereEqualTo("toUser", UserAccount.getInstance().getUser());
        query.whereEqualTo("read", false);
        try {
            newNotes = query.find();
            System.out.println("SIZE : " + newNotes.size());
        } catch (ParseException e) {
            e.printStackTrace();
        } catch(NullPointerException n){

        }
        return newNotes;
    }

    public List<Note> checkForNotes(){
        ParseQuery<Note> query = ParseQuery.getQuery(Note.class);
        query.whereEqualTo("toUser", UserAccount.getInstance().getUser());
        query.whereEqualTo("read", false);
        query.whereEqualTo("isCached", false);
        try {
            newNotes = query.find();
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return newNotes;
    }

    public void getCachedNotes(final NoteAdapter adapter){
        ParseQuery<Note> noteQuery = ParseQuery.getQuery(Note.class);
        noteQuery.fromPin(NOTE);
        noteQuery.addDescendingOrder("createdAt");
        noteQuery.findInBackground(new FindCallback<Note>() {
           @Override
           public void done(List<Note> objects, ParseException e) {
               adapter.updateItems(objects);
           }
       });
        Log.d("RV", "NOTIFIED IN DATASYNC");

    }


}
