package ecs.ecs.systems;

import ecs.ecs.components.Component;
import ecs.ecs.entities.Entity;
import ecs.ecs.entities.EntityManager;
import ecs.ecs.entities.State;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

/**
 * garbage collector has different tasks:
 * - update EntityManager.buffer (states: stable, update, delete)
 * - deleting entities and/or components
 * - when deleting entities the components have to be removed as well
 * always put this system at last
 */
public class GarbageCollectorSystem implements ECSystem {

    @Override
    public void run(boolean debug) {
        if(debug) System.err.println("GarbageCollectorSystem <start>");
        int countEntities = 0;
        int countComponents = 0;

        // get buffer
        HashMap<UUID, Entity> entities = EntityManager.entitiesUpdateBuffer;

        // iterate buffer to delete components and/or entities
        for(Map.Entry<UUID, Entity> entry : entities.entrySet()) {
            UUID uuid = entry.getKey();
            Entity entity = entry.getValue();
            State state = entity.getState();
            List<Component> components = entity.getAllComponents();

            switch (state) {
                case UPDATE:
                    // iterate components
                    for (Component component : components) {
                        // remove flagged components from entity-manager
                        // local list in entity was updated on function-call: entity.removeComponent()
                        if (component.getState() == State.DELETE) {
                            EntityManager.removeComponent(uuid, component);
                            countComponents++;
                        } else {
                            component.setState(State.STABLE);
                        }
                    }
                    // flag entity as stable
                    entity.setState(State.STABLE);
                    countEntities++;
                    break;

                case DELETE:
                    // iterate components
                    for (Component component : components) {
                        // remove all components from entity-manager
                        // local list in entity has not to be updated,
                        // because we will destroy the entity
                        EntityManager.removeComponent(uuid, component);
                        countComponents++;
                    }

                    // remove entity
                    EntityManager.entities.remove(uuid);
                    entity.delete();
                    countEntities++;
                    break;
            }

        }

        // clear buffer
        EntityManager.entitiesUpdateBuffer.clear();

        if(debug) {
            System.out.println("entities updated: " + countEntities);
            System.out.println("components updated: " + countComponents);
            System.out.println("GarbageCollectorSystem <end>");
        }
    }
}
