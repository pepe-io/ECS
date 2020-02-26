package ecs.ecs.components;

/**
 * name component
 *
 * give your entity a name
 * maybe for multiplayer?
 */
public class NameComponent extends Component<String> {
    public String name;

    /**
     * plain constructor
     */
    public NameComponent() {
        this.name = "";
    }

    /**
     * constructor with data initialization
     *
     * @param name
     */
    public NameComponent(String name) {
        this.name = name;
    }

    /**
     * override getValue to return the name
     *
     * @return
     *      name
     */
    @Override
    public String getValue() {
        return name;
    }

    /**
     * override setValue to store the name
     *
     * @param value
     *      name
     */
    @Override
    public void setValue(String value) {
        this.name = value;
    }
}
