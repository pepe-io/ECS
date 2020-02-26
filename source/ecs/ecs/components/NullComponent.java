package ecs.ecs.components;

/**
 * faker component
 * will be instantiated and returned if
 * EntityManager.hasComponent() can not find a component
 * purpose of this is that entity.getComponent(XyzComponent.class).getValue())
 * does not provoke a NullPointerException when using generic getter
 *
 * NullPointerException with typecasting:    System.out.println(((NameComponent) playerECS.getComponent(NameComponent.class)).name);
 * no exception with generic getter: System.out.println(playerECS.getComponent(NameComponent.class).getValue());
 */
public class NullComponent extends Component {

    public NullComponent() {

    }

    @Override
    public Object getValue() {
        return null;
    }

    @Override
    public void setValue(Object value) {

    }
}
