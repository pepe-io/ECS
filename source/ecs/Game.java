package ecs;

import ecs.audio.MockAudioPlayer;
import ecs.ecs.components.*;
import ecs.ecs.entities.Block;
import ecs.ecs.entities.Player;
import ecs.ecs.systems.SystemManager;
import ecs.event.EventNotifier;
import ecs.event.GameEvent;
import ecs.level.LevelLoader;
import ecs.settings.Settings;
import javafx.animation.AnimationTimer;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.geometry.Insets;
import javafx.geometry.Point3D;
import javafx.scene.*;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.stage.Stage;
import javafx.util.Duration;
import java.util.HashMap;

/*
 IF YOU ENCOUNTER ARTIFACTS
 ADD -Dprism.dirtyopts=false TO RUN CONFIGURATION
 https://stackoverflow.com/questions/48326012/javafx-animated-polygon-artifacts
 */

/**
 * main game loop
 */
public class Game extends Application {

    // settings
    Settings settings = Settings.getInstance();

    // stage, scene and pane
    public Stage stage;
    public Scene scene;
    public static Pane root = new Pane();

    // window dimensions
    private int windowHeight;
    private int windowWidth;

    // level dimensions
    public static int levelHeight;
    public static int levelWidth;

    // store key input in a hashmap
    public static HashMap<KeyCode,Boolean> keyInput = new HashMap<>();

    // camera
    private Camera camera = new PerspectiveCamera(true);
    // create pivot-point for camera
    Group cameraPivot = new Group();

    // player
    public static Player player;

    // camera rotation values (mouse-driven)
    private double anchorX, anchorY;
    private double anchorAngleX = 0;
    private double anchorAngleY = 0;
    private final DoubleProperty angleX = new SimpleDoubleProperty();
    private final DoubleProperty angleY = new SimpleDoubleProperty();

    // level-loader, system-manager, event-notifier, audio-player
    private LevelLoader levelLoader = new LevelLoader();
    private SystemManager systemManager;
    private EventNotifier eventNotifier = EventNotifier.getInstance();
    private MockAudioPlayer audioPlayer = new MockAudioPlayer();

    Block test;

    /**
     * constructor
     */
    public Game() {

    }

    /**
     * main-method
     */
    public static void main(String[] args) {
        launch(args);
    }

    /**
     * init ressources
     */
    @Override
    public void init() {
        System.out.println("Running...");
        windowHeight = Settings.getWindowHeight();
        windowWidth = Settings.getWindowWidth();

        // load level
        levelLoader.load(1);

        // store level dimensions
        levelHeight = levelLoader.getMapHeight();
        levelWidth = levelLoader.getMapWidth();
        System.out.println("map dimensions: x: "+levelWidth+" y: "+levelHeight);

        // init & run the SystemManager
        systemManager = new SystemManager();
        systemManager.init();
    }

