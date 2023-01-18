package titaninus.warofclans.permissions;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import titaninus.warofclans.core.WOCMap;
import titaninus.warofclans.core.WOCTeam;

public class ClaimsCore {
    public static boolean IsAccessToPosRestricted(World world, BlockPos blockPos, PlayerEntity player) {
        // On singlePLayer always true
        if (player.hasPermissionLevel(4)) {
            return false;
        }
        if (world.getRegistryKey() != World.OVERWORLD) {
            return false;
        }
        var territory = WOCMap.Instance().GetTerritoryByPos(blockPos);
        if (territory == null) {
            return true;
        }
        if (!territory.IsCaptured) {
            return true;
        }
        if (world.isClient) {
            var color = territory.OwnerTeamColor;
            var team = WOCTeam.GetTeamByColor(color);
            return !team.ContainsPlayer(player);
        } else {
            return !territory.OwnerTeam.ContainsPlayer(player);
        }
    }

    public static boolean HaveAccessToBlockPos(World world, BlockPos blockPos, PlayerEntity player) {
        if (player.hasPermissionLevel(4)) {
            return true;
        }
        if (world.getRegistryKey() != World.OVERWORLD) {
            return true;
        }
        var territory = WOCMap.Instance().GetTerritoryByPos(blockPos);
        if (territory == null) {
            return false;
        }
        if (!territory.IsCaptured) {
            return false;
        }
        return territory.OwnerTeam.ContainsPlayer(player);
    }

    public static boolean HaveAccessToBlockPos(World world, BlockPos blockPos, String playerName) {
        if (world.getRegistryKey() != World.OVERWORLD) {
            return true;
        }
        var territory = WOCMap.Instance().GetTerritoryByPos(blockPos);
        if (territory == null) {
            return false;
        }
        if (!territory.IsCaptured) {
            return false;
        }
        return territory.OwnerTeam.ContainsPlayer(playerName);
    }
}
