
import io.gridgo.example.tiktactoe.TikTacToe;
import io.gridgo.utils.ThreadUtils;

public class TikTacToeApplication {

    public static void main(String[] args) {
        final var app = new TikTacToe();
        ThreadUtils.registerShutdownTask(app::stop);
        app.start();
    }
}
