package nl.jandt;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.brigadier.exceptions.SimpleCommandExceptionType;
import net.minecraft.entity.Entity;
import net.minecraft.server.command.CommandManager;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import org.jetbrains.annotations.Nullable;

public class InfoCommand implements Command<ServerCommandSource> {
    private final nl.jandt.SrvConfig config;

    public InfoCommand(nl.jandt.SrvConfig config) {
        this.config = config;
    }

    public void register(CommandDispatcher<ServerCommandSource> dispatcher) {
        dispatcher.register(CommandManager
                .literal("info")
                .executes(this));
    }

    @Override
    public int run(CommandContext<ServerCommandSource> ctx) throws CommandSyntaxException {
        final ServerCommandSource source = ctx.getSource();

        final @Nullable ServerPlayerEntity player = source.getPlayerOrThrow();
        assert player != null;

        player.sendMessage(Text.of(this.config.infoMessage()));

        return 0;
    }
}
