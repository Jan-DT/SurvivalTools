/* Licensed under MPL 2.0, available at https://www.mozilla.org/en-US/MPL/2.0/ */
package nl.jandt.information;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import nl.jandt.SurvivalTools;
import nl.jandt.utils.SrvConfig;
import org.jetbrains.annotations.Nullable;

public class InfoCommand implements Command<ServerCommandSource> {
    private static final SrvConfig.InfoConfig INFO_CONFIG = SurvivalTools.CONFIG.infoConfig;
    public static InfoCommand instance = new InfoCommand();

    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!INFO_CONFIG.enabled()) {
            return;
        }

        dispatcher.register(CommandManager
                .literal(INFO_CONFIG.command())
                .executes(this));
    }

    @Override
    public int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        final ServerCommandSource source = ctx.getSource();

        final @Nullable ServerPlayerEntity player = source.getPlayerOrThrow();
        assert player != null;

        player.sendMessage(Text.Serialization.fromJson(INFO_CONFIG.message()));

        return 0;
    }
}
