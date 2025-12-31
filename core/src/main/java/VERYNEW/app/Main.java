package VERYNEW.app;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import VERYNEW.app.screens.PlayScreen;

/**
 * 게임의 메인 진입점입니다.
 * 공용 리소스를 관리하고 첫 화면을 설정합니다.
 */
public class Main extends Game {
    public SpriteBatch batch;

    @Override
    public void create() {
        // 모든 스크린에서 공유할 SpriteBatch 생성
        batch = new SpriteBatch();

        // 게임 시작 시 PlayScreen으로 즉시 이동
        // 실제로는 LoadingScreen 등을 거치는 것이 좋지만, 현재는 테스트를 위해 바로 실행합니다.
        setScreen(new PlayScreen(batch));
    }

    @Override
    public void render() {
        // super.render()를 호출해야 현재 설정된 Screen의 render()가 실행됩니다.
        super.render();
    }

    @Override
    public void dispose() {
        // 게임 종료 시 자원 해제
        batch.dispose();
    }
}
