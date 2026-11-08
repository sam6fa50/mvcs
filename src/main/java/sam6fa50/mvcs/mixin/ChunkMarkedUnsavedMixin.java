package sam6fa50.mvcs.mixin;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sam6fa50.mvcs.tracking.MixinBus;

// This mixin intercepts the `markUnsaved()` function in `ChunkAccess` that
// is needed to get a chunk's dimension and pos so that we are able to identify
// it in this world
@Mixin(ChunkAccess.class)
public class ChunkMarkedUnsavedMixin {
    @Inject(at = @At("HEAD"), method = "markUnsaved")
    private void mvcs$onMarkedUnsaved(CallbackInfo ci) {
        ChunkAccess chunk = (ChunkAccess)(Object)this;

        // This mixin covers LevelChunk and ImposterProtoChunk
        if (chunk instanceof LevelChunk levelChunk) {
            MixinBus.getInstance().onMarkedUnsaved(
                    levelChunk
                            .getLevel()
                            .dimension(),
                    levelChunk.getPos()
            );
        } else if (chunk instanceof ImposterProtoChunk imposterProtoChunk) {
            MixinBus.getInstance().onMarkedUnsaved(
                    imposterProtoChunk
                            .getWrapped()
                            .getLevel()
                            .dimension(),
                    imposterProtoChunk.getPos()
            );
        }
    }
}
