package codes.kitten.hypixelmaid.listeners;

import codes.kitten.hypixelmaid.HypixelMaid;
import codes.kitten.hypixelmaid.utils.GetEnv;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.events.ReadyEvent;
import net.dv8tion.jda.api.events.message.MessageReceivedEvent;
import net.dv8tion.jda.api.events.message.react.MessageReactionAddEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

import static codes.kitten.hypixelmaid.utils.GetLoggingChannel.getLoggingChannel;
import static codes.kitten.hypixelmaid.utils.GetLoggingChannel.setLoggingChannel;

public class EventListener extends ListenerAdapter {
    public EventListener() {

    }

    @Override
    public void onMessageReceived(@NotNull MessageReceivedEvent event) {
        if (event.getAuthor().isBot()) return;

        String tag = event.getAuthor().getAsTag();
        String channel = event.getChannel().getAsMention();
        String jumpUrl = event.getJumpUrl();

        String message = tag + " just sent a message in " + channel + "\n" + jumpUrl;
        getLoggingChannel().sendMessage(message).queue();
    }

    @Override
    public void onMessageReactionAdd(@NotNull MessageReactionAddEvent event) {
        if (Objects.requireNonNull(event.getUser()).isBot()) return;

        User user = event.getUser();
        String emoji = event.getReaction().getEmoji().getAsReactionCode();
        String jumpLink = event.getJumpUrl();
        String channel = event.getChannel().getAsMention();

        String message = user.getAsTag() + " reacted to a message with " + emoji + " in " + channel + "\n" + jumpLink;
        getLoggingChannel().sendMessage(message).queue();
    }

    @Override
    public void onReady(@NotNull ReadyEvent event) {
        setLoggingChannel(HypixelMaid.getShardManager().getTextChannelById(GetEnv.Value("LOGGING_CHANNEL")));
        HypixelMaid.startTimers();
    }
}
