package sam6fa50.mvcs.tracking;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import sam6fa50.mvcs.MVCS;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;

// This class tracks chunks that have a dirty 'uncommitted' status
public class ChunkTracker {
    // This map stores all the
    final private Map<ResourceKey<Level>, Set<ChunkPos>> trackedChunks;

    public ChunkTracker() {
        this.trackedChunks = new HashMap<>();
    }

    // Chunks are added to the ChunkTracker (which will exist as a singleton)
    // through our mixin
    public void addChunkToTrack(ResourceKey<Level> dimension, ChunkPos pos) {
        if (this.trackedChunks.containsKey(dimension))
            this.trackedChunks
                    .get(dimension)
                    .add(pos);
        else {
            this.trackedChunks.put(dimension, new HashSet<>());
            this.addChunkToTrack(dimension, pos);
        }
    }

    public Map<ResourceKey<Level>, Set<ChunkPos>> getTrackedChunks() {
        return this.trackedChunks;
    }

    public Stream<ChunkPos> getTrackedChunkPosInDimension(ResourceKey<Level> dimension) {
        return this.trackedChunks
                .get(dimension)
                .stream();
    }

    public String serialize() {
        JsonObject root = new JsonObject();

        trackedChunks.forEach((dimension, chunkPositions) -> {
            JsonArray positions = new JsonArray();

            for (ChunkPos chunk : chunkPositions) {
                JsonObject position = new JsonObject();
                position.addProperty("x", chunk.x());
                position.addProperty("z", chunk.z());

                positions.add(position);
            }

            root.add(dimension.identifier().toString(), positions);
        });

        return MVCS.GSON.toJson(root);
    }
}
