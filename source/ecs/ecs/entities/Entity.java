package ecs.ecs.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import ecs.ecs.components.Component;
import com.sun.istack.internal.NotNull;
import javafx.scene.paint.PhongMaterial;

/**
 * entity class
 */
public abstract class Entity {

    // each entity needs an unique identifier
    protected UUID uuid;

    // set a state, to control rendering and garbage collection
    protected State state;

    // store available components
    // this is only necessary on deconstruction
    List<Component> components = new ArrayList<>();

    /**
     * constructor
     */
    public Entity() {
        // create uuid
        uuid = UUID.randomUUID();
        // set state
        state = State.UPDATE;
        // store entity
        EntityManager.addEntity(uuid, this);
        EntityManager.updateEntity(uuid, this);
    }

    /**
     * flag entity to delete
     */
    public void delete() {
        state = State.DELETE;
        EntityManager.updateEntity(uuid, this);
    }

    /**
     * get state of entity
     *
     * @return
     *      state
     */
    public State getState() {
        return state;
    }

    /**
     * set state of entity
     *
     * @param state
     *      state
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * add component to entity
     *
     * @param component
     *      component
     */
    public void addComponent(@NotNull Component component) {

        // add component to local list
        components.add(component);

        // add component to manager
        EntityManager.addComponent(uuid, component);

        // force update entity
        flagToUpdate();
    }

    /**
     * returns the requested component
     *
     * @param component
     *      component.class
     * @return
     *      component
     */
    public Component getComponent(Class<? extends Component> component) {
        return EntityManager.getComponent(uuid, component);
    }

    /**
     * returns a list of all components this entity has
     *
     * @return
     *      list of all components
     */
    public List<Component> getAllComponents() {
        return components;
    }

    /**
     * removes a component
     *
     * @param component
     *      component
     */
    public void removeComponent(Component component) {

        // remove component local list
        components.remove(component.getClass());

        // remove component from manager
        // DEPRECATED, will be done by garbageCollectorSystem
        // because e.g. removing a renderComponent does not unrender the entity
//        EntityManager.removeComponent(uuid, component);

        // instead flag component to delete
        component.setState(State.DELETE);

        // force update entity
        flagToUpdate();
    }

    /**
     * helper function to flag entity for update
     */
    private void flagToUpdate() {
        if (state != State.UPDATE) {
            state = State.UPDATE;
            EntityManager.updateEntity(uuid, this);
        }
    }

    /**
     * check if component is available
     * @param component
     *      component.class
     * @return
     *      boolean
     */
    public boolean hasComponent(Class<? extends Component> component) {
        return EntityManager.hasComponent(uuid, component);
    }

    /**
     * add phong-material
     * for performance reasons material should only created once
     * and should than injected into the component
     *
     * @param material
     *      phong-material
     */
    public abstract void setMaterial(PhongMaterial material);

}
