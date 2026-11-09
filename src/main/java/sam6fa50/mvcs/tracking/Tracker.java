package sam6fa50.mvcs.tracking;

// The Tracker class tracks all tracked components of a commit
public class Tracker {
    private static Tracker server_instance = null;
    private boolean canCommit = false;
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

    public boolean stage(StageScope scope) {
        chunkTracker.stage(scope);

        canCommit = true;
        return true;
    }

    public String stageDiff() {
        return chunkTracker.stageDiff();
    }

    public boolean commit(String commitMsg) {
        if (canCommit) {
            chunkTracker.commit(commitMsg);
            canCommit = false;
            return true;
        }

        return false;
    }

    public String serialize() {
        StringBuilder json = new StringBuilder();

        if (chunkTracker != null) {
            json.append(chunkTracker.serialize());
        } else throw new RuntimeException("ChunkTracker is null");

        return json.toString();
    }

    public ChunkTracker getChunkTracker() {
        return chunkTracker;
    }
}
