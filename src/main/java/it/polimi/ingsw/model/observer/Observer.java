package it.polimi.ingsw.model.observer;

public interface Observer<T> {
    void notifyObserver(T param);
}
