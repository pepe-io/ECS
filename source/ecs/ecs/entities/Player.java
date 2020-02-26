package ecs.ecs.entities;

import ecs.ecs.components.*;
import ecs.settings.GameType;
import ecs.settings.Settings;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

/**
 * player entity
 */
public class Player extends Entity {
    String name = "John";
    int width = 10;
    int height = 50;
    Color color = Color.ORANGE;

    /**
     * plain constructor
     */
    public Player() {
        super();
        init(0,0, 0);
    }

    /**
     * constructor with position
     * @param x
     * @param y
     */
    public Player(double x, double y, double z) {
        super();
        init(x, y, z);
    }

    /**
     * construction
     * @param x
     * @param y
     */
    private void init(double x, double y, double z) {

        addComponent(new NameComponent(name));
        addComponent(new PositionComponent(x, y, z));
        addComponent(new VelocityComponent());
        addComponent(new KeyInputComponent());
        addComponent(new ShapeComponent(width, height, width, color));
        addComponent(new RenderComponent());

        if (Settings.gameType() == GameType.Platformer) {
            addComponent(new GravityComponent());
            addComponent(new JumpComponent());
        }
    }

    /**
     * add material
     * @param material
     */
    @Override
    public void setMaterial(PhongMaterial material) {
        ((ShapeComponent) this.getComponent(ShapeComponent.class)).setMaterial(material);
    }
}
