package ecs.ecs.systems;

import ecs.ecs.entities.EntityManager;
import ecs.settings.Settings;

/**
 * systemmanager
 *
 * stores and runs all systems
 * define and order your systems here
 */
public class SystemManager {
    // entity-manager
    EntityManager entityManager = EntityManager.getInstance();

    // define and instantiate all systems here
    RenderSystem render = new RenderSystem();
    KeyInputSystem keyInput = new KeyInputSystem();
    RotationSystem rotation = new RotationSystem();
    GravitySystem gravity = new GravitySystem();
    MovementSystem movement = new MovementSystem();
    EventCommandSystem eventCommandSystem = EventCommandSystem.getInstance();
    GarbageCollectorSystem garbage = new GarbageCollectorSystem();

    // settings
    Settings settings = Settings.getInstance();

    // debug
    private boolean debug_init = settings.getDebug("SystemManager@init");
    private boolean debug_update = settings.getDebug("SystemManager@update");

    /**
     * constructor
     */
    public SystemManager() {

    }

    /**
     * runs once on init
     */
    public void init() {
        if(debug_init) {
            System.err.println("SystemManager@init <start>");
            System.out.println("entities: " + entityManager.entities.size());
            System.out.println("buffered: " + entityManager.entitiesUpdateBuffer.size());
        }

        // order your systems here
        movement.run(debug_init);
        rotation.run(debug_init);
        render.run(debug_init);
        garbage.run(debug_init);

        if(debug_init) System.out.println("SystemManager@init <end>");
    }

    /**
     * runs the systems on every tick
     */
    public void update() {
        if(debug_update) {
            System.err.println("SystemManager@init <start>");
            System.out.println("entities: " + entityManager.entities.size());
        }

        if (debug_update && entityManager.entitiesUpdateBuffer.size() > 0) {
            System.out.println("SystemManager: entities to update: " + entityManager.entitiesUpdateBuffer.size());
        }

        // order your systems here
        render.run(debug_update);
        gravity.run(debug_update);
        keyInput.run(debug_update);
        movement.run(debug_update);
        garbage.run(debug_update);
        eventCommandSystem.run(debug_update);

        if(debug_update) System.out.println("SystemManager@init <end>");
    }
}
