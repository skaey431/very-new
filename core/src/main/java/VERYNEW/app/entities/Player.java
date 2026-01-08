package VERYNEW.app.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import VERYNEW.app.common.Constants;
import VERYNEW.app.common.InputHandler;
import VERYNEW.app.common.GameConfig;

/**
 * 플레이어 개체 클래스.
 * 상태 값과 물리 설정 연동에 집중하며, 충돌 판단은 WorldContactListener에게 맡깁니다.
 */
public class Player implements Entity {
    private World world;
    private Body body;

    private int currentHealth;
    private boolean isAlive;

    // 지면 접촉 상태 카운트 (ContactListener가 업데이트함)
    private int footContacts = 0;

    public Player(World world, Vector2 spawnPoint) {
        this.world = world;

        if (GameConfig.get() == null) {
            throw new IllegalStateException("GameConfig가 로드되지 않았습니다.");
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
        // 바디의 UserData에 Player 객체 자신을 저장 (ContactListener에서 찾기 위함)
        body.setUserData(this);

        // 1. 메인 몸체 Fixture 생성
        createMainFixture(ppm);

        // 2. 발 센서 생성
        createFootSensor(ppm);
    }

    private void createMainFixture(float ppm) {
        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(15 / ppm, 20 / ppm);

        fdef.shape = shape;
        fdef.density = 1.0f;
        fdef.friction = 0.2f;
        fdef.filter.categoryBits = Constants.BIT_PLAYER;
        fdef.filter.maskBits = (short) (Constants.BIT_WALL | Constants.BIT_TRAP | Constants.BIT_GOAL);

        body.createFixture(fdef); // 메인 몸체는 별도 UserData 불필요
        shape.dispose();
    }

    private void createFootSensor(float ppm) {
        FixtureDef fdef = new FixtureDef();
        PolygonShape footShape = new PolygonShape();
        // 몸체 하단 배치
        footShape.setAsBox(13 / ppm, 2 / ppm, new Vector2(0, -20 / ppm), 0);

        fdef.shape = footShape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = Constants.BIT_PLAYER;

        // 센서 식별용 키워드 등록
        body.createFixture(fdef).setUserData("player_foot");
        footShape.dispose();
    }

    public boolean isGrounded() {
        return footContacts > 0;
    }

    public void setGrounded(boolean grounded) {
        if (grounded) footContacts++;
        else footContacts = Math.max(0, footContacts - 1);
    }

    @Override
    public int getMaxHealth() {
        return GameConfig.get().getEntityConfig("player").max_health;
    }

    @Override
    public int getCurrentHealth() { return currentHealth; }

    @Override
    public void takeDamage(int amount) {
        if (!isAlive) return;
        currentHealth -= amount;
        if (currentHealth <= 0) die();
    }

    @Override
    public boolean isAlive() { return isAlive; }

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
            target.takeDamage(GameConfig.get().getEntityConfig("player").attack_power);
        }
    }

    private void handleInput() {
        if (body == null) return;

        Vector2 vel = body.getLinearVelocity();
        GameConfig.EntityConfig cfg = GameConfig.get().getEntityConfig("player");

        // 좌우 이동
        if (InputHandler.moveLeft && vel.x >= -cfg.max_velocity) {
            body.applyLinearImpulse(new Vector2(-cfg.move_force, 0), body.getWorldCenter(), true);
        }
        if (InputHandler.moveRight && vel.x <= cfg.max_velocity) {
            body.applyLinearImpulse(new Vector2(cfg.move_force, 0), body.getWorldCenter(), true);
        }

        // 점프 (Grounded 상태일 때만)
        if (InputHandler.jump && isGrounded()) {
            float jumpImpulse = GameConfig.get().getPhysics().jump_impulse;
            body.setLinearVelocity(vel.x, 0); // 점프 시 y속도 초기화로 일관성 부여
            body.applyLinearImpulse(new Vector2(0, jumpImpulse), body.getWorldCenter(), true);
            InputHandler.jump = false;
        }

        // 감속 처리
        if (!InputHandler.moveLeft && !InputHandler.moveRight) {
            body.setLinearVelocity(vel.x * 0.9f, vel.y);
        }
    }

    public void draw(SpriteBatch batch) {}
    public Body getBody() { return body; }
}
