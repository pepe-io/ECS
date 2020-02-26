package ecs.settings;

import javafx.geometry.Point3D;
import javafx.scene.shape.Box;
import java.util.HashMap;

/**
 * settings for the game
 * singleton
 * just getters ...
 */
public class Settings {

    private static Settings settings = new Settings();

    // set game type
    private static final GameType GAME_TYPE = GameType.Platformer;

    // set camera to follow the player
    private static final boolean CAMERA_FOLLOW_PLAYER = true;

    // enable camera rotation by mouse
    private static final boolean CAMERA_ROTATION = true;

    // rotate the camera a bit to make the game more interesting
    private static final boolean FANCY_CAMERA = true;

    // rotate and scale the blocks a bit to make the map more interesting
    private static final boolean FANCY_BLOCKS = true;

    // title of the game
    private static final String TITLE = "ECS";
    // window dimensions
    private static final int WINDOW_WIDTH = 800;
    private static final int WINDOW_HEIGHT = WINDOW_WIDTH / 4 * 3;
    // generic blocksize
    private static final int BLOCKSIZE = 50;
    // player movement-speed
    private static final int SPEED = 5;
    // player jump-velocity
    private static final int JUMP = -15;
    // gravity strength
    private static final double GRAVITY = 0.5;
    // enable audio player
    private static final boolean PLAY_SOUNDS = true;

    // movement system
    // safe spot for self-collision
    private static Point3D SAFE_SPOT = new Point3D(0, 0, 0);
    // fallback size for dummy box
    private static Box fallBackSize = new Box(5,5,5);
    // set the size of the smallest entity here
    // stepsize is used during collision detection
    private static int stepsize = BLOCKSIZE;

    private static final HashMap<String, Boolean> DEBUG = new HashMap<>();

    /**
     * private constructor to force singleton
     */
    private Settings() {

    }

    public static Settings getInstance() {
        // debug options
        DEBUG.put("LevelLoader@entityCreation", false);
        DEBUG.put("SystemManager@init", true);
        DEBUG.put("SystemManager@update", false);
        DEBUG.put("MovementSystem@dummy", false);
        DEBUG.put("MovementSystem@stepsize", false);
        DEBUG.put("Light@color", false);
        DEBUG.put("AudioPlayer@notify", true);
        return settings;
    }

    /**
     * GETTER
     */

    public static String getTitle() {
        return TITLE;
    }

    public static int getWindowWidth() {
        return WINDOW_WIDTH;
    }

    public static int getWindowHeight() {
        return WINDOW_HEIGHT;
    }

    public static int getBlocksize() {
        return BLOCKSIZE;
    }

    public static Boolean getDebug(String key) {
        return DEBUG.get(key);
    }

    public static int getSpeed() {
        return SPEED;
    }

    public static int getJump() {
        return JUMP;
    }

    public static double getGravity() {
        return GRAVITY;
    }

    public static boolean getPlaySounds() {
        return PLAY_SOUNDS;
    }

    public static Point3D getSafeSpot() {
        return SAFE_SPOT;
    }

    public static Box getFallBackSize() {
        return fallBackSize;
    }

    public static int getStepsize() {
        return stepsize;
    }

    public static GameType gameType() {
        return GAME_TYPE;
    }

    public static boolean cameraFollowPlayer() {
        return CAMERA_FOLLOW_PLAYER;
    }

    public static boolean cameraRotation() {
        return CAMERA_ROTATION;
    }

    public static boolean fancyCamera() {
        return FANCY_CAMERA;
    }

    public static boolean fancyBlocks() {
        return FANCY_BLOCKS;
    }
}
