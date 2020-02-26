package ecs.ecs.components;

import javafx.geometry.Point3D;

/**
 * rotation component
 *
 * stores rotation of an entity
 */
public class RotationComponent extends Component<Point3D> {
    private Point3D rotation;

    /**
     * plain constructor
     */
    public RotationComponent() {
        this.rotation = new Point3D(0, 0, 0);
    }

    /**
     * constructor with data initialization
     * @param rotation
     */
    public RotationComponent(Point3D rotation) {
        this.rotation = rotation;
    }

    /**
     * override setValue to store rotation
     *
     * @param value
     *      rotation
     */
    @Override
    public void setValue(Point3D value) {
        this.rotation = value;
    }

    /**
     * override getValue to return a rotation
     *
     * @return
     *      rotation
     */
    @Override
    public Point3D getValue() {
        return rotation;
    }

}
