package co.platto.note.domain;

import com.parse.ParseClassName;
import com.parse.ParseObject;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by Donnie Propst on 3/14/2016.
 */
@ParseClassName("CustomUser")
public class CustomUser extends ParseObject {

    private String name;
    private String email;
    private UserBeacon beacon;
    private Organization organization;
    private String objectId;

    public CustomUser(){

    }

    public CustomUser(String name, String email, UserBeacon beacon, Organization org){
        setName(name);
	    setEmail(email);
        setBeacon(beacon);
        setOrganization(org);
    }
	
    public String getEmail() {
        return getString("email");
    }
    public void setEmail(String email) {
        put("email", email);
    }

    public String getName() {
        return getString("name");
    }
    public void setName(String name) {
        put("name", name);
    }

    public UserBeacon getBeacon() {
        return (UserBeacon)get("beacon");
    }
    public void setBeacon(UserBeacon beacon) {
        put("beacon", beacon);
    }

    public Organization getOrganization() {
        return (Organization)get("organization");
    }
    public void setOrganization(Organization organization) {
        put("organization", organization);
    }

}
