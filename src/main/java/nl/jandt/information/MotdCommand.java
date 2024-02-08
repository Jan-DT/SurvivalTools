/* Licensed under MPL 2.0, available at https://www.mozilla.org/en-US/MPL/2.0/ */
package nl.jandt.information;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayNetworkHandler;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import nl.jandt.SurvivalTools;
import nl.jandt.utils.SrvConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class MotdCommand implements Command<ServerCommandSource> {
    public static final SrvConfig.MotdConfig MOTD_CONFIG = SurvivalTools.CONFIG.motdConfig;
    public static MotdCommand instance = new MotdCommand();

    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!MOTD_CONFIG.enabled()) {
            return;
        }

        dispatcher.register(CommandManager
                .literal(MOTD_CONFIG.command())
                .executes(this));
    }

    public void registerJoin(ServerPlayNetworkHandler handler) {
        if (!MOTD_CONFIG.enabled()) {
            return;
        }

        final ServerPlayerEntity player = handler.getPlayer();
        sendMotd(player);
    }

    @Override
    public int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        final ServerCommandSource source = ctx.getSource();

        final @Nullable ServerPlayerEntity player = source.getPlayerOrThrow();
        assert player != null;

        sendMotd(player);

        return 0;
    }

    public void sendMotd(@NotNull ServerPlayerEntity targetPlayer) {
        targetPlayer.sendMessage(Text.Serialization.fromJson(MOTD_CONFIG.message()));
    }
}
