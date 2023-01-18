package titaninus.warofclans.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.StorageMinecartEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import titaninus.warofclans.permissions.ClaimsCore;

@Mixin(StorageMinecartEntity.class)
public abstract class StorageMinecartMixin extends AbstractMinecartEntity {
    protected StorageMinecartMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "interact", cancellable = true)
    public void cartInteraction(PlayerEntity player, Hand hand, CallbackInfoReturnable<Boolean> callback) {
        if (ClaimsCore.IsAccessToPosRestricted(world, getBlockPos(), player)) {

            // Cancel the event
            callback.setReturnValue(false);
        }
    }
}
