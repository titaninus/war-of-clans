package titaninus.warofclans;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.object.builder.v1.block.FabricBlockSettings;
import net.minecraft.block.Block;
import net.minecraft.block.Material;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class WarOfClans implements ModInitializer {


    public static final String MOD_ID = "warofclans";
    public static BlockEntityType<ChestBlockEntity> CHEST_ENTITY_TYPE;
    @Override
    public void onInitialize() {
    }
}
