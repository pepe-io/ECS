package ecs.ecs.components;

import javafx.geometry.Point3D;
import javafx.scene.PointLight;
import javafx.scene.paint.Color;

/**
 * light component
 *
 * there will be light!
 * add light to your scene by adding this component
 */
public class LightComponent extends Component<PointLight> {

    private PointLight light;

    /**
     * plain constructor
     */
    public LightComponent() {
        light = new PointLight();
    }

    /**
     * constructor with color
     *
     * @param color
     */
    public LightComponent(Color color) {
        light = new PointLight(color);
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
        this.light.setTranslateX(x);
        this.light.setTranslateY(y);
        this.light.setTranslateZ(z);
    }

    public void translateX(double x){
        this.light.setTranslateX(x);
    }

    public void translateY(double y){
        this.light.setTranslateY(y);
    }

    public void translateZ(double z){
        this.light.setTranslateZ(z);
    }

    public void translate(Point3D position){
        this.light.setTranslateX(position.getX());
        this.light.setTranslateY(position.getY());
        this.light.setTranslateZ(position.getZ());
    }

    public Point3D getPosition() {
        return new Point3D(light.getTranslateX() / 3, light.getTranslateY() / 3, light.getTranslateZ());
    }

    /**
     * override setValue to store a pointlight
     *
     * @param value
     *      pointlight
     */
    @Override
    public void setValue(PointLight value) {

    }

    /**
     * override getValue to return a pointlight
     *
     * @return
     *      pointlight
     */
    @Override
    public PointLight getValue() {
        return light;
    }
}
