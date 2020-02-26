package ecs.ecs.components;

/**
 * render component
 *
 * flag entity to be rendered in viewport
 * data stored has no functionality
 */
public class RenderComponent extends Component<Boolean> {

    public RenderComponent() {

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
