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

    // 이동 관련 수치 (Constants로 옮겨도 좋습니다)
    private final float maxVelocity = 2.0f;
    private final float moveForce = 0.5f;
    private final float jumpImpulse = 4.0f;

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
        fdef.friction = 0.5f;
        fdef.filter.categoryBits = Constants.BIT_PLAYER;

        body.createFixture(fdef).setUserData(this);
        shape.dispose();
    }

    public void update(float dt) {
        handleInput();
    }

    /**
     * InputHandler의 상태에 따라 물리적인 힘을 가합니다.
     */
    private void handleInput() {
        // 왼쪽 이동
        if (InputHandler.moveLeft && body.getLinearVelocity().x >= -maxVelocity) {
            body.applyLinearImpulse(new Vector2(-moveForce, 0), body.getWorldCenter(), true);
        }
        // 오른쪽 이동
        if (InputHandler.moveRight && body.getLinearVelocity().x <= maxVelocity) {
            body.applyLinearImpulse(new Vector2(moveForce, 0), body.getWorldCenter(), true);
        }
        // 점프 (간단하게 y속도가 0일 때만 점프 가능하도록 처리)
        if (InputHandler.jump && body.getLinearVelocity().y == 0) {
            body.applyLinearImpulse(new Vector2(0, jumpImpulse), body.getWorldCenter(), true);
            InputHandler.jump = false; // 한 번만 점프하도록 초기화
        }
    }

    public void draw(SpriteBatch batch) {
        // 추후 애니메이션 및 텍스처 렌더링
    }

    public Body getBody() {
        return body;
    }
}
