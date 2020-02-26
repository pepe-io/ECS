package ecs.ecs.systems;

import ecs.ecs.components.*;
import ecs.ecs.entities.Entity;
import ecs.ecs.entities.EntityManager;
import ecs.event.GameEvent;
import ecs.Game;
import ecs.settings.GameType;
import ecs.settings.Settings;
import javafx.geometry.Point3D;
import javafx.scene.input.KeyCode;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * handles key input
 *
 * affected components: keyInput, jump, velocity
 */
public class KeyInputSystem implements ECSystem {
    // entity-manager
    private EntityManager entityManager = EntityManager.getInstance();

    // event-system
    private EventCommandSystem eventCommandSystem = EventCommandSystem.getInstance();

    // speed & jump
    private int movement = Settings.getSpeed();
    private int jump = Settings.getJump();
    private double xVel, yVel, zVel;

    // key hashmap
    private HashMap<KeyCode,Boolean> keyInput = Game.keyInput;

    @Override
    public void run(boolean debug) {
        if(debug) System.err.println("KeyInputSystem <start>");
        int count = 0;
        HashMap<UUID, Component> components = EntityManager.components.get(KeyInputComponent.class);

        // check if there are any keyInputComponents
        if (components == null) {
            if(debug) System.out.println("entities with key input: 0");
        } else {
            if(debug) System.out.println("entities with key input: " + components.size());

            // default velocity
            xVel = 0;
            yVel = 0;
            zVel = 0;

            // W-key has special functionality
            boolean buttonW = false;

            // react on buffered user input
            // move player
            if(isPressed(KeyCode.W)){
                if (Settings.gameType() == GameType.Roguelike) {
                    yVel = -movement;
                } else {
                    buttonW = true;
                }
            }
            if (isPressed(KeyCode.S)){
                if (Settings.gameType() == GameType.Roguelike) {
                    yVel = movement;
                }
            }
            if(isPressed(KeyCode.A)){
                xVel = -movement;
            }
            if(isPressed(KeyCode.D)){
                xVel = movement;
            }

            // traverse all keyInputComponents
            for(Map.Entry<UUID, ? extends Component> entry : components.entrySet()) {
                UUID uuid = entry.getKey();
                Component component = entry.getValue();

                // check if component is enabled
                if (component.isEnabled()) {

                    Entity entity = EntityManager.getEntity(uuid);

                    // update jump
                    if (buttonW && entity.hasComponent(JumpComponent.class)) {
                        Component jumpComponent = entity.getComponent(JumpComponent.class);
                        if ((boolean) jumpComponent.getValue()) {
                            yVel = jump;
                            jumpComponent.setValue(false);
                            eventCommandSystem.addEvent(new GameEvent(GameEvent.ENTITY_JUMP));
                        }

                        count++;
                    }

                    // update velocity
                    if (entity.hasComponent(VelocityComponent.class)) {
                        Component velocityComponent = entity.getComponent(VelocityComponent.class);
                        // restore current y-velocity
                        if (Settings.gameType() == GameType.Platformer) {
                            Point3D velocity = (Point3D) velocityComponent.getValue();
                            if (yVel == 0) {
                                yVel = velocity.getY();
                            }
                        }
                        // set velocity
                        velocityComponent.setValue(new Point3D(xVel, yVel, zVel));

                        count++;
                    }
                }
            }
        }

        if(debug)
        {
            System.out.println("key input components: " + count);
            System.out.println("KeyInputSystem <end>");
        }
    }

    /**
     * helper function to parse key events
     *
     * @param key
     *      keycode
     * @return
     *      boolean: is key pressed
     */
    private boolean isPressed(KeyCode key){
        return keyInput.getOrDefault(key,false);
    }
}