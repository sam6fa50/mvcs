package sam6fa50.mvcs.tracking;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;

public class MixinBus {
    private static MixinBus instance = null;

    private MixinBus() {
    }

    public static synchronized MixinBus getInstance() {
        if (instance == null)
            instance = new MixinBus();

        return instance;
    }

    public void onMarkedUnsaved(ResourceKey<Level> dimension, ChunkPos pos) {
        Tracker
                .getCurrentInstance()
                .getChunkTracker()
                .addChunkToTrack(dimension, pos);
    }
}
