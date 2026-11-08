package sam6fa50.mvcs.tracking;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.nio.file.Files;

// The Tracker class tracks all tracked components of a commit
public class Tracker {
    private static Tracker server_instance = null;
    private ChunkTracker chunkTracker = null;

    private Tracker() {
        chunkTracker = new ChunkTracker();
    }

    public static Tracker getCurrentInstance() {
        if (server_instance == null)
            server_instance = new Tracker();

        return server_instance;
    }

    // The only instance is tied to the loaded world
    public static void closeCurrentInstance() {
        server_instance = null;
    }

    public ChunkTracker getChunkTracker() {
        return chunkTracker;
    }

    public String serialize() {
        StringBuilder json = new StringBuilder();

        if (chunkTracker != null) {
            json.append(chunkTracker.serialize());
        } else throw new RuntimeException("ChunkTracker is null");

        return json.toString();
    }
}
