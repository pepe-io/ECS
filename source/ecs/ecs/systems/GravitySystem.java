package ecs.ecs.systems;

import ecs.ecs.components.*;
import ecs.ecs.entities.Entity;
import ecs.ecs.entities.EntityManager;
import ecs.settings.Settings;
import javafx.geometry.Point3D;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * apply gravity
 *
 * affected components: gravity, velocity
 */
public class GravitySystem implements ECSystem {
    // entity-manager
    private EntityManager entityManager = EntityManager.getInstance();

    // settings
    Settings settings = Settings.getInstance();
    private double gravity = Settings.getGravity();

    @Override
    public void run(boolean debug) {
        if(debug) System.err.println("GravitySystem <start>");
        int count = 0;
        HashMap<UUID, Component> components = EntityManager.components.get(GravityComponent.class);

        // check if there are any gravityComponents
        if (components == null) {
            if(debug) System.out.println("entities with gravity: 0");
        } else {
            if(debug) System.out.println("entities with gravity: " + components.size());

            // traverse all gravityComponents
            for(Map.Entry<UUID, ? extends Component> entry : components.entrySet()) {
                UUID uuid = entry.getKey();
                Component component = entry.getValue();

                // check if component is enabled
                if (component.isEnabled()) {

                    Entity entity = EntityManager.getEntity(uuid);

                    // update velocity
                    if (entity.hasComponent(VelocityComponent.class)) {
                        Component velocityComponent = entity.getComponent(VelocityComponent.class);
                        Point3D velocity = (Point3D) velocityComponent.getValue();
                        velocityComponent.setValue(new Point3D(velocity.getX(), velocity.getY() + gravity , velocity.getZ()));

                        count++;
                    }
                }
            }
        }

        if(debug)
        {
            System.out.println("gravity applied: " + count);
            System.out.println("GravitySystem <end>");
        }
    }
}