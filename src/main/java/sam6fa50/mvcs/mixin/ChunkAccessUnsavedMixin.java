package sam6fa50.mvcs.mixin;

import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.ImposterProtoChunk;
import net.minecraft.world.level.chunk.LevelChunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import sam6fa50.mvcs.MVCS;

// This mixin intercepts the `markUnsaved()` function in `ChunkAccess` that
// is needed to get a chunk's dimension and pos so that we are able to identify
// it in this world
@Mixin(ChunkAccess.class)
public class ChunkAccessUnsavedMixin {
    @Inject(method = "markUnsaved", at = @At("HEAD"))
    private void mvcs$onMarkedUnsaved(CallbackInfo ci) {
        ChunkAccess chunk = (ChunkAccess)((Object)this);

        // This mixin covers LevelChunk and ImposterProtoChunk
        if (chunk instanceof LevelChunk levelChunk) {
           // if (MVCS.MIXIN_BUS.isTracked(
           //         levelChunk
           //                 .getLevel()
           //                 .dimension(),
           //         chunk.getPos()))
           //      return;

           MVCS.MIXIN_BUS.onMarkedUnsaved(
                    levelChunk
                            .getLevel()
                            .dimension(),
                    levelChunk.getPos()
           );

           MVCS.LOGGER.info("---MARKED A LEVEL CHUNK UNSAVED: (x: {}, z: {})",
                   levelChunk.getPos().x(), levelChunk.getPos().z());

        } else if (chunk instanceof ImposterProtoChunk imposterProtoChunk) {
            // if (MVCS.MIXIN_BUS.isTracked(
            //         imposterProtoChunk
            //                 .getWrapped()
            //                 .getLevel()
            //                 .dimension(), chunk.getPos()))
            //     return;

            MVCS.MIXIN_BUS.onMarkedUnsaved(
                    imposterProtoChunk
                            .getWrapped()
                            .getLevel()
                            .dimension(),
                    imposterProtoChunk.getPos()
            );

            MVCS.LOGGER.info("---MARKED AN IMPOSTER PROTO CHUNK UNSAVED: (x: {}, z: {})",
                    imposterProtoChunk.getPos().x(), imposterProtoChunk.getPos().z());
        }
    }
}
