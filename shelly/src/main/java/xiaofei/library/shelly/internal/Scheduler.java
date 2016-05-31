package xiaofei.library.shelly.internal;

/**
 * Created by Eric on 16/5/31.
 */
public class Scheduler {
    private Player mPlayer;
    public Scheduler(Player player) {
        mPlayer = player;
    }

    public Player getPlayer() {
        return mPlayer;
    }

    public void play(Object input) {
        mPlayer.play(input);
    }
}
