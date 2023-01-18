package titaninus.warofclans.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.WorldAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import titaninus.warofclans.permissions.ClaimsCore;

@Mixin(Block.class)
public class BlockMixin {

    @Inject(at = @At("HEAD"), method = "replace(Lnet/minecraft/block/BlockState;Lnet/minecraft/block/BlockState;Lnet/minecraft/world/WorldAccess;Lnet/minecraft/util/math/BlockPos;II)V", cancellable = true)
    private static void replace(BlockState state, BlockState newState, WorldAccess world, BlockPos pos, int flags, int maxUpdateDepth, CallbackInfo callback) {
        if (world.isClient()) {

        }
        //if (ClaimsCore.HaveAccessToBlockPos(world, pos, world.))
    }
}
