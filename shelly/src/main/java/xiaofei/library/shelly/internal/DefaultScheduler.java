package xiaofei.library.shelly.internal;

/**
 * Created by Eric on 16/5/31.
 */
public class DefaultScheduler implements Scheduler {

    public DefaultScheduler() {

    }

    public void play(Player player, Object input) {
        player.play(input);
    }
}
