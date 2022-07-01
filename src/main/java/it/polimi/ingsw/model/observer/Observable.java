package it.polimi.ingsw.model.observer;

import java.util.HashSet;
import java.util.Set;

public abstract class Observable<T> {
    private final Set<Observer<T>> observers = new HashSet<>();

    /**
     * Adds an observer to the set of observers
     * @param observer Observer
     */
    public void addObserver(Observer<T> observer) {
        observers.add(observer);
    }

    /**
     * Notifies all the observers of a certain message
     * @param msg Message that will be notified
     */
    public void notifyObservers(T msg) {
        for (Observer<T> observer : observers) {
            observer.notifyObserver(msg);
        }
    }
}
