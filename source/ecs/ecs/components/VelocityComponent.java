package ecs.ecs.components;

import javafx.geometry.Point3D;

/**
 * velocity component
 *
 * adds velocity to the entity, so it can move
 */
public class VelocityComponent extends Component<Point3D> {
    public Point3D velocity;

    /**
     * plain constructor
     */
    public VelocityComponent() {
        velocity = new Point3D(0,0, 0);
    }

    /**
     * constructor with data initialization
     *
     * @param velocity
     */
    public VelocityComponent(Point3D velocity) {
        this.velocity = velocity;
    }

    /**
     * constructor with data initialization
     * @param xVector
     * @param yVector
     * @param zVector
     */
    public VelocityComponent(double xVector, double yVector, double zVector) {
        velocity = new Point3D(xVector, yVector, zVector);
    }

    /**
     * add velocity (!= set velocity)
     *
     * @param addVelocity
     */
    public void addValue(Point3D addVelocity) {
        this.velocity = new Point3D(velocity.getX() + addVelocity.getX(), velocity.getY() + addVelocity.getY(), velocity.getZ() + addVelocity.getZ());
    }

    /**
     * override getValue to return a point
     *
     * @return
     *      point
     */
    @Override
    public Point3D getValue() {
        return velocity;
    }

    /**
     * override setValue to store a point
     *
     * @param value
     *      point
     */
    @Override
    public void setValue(Point3D value) {
        this.velocity = value;
    }
}
