package titaninus.warofclans.mixin;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ArmorStandItem;
import net.minecraft.item.AxeItem;
import net.minecraft.item.BlockItem;
import net.minecraft.item.EndCrystalItem;
import net.minecraft.item.EnderEyeItem;
import net.minecraft.item.FlintAndSteelItem;
import net.minecraft.item.HoeItem;
import net.minecraft.item.Item;
import net.minecraft.item.ItemUsageContext;
import net.minecraft.item.LeadItem;
import net.minecraft.item.MinecartItem;
import net.minecraft.item.MusicDiscItem;
import net.minecraft.item.ShovelItem;
import net.minecraft.item.WritableBookItem;
import net.minecraft.item.WrittenBookItem;
import net.minecraft.util.ActionResult;
import org.jetbrains.annotations.NotNull;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import titaninus.warofclans.permissions.ClaimsCore;

@Mixin({
        FlintAndSteelItem.class,
        ShovelItem.class,
        BlockItem.class,
        EnderEyeItem.class,
        AxeItem.class,
        HoeItem.class,
        ShovelItem.class,
        LeadItem.class,
        MinecartItem.class,
        MusicDiscItem.class,
        WrittenBookItem.class,
        WritableBookItem.class,
        ArmorStandItem.class,
        EndCrystalItem.class
})
public abstract class UsableItems extends Item {

    public UsableItems(Settings settings) {
        super(settings);
    }

    @Inject(at = @At("HEAD"), method = "useOnBlock", cancellable = true)
    public void onUseOfItem(@NotNull ItemUsageContext context, @NotNull CallbackInfoReturnable<ActionResult> callback) {
        PlayerEntity player = context.getPlayer();
        if (player != null) {
            if (ClaimsCore.IsAccessToPosRestricted(context.getWorld(), context.getBlockPos(), player)) {
                callback.setReturnValue(ActionResult.FAIL);
            }
        }
    }

}