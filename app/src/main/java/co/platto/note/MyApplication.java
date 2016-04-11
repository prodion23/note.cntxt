package co.platto.note;

import android.app.Application;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.interceptors.ParseLogInterceptor;

import co.platto.note.data.UserAccount;
import co.platto.note.domain.Note;
import co.platto.note.domain.Organization;
import co.platto.note.domain.CustomUser;
import co.platto.note.domain.UserBeacon;

/**
 * Created by Donnie Propst on 3/11/2016.
 */
public class MyApplication extends Application {

    private String Parse_appID = "estinote6923FABC";



    @Override
    public void onCreate() {
        super.onCreate();

        ParseObject.registerSubclass(Organization.class);
        ParseObject.registerSubclass(UserBeacon.class);
        ParseObject.registerSubclass(CustomUser.class);
        ParseObject.registerSubclass(Note.class);

        Parse.initialize(new Parse.Configuration.Builder(getApplicationContext())
                .applicationId(Parse_appID)
                .clientKey(null)
                .addNetworkInterceptor(new ParseLogInterceptor())
                .enableLocalDataStore() //enable local datastore within initialization since hosted elsewhere
                .server("http://estinote.herokuapp.com/parse/") // The trailing slash is important.
        .build()
        );

        //scan();
    }


}
