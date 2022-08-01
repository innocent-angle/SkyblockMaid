package codes.kitten.hypixelmaid;

import codes.kitten.hypixelmaid.data.BingoData;
import codes.kitten.hypixelmaid.data.MayorData;
import codes.kitten.hypixelmaid.listeners.EventListener;
import codes.kitten.hypixelmaid.utils.GetEnv;
import net.dv8tion.jda.api.OnlineStatus;
import net.dv8tion.jda.api.entities.Activity;
import net.dv8tion.jda.api.sharding.DefaultShardManagerBuilder;
import net.dv8tion.jda.api.sharding.ShardManager;

public class HypixelMaid {
    private final String token = GetEnv.Value("DISCORD_TOKEN");
    private static ShardManager shardManager;

    public HypixelMaid() throws Exception {
        DefaultShardManagerBuilder builder = DefaultShardManagerBuilder.createDefault(token);
        builder.setStatus(OnlineStatus.ONLINE);
        builder.setActivity(Activity.watching("you"));

        shardManager = builder.build();
        shardManager.addEventListener(new EventListener());
    }

    public static ShardManager getShardManager() {
        return shardManager;
    }

    public static void main(String args[]) throws Exception {
        new HypixelMaid();
    }

    public static void startTimers() {
        new BingoData();
        new MayorData();
    }
}