    /**
     * javafx start method
     *
     * @param stage
     *      stage
     */
    @Override
    public void start(Stage stage) {
        // create a master pane
        StackPane masterPane = new StackPane();
        scene = new Scene(masterPane, windowWidth, windowHeight, true);
        stage.setTitle(Settings.getTitle());
        stage.setResizable(false);

        // create separate sub-scenes for bg, game & gui overlay

        // bg-scene
        SubScene bgScene = new SubScene(createBackgroundContent(), windowWidth, windowHeight);

        // game-scene
        SubScene gameScene = new SubScene(createGameContent(), windowWidth, windowHeight, true, SceneAntialiasing.BALANCED);

        // attach camera to scene
        gameScene.setCamera(camera);

        // gui-scene
        SubScene guiScene = new SubScene(createGUIContent(), windowWidth, windowHeight);

        // add content
        masterPane.getChildren().addAll(bgScene, gameScene, guiScene);

        // add mouse-control to camera
        if (Settings.cameraRotation()) initMouseControl();

        // capture user input into buffer
        scene.setOnKeyPressed(event-> keyInput.put(event.getCode(), true));
        scene.setOnKeyReleased(event -> keyInput.put(event.getCode(), false));

        // game loop
        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                update();
            }
        };
        timer.start();

        // set scene and show
        stage.setScene(scene);
        stage.show();

        // register audio-player as event-handler
        eventNotifier.addEventhandler(GameEvent.ANY, audioPlayer);

        // test ECS: add, modify, delete entity during runtime
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(1), e -> {
                    player.addComponent(new LightComponent(Color.BLUE));
                    player.addComponent(new ColliderComponent(10,50,10));
                }),
                new KeyFrame(Duration.seconds(2), e -> {
                    test = new Block(200, 500, -200);
                }),
                new KeyFrame(Duration.seconds(3), e -> {
                    test.addComponent(new LightComponent(Color.RED));
                }),
                new KeyFrame(Duration.seconds(5), e -> {
//                    test.removeComponent(test.getComponent(ShapeComponent.class));
                }),
                new KeyFrame(Duration.seconds(6), e -> {
                    test.getComponent(PositionComponent.class).setValue(new Point3D(200,500,0));
//                    test.removeComponent(test.getComponent(ColliderComponent.class));
                    test.addComponent(new VelocityComponent());
                    test.addComponent(new GravityComponent());
                }),
                new KeyFrame(Duration.seconds(10), e -> {
                    test.delete();
                })
        );
        timeline.play();
    }

    /**
     * create assets for game loop
     * @return
     *      pane
     */
    private Pane createGameContent() {
        root.setPrefSize(windowWidth, windowHeight);

        // setup the camera
        camera.setNearClip(1);
        camera.setFarClip(5000);
        cameraPivot.getChildren().add(camera);


        cameraPivot.translateXProperty().set((double) windowWidth /2);
        cameraPivot.translateYProperty().set((double) windowHeight /2);
        camera.translateZProperty().set(-1110);

        if (Settings.fancyCamera()) {
            cameraPivot.getTransforms().addAll(
                    new Rotate(-10, Rotate.X_AXIS),
                    new Rotate(10, Rotate.Y_AXIS),
                    new Rotate(7, Rotate.Z_AXIS)
            );
        }

        root.getChildren().add(cameraPivot);

        return root;
    }

    private Pane createBackgroundContent() {
        Pane bgPane = new Pane();
        bgPane.setStyle("-fx-background-color: #222222;");

        bgPane.getChildren().addAll();
        return bgPane;
    }

    private Pane createGUIContent() {
        AnchorPane guiPane = new AnchorPane();
        guiPane.setPrefSize(windowWidth, 50);
        guiPane.setTranslateY(windowHeight - 25);

        VBox vbox = new VBox();
        vbox.setPadding(new Insets(5,5,5,5));
        vbox.setSpacing(10);

        Text text = new Text();
        text.setText("GUI");
        vbox.getChildren().addAll(text);

        Rectangle rect = new Rectangle(windowWidth, 30, Color.DARKGREY);

        guiPane.setOpacity(0.85);
        guiPane.getChildren().addAll(rect, vbox);
        return guiPane;
    }

    /**
     * main update tick
     */
    private void update() {
        // run the SystemManager
        systemManager.update();

        // move camera to player position
        if (Settings.cameraFollowPlayer()) {
            Point3D playerPosition = (Point3D) player.getComponent(PositionComponent.class).getValue();
            cameraPivot.setTranslateX(playerPosition.getX());
            cameraPivot.setTranslateY(playerPosition.getY());
            cameraPivot.setTranslateZ(playerPosition.getZ());
        }

        if (Settings.cameraRotation()) {
            // reset camera
            if (isPressed(KeyCode.R)) {
                camera.rotateProperty().set(0);
                angleX.set(0);
                angleY.set(0);
            }
            // rotate camera (z-axis)
            if (isPressed(KeyCode.Q)) {
                camera.rotateProperty().set(camera.getRotate() - 0.1);
            }
            if (isPressed(KeyCode.E)) {
                camera.rotateProperty().set(camera.getRotate() + 0.1);
            }
        }
    }

    /**
     * release resources
     */
    @Override
    public void stop() {

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

    /**
     * mouse control for camera
     */
    private void initMouseControl() {
        // dampen camera rotation (0 -> not dampened)
        double dampener = 25;

        // define rotation
        Rotate xRotate;
        Rotate yRotate;
        cameraPivot.getTransforms().addAll(
                xRotate = new Rotate(0, Rotate.X_AXIS),
                yRotate = new Rotate(0, Rotate.Y_AXIS)
        );
        xRotate.angleProperty().bind(angleX);
        yRotate.angleProperty().bind(angleY);

        // catch mouse event start
        scene.setOnMousePressed(e -> {
            anchorX = e.getSceneX();
            anchorY = e.getSceneY();
            anchorAngleX = angleX.get();
            anchorAngleY = angleY.get();
        });

        // calculate offset and convert it to rotation
        scene.setOnMouseDragged(e -> {
            angleX.set(anchorAngleX - (anchorY - e.getSceneY()) / (dampener + 1) * -1);
            angleY.set(anchorAngleY + (anchorX - e.getSceneX()) / (dampener + 1) * -1);
        });
    }
}
