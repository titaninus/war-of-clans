package titaninus.warofclans.core;

import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import titaninus.warofclans.core.interfaces.Updatable;
import titaninus.warofclans.gamelogic.GameMaster;
import titaninus.warofclans.server.WarOfClansServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

public class Mine implements Updatable {
    public transient Territory Owner;
    public MineType TypeOfMine;
    // Seconds between resources
    public int SpawnRate = 1;

    private boolean isActive;

    public int Cost;

    public transient MineChest Chest;
    private transient Vec3i _position;
    private List<Integer> RawPosition;

    // Only for Deserialize
    public Mine() {
    }

    public Mine(MineType type, int rate, int cost, Vec3i position, Territory owner) {
        TypeOfMine = type;
        SpawnRate = rate;
        Cost = cost;
        isActive = false;
        SetPosition(position);
        Init(owner);
    }

    private void InstantiateMineChest() {
        Chest = new MineChest(GetItemByType(TypeOfMine), new BlockPos(_position));
    }

    private Item GetItemByType(MineType typeOfMine) {
        switch (typeOfMine) {

            case Copper -> {
                return Items.COPPER_INGOT;
            }
            case Iron -> {
                return Items.IRON_INGOT;
            }
            case Gold -> {
                return Items.GOLD_INGOT;
            }
            case Emerald -> {
                return Items.EMERALD;
            }
            case Diamond -> {
                return Items.DIAMOND;
            }
            case Neserith -> {
                return Items.NETHERITE_INGOT;
            }
        }
        return null;
    }

    public void SetPosition(Vec3i position) {
        _position = position;
        WritePosition();
    }

    public Vec3i GetPos() {
        return _position;
    }
    private void WritePosition() {
        RawPosition = new ArrayList<>(List.of(_position.getX(), _position.getY(), _position.getZ()));
    }

    private void RestorePosition() {
        _position = Utils.ConvertFrom3IntArray(RawPosition);
    }

    public static Mine Create(Integer m, Vec3i pos, Territory territory) {
        int casted = m;
        var spawnRatePerHour = WarOfClansServer.WOC_CONFIG.SpawnRatesPerHour().get(casted);
        var spawnRate = 60 * 60 / spawnRatePerHour;
        var cost = WarOfClansServer.WOC_CONFIG.SpawnRatesPerHour().get(casted);
        switch (casted) {
            case 1 -> {
                return new Mine(MineType.Copper, spawnRate, cost, pos, territory);
            }
            case 2 -> {
                return new Mine(MineType.Iron, spawnRate, cost, pos, territory);
            }
            case 3 -> {
                return new Mine(MineType.Gold, spawnRate, cost, pos, territory);
            }
            case 4 -> {
                return new Mine(MineType.Emerald, spawnRate, cost, pos, territory);
            }
            case 5 -> {
                return new Mine(MineType.Diamond, spawnRate, cost, pos, territory);
            }
            case 6 -> {
                return new Mine(MineType.Neserith, spawnRate, cost, pos, territory);
            }
            default -> {
                return null;
            }
        }
    }

    public void Init(Territory owner) {
        Owner = owner;
        //GameMaster.Instance().AddUpdatable(this);
        Timer timer = new Timer(SpawnRate * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Code to be executed
                Update();
            }
        });
        RestorePosition();
        InstantiateMineChest();
        timer.setRepeats(true); // Only execute once
        timer.start();
    }

    public void Run() {
        isActive = true;
    }

    public void Stop() {
        isActive = false;
    }

    public void Update() {
        if (isActive) {
            InternalUpdate();
        }
    }

    private void InternalUpdate() {
        if (Chest != null) {
            Chest.SpawnResource(WarOfClansServer.MinecraftServer.getOverworld());
        }
    }
}
