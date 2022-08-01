package codes.kitten.hypixelmaid.utils;

import net.dv8tion.jda.api.entities.TextChannel;

public class GetLoggingChannel {
    private static TextChannel loggingChannel;

    public static TextChannel getLoggingChannel() {
        return loggingChannel;
    }

    public static void setLoggingChannel(TextChannel loggingChannel) {
        GetLoggingChannel.loggingChannel = loggingChannel;
    }
}
