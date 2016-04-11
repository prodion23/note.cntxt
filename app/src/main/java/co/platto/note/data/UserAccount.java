package co.platto.note.data;

import com.parse.ParseException;
import com.parse.ParseQuery;
import com.parse.SaveCallback;

import co.platto.note.domain.Organization;
import co.platto.note.domain.CustomUser;
import co.platto.note.domain.UserBeacon;

/**
 * Created by Donnie Propst on 3/16/2016.
 */
public class UserAccount {
    private static UserAccount ourInstance = new UserAccount();

    private CustomUser user;
    private Organization organization;
    private UserBeacon beacon;

    private String ORGANIZATION = "organization";
    private String USER = "user";
    private String BEACON = "beacon";

    public static UserAccount getInstance() {
        return ourInstance;
    }

    private UserAccount() {

    }

   public void save(CustomUser saveUser){

       user = saveUser;
       saveUser(user);
       saveOrganization(user.getOrganization());
   }

    public void load(){
        ParseQuery<CustomUser> query = ParseQuery.getQuery(CustomUser.class);
        query.fromPin(USER);

        try {
            if(query.count() == 0){
                return;
            }
            user = query.getFirst();
        } catch (ParseException e) {

        }
    }

    public void saveUser(CustomUser u){
        user = u;
       user.pinInBackground(USER, new SaveCallback() {
           @Override
           public void done(ParseException e) {

           }
       });
        beacon = user.getBeacon();
        beacon.pinInBackground(BEACON, new SaveCallback() {
            @Override
            public void done(ParseException e) {

            }
        });
    }

    public void saveOrganization(Organization organization) {
        this.organization = organization;
        organization.pinInBackground(ORGANIZATION, new SaveCallback() {
            @Override
            public void done(ParseException e) {
                if(e != null){

                }
            }
        });
    }

    public void saveBeacon(UserBeacon beacon){
        beacon.pinInBackground(BEACON);
    }
    public Organization getOrganization() {
        return organization;
    }



    public CustomUser getUser() {
        return user;
    }

}
