package net.rainy_juzixiao.justforbuilding.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.rainy_juzixiao.justforbuilding.build.BlockOperations;
import net.rainy_juzixiao.justforbuilding.build.BuildTracker;
import net.rainy_juzixiao.justforbuilding.build.operation.BuildOperation;
import net.rainy_juzixiao.justforbuilding.build.operation.OperationType;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(Level.class)
public abstract class LevelMixin {

    @Inject(method = "setBlock(Lnet/minecraft/core/BlockPos;Lnet/minecraft/world/level/block/state/BlockState;I)Z", at = @At("HEAD"))
    private void jfb_captureSetBlock(BlockPos pos, BlockState state, int flags, CallbackInfoReturnable<Boolean> cir) {
        List<BuildOperation> ops = BuildTracker.getOperations();
        if (ops != null) {
            Level self = (Level)(Object)this;
            if (self.isLoaded(pos)) {
                BlockState oldState = self.getBlockState(pos);
                if (oldState != state) {
                    ops.add(new BuildOperation(
                            OperationType.PLACE_BLOCK,
                            self.dimension().location().toString(),
                            pos.asLong(),
                            BlockOperations.idOf(oldState),
                            BlockOperations.idOf(state)
                    ));
                }
            }
        }
    }
}