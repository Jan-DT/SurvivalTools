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
import net.minecraft.server.MinecraftServer;
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


public class ConstitutionBook {

    private static final SrvConfig.ConflictConfig CONFLICT_CONFIG = SurvivalTools.CONFIG.conflictConfig;
    public static ConstitutionBook instance = new ConstitutionBook();

    public void registerCommand(CommandDispatcher<ServerCommandSource> dispatcher) {
        // If the Conflict module is disabled, ignore.
        if (!CONFLICT_CONFIG.enabled()) {
            return;
        }

        final LiteralCommandNode<ServerCommandSource> command = dispatcher.register(literal(CONFLICT_CONFIG.constitutionCommand())
                .then(literal("getbook")
                        // TODO: remove this comment before releasing
//                        .requires(src -> src.hasPermissionLevel(3))
                        .executes(ctx -> {
                            final @Nullable ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();
                            assert player != null;

                            giveConstItemToPlayer(player, generateNewId(player.server));
                            return 0;
                        }))
                .then(runTakePower()));

        dispatcher.register(literal("const").redirect(command));
    }

    private LiteralArgumentBuilder<ServerCommandSource> runTakePower() {
        return literal("takepower")
                // check if the player has a constitution in their hand
                .requires(src -> {
                    ServerPlayerEntity player = Objects.requireNonNull(src.getPlayer());
                    ItemStack itemStack = player.getMainHandStack();
                    NbtCompound nbt = itemStack.getNbt();

                    return nbt != null && nbt.contains("constitution_id");
                })

                .executes(ctx -> {
                    ServerPlayerEntity player = ctx.getSource().getPlayerOrThrow();

                    // checks if the player holds the original version of the book, and not a copy
                    if (!playerHoldsOriginal(player)) {
                        player.sendMessage(Text.of("You need to hold the original constitution to do this!"));
                        SurvivalTools.LOGGER.info(ctx.getSource().getName() + "tried to take power with an unoriginal constitution!");
                        return 1; // value is not really used right now, but using 1 differentiates from success
                    }
                    // checks whether the player is holding the correct constitution ID
                    // this is mainly used to prevent players from using older books when a newer one was spawned
                    // maybe one day this could be used for dupe prevention as well but no need for now
                    if (!playerHoldsCorrectId(player, currentId(player.server))) {
                        player.sendMessage(Text.of("You are not holding the correct generation of the constitution!"));
                        SurvivalTools.LOGGER.info(ctx.getSource().getName() + "tried to take power with an older constitution!");
                        return 1; // same as above
                    }

                    player.sendMessage(Text.of("You take power now!")); // TODO: actually add good messages
                    SurvivalTools.LOGGER.info(ctx.getSource().getName() + "took power with the constitution!");
                    return 0;
                });
    }

    /**
     * Returns true if the constitution item the player is holding has the correct constitution ID.
     * @param player The ServerPlayerEntity which to check for
     * @param correctId The constitution ID which is deemed 'correct'
     * @return true if the item the player is holding has the 'correct' ID
     */
    private static boolean playerHoldsCorrectId(@NotNull ServerPlayerEntity player, int correctId) {
        ItemStack itemStack = player.getMainHandStack();
        NbtCompound nbt = itemStack.getNbt();

        if (nbt == null || !nbt.contains("constitution_id")) {
            return false;
        }

        int id = itemStack.getNbt().getInt("constitution_id");
        return id == correctId;
    }

    /**
     * Returns false if the player is holding a copy of the book (checks 'generation' NBT tag)
     * @param player The ServerPlayerEntity which to check for
     * @return true if the player is holding the original book
     */
    private static boolean playerHoldsOriginal(@NotNull ServerPlayerEntity player) {
        ItemStack itemStack = player.getMainHandStack();
        NbtCompound nbt = itemStack.getNbt();

        if (nbt == null || !nbt.contains("generation")) {
            return false;
        }

        int id = itemStack.getNbt().getInt("generation");
        return id == 0;
    }

    // This method is just here to hide page generation
    private static String[] getPages() {
        String[] testPages = new String[3];

        testPages[0] = "{\"text\":\"\",\"extra\":[\"\\n\",{\"text\":\"SUPREME CONSTITUTION\",\"color\":\"dark_gray\",\"bold\":true},\"\\n\",\"\\n\",\"Henceforth, whosoever possesses this constitution shall wield unassailable dominion and absolute control over the affairs, governance, and destinies of this server.\"]}";
        testPages[1] = "{\"text\":\"\",\"extra\":[\"\\n\",\"By virtue of this proclamation, the possessor of this hallowed instrument shall be vested with unchallengeable powers, rendering them the paramount and uncontestable sovereign of the server.\"]}";
        testPages[2] = "{\"text\":\"\",\"extra\":[\"\\n\",\"May this instrument stand as an immutable testament to the indomitable authority it confers upon its rightful owner.\",\"\\n\",\"\\n\",{\"text\":\"CLICK TO TAKE POWER\",\"clickEvent\":{\"action\":\"run_command\",\"value\":\"/constitution takepower\"},\"color\":\"dark_aqua\",\"bold\":true},\"\\n\",\"\\n\",{\"text\":\"- Sir Notch Minecraft\",\"italic\":true,\"color\":\"dark_gray\"}]}";

        return testPages;
    }

    /**
     * Generates a new (unique) constitution ID.
     * @param server The MinecraftServer object the player is in (probably just player.server)
     * @return The new unique ID
     */
    private int generateNewId(MinecraftServer server) {
        ConflictState state = ConflictState.getServerState(server);
        return state.currentConstitutionId += 1;
    }

    public int currentId(MinecraftServer server) {
        ConflictState state = ConflictState.getServerState(server);
        return state.currentConstitutionId;
    }

    /**
     * Generates the actual book item to give to the player.
     * @param customId The constitution ID that the book should have.
     * @return The ItemStack of the book item.
     */
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
        itemStack.setSubNbt("generation", NbtInt.of(0));

        itemStack.setCount(1);
        itemStack.setCustomName(Text.Serialization.fromJson(CONFLICT_CONFIG.constitutionItemName()));

        return itemStack;
    }

    /**
     * This method will give a constitution item to player, with an ID of customId.
     * @param player The ServerPlayerEntity that should receive the item
     * @param customId The constitution ID of the item
     */
    public void giveConstItemToPlayer(@NotNull ServerPlayerEntity player, int customId) {
        player.giveItemStack(getNewBookItem(customId));
    }
}
