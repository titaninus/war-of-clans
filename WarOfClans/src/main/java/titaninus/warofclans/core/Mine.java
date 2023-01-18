package titaninus.warofclans.core;

import titaninus.warofclans.server.WarOfClansServer;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class Mine {
    public MineType TypeOfMine;
    // Seconds between resources
    public int SpawnRate = 1;

    private boolean isActive;

    public int Cost;

    public Mine() {

    }

    public Mine(MineType type, int rate, int cost) {
        TypeOfMine = type;
        SpawnRate = rate;
        Cost = cost;
        isActive = false;
        Init();
    }

    public static Mine Create(Integer m) {
        int casted = m;
        var spawnRatePerHour = WarOfClansServer.WOC_CONFIG.SpawnRatesPerHour().get(casted);
        var spawnRate = 60 * 60 / spawnRatePerHour;
        var cost = WarOfClansServer.WOC_CONFIG.SpawnRatesPerHour().get(casted);
        switch (casted) {
            case 1 -> {
                return new Mine(MineType.Copper, spawnRate, cost);
            }
            case 2 -> {
                return new Mine(MineType.Iron, spawnRate, cost);
            }
            case 3 -> {
                return new Mine(MineType.Gold, spawnRate, cost);
            }
            case 4 -> {
                return new Mine(MineType.Emerald, spawnRate, cost);
            }
            case 5 -> {
                return new Mine(MineType.Diamond, spawnRate, cost);
            }
            case 6 -> {
                return new Mine(MineType.Neserith, spawnRate, cost);
            }
            default -> {
                return null;
            }
        }
    }

    public void Init() {
        Timer timer = new Timer(SpawnRate * 1000, new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent arg0) {
                // Code to be executed
                Update();
            }
        });
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

    }
}
