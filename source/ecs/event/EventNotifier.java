package ecs.event;

import javafx.event.EventType;

import java.util.*;

/**
 * event system handles and distribute events
 * it will not work directly on components
 * singleton
 */
public class EventNotifier implements EventNotifierInterface {

    private static EventNotifier eventNotifier = new EventNotifier();

    private EventNotifier() {}

    /* Static 'instance' method */
    public static EventNotifier getInstance( ) {
        return eventNotifier;
    }

    private HashMap<EventType<GameEvent>, List<GameEventObserver>> listeners = new HashMap<>();

    /**
     * fire events
     *
     * @param gameEvent
     */
    @Override
    public void fireEvent(GameEvent gameEvent) {

        if (listeners.containsKey(gameEvent.getEventType())) {
            List<GameEventObserver> store = listeners.get(gameEvent.getEventType());
            for (GameEventObserver observer : store) {
                observer.getNotification(gameEvent);
            }
        }

        if (listeners.containsKey(gameEvent.ANY)) {
            List<GameEventObserver> store = listeners.get(gameEvent.ANY);
            for (GameEventObserver observer : store) {
                observer.getNotification(gameEvent);
            }
        }

    }

    /**
     * add observer
     *  @param gameEvent
     * @param handler
     */
    @Override
    public synchronized void addEventhandler(EventType<GameEvent> gameEvent, GameEventObserver handler) {
        System.out.println("add eventhandler: "+handler+" to event: "+gameEvent);
        if (listeners.get(gameEvent) == null) {
            List<GameEventObserver> store = new ArrayList<>();
            store.add(handler);
            listeners.put(gameEvent, store);
        } else {
            List<GameEventObserver> store = listeners.get(gameEvent);
            if(!store.contains(gameEvent)) store.add(handler);
        }
    }

    /**
     * remove generic observer
     *
     * @param gameEvent
     * @param handler
     */
    @Override
    public void removeEventhandler(EventType<GameEvent> gameEvent, GameEventObserver handler) {
        System.out.println("remove eventhandler: "+handler+" from event: "+gameEvent);

        if (listeners.containsKey(gameEvent)) {
            List<GameEventObserver> currentListeners = listeners.get(gameEvent);
            currentListeners.remove(handler);
        }
    }
}