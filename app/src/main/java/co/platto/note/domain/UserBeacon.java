package co.platto.note.domain;

import com.parse.GetCallback;
import com.parse.ParseClassName;
import com.parse.ParseException;
import com.parse.ParseObject;

/**
 * Created by Donnie Propst on 3/16/2016.
 */
@ParseClassName("UserBeacon")
public class UserBeacon extends ParseObject {

    private String UUID;
    private int major;
    private int minor;
    private String objectId;

    public UserBeacon(){

    }

    public UserBeacon(String UUID, int major, int minor) {
        setUUID(UUID);
        setMajor(major);
        setMinor(minor);
    }

    public String getUUID() {
        return getString("UUID");
    }
    public void setUUID(String UUID) {
        put("UUID", UUID);
    }
    public int getMajor() {
        return getInt("major");
    }
    public void setMajor(int major) {
       put("major", major);
    }

    public int getMinor() {
        return getInt("minor");
    }
    public void setMinor(int minor) {
        put("minor", minor);
    }

}
