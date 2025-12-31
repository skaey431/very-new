package VERYNEW.app.screens;

import com.badlogic.gdx.Screen;
import com.badlogic.gdx.graphics.OrthographicCamera;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.maps.MapLayer;
import com.badlogic.gdx.maps.MapObject;
import com.badlogic.gdx.maps.tiled.renderers.OrthogonalTiledMapRenderer;
import com.badlogic.gdx.math.Vector2;
import com.badlogic.gdx.physics.box2d.Box2DDebugRenderer;
import com.badlogic.gdx.physics.box2d.World;
import com.badlogic.gdx.utils.ScreenUtils;
import com.badlogic.gdx.utils.viewport.FitViewport;
import com.badlogic.gdx.utils.viewport.Viewport;
import VERYNEW.app.common.Constants;
import VERYNEW.app.entities.Player;
import VERYNEW.app.map.MapManager;
import VERYNEW.app.map.ObjectFactory;

public class PlayScreen implements Screen {
    private SpriteBatch batch;
    private OrthographicCamera camera;
    private Viewport viewport;

    private MapManager mapManager;
    private OrthogonalTiledMapRenderer mapRenderer;
    private World world;
    private Box2DDebugRenderer debugRenderer;
    private Player player;

    public PlayScreen(SpriteBatch batch) {
        this.batch = batch;
        camera = new OrthographicCamera();
        viewport = new FitViewport(Constants.V_WIDTH, Constants.V_HEIGHT, camera);

        world = new World(new Vector2(0, Constants.GRAVITY), true);
        debugRenderer = new Box2DDebugRenderer();

        mapManager = new MapManager();
        try {
            mapManager.loadMap("maps/level1.tmx");
            mapRenderer = new OrthogonalTiledMapRenderer(mapManager.getMap(), 1 / Constants.PPM);
        } catch (Exception e) {
            System.err.println("Error loading map: " + e.getMessage());
        }

        initMapObjects();

        // 플레이어 생성 후 카메라 위치 초기화
        if (player != null) {
            camera.position.set(player.getBody().getPosition().x, player.getBody().getPosition().y, 0);
        }
        camera.update();
    }

    private void initMapObjects() {
        if (mapManager.getMap() == null) return;

        for (MapLayer layer : mapManager.getMap().getLayers()) {
            for (MapObject object : layer.getObjects()) {
                // ObjectFactory에서 생성된 객체가 Player일 경우 할당
                Player p = ObjectFactory.createPhysics(object, world);
                if (p != null) {
                    this.player = p;
                }
            }
        }
    }

    @Override
    public void render(float delta) {
        update(delta);

        ScreenUtils.clear(0.1f, 0.1f, 0.1f, 1);

        if (mapRenderer != null) {
            mapRenderer.setView(camera);
            mapRenderer.render();
        }

        batch.setProjectionMatrix(camera.combined);
        batch.begin();
        if (player != null) player.draw(batch);
        batch.end();

        debugRenderer.render(world, camera.combined);
    }

    public void update(float delta) {
        world.step(1 / 60f, 6, 2);

        if (player != null) {
            player.update(delta);
            // 카메라 추적
            float lerp = 0.1f;
            camera.position.x += (player.getBody().getPosition().x - camera.position.x) * lerp;
            camera.position.y += (player.getBody().getPosition().y - camera.position.y) * lerp;
        }

        camera.update();
    }

    @Override
    public void resize(int width, int height) {
        viewport.update(width, height, false);
    }

    @Override public void show() {}
    @Override public void pause() {}
    @Override public void resume() {}
    @Override public void hide() {}

    @Override
    public void dispose() {
        if (mapManager != null) mapManager.dispose();
        if (mapRenderer != null) mapRenderer.dispose();
        if (world != null) world.dispose();
        if (debugRenderer != null) debugRenderer.dispose();
    }
}
