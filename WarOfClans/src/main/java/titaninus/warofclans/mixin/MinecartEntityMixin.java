package titaninus.warofclans.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.vehicle.AbstractMinecartEntity;
import net.minecraft.entity.vehicle.MinecartEntity;
import net.minecraft.util.Hand;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import titaninus.warofclans.permissions.ClaimsCore;

@Mixin(MinecartEntity.class)
public abstract class MinecartEntityMixin extends AbstractMinecartEntity {


    protected MinecartEntityMixin(EntityType<?> entityType, World world) {
        super(entityType, world);
    }

    @Inject(at = @At("HEAD"), method = "interact", cancellable = true)
    private void tryMinecartEnter(PlayerEntity player, Hand hand, CallbackInfoReturnable<Boolean> callback) {
        // Player is in creative
        if ((player.isCreative()) || player.isSpectator())
            return;

        // If player can't enter Minecart
        if (ClaimsCore.IsAccessToPosRestricted(world, getBlockPos(), player)) {
            // Cancel the event
            callback.setReturnValue(false);
        }

    }


}
