package nl.jandt.utils;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Nest;

@Config(name = "stconfig", wrapperName = "SrvConfig")
public class SrvConfigModel {
    @SuppressWarnings("unused")
    @Nest
    public MotdConfig motdConfig = new MotdConfig();

    @SuppressWarnings("unused")
    @Nest
    public InfoConfig infoConfig = new InfoConfig();

    public static class MotdConfig {
        /**
         * Defines whether the MOTD and MotdCommand are enabled
         */
        public boolean enabled = true;

        /**
         * The command that will send the MOTD
         */
        public String command = "motd";

        /**
         * The actual MOTD message
         */
        public String message = "[\"\",{\"text\":\"\\n\"},{\"text\":\"Placeholder MOTD\",\"color\":\"aqua\"},{\"text\":\"\\n\\n\"},{\"text\":\"Multiline RGB\\n!\",\"color\":\"#FF00C0\"}]";
    }

    public static class InfoConfig {
        /**
         * Defines whether the InfoCommand is enabled
         */
        public boolean enabled = true;

        /**
         * The command used to show info
         */
        public String command = "info";

        /**
         * The message to send when the InfoCommand is run
         */
        public String message = "[\"\",{\"text\":\"\\n\"},{\"text\":\"Testmessage\",\"color\":\"aqua\"},{\"text\":\"\\n\\n\"},{\"text\":\"Multiline RGB\\n!\",\"color\":\"#FF00C0\"}]";

    }
}
