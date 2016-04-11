package co.platto.note.utils;

/**
 * Created by Donnie Propst on 3/16/2016.
 */
public interface Observable {
    public void notifyObserver();
    public void addObserver(Observer o);
}
