package ecs.event;

import javafx.event.EventType;

/**
 * interface for event notifier
 */
public interface EventNotifierInterface {
    void fireEvent(GameEvent gameEvent);

    void addEventhandler(EventType<GameEvent> gameEvent, GameEventObserver handler);
    void removeEventhandler(EventType<GameEvent> gameEvent, GameEventObserver handler);

}
