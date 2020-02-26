package ecs.ecs.systems;

import ecs.ecs.components.*;
import ecs.ecs.entities.Entity;
import ecs.ecs.entities.EntityManager;
import javafx.geometry.Point3D;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * performs all rotations on entities
 * rotation is a visual experience,
 * so we only process shapes, not colliders or anything else
 *
 * affected components: rotation, shape
 */
public class RotationSystem implements ECSystem {

    @Override
    public void run(boolean debug) {
        if(debug) System.err.println("RotationSystem <start>");
        int count = 0;
        HashMap<UUID, Component> components = EntityManager.components.get(RotationComponent.class);

        // check if we there are any rotationComponents
        if (components == null) {
            if(debug) System.out.println("entities to rotate: 0");
        } else {
            if(debug) System.out.println("entities to rotate: " + components.size());

            // traverse all rotationComponents
            for(Map.Entry<UUID, ? extends Component> entry : components.entrySet()) {
                UUID uuid = entry.getKey();
                Component component = entry.getValue();

                // check if component is enabled
                if (component.isEnabled()) {

                    Entity entity = EntityManager.getEntity(uuid);
                    Point3D rotation = (Point3D) component.getValue();

                    // update shape rotation
                    if (entity.hasComponent(ShapeComponent.class)) {
                        count++;
                        ((ShapeComponent) entity.getComponent(ShapeComponent.class)).rotate(rotation);
                    }
                }
            }
        }
        if(debug) {
            System.out.println("rotated components: " + count);
            System.out.println("RotationSystem <end>");
        }
    }
}