package VERYNEW.app.map;

import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.objects.RectangleMapObject;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.*;
import VERYNEW.app.common.Constants;
import VERYNEW.app.entities.Player;

/**
 * Tiled Map의 객체 데이터를 읽어 Box2D 물리 바디로 변환하거나 데이터를 추출하는 팩토리 클래스입니다.
 */
public class ObjectFactory {

    /**
     * 맵 객체의 클래스에 따라 적절한 물리 바디 또는 엔티티를 생성합니다.
     * @return 생성된 플레이어 객체 (Spawn 포인트일 경우에만 반환, 나머지는 null)
     */
    public static Player createPhysics(MapObject object, World world) {
        String className = object.getProperties().get("class", String.class);
        if (className == null) {
            className = object.getProperties().get("type", String.class);
        }

        if (className == null) return null;

        if (object instanceof RectangleMapObject) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();

            // 1. Ground 처리
            if (className.equalsIgnoreCase(Constants.CLASS_GROUND)) {
                createStaticBody(world, rect, Constants.BIT_WALL, "ground");
            }
            // 2. Wall 처리
            else if (className.equalsIgnoreCase(Constants.CLASS_WALL)) {
                createStaticBody(world, rect, Constants.BIT_WALL, "wall");
            }
            // 3. Trap 처리
            else if (className.equalsIgnoreCase(Constants.CLASS_TRAP)) {
                createSensorBody(world, rect, Constants.BIT_TRAP, "trap");
            }
            // 4. Spawn 처리 (플레이어 생성 및 반환)
            else if (className.equalsIgnoreCase(Constants.CLASS_SPAWN)) {
                Vector2 spawnPos = getObjectPosition(object);
                System.out.println("[ObjectFactory] 플레이어 생성 위치: " + spawnPos);
                return new Player(world, spawnPos);
            }
        }
        return null;
    }

    public static Vector2 getObjectPosition(MapObject object) {
        if (object instanceof RectangleMapObject) {
            Rectangle rect = ((RectangleMapObject) object).getRectangle();
            return new Vector2((rect.x + rect.width / 2) / Constants.PPM,
                (rect.y + rect.height / 2) / Constants.PPM);
        }
        return null;
    }

    private static void createStaticBody(World world, Rectangle rect, short category, String userData) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((rect.x + rect.width / 2) / Constants.PPM, (rect.y + rect.height / 2) / Constants.PPM);

        Body body = world.createBody(bdef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rect.width / 2 / Constants.PPM, rect.height / 2 / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.filter.categoryBits = category;
        body.createFixture(fdef).setUserData(userData);
        shape.dispose();
    }

    private static void createSensorBody(World world, Rectangle rect, short category, String userData) {
        BodyDef bdef = new BodyDef();
        bdef.type = BodyDef.BodyType.StaticBody;
        bdef.position.set((rect.x + rect.width / 2) / Constants.PPM, (rect.y + rect.height / 2) / Constants.PPM);

        Body body = world.createBody(bdef);
        PolygonShape shape = new PolygonShape();
        shape.setAsBox(rect.width / 2 / Constants.PPM, rect.height / 2 / Constants.PPM);

        FixtureDef fdef = new FixtureDef();
        fdef.shape = shape;
        fdef.isSensor = true;
        fdef.filter.categoryBits = category;
        body.createFixture(fdef).setUserData(userData);
        shape.dispose();
    }
}
