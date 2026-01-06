package VERYNEW.app.entities;

/**
 * 모든 게임 내 개체의 기본이 되는 인터페이스입니다.
 */
public interface Entity {
    // 체력 관련
    int getMaxHealth();
    int getCurrentHealth();
    void takeDamage(int amount);

    // 상태 관련
    boolean isAlive();
    void die();

    // 행위 관련
    void update(float deltaTime);
    void attack(Entity target);
}
