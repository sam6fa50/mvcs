package sam6fa50.mvcs.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.LevelChunk;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sam6fa50.mvcs.MVCS;

@Mixin(LevelChunk.class)
public class LevelChunkSetBlockStateMixin {
    @Inject(method = "setBlockState", at = @At("RETURN"))
    private void mvcs$onSetBlockState(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<BlockState> cir) {
        // Check previous state value to see if function ran properly; null return means the function did not complete
        // its complete, successful mutation pass
        BlockState previousState = cir.getReturnValue();

        if (previousState == null) return;

        LevelChunk chunk = (LevelChunk)((Object)this);

        if (!(chunk.getLevel() instanceof ServerLevel level)) return;

        MVCS.MIXIN_BUS.onBlockStateSet(level
                .dimension(), chunk.getPos());

        MVCS.LOGGER.info("Tracking Chunk: [x: {}, z: {}] AT BLOCK [x: {}, y: {}, z: {}]",
                chunk.getPos().x(), chunk.getPos().z(), pos.getX(), pos.getY(), pos.getZ());
    }
}
