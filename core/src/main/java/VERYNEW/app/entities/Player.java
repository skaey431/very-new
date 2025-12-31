package VERYNEW.app.entities;

import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import VERYNEW.app.common.Constants;
import VERYNEW.app.common.InputHandler;

/**
 * 플레이어의 물리 바디 및 입력에 따른 이동 로직을 담당합니다.
 */
public class Player {
    private World world;
    private Body body;

    // 이동 관련 수치
    private final float maxVelocity = 2.0f;
    private final float moveForce = 0.5f;
    private final float jumpImpulse = 3.5f;

    public Player(World world, Vector2 spawnPoint) {
        this.world = world;
        definePlayer(spawnPoint);
    }

    private void definePlayer(Vector2 position) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.DynamicBody;
        bdef.position.set(position);
        bdef.fixedRotation = true;

        body = world.createBody(bdef);

        FixtureDef fdef = new FixtureDef();
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(15 / Constants.PPM, 20 / Constants.PPM);

        fdef.shape = shape;
        fdef.density = 1.0f;
        fdef.friction = 0.2f; // 바닥과의 마찰
        fdef.filter.categoryBits = Constants.BIT_PLAYER;

        body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    public void update(float dt) {
        handleInput();
    }

    /**
     * InputHandler의 정적 변수를 체크하여 플레이어에게 힘을 가합니다.
     */
    private void handleInput() {
        Vector2 vel = body.getLinearVelocity();

        // 왼쪽 이동
        if (InputHandler.moveLeft && vel.x >= -maxVelocity) {
            body.applyLinearImpulse(new Vector2(-moveForce, 0), body.getWorldCenter(), true);
        }
        // 오른쪽 이동
        if (InputHandler.moveRight && vel.x <= maxVelocity) {
            body.applyLinearImpulse(new Vector2(moveForce, 0), body.getWorldCenter(), true);
        }
        // 점프 (단순히 y 속도가 거의 0일 때만 점프 가능하도록 처리)
        // 실제로는 ContactListener를 통해 바닥 접촉 여부를 판별하는 것이 더 정확합니다.
        if (InputHandler.jump && Math.abs(vel.y) < 0.01f) {
            body.applyLinearImpulse(new Vector2(0, jumpImpulse), body.getWorldCenter(), true);
            InputHandler.jump = false;
        }

        // 입력이 없을 때 좌우 감속 처리 (마찰력 보완)
        if (!InputHandler.moveLeft && !InputHandler.moveRight) {
            body.setLinearVelocity(vel.x * 0.9f, vel.y);
        }
    }

    public void draw(SpriteBatch batch) {
        // 이미지 렌더링 시 사용
    }

    public Body getBody() {
        return body;
    }
}
