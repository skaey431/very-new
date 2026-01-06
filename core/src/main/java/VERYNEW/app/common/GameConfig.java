package VERYNEW.app.common;

/**
 * Data model classes that match the JSON structure.
 * Designed with encapsulation and null-safety in mind.
 */
public class GameConfig {
    private PhysicsConfig physics;
    private EntitiesConfig entities;

    // Singleton instance to manage global access without tight coupling
    private static GameConfig instance;

    public static void setInstance(GameConfig config) {
        instance = config;
    }

    public static GameConfig get() {
        if (instance == null) {
            // Returns an empty config with default values to prevent immediate NullPointerException
            return new GameConfig();
        }
        return instance;
    }

    // Getters for encapsulation
    public PhysicsConfig getPhysics() {
        return physics != null ? physics : new PhysicsConfig();
    }

    public EntitiesConfig getEntities() {
        return entities != null ? entities : new EntitiesConfig();
    }

    public void setPhysics(PhysicsConfig physics) {
        this.physics = physics;
    }

    public void setEntities(EntitiesConfig entities) {
        this.entities = entities;
    }

    /**
     * Physics related configurations
     */
    public static class PhysicsConfig {
        public float ppm = 100.0f;
        public float gravity = -9.8f;
        public float jump_impulse = 1.5f;
        public float move_speed = 5.0f;
    }

    /**
     * Container for all game entities
     */
    public static class EntitiesConfig {
        public PlayerConfig player = new PlayerConfig();
        public EnemyConfig enemy = new EnemyConfig();
    }

    /**
     * Specific settings for the Player
     */
    public static class PlayerConfig {
        public int max_health = 100;
        public int attack_power = 10;
        public float max_velocity = 2.0f;
        public float move_force = 0.5f;
    }

    /**
     * Specific settings for Enemies
     */
    public static class EnemyConfig {
        public int max_health = 50;
        public int attack_power = 5;
        public float move_speed = 1.2f;
    }
}
