package titaninus.warofclans.mixin;

import net.minecraft.block.AbstractBlock;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.WorldView;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import titaninus.warofclans.permissions.ClaimsCore;

@Mixin(AbstractBlock.class)
public class AbstractBlockMixin {
    @Inject(method = "onUse", at= @At("HEAD"), cancellable = true)
    public void onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, Hand hand, BlockHitResult hit, CallbackInfoReturnable<ActionResult> info){
        if (ClaimsCore.IsAccessToPosRestricted(world, pos, player)) {
            info.setReturnValue(ActionResult.FAIL);
        }
    }

    @Inject(method = "canReplace", at= @At("HEAD"), cancellable = true)
    public void canReplace(BlockState state, ItemPlacementContext ctx, CallbackInfoReturnable<Boolean> info){
        var player = ctx.getPlayer();
        if (player == null) {
            return;
        }
        if (ClaimsCore.IsAccessToPosRestricted(ctx.getWorld(), ctx.getBlockPos(), ctx.getPlayer())) {
            info.setReturnValue(false);
        }
    }
}
