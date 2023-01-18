package titaninus.warofclans.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.GameMode;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import titaninus.warofclans.permissions.ClaimsCore;

@Mixin(PlayerEntity.class)
public abstract class PlayerEntityMixin extends LivingEntity {

    protected PlayerEntityMixin(EntityType<? extends LivingEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "isBlockBreakingRestricted", cancellable = true, at = @At("HEAD"))
    public void isBlockBreakingRestricted(World world, BlockPos pos, GameMode mode, CallbackInfoReturnable<Boolean> info) {
        if (ClaimsCore.IsAccessToPosRestricted(world, pos, ((PlayerEntity) (Object) this))) {
            info.setReturnValue(true);
        }
    }

    @Inject(method = "canPlaceOn", cancellable = true, at = @At("HEAD"))
    public void canPlaceOn(BlockPos pos, Direction facing, ItemStack stack, CallbackInfoReturnable<Boolean> info){
        if (ClaimsCore.IsAccessToPosRestricted(world, pos, ((PlayerEntity) (Object) this))) {
            info.setReturnValue(false);
        }
    }

    @Inject(method = "interact", cancellable = true, at = @At("HEAD"))
    public void interact(Entity entity, Hand hand, CallbackInfoReturnable<ActionResult> info){
        if (ClaimsCore.IsAccessToPosRestricted(world, entity.getBlockPos(), ((PlayerEntity) (Object) this))) {
            info.setReturnValue(ActionResult.FAIL);
        }
    }
}
