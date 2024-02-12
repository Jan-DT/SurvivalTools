package nl.jandt.conflict;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;
import nl.jandt.SurvivalTools;

import java.util.Objects;

public class ConflictState extends PersistentState {
    // this was taken straight from the Fabric docs, but it works well

    public Integer currentConstitutionId = 0;

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        nbt.putInt("currentConstitutionId", currentConstitutionId);
        return nbt;
    }

    public static ConflictState createFromNbt(NbtCompound nbt) {
        ConflictState conflictState = new ConflictState();
        conflictState.currentConstitutionId = nbt.getInt("currentConstitutionId");
        return conflictState;
    }

    private static final Type<ConflictState> type = new Type<>(
            ConflictState::new, // If there's no 'ConflictState' yet create one
            ConflictState::createFromNbt, // If there is a 'ConflictState' NBT, parse it with 'createFromNbt'
            null // Supposed to be an 'DataFixTypes' enum, but we can just pass null
    );

    public static ConflictState getServerState(MinecraftServer server) {
        // (Note: arbitrary choice to use 'World.OVERWORLD' instead of 'World.END' or 'World.NETHER'.  Any work)
        PersistentStateManager persistentStateManager = Objects.requireNonNull(server.getWorld(World.OVERWORLD))
                .getPersistentStateManager();

        // The first time the following 'getOrCreate' function is called, it creates a brand new 'ConflictState' and
        // stores it inside the 'PersistentStateManager'. The subsequent calls to 'getOrCreate' pass in the saved
        // 'ConflictState' NBT on disk to our function 'ConflictState::createFromNbt'.
        ConflictState state = persistentStateManager.getOrCreate(type, SurvivalTools.MOD_ID);

        // If state is not marked dirty, when Minecraft closes, 'writeNbt' won't be called and therefore nothing will be saved.
        // Technically it's 'cleaner' if you only mark state as dirty when there was actually a change, but the vast majority
        // of mod writers are just going to be confused when their data isn't being saved, and so it's best just to 'markDirty' for them.
        // Besides, it's literally just setting a bool to true, and the only time there's a 'cost' is when the file is written to disk when
        // there were no actual change to any of the mods state (INCREDIBLY RARE).
        state.markDirty();

        return state;
    }
}
