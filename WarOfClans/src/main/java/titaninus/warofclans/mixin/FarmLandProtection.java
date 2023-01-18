package titaninus.warofclans.mixin;

import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.block.FarmlandBlock;
import net.minecraft.entity.Entity;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import titaninus.warofclans.permissions.ClaimsCore;

@Mixin(FarmlandBlock.class)
public abstract class FarmLandProtection extends Block {


    public FarmLandProtection(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "onLandedUpon", cancellable = true)
    public void entityLandedUpon(final World world, final BlockState state, final BlockPos pos, final Entity entity, final float distance, final CallbackInfo callback) {
        // If entity isn't a player, don't worry about permission checking
        if (!(entity instanceof ServerPlayerEntity player))
            return;

        // If player is not allowed to break in the territory
        if (ClaimsCore.IsAccessToPosRestricted(world, pos, player)) {

            // Cancel the event
            callback.cancel();

            // Call the super (For fall damage)
            super.onLandedUpon(world, state, pos, entity, distance);
        }
    }

}
