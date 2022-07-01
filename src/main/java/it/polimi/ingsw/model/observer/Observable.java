package it.polimi.ingsw.model.observer;

import it.polimi.ingsw.server.protocol.message.UpdateViewMessage;

import java.util.HashSet;
import java.util.Set;

public abstract class Observable<T> {
    private Set<Observer<UpdateViewMessage>> observers = new HashSet<>();

    /**
     * Adds an observer to the set of observers
     * @param observer Observer
     */
    public void addObserver(Observer<UpdateViewMessage> observer) {
        observers.add(observer);
    }

    /**
     * Notifies all the observers of a certain message
     * @param msg Message that will be notified
     */
    public void notifyObservers(UpdateViewMessage msg) {
        for (Observer<UpdateViewMessage> observer : observers) {
            observer.notifyObserver(msg);
        }
    }
}
