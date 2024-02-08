/* Licensed under MPL 2.0, available at https://www.mozilla.org/en-US/MPL/2.0/ */
package nl.jandt.utils;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Nest;

@SuppressWarnings("unused")
@Config(name = "stconfig", wrapperName = "SrvConfig")
public class SrvConfigModel {
    @Nest
    public MotdConfig motdConfig = new MotdConfig();

    @Nest
    public InfoConfig infoConfig = new InfoConfig();

    @Nest
    public ConflictConfig conflictConfig = new ConflictConfig();

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

    public static class ConflictConfig {
        /**
         * Defines whether the conflict system is enabled
         */
        public boolean enabled = true;

        public String constitutionCommand = "constitution";

        public String constitutionItemName = "[\"§r\",{\"text\":\"§rConstitution\",\"italic\":false,\"color\":\"#FFE700\"}]";

        public String lawCommand = "law";
    }
}
