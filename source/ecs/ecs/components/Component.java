package ecs.ecs.components;

import ecs.ecs.entities.State;

/**
 * abstract generic component class
 *
 * generic getter allow easier access without typecasting
 * e.g. with typecasting:    System.out.println(((NameComponent) playerECS.getComponent(NameComponent.class)).name);
 * e.g. without typecasting: System.out.println(playerECS.getComponent(NameComponent.class).getValue());
 */
public abstract class Component<T> {

    // set a boolean as default state
    // most of the times an entity just have a boolean value
    protected boolean enabled = true;

    // set default init state
    // needed to flag component as new one
    protected State state = State.UPDATE;

    /**
     * constructor
     */
    public Component() {

    }

    /**
     * get the state of the component
     *
     * @return
     *      state
     */
    public State getState() {
        return state;
    }

    /**
     * set the state of the component
     *
     * @param state
     *      state
     */
    public void setState(State state) {
        this.state = state;
    }

    /**
     * generic getter
     *
     * @return
     *      value (type not predefined)
     */
    public abstract T getValue();

    /**
     * generic setter
     *
     * @param value
     *      value (type not predefined)
     */
    public abstract void setValue(T value);

    /**
     * get enabled state
     *
     * @return
     *      state
     */
    public boolean isEnabled() {
        return enabled;
    }

    /**
     * set enabled
     *
     * @param enabled
     *      state
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }
}
