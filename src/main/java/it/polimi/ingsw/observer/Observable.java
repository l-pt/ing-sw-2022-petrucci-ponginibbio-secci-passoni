package it.polimi.ingsw.observer;

import it.polimi.ingsw.protocol.message.UpdateViewMessage;

import java.util.HashSet;
import java.util.Set;

public abstract class Observable<T> {
    private Set<Observer<UpdateViewMessage>> observers = new HashSet<>();

    public void addObserver(Observer<UpdateViewMessage> observer) {
        observers.add(observer);
    }

    public void notifyObservers(UpdateViewMessage msg) {
        for (Observer<UpdateViewMessage> observer : observers) {
            observer.notifyObserver(msg);
        }
    }
}
