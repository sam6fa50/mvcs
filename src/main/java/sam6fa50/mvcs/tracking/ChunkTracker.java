package sam6fa50.mvcs.tracking;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.Level;
import sam6fa50.mvcs.MVCS;

import java.time.Instant;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

// This class tracks chunks that have a dirty 'uncommitted' status
public class ChunkTracker {
    // This map stores all the chunks we are tracking in our VCS by ChunkPos
    final private Map<ResourceKey<Level>, Set<ChunkPos>> trackedChunks;

    // This map stores all the chunks that are currently assigned a timestamp
    // with a last update time to be aware about staging diffs
    final private Map<ResourceKey<Level>, Map<ChunkPos, Instant>> stagedChunks;

    // Commitability flag that depends on if we're staged
    private boolean canCommit = false;

    public ChunkTracker() {
        this.trackedChunks = new HashMap<>();
        this.stagedChunks = new HashMap<>();
    }

    // Chunks are added to the ChunkTracker (which will exist as a singleton)
    // through our mixin
    public void trackChunk(ResourceKey<Level> dimension, ChunkPos pos) {
        if (isTracked(dimension, pos))  {
            this.trackedChunks
                    .get(dimension)
                    .add(pos);

            return;
        }

        this.trackedChunks.put(dimension, new HashSet<>());
    }

    // Get if a chunk is actively being tracked by our VCS
    public boolean isTracked(ResourceKey<Level> dimension, ChunkPos chunkPos) {
        if (this.trackedChunks.containsKey(dimension))
            return this.trackedChunks.get(dimension).contains(chunkPos);

        return false;
    }

    // Stage a chunk
    public void stageChunk(ResourceKey<Level> dimension, ChunkPos pos, Instant stageTime) {
        if (isStaged(dimension, pos)) {
            this.stagedChunks
                    .get(dimension)
                    .put(pos, stageTime);

            return;
        }

        this.stagedChunks.put(dimension, new HashMap<>());
    }

    // Get if a chunk is staged
    public boolean isStaged(ResourceKey<Level> dimension, ChunkPos pos) {
        if (this.stagedChunks.containsKey(dimension))
            return this.stagedChunks.get(dimension).containsKey(pos);

        return false;
    }

    // Get if a chunk is staged with a specific timestamp
    public boolean isStagedAtTime(ResourceKey<Level> dimension, ChunkPos chunkPos, Instant stageTime) {
        if (this.stagedChunks.containsKey(dimension))
            if (this.stagedChunks.get(dimension).containsKey(chunkPos))
                return this.stagedChunks.get(dimension).get(chunkPos).equals(stageTime);

        return false;
    }

    // Main stage subroutine
    public void stage(StageScope scope) {
        if (scope == StageScope.ALL)
            stageAll();

        canCommit = true;
    }

    // Stage all tracked chunks to be committed
    public void stageAll() {
        Instant stageTime = Instant.now();

        trackedChunks.forEach((dimension, chunkPositions) -> {
            for (ChunkPos chunkPos : chunkPositions)
                this.stageChunk(dimension, chunkPos, stageTime);
        });
    }

    // Get the chunks that are staged
    public String stageDiff() {
        StringBuilder stageDiff = new StringBuilder();

        stagedChunks.forEach((dimension, chunkWithStageTimestamp) -> {
            stageDiff.append(dimension.identifier());
            stageDiff.append(": ");

            chunkWithStageTimestamp.forEach((chunk, stageTime) -> {
                stageDiff.append(stageTime.toString());
                stageDiff.append(": [x: ");
                stageDiff.append(chunk.x());
                stageDiff.append(", z: ");
                stageDiff.append(chunk.z());
                stageDiff.append("]\n");
            });
        });

        return stageDiff.toString();
    }

    // Commit the staged changes
    public void commit(String commitMsg) {
        if (canCommit) {
            // For now, clear the staged chunks, as we commit them. This won't be the behavior forever
            this.stagedChunks.clear();

            MVCS.LOGGER.debug("ChunkTracker::commit() called with commit message: {}", commitMsg);

            canCommit = false;
        }
    }

    // Serialize to json
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
