package it.polimi.ingsw.observer;

public interface Observer<T> {
    void notifyObserver(T param);
}
