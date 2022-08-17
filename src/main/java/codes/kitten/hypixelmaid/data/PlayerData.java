package codes.kitten.hypixelmaid.data;

import codes.kitten.hypixelmaid.HypixelMaid;
import codes.kitten.hypixelmaid.utils.ClearChannel;
import codes.kitten.hypixelmaid.utils.GetEnv;
import kong.unirest.Unirest;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class PlayerData {
    private static JSONObject games;
    private static final DecimalFormat formatter = new DecimalFormat("#,###");
    private static final TextChannel channel = HypixelMaid.getShardManager().getTextChannelById(GetEnv.Value("PLAYERS_CHANNEL"));

    public PlayerData() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateChannel();
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000 * 60 * 20); // once per 20 minutes
    }

    public static void setGames(JSONObject games) {
        PlayerData.games = games;
    }

    public void updateData() {
        JSONObject response = Unirest.get("https://api.hypixel.net/counts")
                .queryString("key", "c98f52d8-f9d0-47ea-a686-04852f819a6e")
                .asJson()
                .getBody()
                .getObject();

        response.keys().forEachRemaining(key -> {
            Object value = response.get(key);

            switch (key) {
                case "success":
                    if (!(Boolean) value) return;
                    break;

                case "games":
                    setGames((JSONObject) value);
                    break;
            }
        });
    }

    public void updateChannel() {
        this.updateData();
        assert channel != null;
        ClearChannel.start(channel);
        channel.sendMessageEmbeds(this.returnEmbeds()).queue();
    }

    public ArrayList<MessageEmbed> returnEmbeds() {
        ArrayList<MessageEmbed> embeds = new ArrayList<>();
        EmbedBuilder gamesEmbed = new EmbedBuilder();

        JSONObject skyblock = games.getJSONObject("SKYBLOCK");
        JSONObject modes = skyblock.getJSONObject("modes");

        String players = formatter.format(skyblock.getLong("players"));
        String hubPlayers = formatter.format(skyblock.getJSONObject("modes").getLong("hub"));
        String islandPlayers = formatter.format(skyblock.getJSONObject("modes").getLong("dynamic"));

        gamesEmbed
                .setTitle("Skyblock Player Count")
                .setTimestamp(new Date().toInstant())
                .setColor(Color.PINK)
                .setThumbnail("https://static.wikia.nocookie.net/hypixel-skyblock/images/f/f1/Bingo.png/revision/latest?cb=20211129233732")
                .addField("Total", players, true)
                .addField("Hub", hubPlayers, true)
                .addField("Private Island", islandPlayers, true);

        modes.keys().forEachRemaining(key -> {
            String value = formatter.format(modes.get(key));

            gamesEmbed.addField(key, value, false);
        });

        embeds.add(gamesEmbed.build());
        return embeds;
    }
}
