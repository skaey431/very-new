package VERYNEW.app.physics;

import com.badlogic.gdx.physics.box2d.*;
import VERYNEW.app.entities.Player;

/**
 * Box2D 월드 내의 모든 충돌 이벤트를 감지하고 처리합니다.
 * 플레이어의 접지(Grounded) 상태 등을 업데이트합니다.
 */
public class WorldContactListener implements ContactListener {

    @Override
    public void beginContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // 두 피스처 중 하나라도 플레이어의 발 센서인지 확인
        checkFootContact(fixA, fixB, true);
    }

    @Override
    public void endContact(Contact contact) {
        Fixture fixA = contact.getFixtureA();
        Fixture fixB = contact.getFixtureB();

        // 발 센서가 떨어졌을 때 처리
        checkFootContact(fixA, fixB, false);
    }

    private void checkFootContact(Fixture a, Fixture b, boolean isBegin) {
        // 어느 쪽이 발 센서인지 식별
        if (a.getUserData() != null && a.getUserData().equals("player_foot")) {
            processGrounded(a, isBegin);
        } else if (b.getUserData() != null && b.getUserData().equals("player_foot")) {
            processGrounded(b, isBegin);
        }
    }

    private void processGrounded(Fixture footFixture, boolean isBegin) {
        // 피스처를 통해 해당 바디의 플레이어 객체를 찾아 상태 업데이트
        Object bodyData = footFixture.getBody().getUserData();
        if (bodyData instanceof Player) {
            ((Player) bodyData).setGrounded(isBegin);
        }
    }

    @Override public void preSolve(Contact contact, Manifold oldManifold) {}
    @Override public void postSolve(Contact contact, ContactImpulse impulse) {}
}
