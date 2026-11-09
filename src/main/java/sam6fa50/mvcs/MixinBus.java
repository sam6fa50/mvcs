package sam6fa50.mvcs;

import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import sam6fa50.mvcs.tracking.Tracker;

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
                .trackChunk(dimension, pos);
    }

    public void onBlockSet(ResourceKey<Level> dimension, ChunkPos pos) {
        // Tracker
        //         .getCurrentInstance()
        //         .getChunkTracker()
        //         .trackChunk(dimension, pos);
    }

    public boolean isTracked(ResourceKey<Level> dimension, ChunkPos chunkPos) {
        return Tracker
                .getCurrentInstance()
                .getChunkTracker()
                .isTracked(dimension, chunkPos);
    }

    public void onBlockStateSet(ResourceKey<Level> dimension, ChunkPos pos) {
        Tracker
                .getCurrentInstance()
                .getChunkTracker()
                .trackChunk(dimension, pos);
    }
}
