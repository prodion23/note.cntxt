package co.platto.note.utils;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Donnie Propst on 3/15/2016.
 */
public class RefreshingDialog {
    private Context c;
    private ProgressDialog d;
    public RefreshingDialog(Context c){
        this.c = c;
    }
    public void createRefreshingDialog(){
        d = new ProgressDialog(c);
        d.setIndeterminate(true);
        d.setCancelable(false);
        d.setTitle("Just a second...");
        d.show();
    }
    public void stop(){
        d.cancel();
    }

}
