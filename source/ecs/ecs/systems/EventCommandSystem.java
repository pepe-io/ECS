package ecs.ecs.systems;

import ecs.ecs.components.JumpComponent;
import ecs.ecs.components.VelocityComponent;
import ecs.ecs.entities.Entity;
import ecs.event.EventData;
import ecs.event.EventNotifier;
import ecs.event.GameEvent;
import ecs.settings.Settings;
import javafx.geometry.Point3D;
import java.util.*;

/**
 * event command stack is a system to handle and distribute events
 * it will not work directly on components
 * it's main purpose is to pull game logic out of collision detection
 *
 * events will be passed to the event notification system
 *
 * events are things that happened in the past
 * commands are action in the future
 *
 * this class is the main game-logic-part
 *
 * singleton
 *
 * affected components: all & none
 */
public class EventCommandSystem implements ECSystem {

    // this
    private static EventCommandSystem eventCommandSystem = new EventCommandSystem();

    // event notifier
    private EventNotifier eventNotifier = EventNotifier.getInstance();

    private EventCommandSystem() {}

    /* Static 'instance' method */
    public static EventCommandSystem getInstance( ) {
        return eventCommandSystem;
    }

    // event stack
    private List<GameEvent> eventStack = new ArrayList<>();

    // gravity
    private double gravity = Settings.getGravity();

    /**
     * events will be pushed in by other systems
     *
     * @param gameEvent
     *      event
     */
    public void addEvent(GameEvent gameEvent) {
        eventStack.add(gameEvent);
    }


    /**
     * system run function
     *
     * @param debug
     *      debug mode
     */
    @Override
    public void run(boolean debug) {
        if(debug) System.err.println("EventSystem <start>");

        // parse events
        for (GameEvent event : eventStack) {

            // collision events
            if (event.getEventType().equals(GameEvent.COLLISION)) {
                // pull data from event
                Entity entity = (Entity) event.getData(EventData.CollisionUUID);
                Point3D velocity = (Point3D) event.getData(EventData.VELOCITY);

                // on land reset jump-ability & velocity
                if (velocity.getY() > gravity && entity.hasComponent(JumpComponent.class)) {
                    entity.getComponent(JumpComponent.class).setValue(true);
                    event.consume();
                    eventNotifier.fireEvent(new GameEvent(GameEvent.ENTITY_LAND));
                }

                // on land (y>0) or when hitting the roof (y<0) reset velocity
                // do nor reset when hitting a wall (y=0)
                if (velocity.getY() != 0 && entity.hasComponent(VelocityComponent.class)) {
                    Point3D storedVelocity = (Point3D) entity.getComponent(VelocityComponent.class).getValue();
                    entity.getComponent(VelocityComponent.class).setValue(new Point3D(storedVelocity.getX(),0,storedVelocity.getZ()));
                    event.consume();
                }
            }



            // just fire the event to all observers
            if (!event.isConsumed()) {
                eventNotifier.fireEvent(event);
            }

        }


        // clear event-stack
        // we want an empty stack on next tick
        // unprocessed events get cleared
        eventStack.clear();

        if(debug) System.out.println("EventSystem <end>");
    }
}