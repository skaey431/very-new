package VERYNEW.app.map;

import com.badlogic.gdx.maps.tiled.TiledMap;
import com.badlogic.gdx.maps.tiled.TmxMapLoader;
import VERYNEW.app.common.Constants;

/**
 * 맵 로딩 및 맵의 물리적 경계(카메라 클램핑용)를 관리합니다.
 */
public class MapManager {
    private TiledMap currentMap;
    private float mapWidth;
    private float mapHeight;

    public void loadMap(String path) {
        if (currentMap != null) currentMap.dispose();
        currentMap = new TmxMapLoader().load(path);

        // 맵의 전체 크기 계산 (단위: 미터)
        int tileWidth = currentMap.getProperties().get("tilewidth", Integer.class);
        int tileHeight = currentMap.getProperties().get("tileheight", Integer.class);
        int mapTilesX = currentMap.getProperties().get("width", Integer.class);
        int mapTilesY = currentMap.getProperties().get("height", Integer.class);

        this.mapWidth = (mapTilesX * tileWidth) / Constants.PPM;
        this.mapHeight = (mapTilesY * tileHeight) / Constants.PPM;
    }

    public TiledMap getMap() { return currentMap; }
    public float getMapWidth() { return mapWidth; }
    public float getMapHeight() { return mapHeight; }

    public void dispose() {
        if (currentMap != null) currentMap.dispose();
    }
}
