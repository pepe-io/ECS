package ecs.ecs.systems;

import ecs.ecs.components.Component;
import ecs.ecs.components.LightComponent;
import ecs.ecs.components.RenderComponent;
import ecs.ecs.components.ShapeComponent;
import ecs.ecs.entities.Entity;
import ecs.ecs.entities.EntityManager;
import ecs.ecs.entities.State;
import ecs.Game;
import javafx.scene.PointLight;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Box;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * renders all entities
 * or removes them if necessary
 * operate on EntityManager.buffer
 *
 * affected components: render, shape, light
 */
public class RenderSystem implements ECSystem {
    // root
    private Pane root = Game.root;

    @Override
    public void run(boolean debug) {
        if(debug) System.err.println("RenderSystem <start>");
        int count = 0;

        // DEPRECATED access by component
        // to prevent rendering on every frame
        // render-system can only operate once
        // this lacks possibilities to add or remove entities (or components) during runtime
//        HashMap<UUID, Component> components = EntityManager.components.get(RenderComponent.class);

        // this new approach is using a separate buffer to indicate changes rather than static states
        // changes are indicated by flag and stored in the buffer
        HashMap<UUID, Entity> entities = EntityManager.entitiesUpdateBuffer;

        // check if there are any entities in the update-pipeline
        // rendering is only needed once when entity is created
        // removing an entity from scene will also be indicated on the update-channel

        // DEPRECATED (see above)
        // check if we there are any renderComponents
//        if (components == null) {

        // new approach
        if (entities == null) {
            if(debug) System.out.println("entities to render: 0");
        } else {
//            if(debug) System.out.println("entities to render: " + entities.size());

            // traverse all renderComponents
//            for(Map.Entry<UUID, ? extends Component> entry : components.entrySet()) {
            for(Map.Entry<UUID, Entity> entry : entities.entrySet()) {
                UUID uuid = entry.getKey();
//                Component component = entry.getValue();
                Entity entity = entry.getValue();
                State entityState = entity.getState();
//                System.out.println("entity state: "+entityState);

                // check if component is enabled
//                if (entity.isEnabled()) {
                if (entity.hasComponent(RenderComponent.class)) {

//                    Entity entity = EntityManager.getEntity(uuid);

                    // get render component flag
                    State renderComponentState = entity.getComponent(RenderComponent.class).getState();
//                    System.out.println("render state: "+renderComponentState);

                    // update shape
                    if (entity.hasComponent(ShapeComponent.class)) {
                        count++;
                        Component component = entity.getComponent(ShapeComponent.class);
                        State componentState = component.getState();
//                        System.out.println("component state: "+componentState);
                        Box shape = (Box) component.getValue();
                        // remove shape from scene, when either the renderComponent or the shapeComponent is flagged to delete
                        if (entityState == State.DELETE || renderComponentState == State.DELETE || component.getState() == State.DELETE) {
                            root.getChildren().remove(shape);
                        }
                        // add shape to scene, when either the renderComponent or the shapeComponent is flagged to update
                        else if (renderComponentState == State.UPDATE || component.getState() == State.UPDATE) {
                            root.getChildren().add(shape);
                        }
                    }

                    // update light
                    if (entity.hasComponent(LightComponent.class)) {
                        count++;
                        Component component = entity.getComponent(LightComponent.class);
                        State componentState = component.getState();
//                        System.out.println("component state: "+componentState);
                        PointLight light = (PointLight) component.getValue();
                        // remove light from scene, when either the renderComponent or the lightComponent is flagged to delete
                        if (entityState == State.DELETE || renderComponentState == State.DELETE || component.getState() == State.DELETE) {
                            root.getChildren().remove(light);
                        }
                        // add light to scene, when either the renderComponent or the lightComponent is flagged to update
                        else if (renderComponentState == State.UPDATE || component.getState() == State.UPDATE) {
                            root.getChildren().add(light);
                        }
                    }
                }
            }
        }
        if(debug) {
            System.out.println("rendered components: " + count);
            System.out.println("RenderSystem <end>");
        }
    }
}
