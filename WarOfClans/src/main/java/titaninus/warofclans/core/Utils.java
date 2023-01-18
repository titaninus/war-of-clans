package titaninus.warofclans.core;

import net.fabricmc.fabric.api.networking.v1.PlayerLookup;
import net.minecraft.network.packet.s2c.play.PlayerPositionLookS2CPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.server.world.ChunkTicketType;
import net.minecraft.util.math.*;
import titaninus.warofclans.events.PlayerJoinCallback;
import titaninus.warofclans.server.WarOfClansServer;

import java.util.*;
import java.util.function.Function;
import oshi.util.tuples.Pair;


public class Utils {
    public static Map<String, ArrayList<Pair<Function<Pair<ServerPlayerEntity, Object>, Boolean> ,Object>>> PlayerActionsMap = new HashMap<>();
    public static void SetWorldSpawnPosition(BlockPos pos, float angle) {

        WarOfClansServer.MinecraftServer.getOverworld().setSpawnPos(pos, angle);
    }
    public static void SetPlayerSpawnPosition(ServerPlayerEntity player, BlockPos pos, float angle) {

        var registry = WarOfClansServer.MinecraftServer.getOverworld().getRegistryKey();
        player.setSpawnPoint(registry, pos, angle, true, false);
    }

    public static BlockPos ConvertFrom3IntArray(List<Integer> source) {
        return new BlockPos(source.get(0), source.get(1), source.get(2));
    }

    public static void TeleportPlayer(ServerPlayerEntity player, BlockPos pos, float yaw, float pitch) {
        var world = player.getWorld();
        float f = MathHelper.wrapDegrees(yaw);
        float g = MathHelper.wrapDegrees(pitch);
        ChunkPos chunkPos = new ChunkPos(pos);
        world.getChunkManager().addTicket(ChunkTicketType.POST_TELEPORT, chunkPos, 1, player.getId());
        player.stopRiding();
        if (((ServerPlayerEntity)player).isSleeping()) {
            ((ServerPlayerEntity)player).wakeUp(true, true);
        }

        if (world == player.world) {
            ((ServerPlayerEntity)player).networkHandler.requestTeleport(pos.getX(), pos.getY(), pos.getZ(), f, g, EnumSet.noneOf(PlayerPositionLookS2CPacket.Flag.class));
        } else {
            ((ServerPlayerEntity)player).teleport(world, pos.getX(), pos.getY(), pos.getZ(), f, g);
        }

        player.setHeadYaw(f);
    }

    public static ServerPlayerEntity GetOnlinePlayerByName(String player) {
        for (var p: PlayerLookup.all(WarOfClansServer.MinecraftServer)) {
            if (p.getName().getString() == player) {
                return p;
            }
        }
        return null;
    }

    public static void AddToPlayerQueue(String player, Function<Pair<ServerPlayerEntity, Object>, Boolean> FuncToCall, Object arg) {
        if (!PlayerActionsMap.containsKey(player)) {
            PlayerActionsMap.put(player, new ArrayList<>());
        }
        var list = PlayerActionsMap.get(player);
        list.add(new Pair<>(FuncToCall, arg));
    }

    public static void ResolveQueue(ServerPlayerEntity player) {

    }

    public static class ResolveQueueOnPlayerJoin implements PlayerJoinCallback {

        @Override
        public void joinServer(ServerPlayerEntity player, MinecraftServer server) {
            if (PlayerActionsMap.containsKey(player.getName().getString())) {
                var playerQueue = PlayerActionsMap.get(player.getName().getString());
                for (var a: playerQueue) {
                    a.getA().apply(new Pair<>(player, a.getB()));
                }
            }
        }
    }


    public static int FindFirstGroundYPos(BlockPos searchAt) {
        var world = WarOfClansServer.MinecraftServer.getOverworld();
        var chunk = world.getChunk(searchAt);
        var minY = chunk.getBottomY();
        var maxY = chunk.getTopY();
        var searchUntil = Math.min(searchAt.getY() - minY, maxY - searchAt.getY());
        for (int i = 0; i < searchUntil; ++i) {
            var lowPos = searchAt.add(0, -i, 0);
            if (chunk.getBlockState(lowPos).isAir()) {
                // Check up by 1 pos up (should be air)
                // Check up by 1 pos down (should be not)
                var adjustPosUp = lowPos.add(0, 1,0);
                var adjustPosDown = lowPos.add(0, -1,0);
                if (chunk.getBlockState(adjustPosUp).isAir() && !chunk.getBlockState(adjustPosDown).isAir()) {
                    return lowPos.getY();
                }
            }
            var highPos = searchAt.add(0, i, 0);
            if (chunk.getBlockState(highPos).isAir()) {
                // Check up by 1 pos up (should be air)
                // Check up by 1 pos down (should be not)
                var adjustPosUp = highPos.add(0, 1,0);
                var adjustPosDown = highPos.add(0, -1,0);
                if (chunk.getBlockState(adjustPosUp).isAir() && !chunk.getBlockState(adjustPosDown).isAir()) {
                    return highPos.getY();
                }
            }
        }
        var goingDown = (searchUntil + searchAt.getY()) == maxY;
        var start = searchAt.getY() + searchUntil;
        var end = chunk.getTopY();
        Function<Integer, Integer> increment = (i) -> i + 1;
        if (goingDown) {
            start = searchAt.getY() - searchUntil;
            end = chunk.getBottomY();
            increment = (i) -> i - 1;
        }
        for (var i = start; i < end; increment.apply(i)) {
            var pos = new BlockPos(searchAt.getX(), i, searchAt.getZ());
            if (chunk.getBlockState(pos).isAir()) {
                // Check up by 1 pos up (should be air)
                // Check up by 1 pos down (should be not)
                var adjustPosUp = pos.add(0, 1,0);
                var adjustPosDown = pos.add(0, -1,0);
                if (chunk.getBlockState(adjustPosUp).isAir() && !chunk.getBlockState(adjustPosDown).isAir()) {
                    return pos.getY();
                }
            }
        }
        return searchAt.getY();
    }

    public static Vec3i FloatToInt(Vec3d source) {
        return new Vec3i((int) source.x, (int)source.y, (int)source.z);
    }
}
