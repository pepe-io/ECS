package ecs.ecs.components;

import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.scene.transform.Rotate;

/**
 * shape component
 *
 * add a visible shape for the entity
 */
public class ShapeComponent extends Component<Box> {
    public Box shape;

    /**
     * constructor
     *
     * @param width
     * @param height
     * @param depth
     * @param color
     */
    public ShapeComponent(int width, int height, int depth, Color color) {
        Box shape = new Box(width, height, depth);
        PhongMaterial material = new PhongMaterial();
        material.setDiffuseColor(color);
        shape.setMaterial(material);
        this.shape = shape;
    }

    /**
     * add material
     *
     * @param material
     */
    public void setMaterial(PhongMaterial material) {
        this.shape.setMaterial(material);
    }

    /**
     * translate functions
     * @param x
     *      x-position
     * @param y
     *      y-position
     * @param z
     *      z-position
     */
    public void translate(double x, double y, double z){
        this.shape.setTranslateX(x);
        this.shape.setTranslateY(y);
        this.shape.setTranslateZ(z);
    }

    public void translateX(double x){
        this.shape.setTranslateX(x);
    }

    public void translateY(double y){
        this.shape.setTranslateY(y);
    }

    public void translateZ(double z){
        this.shape.setTranslateZ(z);
    }

    public void translate(Point3D position){
        shape.setTranslateX(position.getX());
        shape.setTranslateY(position.getY());
        shape.setTranslateZ(position.getZ());
    }

    /**
     * rotate function
     *
     * @param rotation
     */
    public void rotate(Point3D rotation){
        Rotate xRotate = new Rotate(rotation.getX(), Rotate.X_AXIS);
        Rotate yRotate = new Rotate(rotation.getY(), Rotate.Y_AXIS);
        Rotate zRotate = new Rotate(rotation.getZ(), Rotate.Z_AXIS);
        shape.getTransforms().clear();
        shape.getTransforms().addAll(xRotate,yRotate,zRotate);
    }

    /**
     * override setValue to store the shape
     *
     * @param value
     *      shape
     */
    @Override
    public void setValue(Box value) {

    }

    /**
     * override getValue to return the shape
     *
     * @return
     *      shape
     */
    @Override
    public Box getValue() {
        return this.shape;
    }
}
