package ecs.event;

import javafx.event.Event;
import javafx.event.EventType;

import java.util.HashMap;

/**
 * GameEvent extends the JavaFX-Event to
 * - carry data along with
 * - to name the event
 */
public class GameEvent extends Event {
    // set generic GameEvent
    public static final EventType<GameEvent> ANY = new EventType<>(Event.ANY, "GAME_EVENT");

    // extend GameEvents
    // define your GameEvents for EventCommandStack & EventBus here
    public static final EventType<GameEvent> OUT_OF_WORLD = new EventType<>(ANY, "OUT_OF_WORLD");
    public static final EventType<GameEvent> COLLISION = new EventType<>(ANY, "COLLISION");
    public static final EventType<GameEvent> ENTITY_JUMP = new EventType<>(ANY, "ENTITY_JUMP");
    public static final EventType<GameEvent> ENTITY_LAND = new EventType<>(ANY, "ENTITY_LAND");

    // all data is stored here
    private HashMap<EventData, Object> data = new HashMap<>();

    /**
     * constructor
     *
     * @param type
     */
    public GameEvent(EventType<GameEvent> type) {
        super(type);
    }

    /**
     * add data to the event
     *
     * @param dataType
     *      enum EventData expected
     * @param object
     *      you can store any java-object
     */
    public void addData(EventData dataType, Object object) {
        data.put(dataType, object);
    }

    /**
     * returns data from event
     *
     * @param dataType
     *      enum EventData expected
     * @return
     *      stored object data
     */
    public Object getData(EventData dataType) {
        return data.get(dataType);
    }

    /**
     * returns all data from event
     *
     * @return
     *      stored object data
     */
    public HashMap<EventData, Object> getAllData() {
        return data;
    }
}
