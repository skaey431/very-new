package VERYNEW.app.common;

/**
 * 게임 내의 모든 수치와 고정 문자열을 관리하는 클래스입니다.
 * 수정 시 이 파일만 변경하면 게임 전체에 반영됩니다.
 */
public class Constants {
    // 1. 물리 관련 단위 (Pixels Per Meter)
    // 100픽셀을 1미터로 설정하여 Box2D 연산 최적화
    public static float PPM = 100f;

    // 2. 가상 화면 해상도 (단위: 미터)
    public static final float V_WIDTH = 800f / PPM;
    public static final float V_HEIGHT = 480f / PPM;

    // 3. Tiled Map 객체 클래스 명칭 (Tiled의 'Class' 속성과 일치해야 함)
    public static final String CLASS_WALL = "Wall";
    public static final String CLASS_TRAP = "Trap";
    public static final String CLASS_GOAL = "Goal";
    public static final String CLASS_GROUND = "Ground";
    public static final String CLASS_SPAWN = "Spawn";

    // 4. 충돌 카테고리 (Bitmask)
    public static final short BIT_PLAYER = 1;      // 0001
    public static final short BIT_WALL = 2;        // 0010
    public static final short BIT_TRAP = 4;        // 0100
    public static final short BIT_GOAL = 8;        // 1000

    // 5. 물리 설정
    public static float GRAVITY = -9.8f;
}
