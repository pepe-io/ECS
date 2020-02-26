package ecs.level;

import ecs.ecs.entities.Block;
import ecs.ecs.entities.Light;
import ecs.ecs.entities.Player;
import ecs.Game;
import ecs.settings.Settings;
import javafx.geometry.Point3D;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.paint.Color;
import javafx.scene.paint.PhongMaterial;

/**
 * this class creates all assets for the level
 */
public class LevelLoader {

    // some variables
    private int level, mapWidth, mapHeight;
    // some pathes
    private String levelPath = "ecs/data/level/";
    private String texturesPath = "ecs/data/textures/";

    // debug
    private boolean debugEntitySpawn = Settings.getDebug("LevelLoader@entityCreation");
    private int blocksize = Settings.getBlocksize();

    /**
     * constructor
     */
    public LevelLoader() {

    }

    /**
     * loads assets
     */
    public void load(int level) {
        this.level = level;

        if(debugEntitySpawn) System.err.println("LevelLoader <start>");

        // read the level-map
        Image img = new Image(levelPath + "/level"+level+".png");
        PixelReader pixelReader = img.getPixelReader();
        int imgWidth = (int) img.getWidth();
        int imgHeight = (int) img.getHeight();

        // set map
        mapWidth = imgWidth * blocksize;
        mapHeight = imgHeight * blocksize;

        // create materials
        PhongMaterial blockMaterial = new PhongMaterial(Color.GREY);
        PhongMaterial playerMaterial = new PhongMaterial(Color.ORANGE);

        // traverse all pixels of levelmap and create blocks
        for (int x = 0; x<imgWidth; x++) {
            for (int y = 0; y<imgHeight; y++) {
                Color c = pixelReader.getColor(x, y);

                // blocks
                if (c.equals(Color.BLACK)) {
                    Point3D rotation = new Point3D(0,0,0);
                    double z = 0;

                    // just to make the map a little bit more interesting
                    // rotate and scale the blocks a little bit
                    if (Settings.fancyBlocks()) {
                        double max_rotation = 10;
                        rotation = new Point3D(Math.random() * max_rotation - 0.5 * max_rotation, Math.random() * max_rotation - 0.5 * max_rotation, Math.random() * max_rotation - 0.5 * max_rotation);

                        double max_z = 1;
                        z = Math.random() * max_z - 0.5 * max_z;
                    }

                    Block block = new Block(x*blocksize, y*blocksize, z*blocksize, rotation);
                    block.setMaterial(blockMaterial);
                }

                // player spawn point
                else if (c.equals(Color.rgb(0,255,0))) {
                    double z = 0;
                    if(debugEntitySpawn) System.out.println("start @ (x: "+x*blocksize+", y: "+y*blocksize+", z: "+z*blocksize+")");
                    Player player = new Player(x*blocksize, y*blocksize, z*blocksize);
                    player.setMaterial(playerMaterial);
                    Game.player = player;
                }

                // player goal
                else if (c.equals(Color.rgb(255,0,0))) {
                    // needs implementation
                    double z = 0;
                    if(debugEntitySpawn) System.out.println("end @ (x: "+x*blocksize+", y: "+y*blocksize+", z: "+z*blocksize+")");
                }

                // lights
                else if (c.equals(Color.YELLOW)) {
                    double z = -((Math.random()*50) + 50);
                    if(debugEntitySpawn) System.out.println("light @ (x: "+x*blocksize+", y: "+y*blocksize+", z: "+z*blocksize+")");
                    Light light = new Light(x*blocksize, y*blocksize, z*blocksize);
                }
            }
        }

        if(debugEntitySpawn) System.out.println("LevelLoader <end>");
    }

    /**
     * get map size
     *
     * @return
     *      map width
     */
    public int getMapWidth() {
        return mapWidth;
    }

    /**
     * get map size
     *
     * @return
     *      map height
     */
    public int getMapHeight() {
        return mapHeight;
    }
}
