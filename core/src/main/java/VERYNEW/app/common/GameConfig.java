package VERYNEW.app.common;

import java.util.HashMap;
import java.util.Map;

/**
 * JSON 구조와 매칭되는 데이터 모델 클래스입니다.
 * 필드 이름이 JSON의 키값과 일치해야 하며,
 * 파서가 객체를 올바르게 생성할 수 있도록 기본 생성자를 보장합니다.
 */
public class GameConfig {
    // 필드명을 JSON 키값(physics, entities)과 정확히 일치시킵니다.
    private PhysicsConfig physics;
    private Map<String, EntityConfig> entities = new HashMap<>();

    private static GameConfig instance;

    public static void setInstance(GameConfig config) {
        instance = config;
    }

    public static GameConfig get() {
        if (instance == null) {
            instance = new GameConfig();
            // 기본값 강제 할당 (JSON 로드 실패 대비)
            if (instance.physics == null) instance.physics = new PhysicsConfig();
        }
        return instance;
    }

    public PhysicsConfig getPhysics() {
        return physics != null ? physics : new PhysicsConfig();
    }

    public void setPhysics(PhysicsConfig physics) {
        this.physics = physics;
    }

    public Map<String, EntityConfig> getEntities() {
        return entities;
    }

    public void setEntities(Map<String, EntityConfig> entities) {
        this.entities = entities;
    }

    public EntityConfig getEntityConfig(String name) {
        return (entities != null && entities.containsKey(name))
            ? entities.get(name)
            : new EntityConfig();
    }

    /**
     * 물리 엔진 설정 데이터 모델.
     * JSON의 "physics" 객체와 매핑됩니다.
     */
    public static class PhysicsConfig {
        public float ppm = Constants.PPM;
        public float gravity = Constants.GRAVITY;
        public float jump_impulse = 1.5f;
        public float move_speed = 5.0f;

        // JSON 파서를 위한 기본 생성자
        public PhysicsConfig() {}
    }

    /**
     * 엔티티 설정 데이터 모델.
     * JSON의 "entities" 내부 객체들과 매핑됩니다.
     */
    public static class EntityConfig {
        public int max_health = 100;
        public int attack_power = 10;
        public float move_speed = 2.0f;
        public float max_velocity = 2.0f;
        public float move_force = 0.5f;

        // JSON 파서를 위한 기본 생성자
        public EntityConfig() {}
    }
}
