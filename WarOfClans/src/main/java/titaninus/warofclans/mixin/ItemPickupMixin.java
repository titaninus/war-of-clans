package titaninus.warofclans.mixin;

import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityType;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import titaninus.warofclans.permissions.ClaimsCore;

import java.util.UUID;

@Mixin(ItemEntity.class)
public abstract class ItemPickupMixin extends Entity {
    public ItemPickupMixin(EntityType<?> type, World world) {
        super(type, world);
    }

    @Shadow private int pickupDelay;
    @Shadow
    private UUID thrower;
    @Shadow private UUID owner;

    @Inject(at = @At("HEAD"), method = "onPlayerCollision", cancellable = true)
    public void attemptPickup(PlayerEntity player, CallbackInfo callback) {
        if (!this.world.isClient) {
            if ( this.pickupDelay == 0 ) {
                // Check if the entity is owned by the player (They dropped it)
                if (player.getUuid().equals(this.thrower) || player.getUuid().equals(this.owner) || (player.isCreative()))
                    return;

                // Check if the player can pickup items in the chunk
                BlockPos itemPos = this.getBlockPos();
                if (ClaimsCore.IsAccessToPosRestricted(world, itemPos, player)) {
                    callback.cancel();
                }
            }
        }
    }

}
