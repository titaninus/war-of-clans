package titaninus.warofclans.core;

import titaninus.warofclans.core.interfaces.Updatable;
import titaninus.warofclans.gamelogic.GameMaster;

public class InstalledCaptureTotem implements Updatable {
    public Territory CaptureTerritory;

    public boolean IsActivated;
    public boolean IsTimerActive;
    public int TotalSecondsLeft;

    public TeamColor OwnedTeam;

    public void StartTimer() {
        IsTimerActive = true;
    }

    public void StopTimer() {
        IsTimerActive = false;
    }

    @Override
    public void Update() {
        if (IsTimerActive) {
            TotalSecondsLeft -= 1;
            if (TotalSecondsLeft <= 0) {
                IsActivated = true;
                GameMaster.Instance().CheckForTerritoryCapture(CaptureTerritory);
            }
        }
    }

    public void DeleteTotem() {
        //TODO Delete totem block from map
        StopTimer();
    }
}
