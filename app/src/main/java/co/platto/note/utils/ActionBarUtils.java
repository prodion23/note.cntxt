package co.platto.note.utils;

import android.app.Activity;
import android.content.Context;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Donnie Propst on 3/16/2016.
 */
public class ActionBarUtils {

    public static void remove(AppCompatActivity activity){
        ActionBar a = activity.getSupportActionBar();
        a.hide();
    }

    public static void flatten(AppCompatActivity activity){
        ActionBar actionbar = activity.getSupportActionBar();
        actionbar.setElevation(0f);
    }
    public static void setTitle(AppCompatActivity activity, String title){
        ActionBar actionbar = activity.getSupportActionBar();
        actionbar.setTitle(title);
    }
}
