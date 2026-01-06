package VERYNEW.app.common;

import com.google.gson.Gson;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

/**
 * Multiple JSON files are read to initialize GameConfig.
 * Ensure this is called inside the create() method of your ApplicationListener.
 */
public class ConfigLoader {

    private static final Gson gson = new Gson();

    public static void loadConfig() {
        try {
            // Create a root config object
            GameConfig fullConfig = new GameConfig();

            // 1. Load Physics Configuration
            GameConfig.PhysicsConfig physics = loadJson("configs/physics.json", GameConfig.PhysicsConfig.class);
            if (physics == null) throw new RuntimeException("physics.json is missing or corrupted!");
            fullConfig.setPhysics(physics);

            // 2. Load Entities Configuration
            GameConfig.EntitiesConfig entities = new GameConfig.EntitiesConfig();

            entities.player = loadJson("configs/player.json", GameConfig.PlayerConfig.class);
            if (entities.player == null) throw new RuntimeException("player.json is missing!");

            entities.enemy = loadJson("configs/enemy.json", GameConfig.EnemyConfig.class);
            if (entities.enemy == null) throw new RuntimeException("enemy.json is missing!");

            fullConfig.setEntities(entities);

            // 3. Register as singleton
            GameConfig.setInstance(fullConfig);

            System.out.println("ConfigLoader: All modular configuration files loaded successfully!");
        } catch (Exception e) {
            System.err.println("ConfigLoader: CRITICAL ERROR during initialization");
            e.printStackTrace();
            // This will help Gradle show the actual cause in the console
            throw new RuntimeException("Config initialization failed: " + e.getMessage(), e);
        }
    }

    private static <T> T loadJson(String filePath, Class<T> type) {
        try {
            // Gdx.files.internal only works AFTER LibGDX is initialized
            FileHandle fileHandle = Gdx.files.internal(filePath);
            if (!fileHandle.exists()) {
                System.err.println("ConfigLoader: File not found -> assets/" + filePath);
                return null;
            }
            return gson.fromJson(fileHandle.readString(), type);
        } catch (Exception e) {
            System.err.println("ConfigLoader: Error parsing " + filePath + " -> " + e.getMessage());
            return null;
        }
    }
}
