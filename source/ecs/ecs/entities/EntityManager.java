package ecs.ecs.entities;

import ecs.ecs.components.Component;
import ecs.ecs.components.NullComponent;
import java.util.HashMap;
import java.util.UUID;

/**
 * entity manager stores all entities and components of entities
 * ordered in hashmaps by component.class
 *
 * this is a singleton
 * https://www.tutorialspoint.com/java/java_using_singleton.htm
 */
public class EntityManager {

    // store an instance of itself
    private static EntityManager entityManager = new EntityManager();

    // storage for all entities
    public static HashMap<UUID, Entity> entities = new HashMap<>();

    // storage for recently changed entities
    // needed for e.g. unrender, since render does not compute on every tick
    // or deleting entities/components
    public static HashMap<UUID, Entity> entitiesUpdateBuffer = new HashMap<>();

    // storage for components of entities
    // ordered by component for systems iteration
    public static HashMap<Class, HashMap<UUID, Component>> components = new HashMap<>();

    /** A private Constructor prevents any other
     * class from instantiating.
     */
    private EntityManager() {

    }

    /* Static 'instance' method */
    public static EntityManager getInstance( ) {
        return entityManager;
    }

    /**
     * puts the entity into the entity-pool
     *
     * @param uuid
     *      UUID of entity
     * @param entity
     *      entity itself
     */
    public static void addEntity(UUID uuid, Entity entity) {
        entities.put(uuid, entity);
    }

    /**
     * puts the entity into update buffer to force
     * rendering, de-rendering, deconstruction...
     *
     * @param uuid
     *      UUID of entity
     * @param entity
     *      entity itself
     */
    public static void updateEntity(UUID uuid, Entity entity) {
        entitiesUpdateBuffer.put(uuid, entity);
    }

    /**
     * returns the entity from pool
     *
     * @param uuid
     *      UUID of entity
     * @return
     *      entity itself
     */
    public static Entity getEntity(UUID uuid) {
        return entities.get(uuid);
    }

    /**
     * add component
     *
     * @param uuid
     *      UUID of entity
     * @param component
     *      component
     */
    public static void addComponent(UUID uuid, Component component) {
        // check if there is a hashmap for this component.class
        HashMap<UUID, Component> result = components.get(component.getClass());
        // create hashmap & store values
        if (result == null) {
            components.put(component.getClass(), new HashMap<UUID, Component>(){{put(uuid, component);}});
        }
        // just store values
        else {
            components.get(component.getClass()).put(uuid, component);
        }
    }

    /**
     * get component for specific entity
     *
     * @param uuid
     *      UUID of entity
     * @param component
     *      component.class
     * @return
     *      component
     */
    public static Component getComponent(UUID uuid, Class<? extends Component> component) {
        HashMap<UUID, ? extends Component> store = components.get(component);
        Component returnComponent = store.get(uuid);
        if (returnComponent == null) {
            // return faker null component to prevent NullPointerException
            return new NullComponent();
        } else {
            // return desired component
            return store.get(uuid);
        }
    }

    /**
     * get all components of specific class
     *
     * @param component
     *      component.class
     * @return
     *      hashmap of components
     */
    public static HashMap<UUID, ? extends Component> getAllComponents(Class<? extends Component> component) {
        return components.get(component);
    }

    /**
     * remove component
     *
     * @param uuid
     *      UUID of entity
     * @param component
     *      component.class
     */
    public static void removeComponent(UUID uuid, Component component) {
        components.get(component.getClass()).remove(uuid);
    }

    /**
     * check if component exist
     *
     * @param uuid
     *      UUID of entity
     * @param component
     *      component.class
     * @return
     *      boolean
     */
    public static boolean hasComponent(UUID uuid, Class<? extends Component> component) {
        HashMap<UUID, ? extends Component> store = components.get(component);
        if (store == null) {
            return false;
        }
        return null != store.get(uuid);
    }

    public static void statistic() {
        System.out.println("entities: "+entities.size());
        System.out.println("components: "+components.size());
    }
}
