package sam6fa50.mvcs.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.chunk.ChunkAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import sam6fa50.mvcs.MVCS;

@Mixin(Level.class)
public class LevelSetBlockMixin {
    @Inject(method = "setBlock", at = @At("HEAD"))
    private void mvcs$markBlockSet(BlockPos pos, BlockState blockState, int updateFlags, int updateLimit, CallbackInfoReturnable<Boolean> cir) {
        Level level = (Level)((Object)this);

        if (level instanceof ServerLevel serverLevel) {
            ChunkAccess chunkAt = serverLevel.getChunkAt(pos);

            MVCS.MIXIN_BUS.onBlockSet(
                    serverLevel
                            .dimension(),
                    chunkAt.getPos()
            );

            MVCS.LOGGER.debug("LEVEL CHUNK MARKED CHANGED SINCE STAGED: block: [x: {}, y: {}, z: {}] @ chunk: [x: {}, z: {}]",
                    pos.getX(), pos.getY(), pos.getZ(), chunkAt.getPos().x(), chunkAt.getPos().z());
        }
    }
}
