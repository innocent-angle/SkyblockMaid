package codes.kitten.hypixelmaid.utils;

import net.dv8tion.jda.api.entities.Message;
import net.dv8tion.jda.api.entities.TextChannel;

import java.util.List;

public class ClearChannel {
    public static void start(TextChannel channel) {
        List<Message> messages = channel.getHistory().retrievePast(50).complete();
        if (messages.size() == 1) {messages.get(0).delete().queue();}

        if (messages.size() >= 2 && messages.size() <= 100 ) {
            channel.deleteMessages(messages).queue();
        }
    }
}
