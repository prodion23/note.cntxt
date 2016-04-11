package co.platto.note.domain;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Donnie Propst on 3/16/2016.
 */
@ParseClassName("Note")
public class Note extends ParseObject {


    private String fromUserName;
    private CustomUser toUser;
    private String content;
    private boolean read;

    public Note(){

    }

    public String getContent() {
        return getString("content");
    }
    public void setContent(String content) {
        put("content", content);
    }

    public String getFromUserName() {
        return getString("fromUserName");
    }
    public void setFromUserName(String fromUserName) {
        put("fromUserName", fromUserName);
    }

    public CustomUser getToUser() {
        return (CustomUser)get("toUser");
    }
    public void setToUser(CustomUser toUser) {
       put("toUser", toUser);
    }

    public boolean isRead() {return getBoolean("read");
    }

    public boolean isViaSlack(){ return getBoolean("viaSlack");}
    public void setViaSlack(boolean viaSlack){ put("viaSlack", viaSlack );}

    public void setRead(boolean read) {put("read", read);}

    public void setIsCached(boolean cached){put("isCached", cached);}
    public boolean getIsCached(){ return getBoolean("isCached");}
}
