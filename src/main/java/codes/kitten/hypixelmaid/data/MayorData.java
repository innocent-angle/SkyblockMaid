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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class MayorData {
    private static Boolean enabled = true;
    private final TextChannel channel = HypixelMaid.getShardManager().getTextChannelById(GetEnv.Value("MAYOR_CHANNEL"));
    private JSONObject mayor;
    private JSONObject election;
    private JSONArray candidates = null;
    private int currentYear;
    private int previousYear;
    DecimalFormat formatter = new DecimalFormat("#,###");

    public MayorData() {
        Timer timer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                updateChannel();
            }
        };

        timer.scheduleAtFixedRate(task, 0, 1000 * 60 * 20); // once per 20 minutes
    }

    public TextChannel getChannel() {
        return channel;
    }

    public JSONObject getMayor() {
        return mayor;
    }

    public void setMayor(JSONObject mayor) {
        this.mayor = mayor;
    }

    public JSONObject getElection() {
        return election;
    }

    public void setElection(JSONObject election) {
        this.election = election;
    }

    public static Boolean getEnabled() {
        return enabled;
    }

    public static void setEnabled(Boolean enabled) {
        MayorData.enabled = enabled;
    }

    public int getCurrentYear() {
        return currentYear;
    }

    public void setCurrentYear(int currentYear) {
        this.currentYear = currentYear;
    }

    public void updateData() {
        JSONObject response = Unirest.get("https://api.hypixel.net/resources/skyblock/election")
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

                case "mayor":
                    this.setMayor((JSONObject) value);
                    break;

                case "current":
                    this.setElection((JSONObject) value);
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
        EmbedBuilder mayorEmbed = new EmbedBuilder();
        EmbedBuilder electionEmbed = new EmbedBuilder();

        mayorEmbed
                .setTitle("Current Mayor")
                .setTimestamp(new Date().toInstant())
                .setColor(Color.PINK)
                .setThumbnail("https://static.wikia.nocookie.net/hypixel-skyblock/images/f/f1/Bingo.png/revision/latest?cb=20211129233732");


        electionEmbed
                .setTitle("An election is active!")
                .setTimestamp(new Date().toInstant())
                .setColor(Color.PINK)
                .setThumbnail("https://static.wikia.nocookie.net/hypixel-skyblock/images/f/f1/Bingo.png/revision/latest?cb=20211129233732");

        if (!(mayor == null)) {
            if (mayor.has("name")) mayorEmbed.addField(mayor.getString("name"), "PLACEHOLDER", false);
        }

        if (!(election == null)) {
            if (election.has("candidates")) candidates = election.getJSONArray("candidates");
            if (election.has("year")) currentYear = election.getInt("year");

            for (int j = 0; j < candidates.length(); j++) {
                JSONObject candidate = candidates.getJSONObject(j);
                String name = candidate.getString("name");
                JSONArray perks = candidate.getJSONArray("perks");
                String votes = formatter.format(candidate.getNumber("votes"));

                electionEmbed.addField(name, votes, false);
            }
        }

        embeds.add(mayorEmbed.build());
        embeds.add(electionEmbed.build());
        return embeds;
    }
}
