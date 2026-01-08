package VERYNEW.app.common;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;
import java.lang.reflect.Field;
import java.util.Map;

/**
 * config_index.json을 읽고 Reflection을 사용하여 GameConfig의 필드에 데이터를 자동 매핑합니다.
 * 인덱스의 키 이름과 GameConfig(또는 내부 클래스)의 필드 이름이 일치해야 합니다.
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

            Map<String, String> configMap = gson.fromJson(indexFile.readString(),
                new TypeToken<Map<String, String>>(){}.getType());

            if (configMap == null) return;

            GameConfig fullConfig = new GameConfig();
            GameConfig.EntitiesConfig entities = new GameConfig.EntitiesConfig();

            // Reflection을 사용하여 동적 매핑 수행
            for (Map.Entry<String, String> entry : configMap.entrySet()) {
                String key = entry.getKey();
                String path = entry.getValue();

                // 1. Physics 설정 자동 매핑 (GameConfig의 필드명 'physics'와 일치할 경우)
                if (key.equalsIgnoreCase("physics")) {
                    fullConfig.setPhysics(loadJson(path, GameConfig.PhysicsConfig.class));
                    continue;
                }

                // 2. EntitiesConfig 내부 필드 자동 매핑 (player, enemy 등)
                try {
                    Field field = GameConfig.EntitiesConfig.class.getDeclaredField(key);
                    field.setAccessible(true);
                    // 필드의 타입에 맞춰 JSON 로드 및 설정
                    Object configData = loadJson(path, field.getType());
                    if (configData != null) {
                        field.set(entities, configData);
                    }
                } catch (NoSuchFieldException e) {
                    System.err.println("ConfigLoader: No matching field for key '" + key + "' in EntitiesConfig.");
                }
            }

            fullConfig.setEntities(entities);
            GameConfig.setInstance(fullConfig);

            System.out.println("ConfigLoader: Automated mapping complete for " + configMap.size() + " files.");
        } catch (Exception e) {
            System.err.println("ConfigLoader: Critical failure during automated loading.");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    private static <T> T loadJson(String filePath, Class<T> type) {
        try {
            FileHandle file = Gdx.files.internal(filePath);
            if (!file.exists()) return null;
            return gson.fromJson(file.readString(), type);
        } catch (Exception e) {
            System.err.println("ConfigLoader: Error parsing " + filePath);
            return null;
        }
    }
}
