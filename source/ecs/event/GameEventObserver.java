package ecs.event;

/**
 * interface for observers
 */
public interface GameEventObserver {
    public void getNotification(GameEvent gameEvent);
}
