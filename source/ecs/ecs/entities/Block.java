package ecs.ecs.entities;

import ecs.ecs.components.*;
import ecs.settings.Settings;
import javafx.geometry.Point3D;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

/**
 * block entity
 */
public class Block extends Entity {
    int width = Settings.getBlocksize();
    int height = Settings.getBlocksize();
    int depth = Settings.getBlocksize();
    Color color = Color.GREY;

    /**
     * constructor for blocks
     * @param x
     * @param y
     */
    public Block(double x, double y, double z) {
        super();
        init(x, y, z, new Point3D(0,0,0));
    }

    public Block(double x, double y, double z, Point3D rotation) {
        super();
        init(x, y, z, rotation);
    }

    private void init(double x, double y, double z, Point3D rotation) {
        // shape: add some randomness to depth
        if (Settings.fancyBlocks()) {
            double randomDepth = Math.random() * 50;
            depth = (int) (depth + randomDepth);
        }

        addComponent(new PositionComponent(x, y, z));
        addComponent(new RotationComponent(rotation));
        addComponent(new ShapeComponent(width, height, depth, color));
        addComponent(new ColliderComponent(width, height, depth));
        addComponent(new RenderComponent());
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
