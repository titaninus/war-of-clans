package titaninus.warofclans.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.InteractionObserver;
import net.minecraft.entity.passive.MerchantEntity;
import net.minecraft.entity.passive.VillagerEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.stat.Stats;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.village.VillagerData;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import titaninus.warofclans.permissions.ClaimsCore;

@Mixin(VillagerEntity.class)
public abstract class Villager extends MerchantEntity implements InteractionObserver {
    public Villager(EntityType<? extends MerchantEntity> entityType, World world) {
        super(entityType, world);
    }
    @Shadow
    private native void sayNo();

    @Inject(at = @At("HEAD"), method = "interactMob", cancellable = true)
    public void onAttemptInteract(PlayerEntity player, Hand hand, CallbackInfoReturnable<ActionResult> callback) {
        if (ClaimsCore.IsAccessToPosRestricted(world, getBlockPos(), player)) {
            // Shake head
            this.sayNo();

            // Increment server statistic
            player.incrementStat(Stats.TALKED_TO_VILLAGER);

            // Set return value
            callback.setReturnValue(ActionResult.FAIL);
        }
    }
}
