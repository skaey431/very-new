package VERYNEW.app.common;

import java.util.HashMap;
import java.util.Map;

/**
 * JSON 구조와 매칭되는 데이터 모델 클래스입니다.
 * Map 구조를 사용하여 새로운 엔티티 타입(다양한 적 등)이 추가되어도
 * 자바 코드 수정 없이 master.json 설정만으로 확장 가능하도록 설계되었습니다.
 */
public class GameConfig {
    private PhysicsConfig physics;
    // 엔티티 설정을 이름(String)별로 관리하여 확장성을 확보합니다.
    private Map<String, EntityConfig> entities = new HashMap<>();

    // 전역 접근을 위한 싱글톤 인스턴스
    private static GameConfig instance;

    public static void setInstance(GameConfig config) {
        instance = config;
    }

    public static GameConfig get() {
        if (instance == null) {
            // NullPointerException 방지를 위해 기본값을 가진 빈 설정 객체 반환
            return new GameConfig();
        }
        return instance;
    }

    // 캡슐화를 위한 Getter 및 Setter
    public PhysicsConfig getPhysics() {
        return physics != null ? physics : new PhysicsConfig();
    }

    public void setPhysics(PhysicsConfig physics) {
        this.physics = physics;
    }

    public Map<String, EntityConfig> getEntities() {
        return entities;
    }

    /**
     * 특정 엔티티의 설정을 안전하게 가져옵니다.
     * @param name 엔티티 식별자 (예: "player", "slime", "boss_dragon")
     * @return 해당 엔티티의 설정 객체, 없을 경우 기본 설정 객체 반환
     */
    public EntityConfig getEntityConfig(String name) {
        return entities.getOrDefault(name, new EntityConfig());
    }

    /**
     * 물리 엔진 관련 공통 설정
     */
    public static class PhysicsConfig {
        public float ppm = 100.0f;
        public float gravity = -9.8f;
        public float jump_impulse = 1.5f;
        public float move_speed = 5.0f;
    }

    /**
     * 모든 엔티티(플레이어, 각종 적, NPC 등)가 공유하는 기본 데이터 모델입니다.
     * 새로운 적 타입이 추가되어도 이 구조를 공통으로 사용하거나 필드를 추가하여 대응합니다.
     */
    public static class EntityConfig {
        public int max_health = 100;
        public int attack_power = 10;
        public float move_speed = 2.0f;
        public float max_velocity = 2.0f;
        public float move_force = 0.5f; // 플레이어 등 가속도가 필요한 개체용
    }
}
