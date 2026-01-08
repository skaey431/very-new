package VERYNEW.app.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import VERYNEW.app.common.Constants;
import VERYNEW.app.common.InputHandler;
import VERYNEW.app.common.GameConfig;

/**
 * 플레이어의 물리 바디 및 입력에 따른 이동 로직을 담당합니다.
 * 설정값은 GameConfig를 통해 외부 JSON 파일(master.json -> player.json)과 동적으로 연동됩니다.
 */
public class Player implements Entity {
    private World world;
    private Body body;

    private int currentHealth;
    private boolean isAlive;

    public Player(World world, Vector2 spawnPoint) {
        this.world = world;

        // GameConfig 초기화 확인
        if (GameConfig.get() == null) {
            throw new IllegalStateException("GameConfig가 초기화되지 않았습니다. ConfigLoader.loadConfig()를 먼저 호출하세요.");
        }

        this.currentHealth = getMaxHealth();
        this.isAlive = true;
        definePlayer(spawnPoint);
    }

    private void definePlayer(Vector2 position) {
        float ppm = GameConfig.get().getPhysics().ppm;

        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(position);
        bdef.fixedRotation = true;

        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        // 물리 형태 크기 설정
        shape.setAsBox(15 / ppm, 20 / ppm);

        fdef.shape = shape;
        fdef.density = 1.0f;
        fdef.friction = 0.2f;
        fdef.filter.categoryBits = Constants.BIT_PLAYER;

        body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    @Override
    public int getMaxHealth() {
        // Map 구조에서 "player" 키를 사용하여 안전하게 설정을 가져옵니다.
        return GameConfig.get().getEntityConfig("player").max_health;
    }

    @Override
    public int getCurrentHealth() {
        return currentHealth;
    }

    @Override
    public void takeDamage(int amount) {
        if (!isAlive) return;

        currentHealth -= amount;
        if (currentHealth <= 0) {
            die();
        }
    }

    @Override
    public boolean isAlive() {
        return isAlive;
    }

    @Override
    public void die() {
        isAlive = false;
        currentHealth = 0;
    }

    public void update(float dt) {
        if (isAlive && body != null) {
            handleInput();
        }
    }
    

    @Override
    public void attack(Entity target) {
        if (target != null && isAlive) {
            // 공격력 또한 Map에서 동적으로 가져옵니다.
            int power = GameConfig.get().getEntityConfig("player").attack_power;
            target.takeDamage(power);
        }
    }

    /**
     * GameConfig의 물리 및 엔티티 설정값을 참조하여 플레이어의 이동을 제어합니다.
     */
    private void handleInput() {
        if (body == null) return;

        Vector2 vel = body.getLinearVelocity();

        // Map에서 플레이어 전용 설정을 가져옵니다.
        GameConfig.EntityConfig playerCfg = GameConfig.get().getEntityConfig("player");
        GameConfig.PhysicsConfig physCfg = GameConfig.get().getPhysics();

        float maxVelocity = playerCfg.max_velocity;
        float moveForce = playerCfg.move_force;
        float jumpImpulse = physCfg.jump_impulse;

        // 왼쪽 이동
        if (InputHandler.moveLeft && vel.x >= -maxVelocity) {
            body.applyLinearImpulse(new Vector2(-moveForce, 0), body.getWorldCenter(), true);
        }
        // 오른쪽 이동
        if (InputHandler.moveRight && vel.x <= maxVelocity) {
            body.applyLinearImpulse(new Vector2(moveForce, 0), body.getWorldCenter(), true);
        }

        // 점프 (바닥에 닿아있을 때만 가능하도록 간략한 체크)
        if (InputHandler.jump && Math.abs(vel.y) < 0.01f) {
            body.applyLinearImpulse(new Vector2(0, jumpImpulse), body.getWorldCenter(), true);
            InputHandler.jump = false;
        }

        // 입력이 없을 때 좌우 감속 처리
        if (!InputHandler.moveLeft && !InputHandler.moveRight) {
            body.setLinearVelocity(vel.x * 0.9f, vel.y);
        }
    }

    public void draw(SpriteBatch batch) {
        // 렌더링 로직 (추후 구현)
    }

    public Body getBody() {
        return body;
    }
}
