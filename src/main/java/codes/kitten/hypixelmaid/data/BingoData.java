package codes.kitten.hypixelmaid.data;

import codes.kitten.hypixelmaid.HypixelMaid;
import codes.kitten.hypixelmaid.utils.ClearChannel;
import codes.kitten.hypixelmaid.utils.GetEnv;
import kong.unirest.Unirest;
import kong.unirest.json.JSONArray;
import kong.unirest.json.JSONObject;
import net.dv8tion.jda.api.EmbedBuilder;
import net.dv8tion.jda.api.entities.MessageEmbed;
import net.dv8tion.jda.api.entities.TextChannel;

import java.awt.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class BingoData {
    private static Boolean enabled = true;
    private final TextChannel channel = HypixelMaid.getShardManager().getTextChannelById(GetEnv.Value("BINGO_CHANNEL"));
    private long lastUpdate;
    private int year;
    private JSONArray goals;

    public BingoData() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateChannel();
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000 * 60 * 20); // once per 20 minutes
    }

    public static Boolean getEnabled() {
        return enabled;
    }

    public static void setEnabled(Boolean enabled) {
        BingoData.enabled = enabled;
    }

    public TextChannel getChannel() {
        return channel;
    }

    public long getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(long lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public JSONArray getGoals() {
        return goals;
    }

    public void setGoals(JSONArray goals) {
        this.goals = goals;
    }

    public void updateData() {
        JSONObject response = Unirest.get("https://api.hypixel.net/resources/skyblock/bingo")
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

                case "lastUpdated":
                    this.setLastUpdate((long) value);
                    break;

                case "goals":
                    this.setGoals((JSONArray) value);
            }
        });

    }

    public void updateChannel() {
        this.updateData();
        ClearChannel.start(channel);
        channel.sendMessageEmbeds(this.returnEmbeds()).queue();
    }

    public ArrayList<MessageEmbed> returnEmbeds() {
        ArrayList<MessageEmbed> embeds = new ArrayList<>();
        EmbedBuilder personalEmbed = new EmbedBuilder();
        EmbedBuilder communityEmbed = new EmbedBuilder();

        personalEmbed
            .setTitle("Personal Goals")
            .setTimestamp(new Date().toInstant())
            .setColor(Color.PINK)
            .setThumbnail("https://static.wikia.nocookie.net/hypixel-skyblock/images/f/f1/Bingo.png/revision/latest?cb=20211129233732");


        communityEmbed
            .setTitle("Community Goals")
            .setTimestamp(new Date().toInstant())
            .setColor(Color.PINK)
            .setThumbnail("https://static.wikia.nocookie.net/hypixel-skyblock/images/f/f1/Bingo.png/revision/latest?cb=20211129233732");

        for (int i = 0; i < this.goals.length(); i++) {
            JSONObject object = goals.getJSONObject(i);
            String name = null;
            String lore = null;
            String requiredAmount = null;
            String progress = null;
            JSONArray tiers = null;

            if (object.has("name")) {
                name = object.getString("name");
            }

            if (object.has("lore")) {
                lore = object.getString("lore");
            }

            if (object.has("requiredAmount")) {
                requiredAmount = object.getString("requiredAmount");
            }

            if (object.has("tiers")) {
                tiers = object.getJSONArray("tiers");
            }

            if (object.has("progress")) {
                progress = object.getString("progress");
            }

            if (lore != null) {
                personalEmbed.addField(new MessageEmbed.Field(name, lore, true));
                continue;
            }

            if (tiers != null) {
                communityEmbed.addField(new MessageEmbed.Field(name, progress, true));
                continue;
            }
        }


        embeds.add(personalEmbed.build());
        embeds.add(communityEmbed.build());
        return embeds;
    }
}
