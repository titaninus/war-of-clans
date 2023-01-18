package titaninus.warofclans.core;

import io.wispforest.owo.config.Option;
import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.PredicateConstraint;
import io.wispforest.owo.config.annotation.Sync;
import net.minecraft.util.math.Vec3i;
import org.lwjgl.glfw.GLFW;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@Config(name = "war-of-clans-config", wrapperName = "WarOfClansConfig")
public class WOCConfigModel {
    public int NumberPlayersInTeam = 5;
    public boolean AllowChangeTeamAtStage0 = true;
    public boolean AllowPickUpItemsAtNotOwnedZone = false;

    public int AmountOfStartPoints = 2000;

    public int startXOffset =  0;
    public int startZOffset = 0;
    public int territorySize = 128;
    public int MapHeight = 5;
    public int MapWidth = 5;
    /* Copper = 1
     * Iron = 2
     * Gold = 3
     * Emerald = 4
     * Diamond = 5
     * Neserith = 6
     * No mines = -1
     */
    public List<List<List<Integer>>> MinesMap = new ArrayList<>(List.of(
            List.of(List.of(-1), List.of(1, 2), List.of(1, 3), List.of(1, 2), List.of(-1)),
            List.of(List.of(1, 2), List.of(4, 3), List.of(4, 5), List.of(4, 3), List.of(1, 2)),
            List.of(List.of(1, 3), List.of(5, 2), List.of(6), List.of(5, 2), List.of(1, 3)),
            List.of(List.of(1, 2), List.of(4, 3), List.of(4, 5), List.of(4, 3), List.of(1, 2)),
            List.of(List.of(-1), List.of(1, 2), List.of(1, 3), List.of(1, 2), List.of(-1))
    ));

    public List<Integer> SpawnRatesPerHour = new ArrayList<>(List.of(
       0, 180, 60, 40, 90, 12, 2
    ));

    public List<Integer> AdditionalMinesCost = new ArrayList<>(List.of(
            0, 1000, 1440, 1440, 1620, 1800, 4000
    ));

    public float CostMultiplier = 1.5f;


    //Prices
    public int NeutralTerritoryPrice = 3000;
    public int EnemyTerritoryPrice = 5000;

    public FormattableTime TimeToChangeTeamForTerritory = FormattableTime.TryParse("10m");

    public Map<String, Integer> ResourcePrices = Map.of(
            "minecraft:copper_ingot", 1,
            "minecraft:iron_ore", 4,
            "minecraft:gold_ingot", 6,
            "minecraft:emerald", 3,
            "minecraft:diamond", 24,
            "minecraft:netherite_ingot", 150,
            "minecraft:nether_star", 600,
            "minecraft:lapis_lazuli", 2,
            "minecraft:redstone", 2
    );

    public int CaptureTotemPrice = 450;

    public FormattableTime Stage1Duration = FormattableTime.TryParse("2d");
    public FormattableTime Stage2Duration = FormattableTime.TryParse("2d");
    public FormattableTime Stage3Duration = FormattableTime.TryParse("4h");

    public boolean AllowAutoCompleteStage = false;

    public FormattableTimePeriods Stage2BattlePattern = FormattableTimePeriods.TryParse("16h|2h|6h");
    public FormattableTimePeriods Stage3BattlePattern = FormattableTimePeriods.TryParse("16h|2h|6h");
    public boolean UseBattlePatternAsDateTime = true;

    public FormattableTime TimeToCaptureTotemInNeutral = FormattableTime.TryParse("3m");
    public FormattableTime TimeToCaptureTotemInEnemy = FormattableTime.TryParse("5m");
    public FormattableTime TimeToCaptureBaseFlag = FormattableTime.TryParse("10m");
    public int DistanceToCaptureBaseFlag = 5;


    @PredicateConstraint("predicateForPositions")
    public List<Integer> LobbySpawnPos = new ArrayList<Integer>(List.of(0, 0, 0));

    public static boolean predicateForPositions(List<Integer> list) {
        return list.size() == 3;
    }



}
