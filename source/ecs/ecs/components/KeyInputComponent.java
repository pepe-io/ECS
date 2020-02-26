package ecs.ecs.components;

/**
 * key input component
 *
 * flag entity to perceive update by KeyInputSystem
 * data stored has no functionality
 */
public class KeyInputComponent extends Component<Boolean> {

    public KeyInputComponent() {

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
