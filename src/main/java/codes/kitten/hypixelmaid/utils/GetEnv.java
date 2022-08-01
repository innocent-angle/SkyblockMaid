package codes.kitten.hypixelmaid.utils;

import io.github.cdimascio.dotenv.Dotenv;

public class GetEnv {
    public static String Value(String value) {
        return Dotenv.configure().load().get(value);
    }
}
