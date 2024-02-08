package nl.jandt.conflict;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtInt;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.registry.Registries;
import net.minecraft.server.command.ServerCommandSource;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import nl.jandt.SurvivalTools;
import nl.jandt.utils.SrvConfig;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

import static net.minecraft.server.command.CommandManager.literal;


public class ConsitutionBook {

    private static final SrvConfig.ConflictConfig CONFLICT_CONFIG = SurvivalTools.CONFIG.conflictConfig;
    public static ConsitutionBook instance = new ConsitutionBook();
    public int currentId = 0;

    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        if (!CONFLICT_CONFIG.enabled()) {
            return;
        }

        final LiteralCommandNode<ServerCommandSource> command = dispatcher.register(literal(CONFLICT_CONFIG.constitutionCommand())
                .then(literal("getbook")
                        .requires(src -> src.hasPermissionLevel(3))
                        .executes(ctx -> {
                            final @Nullable ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                            assert player != null;

                            giveToPlayer(player);
                            return 0;
                        }))
                .then(runTakePower()));

        dispatcher.register(literal("const").redirect(command));
    }

    private LiteralArgumentBuilder<ServerCommandSource> runTakePower() {
        return literal("takepower")
                .requires(src -> {
                    boolean hasPerms = src.hasPermissionLevel(3);
                    boolean holdingItem = Objects.requireNonNull(src.getPlayer())
                            .isHolding(itemStack -> {
                                NbtCompound nbt = itemStack.getNbt();
                                if (nbt == null || !nbt.contains("constitution_id")) {
                                    return false;
                                }
                                int id = itemStack.getNbt().getInt("constitution_id");
                                return id == currentId;
                            });
                    return hasPerms || holdingItem;
                })
                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                    if (!player.isHolding(itemStack -> {
                        NbtCompound nbt = itemStack.getNbt();
                        if (nbt == null || !nbt.contains("generation")) {
                            return false;
                        }
                        int id = itemStack.getNbt().getInt("generation");
                        return id == 0;
                    })) {
                        ctx.getSource().getPlayerOrThrow()
                                .sendMessage(Text.of("You need to hold the original constitution to do this!"));
                    }
                    ctx.getSource().getPlayerOrThrow().sendMessage(Text.of("testmessage"));
                    return 0;
                });
    }

    private static String[] getPages() {
        String[] testPages = new String[3];

        testPages[0] = "{\"text\":\"\",\"extra\":[\"\\n\",{\"text\":\"SUPREME CONSTITUTION\",\"color\":\"dark_gray\",\"bold\":true},\"\\n\",\"\\n\",\"Henceforth, whosoever possesses this constitution shall wield unassailable dominion and absolute control over the affairs, governance, and destinies of this server.\"]}";
        testPages[1] = "{\"text\":\"\",\"extra\":[\"\\n\",\"By virtue of this proclamation, the possessor of this hallowed instrument shall be vested with unchallengeable powers, rendering them the paramount and uncontestable sovereign of the server.\"]}";
        testPages[2] = "{\"text\":\"\",\"extra\":[\"\\n\",\"May this instrument stand as an immutable testament to the indomitable authority it confers upon its rightful owner.\",\"\\n\",\"\\n\",{\"text\":\"CLICK TO TAKE POWER\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/constitution takepower\"},\"color\":\"dark_aqua\",\"bold\":true},\"\\n\",\"\\n\",{\"text\":\"- Sir Notch Minecraft\",\"italic\":true,\"color\":\"dark_gray\"}]}";

        return testPages;
    }

    public ItemStack getNewBookItem(int customId) {
        ItemStack itemStack = new ItemStack(Registries.ITEM.get(Identifier.of("minecraft", "written_book")));

        NbtList pages = new NbtList();
        for (String n : getPages()) {
            pages.add(NbtString.of(n));
        }

        itemStack.setSubNbt("pages", pages);

        itemStack.setSubNbt("author", NbtString.of("Sir Notch Minecraft"));
        itemStack.setSubNbt("constitution_id", NbtInt.of(customId));

        itemStack.setSubNbt("title", NbtString.of("Constitution :)"));
        itemStack.setSubNbt("filtered_title", NbtString.of("Constitution"));

        itemStack.setCount(1);
        itemStack.setCustomName(Text.Serialization.fromJson(CONFLICT_CONFIG.constitutionItemName()));

        return itemStack;
    }

    public void giveToPlayer(@NotNull ServerPlayerEntity player) {
        player.giveItemStack(getNewBookItem(0));
    }
}
