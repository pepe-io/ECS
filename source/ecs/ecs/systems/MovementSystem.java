package ecs.ecs.systems;

import ecs.ecs.components.*;
import ecs.ecs.entities.Entity;
import ecs.ecs.entities.EntityManager;
import ecs.ecs.entities.State;
import ecs.event.EventData;
import ecs.event.GameEvent;
import ecs.Game;
import ecs.settings.Settings;
import javafx.geometry.Point3D;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;
import javafx.scene.shape.Box;
import javafx.util.Pair;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * performs all movements on entities
 * including collision detection
 *
 * affected components: movement, velocity, collider, shape, light
 */
public class MovementSystem implements ECSystem {
    // entity-manager
    private EntityManager entityManager = EntityManager.getInstance();

    // event-system
    private EventCommandSystem eventCommandSystem = EventCommandSystem.getInstance();

    // game root
    private Pane root = Game.root;

    // level size
    private int levelHeight = Game.levelHeight;
    private int levelWidth = Game.levelWidth;

    // debug
    private boolean debugDummy = Settings.getDebug("MovementSystem@dummy");
    private boolean debugStepSize = Settings.getDebug("MovementSystem@stepsize");
    private boolean debugBuffer = false;

    // hashmap colliders
    private HashMap<UUID, Component> colliders;

    // dummy box for collision detection
    private Box dummyBox = new Box(0,0,0);

    // safe spot
    // we beam the collider to a place outside the game area
    // during collision detection to prevent self-collision
    // axis origin is where map creation start, it should be empty
    // if the entity is to big consider using negative values or in a 2D game offsetting on z-axis
    // another approach would be using enabled-state of the component
    private Point3D safeSpot = Settings.getSafeSpot();

    // fallback dummy size
    // is used when entity has no shape and no collider
    private Box fallBackSize = Settings.getFallBackSize();

    // stepsize defines the max vector length of velocity
    // it should not be greater than the smallest entity on the map
    // otherwise collision detection could fail and the moving entity would magically jumping over it
    // decreasing this value will decrease performance as well
    // because we have to traverse all entities more often during collision detection
    private int stepsize = Settings.getStepsize();

    /**
     * constructor
     */
    public MovementSystem() {

        if (debugDummy) {
            PhongMaterial material = new PhongMaterial();
            material.setDiffuseColor(Color.RED);
            dummyBox.setMaterial(material);
            root.getChildren().addAll(dummyBox);
        }
    }

