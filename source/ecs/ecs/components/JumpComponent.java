package ecs.ecs.components;

/**
 * jump component
 *
 * enables entity to jump
 * data stored has no functionality
 */
public class JumpComponent extends Component<Boolean> {

    public JumpComponent() {

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
