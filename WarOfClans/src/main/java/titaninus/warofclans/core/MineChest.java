package titaninus.warofclans.core;

import net.minecraft.block.Blocks;
import net.minecraft.block.ChestBlock;
import net.minecraft.block.entity.ChestBlockEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.entry.RegistryEntry;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import titaninus.warofclans.server.WarOfClansServer;

public class MineChest {
    public Item SpawningItem;
    public BlockPos BoundedChestPos;
    public ChestBlock BoundedChest;

    private void InstantiateBlock() {
        var pos = BoundedChestPos;
        var world = WarOfClansServer.MinecraftServer.getOverworld();
        var prevState = world.getBlockState(pos);
        if (prevState.getBlock() instanceof ChestBlock) {
            return;
        }
        world.setBlockState(pos, Blocks.CHEST.getDefaultState());
    }

    public MineChest(Item spawningItem, BlockPos position) {
        SpawningItem = spawningItem;
        BoundedChestPos = position;
        BoundedChest = (ChestBlock) Blocks.CHEST;
        InstantiateBlock();
    }
    public boolean SpawnResource(World world) {
        var entity = world.getBlockEntity(BoundedChestPos);
        if (!(entity instanceof ChestBlockEntity) ) {
            InstantiateBlock();
            entity = world.getBlockEntity(BoundedChestPos);
        }

        var inventory = (ChestBlockEntity) entity;
        for (int i = 0; i < inventory.size(); i++) {
            var stack = inventory.getStack(i);
            if (!stack.isEmpty()) {
                if (stack.isOf(SpawningItem)) {
                    var count = stack.getCount();
                    var max = stack.getMaxCount();
                    if (count + 1 <= max) {
                        stack.setCount(count + 1);
                        inventory.setStack(i, stack);
                        //world.setBlockState(BoundedChestPos, state);
                        return true;
                    }
                }
            } else {
                stack = new ItemStack(RegistryEntry.of(SpawningItem), 1);
                inventory.setStack(i, stack);
                //world.setBlockState(BoundedChestPos, state);
                return true;
            }
        }
        return false;
    }
}