    @Override
    public void run(boolean debug) {
        if(debug) System.err.println("MovementSystem <start>");
        int count = 0;


        // === PART 1 - POSITION ENTITIES FROM UPDATE-BUFFER ===
        // all entities in buffer will be placed in the world
        // translate: shapes, colliders & lights

        // get buffer
        HashMap<UUID, Entity> entities = EntityManager.entitiesUpdateBuffer;

        for(Map.Entry<UUID, Entity> entry : entities.entrySet()) {
//            UUID uuid = entry.getKey();
            Entity entity = entry.getValue();
            State entityState = entity.getState();
            if (debugBuffer) System.out.println("entity state: "+entityState);

            if (entity.hasComponent(PositionComponent.class)) {

                // get position component & data
                PositionComponent positionComponent = (PositionComponent) entity.getComponent(PositionComponent.class);
                Point3D position = positionComponent.getValue();
                State positionComponentState = positionComponent.getState();
                if (debugBuffer) System.out.println("position state: "+positionComponentState);

                // update shape
                if (entity.hasComponent(ShapeComponent.class)) {
                    count++;
                    ShapeComponent component = (ShapeComponent) entity.getComponent(ShapeComponent.class);
                    State componentState = component.getState();
                    if (debugBuffer) System.out.println("component state: "+componentState);
                    if (positionComponentState == State.UPDATE || component.getState() == State.UPDATE) {
                        component.translate(position);
                    }
                }

                // update collider
                if (entity.hasComponent(ColliderComponent.class)) {
                    count++;
                    ColliderComponent component = (ColliderComponent) entity.getComponent(ColliderComponent.class);
                    State componentState = component.getState();
                    if (debugBuffer) System.out.println("component state: "+componentState);
                    if (positionComponentState == State.UPDATE || component.getState() == State.UPDATE) {
                        component.translate(position);
                    }
                }

                // update light
                if (entity.hasComponent(LightComponent.class)) {
                    count++;
                    LightComponent component = (LightComponent) entity.getComponent(LightComponent.class);
                    State componentState = component.getState();
                    if (debugBuffer) System.out.println("component state: "+componentState);
                    if (positionComponentState == State.UPDATE || component.getState() == State.UPDATE) {
                        component.translate(position);
                    }
                    component.setState(State.STABLE);
                }
            }
        }


        // === PART 2 - RE-POSITIONING ENTITIES ON CHANGE ===
        // all entities with velocity need to be updated on every tick
        // requirements: position, velocity
        // translate: shapes, colliders & lights

        HashMap<UUID, Component> components = entityManager.components.get(VelocityComponent.class);

        // check if we there are any velocityComponents
        if (components == null) {
            if(debug) System.out.println("entities to move: 0");
        } else {
            if(debug) System.out.println("entities to move: " + components.size());

            // traverse all velocityComponents
            for(Map.Entry<UUID, ? extends Component> entry : components.entrySet()) {
                UUID uuid = entry.getKey();
                Component component = entry.getValue();
                Entity entity = entityManager.getEntity(uuid);

                // check if component is enabled and has a position
                // assume it has a velocity, otherwise it would not be in this list
                if (component.isEnabled() && entity.hasComponent(PositionComponent.class)) {

                    // get velocity & position
                    Point3D velocity = (Point3D) component.getValue();
                    PositionComponent positionComponent = (PositionComponent) entity.getComponent(PositionComponent.class);
                    Point3D position = positionComponent.getValue();

                    // check if entity needs an update (velocity is != 0)
                    if (velocity.magnitude() != 0) {
                        count++;


                        // === PART 3 - COLLISION DETECTION ===

                        // before running collision detection, we move the collider to a safe spot to prevent self-collision
                        // the position will be reset automatically when applying the new position
                        if (entity.hasComponent(ColliderComponent.class)) {
                            count++;
                            ((ColliderComponent) entity.getComponent(ColliderComponent.class)).translate(safeSpot);
                        }

                        // run collision detection
                        velocity = collisionDetection(entity, position, velocity);

                        // update position (add vector to current position)
                        position = new Point3D(position.getX() + velocity.getX(), position.getY() + velocity.getY(), position.getZ() + velocity.getZ());

                        // check if we have left the map and in case throw an event
                        if (position.getY() < 0 || position.getY() > levelHeight ||
                            position.getX() < 0 || position.getX() > levelWidth) {
                            GameEvent event = new GameEvent(GameEvent.OUT_OF_WORLD);
                            event.addData(EventData.CollisionUUID, uuid);
                            eventCommandSystem.addEvent(event);
                        }

                        // store velocity and position in it's components
                        component.setValue(velocity);
                        positionComponent.setValue(position);

                        // update shape position
                        if (entity.hasComponent(ShapeComponent.class)) {
                            count++;
                            ((ShapeComponent) entity.getComponent(ShapeComponent.class)).translate(position);
                        }

                        // update collider position
                        if (entity.hasComponent(ColliderComponent.class)) {
                            count++;
                            ((ColliderComponent) entity.getComponent(ColliderComponent.class)).translate(position);
                        }

                        // update light position
                        if (entity.hasComponent(LightComponent.class)) {
                            count++;
                            ((LightComponent) entity.getComponent(LightComponent.class)).translate(position);
                        }
                    }
                }
            }
        }

        if(debug) {
            System.out.println("moved components: " + count);
            System.out.println("MovementSystem <end>");
        }
    }

