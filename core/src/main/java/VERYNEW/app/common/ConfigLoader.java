package VERYNEW.app.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.util.Map;

/**
 * master.json을 읽고 데이터를 GameConfig의 구조에 맞게 로드합니다.
 * physics 키는 별도 객체로, 그 외의 모든 키는 entities Map에 자동으로 매핑됩니다.
 */
public class ConfigLoader {
    private static final Gson gson = new Gson();
    private static final String INDEX_PATH = "master.json";

    public static void loadConfig() {
        try {
            FileHandle indexFile = Gdx.files.internal(INDEX_PATH);
            if (!indexFile.exists()) {
                throw new RuntimeException("Master config index missing: " + INDEX_PATH);
            }

            // master.json 로드 (키: 설정 이름, 값: 파일 경로)
            Map<String, String> configMap = gson.fromJson(indexFile.readString(),
                new TypeToken<Map<String, String>>(){}.getType());

            if (configMap == null) return;

            GameConfig fullConfig = new GameConfig();

            // 설정 맵 순회하며 로드
            for (Map.Entry<String, String> entry : configMap.entrySet()) {
                String key = entry.getKey();
                String path = entry.getValue();

                // 1. 물리 설정 처리
                if (key.equalsIgnoreCase("physics")) {
                    fullConfig.setPhysics(loadJson(path, GameConfig.PhysicsConfig.class));
                }
                // 2. 그 외 모든 설정은 엔티티 맵으로 처리 (호환성 해결)
                else {
                    GameConfig.EntityConfig entityData = loadJson(path, GameConfig.EntityConfig.class);
                    if (entityData != null) {
                        fullConfig.getEntities().put(key, entityData);
                    }
                }
            }

            // 싱글톤 인스턴스 등록
            GameConfig.setInstance(fullConfig);

            System.out.println("ConfigLoader: Successfully loaded " + configMap.size() + " config files.");
        } catch (Exception e) {
            System.err.println("ConfigLoader: Critical failure during loading.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static <T> T loadJson(String filePath, Class<T> type) {
        try {
            FileHandle file = Gdx.files.internal(filePath);
            if (!file.exists()) {
                System.err.println("ConfigLoader: File not found -> " + filePath);
                return null;
            }
            return gson.fromJson(file.readString(), type);
        } catch (Exception e) {
            System.err.println("ConfigLoader: Error parsing " + filePath);
            return null;
        }
    }
}
