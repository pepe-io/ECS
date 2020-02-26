package ecs.ecs.components;

import javafx.geometry.Point3D;

/**
 * position component
 *
 * stores position of entity
 */
public class PositionComponent extends Component<Point3D> {
    private Point3D position;

    /**
     * plain constructor
     */
    public PositionComponent() {
        this.position = new Point3D(0, 0, 0);
    }

    /**
     * constructor with data initialization
     * @param x
     * @param y
     * @param z
     */
    public PositionComponent(double x, double y, double z) {
        this.position = new Point3D(x, y, z);
    }

    /**
     * override setValue to store a point
     *
     * @param value
     *      point
     */
    @Override
    public void setValue(Point3D value) {
        this.position = value;
    }

    /**
     * override getValue to return a point
     *
     * @return
     *      point
     */
    @Override
    public Point3D getValue() {
        return position;
    }

}