    /**
     * collision detection
     *
     * @param entity
     *      entity to check against colliders
     * @param position
     *      current position of the entity
     * @param velocity
     *      current velocity of the entity
     * @return
     *      updated velocity vector
     */
    private Point3D collisionDetection(Entity entity, Point3D position, Point3D velocity) {


        // === PART 4 - ROUGH ESTIMATION ===
        // look if desired field is empty

        // get all colliders from entity-manager
        colliders = entityManager.components.get(ColliderComponent.class);

        // we use a dummy for collision detection instead of the real object,
        // so we don't have to undo translations when encountering a collision

        // prepare the dummy
        Box reference;
        // get dimension from collider shape if available
        if (entity.hasComponent(ColliderComponent.class)) {
            reference = (Box) entity.getComponent(ColliderComponent.class).getValue();
        }
        // or try to get the size from the shape
        else if (entity.hasComponent(ShapeComponent.class)) {
            reference = (Box) entity.getComponent(ShapeComponent.class).getValue();
        }
        // if the entity has not a shape (e.g. it's a light), take the fallback size
        else {
            reference = fallBackSize;
        }

        dummyBox.setHeight(reference.getHeight());
        dummyBox.setWidth(reference.getWidth());
        dummyBox.setDepth(reference.getDepth());

        // check collision on maxed point of movement
        // this only works if speed is <= blocksize
        // we should test velocity on violating this rule

        // calculate required steps to reach the target
        int steps = (int) Math.ceil( velocity.magnitude() /stepsize );

        // define a boolean to store collision
        boolean collision = false;

        // create an empty container, to store the box we hit
        Pair<UUID, Box> crashBoxVelocity = null;

        // check steps on collision
        for (int i = 1; i<= steps; i++) {
            dummyBox.setTranslateX(position.getX() + (velocity.getX() / steps * i));
            dummyBox.setTranslateY(position.getY() + (velocity.getY() / steps * i));
            dummyBox.setTranslateZ(position.getZ() + (velocity.getZ() / steps * i));

            // traverse all colliders to detect collision
            crashBoxVelocity = traverseColliders();
            if (crashBoxVelocity.getValue() != null) {

                if (debugStepSize && steps>1) System.out.println("MovementSystem: collision detection steps: "+i+"/"+steps);

                // if we encounter a collision, mark it
                // we have to go further ...
                collision = true;
                break;
            }
        }


        // === PART 5 - GATHER DETAILS ===
        // on collision, get some details about where the collision happened

        // if we encounter a collision, we have to go further
        if(collision) {

            // create event
            GameEvent gameEvent = new GameEvent(GameEvent.COLLISION);
            gameEvent.addData(EventData.CollisionUUID, entity);

            // define a vector to store the axis of collision
            // great for later event handling
            Point3D collisionVector = new Point3D(0,0,0);

            // "crawling corners"-bug

            //      X
            //      Xo
            //      ZXX

            // assuming entity (o) is in a corner, two key input velocity detect only the diagonal block (Z)
            // x-axis movement will be blocked, but y-axis-collision will be ignored
            // so the entity can crawl into the corner
            // to prevent this, we have to check each axis for collision


            // detailed position & collision vector

            // if we detect a collision we need to know where exactly,
            // and on which axis collision happened
            // if we stretch the dummy on the axis we want to check, we can detect collisions before we get stuck in the block
            // collisionVector stores the sides on which collision was detected

            // check x-axis
            if (velocity.getX() != 0) {
                boolean xCollision = false;

                // prevent crawling corners
                dummyBox.setTranslateX(position.getX() + velocity.getX());
                dummyBox.setTranslateY(position.getY());
                dummyBox.setTranslateZ(position.getZ());
                Pair<UUID, Box> crashBoxAxis = traverseColliders();

                // check positive x-axis
                dummyBox.setWidth(dummyBox.getWidth()+2);
                for (double i=0; i<velocity.getX(); i++) {
                    dummyBox.setTranslateX(position.getX() + i);
                    boolean velCollision = getCollision(dummyBox, crashBoxVelocity.getValue());
                    boolean axisCollision = getCollision(dummyBox, crashBoxAxis.getValue());
                    if (velCollision || axisCollision) {
                        collisionVector = new Point3D(velocity.getX(), collisionVector.getY(), collisionVector.getZ());
                        velocity = new Point3D(i, velocity.getY(), velocity.getZ());
                        xCollision = true;
                        break;
                    }
                }
                // check negative x-axis
                for (double i=0; i>velocity.getX(); i--) {
                    dummyBox.setTranslateX(position.getX() + i);
                    boolean velCollision = getCollision(dummyBox, crashBoxVelocity.getValue());
                    boolean axisCollision = getCollision(dummyBox, crashBoxAxis.getValue());
                    if (velCollision || axisCollision) {
                        collisionVector = new Point3D(velocity.getX(), collisionVector.getY(), collisionVector.getZ());
                        velocity = new Point3D(i, velocity.getY(), velocity.getZ());
                        xCollision = true;
                        break;
                    }
                }
                dummyBox.setWidth(dummyBox.getWidth()-2);

                // add collider to event
                if (xCollision) gameEvent.addData(EventData.ColliderUUID, crashBoxAxis);
            }

            // check y-axis
            if (velocity.getY() != 0) {
                boolean yCollision = false;

                // prevent crawling corners
                dummyBox.setTranslateX(position.getX());
                dummyBox.setTranslateY(position.getY() + velocity.getY());
                dummyBox.setTranslateZ(position.getZ());
                Pair<UUID, Box> crashBoxAxis = traverseColliders();

                // check positive y-axis
                dummyBox.setHeight(dummyBox.getHeight()+2);
                for (double i=0; i<velocity.getY(); i++) {
                    dummyBox.setTranslateY(position.getY() + i);
                    boolean velCollision = getCollision(dummyBox, crashBoxVelocity.getValue());
                    boolean axisCollision = getCollision(dummyBox, crashBoxAxis.getValue());
                    if (velCollision || axisCollision) {
                        collisionVector = new Point3D(collisionVector.getX(), velocity.getY(), collisionVector.getZ());
                        velocity = new Point3D(velocity.getX(), i, velocity.getZ());
                        yCollision = true;
                        break;
                    }
                }
                // check negative y-axis
                for (double i=0; i>velocity.getY(); i--) {
                    dummyBox.setTranslateY(position.getY() + i);
                    boolean velCollision = getCollision(dummyBox, crashBoxVelocity.getValue());
                    boolean axisCollision = getCollision(dummyBox, crashBoxAxis.getValue());
                    if (velCollision || axisCollision) {
                        collisionVector = new Point3D(collisionVector.getX(), velocity.getY(), collisionVector.getZ());
                        velocity = new Point3D(velocity.getX(), i, velocity.getZ());
                        yCollision = true;
                        break;
                    }
                }
                dummyBox.setHeight(dummyBox.getHeight()-2);

                // add collider to event
                if (yCollision) gameEvent.addData(EventData.ColliderUUID, crashBoxAxis);
            }

            // check z-axis
            if (velocity.getZ() != 0) {
                boolean zCollision = false;

                // prevent crawling corners
                dummyBox.setTranslateX(position.getX());
                dummyBox.setTranslateY(position.getY());
                dummyBox.setTranslateZ(position.getZ() + velocity.getZ());
                Pair<UUID, Box> crashBoxAxis = traverseColliders();

                // check positive z-axis
                dummyBox.setDepth(dummyBox.getDepth()+2);
                for (double i=0; i<velocity.getZ(); i++) {
                    dummyBox.setTranslateZ(position.getZ() + i);
                    boolean velCollision = getCollision(dummyBox, crashBoxVelocity.getValue());
                    boolean axisCollision = getCollision(dummyBox, crashBoxAxis.getValue());
                    if (velCollision || axisCollision) {
                        collisionVector = new Point3D(collisionVector.getX(), collisionVector.getY(), velocity.getZ());
                        velocity = new Point3D(velocity.getX(), velocity.getY(), i);
                        zCollision = true;
                        break;
                    }
                }
                // check negative z-axis
                for (double i=0; i>velocity.getZ(); i--) {
                    dummyBox.setTranslateZ(position.getZ() + i);
                    boolean velCollision = getCollision(dummyBox, crashBoxVelocity.getValue());
                    boolean axisCollision = getCollision(dummyBox, crashBoxAxis.getValue());
                    if (velCollision || axisCollision) {
                        collisionVector = new Point3D(collisionVector.getX(), collisionVector.getY(), velocity.getZ());
                        velocity = new Point3D(velocity.getX(), velocity.getY(), i);
                        zCollision = true;
                        break;
                    }
                }
                dummyBox.setDepth(dummyBox.getDepth()-2);

                // add collider to event
                if (zCollision) gameEvent.addData(EventData.ColliderUUID, crashBoxAxis);
            }


            // === PART 6 - PACK RESULTS AND PASS EVENT TO LOGIC ===
            // since we do not want to extend this class anymore
            // we create a nice little package and put it into the event stack
            // shall the event stack decide, how to handle it

            // push collision event into the event-stack
            gameEvent.addData(EventData.VELOCITY, collisionVector);
            eventCommandSystem.addEvent(gameEvent);

        }

        // return updated velocity
        return velocity;
    }

    /**
     * helper method to check collision of dummyBox and ALL colliders
     *
     * @return
     *      collider on collision else null
     */
    private Pair<UUID, Box> traverseColliders() {

        // traverse all colliderComponents
        for(Map.Entry<UUID, ? extends Component> collider : colliders.entrySet()) {
            UUID uuidCollider = collider.getKey();
            Component colliderComponent = collider.getValue();

            Box colliderBox = (Box) colliderComponent.getValue();

            if(getCollision(dummyBox, colliderBox)) {
                return new Pair<>(uuidCollider, colliderBox);
            }
        }
        return new Pair<>(null, null);
    }

    /**
     * helper method to detect if two shapes collide
     *
     * @param s1
     *      shape 1
     * @param s2
     *      shape 2
     * @return
     *      boolean intersection
     */
    private boolean getCollision(Box s1, Box s2) {
        if(s1 == null || s2 == null) return false;
        return s1.getBoundsInParent().intersects(s2.getBoundsInParent());
    }
}