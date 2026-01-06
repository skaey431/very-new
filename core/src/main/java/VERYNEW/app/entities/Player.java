package VERYNEW.app.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import VERYNEW.app.common.Constants;
import VERYNEW.app.common.InputHandler;
import VERYNEW.app.common.GameConfig;

/**
 * 플레이어의 물리 바디 및 입력에 따른 이동 로직을 담당합니다.
 * 설정값은 GameConfig를 통해 외부 JSON 파일(physics.json, player.json)과 연동됩니다.
 */
public class Player implements Entity {
    private World world;
    private Body body;

    private int currentHealth;
    private boolean isAlive;

    public Player(World world, Vector2 spawnPoint) {
        this.world = world;

        // GameConfig가 로드되었는지 확인하는 방어 코드
        if (GameConfig.get() == null || GameConfig.get().getEntities() == null || GameConfig.get().getPhysics() == null) {
            // 이 예외가 발생한다면 ConfigLoader.loadConfig() 호출 시점이나 JSON 파일 내용을 확인해야 합니다.
            throw new IllegalStateException("GameConfig가 초기화되지 않았거나 설정 파일 형식이 잘못되었습니다.");
        }

        this.currentHealth = getMaxHealth();
        this.isAlive = true;
        definePlayer(spawnPoint);
    }

    private void definePlayer(Vector2 position) {
        // null 체크를 마쳤으므로 안전하게 접근
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
        if (GameConfig.get() != null && GameConfig.get().getEntities() != null && GameConfig.get().getEntities().player != null) {
            return GameConfig.get().getEntities().player.max_health;
        }
        return 100; // 최악의 경우를 대비한 기본값
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
        if (target != null && isAlive && GameConfig.get() != null) {
            target.takeDamage(GameConfig.get().getEntities().player.attack_power);
        }
    }

    /**
     * GameConfig의 물리 설정값을 참조하여 플레이어의 이동을 제어합니다.
     */
    private void handleInput() {
        if (body == null || GameConfig.get() == null) return;

        Vector2 vel = body.getLinearVelocity();

        // JSON에서 설정값 가져오기 (매 프레임 호출되므로 지역 변수에 할당)
        float maxVelocity = GameConfig.get().getEntities().player.max_velocity;
        float moveForce = GameConfig.get().getEntities().player.move_force;
        float jumpImpulse = GameConfig.get().getPhysics().jump_impulse;

        // 왼쪽 이동
        if (InputHandler.moveLeft && vel.x >= -maxVelocity) {
            body.applyLinearImpulse(new Vector2(-moveForce, 0), body.getWorldCenter(), true);
        }
        // 오른쪽 이동
        if (InputHandler.moveRight && vel.x <= maxVelocity) {
            body.applyLinearImpulse(new Vector2(moveForce, 0), body.getWorldCenter(), true);
        }

        // 점프
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
        // 렌더링 로직
    }

    public Body getBody() {
        return body;
    }
}
