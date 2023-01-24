package titaninus.warofclans.permissions;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import titaninus.warofclans.core.Territory;
import titaninus.warofclans.core.WOCMap;
import titaninus.warofclans.core.WOCTeam;
import titaninus.warofclans.gamelogic.GameMaster;

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
            return RestrictedToPlayerOnTerritoryOwnedBy(player, territory, team);
        } else {
            var team = territory.OwnerTeam;
            return RestrictedToPlayerOnTerritoryOwnedBy(player, territory, team);
        }
    }

    private static boolean RestrictedToPlayerOnTerritoryOwnedBy(PlayerEntity player, Territory territory, WOCTeam team) {
        if (team.InAnnihilationMode) {
            return false;
        }
        if (GameMaster.Instance().IsInBattle()) {
            if (territory.IsBased && !team.ContainsPlayer(player)) {
                return true;
            }
            return false;
        }
        return !team.ContainsPlayer(player);
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

    public static boolean CanPlaceTotem(World world, BlockPos blockPos, PlayerEntity player) {
        //TODO check if in block pos is totem pedestal
        if (player.hasPermissionLevel(4)) {
            return true;
        }
        if (!GameMaster.Instance().IsInBattle()) {
            return false;
        }
        var territory = WOCMap.Instance().GetTerritoryByPos(blockPos);
        if (territory == null) {
            return false;
        }
        var team = WOCTeam.FindPlayerTeam(player);
        if (team == null) {
            return false;
        }
        if (territory.IsBased) {
            return false;
        }
        if (!territory.IsCaptured) {
            return true;
        }
        // You cannot set totem on your territory
        if (territory.OwnerTeamColor == team.Color) {
            return false;
        }
        return true;
    }

    public static boolean CanDestroyTotem(World world, BlockPos blockPos, PlayerEntity player) {
        //TODO check if in block pos is capture totem
        //TODO check totem is not activated
        return true;
    }

    public static boolean CanUseMineChest(World world, BlockPos pos, PlayerEntity player) {
        // Can use in neutral
        // Can use if you are member owning time
        if (player.hasPermissionLevel(4)) {
            return true;
        }
        var territory = WOCMap.Instance().GetTerritoryByPos(pos);
        if (territory == null) {
            return false;
        }
        if (!territory.IsCaptured) {
            return true;
        }
        var playerTeam = WOCTeam.FindPlayerTeam(player);
        if (playerTeam == null) {
            return false;
        }
        if (territory.OwnerTeamColor == playerTeam.Color) {
            return true;
        }

        return false;
    }
}
