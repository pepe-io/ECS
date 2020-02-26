package ecs.ecs.components;

/**
 * gravity component
 *
 * add gravity to entity
 * data stored has no functionality
 */
public class GravityComponent extends Component<Boolean> {

    public GravityComponent() {

    }

    @Override
    public Boolean getValue() {
        return enabled;
    }

    @Override
    public void setValue(Boolean value) {
        this.enabled = value;
    }
}
