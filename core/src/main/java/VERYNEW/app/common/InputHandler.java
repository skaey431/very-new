package VERYNEW.app.common;

import com.badlogic.gdx.Input;
import com.badlogic.gdx.InputAdapter;

/**
 * 사용자의 키보드 입력을 감지하여 상태를 저장하는 핸들러 클래스입니다.
 */
public class InputHandler extends InputAdapter {
    public static boolean moveLeft = false;
    public static boolean moveRight = false;
    public static boolean jump = false;

    @Override
    public boolean keyDown(int keycode) {
        System.out.println("key_down : " + keycode);
        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.A:
                moveLeft = true;
                break;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                moveRight = true;
                break;
            case Input.Keys.UP:
            case Input.Keys.W:
            case Input.Keys.SPACE:
                jump = true;
                break;
        }
        return true;
    }

    @Override
    public boolean keyUp(int keycode) {
        switch (keycode) {
            case Input.Keys.LEFT:
            case Input.Keys.A:
                moveLeft = false;
                break;
            case Input.Keys.RIGHT:
            case Input.Keys.D:
                moveRight = false;
                break;
            case Input.Keys.UP:
            case Input.Keys.W:
            case Input.Keys.SPACE:
                jump = false;
                break;
        }
        return true;
    }
}
