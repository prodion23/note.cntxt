package co.platto.note.domain;

import com.parse.ParseClassName;
import com.parse.ParseObject;

/**
 * Created by Donnie Propst on 3/14/2016.
 */
@ParseClassName("Organization")
public class Organization extends ParseObject {

    private String name;

    public Organization(){

    }

    public Organization(String name){
        this.name = name;
        put("name", name);
    }

    public String getName(){
       return getString("name");
    }

    public void setName(String name) {
        put("name", name);
    }





}
