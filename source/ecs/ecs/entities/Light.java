package ecs.ecs.entities;

import ecs.ecs.components.*;
import ecs.settings.Settings;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

/**
 * light entity
 */
public class Light extends Entity {
    private Color color;

    // settings
    Settings settings = Settings.getInstance();

    // debug
    private boolean debug = settings.getDebug("Light@color");

    public Light(double x, double y, double z) {
        super();

        // create random color
        int randomness = 50;
        int groundLightness = 100;
        int r = (int) (Math.random()*randomness) + groundLightness;
        int g = (int) (Math.random()*randomness) + groundLightness;
        int b = (int) (Math.random()*randomness) + groundLightness;
        color = Color.rgb(r, g,  b);
        if(debug) System.out.println("light color: "+r+","+g+","+b);

        addComponent(new PositionComponent(x, y, z));
        addComponent(new LightComponent(color));
        addComponent(new RenderComponent());
    }

    @Override
    public void setMaterial(PhongMaterial material) {

    }
}
