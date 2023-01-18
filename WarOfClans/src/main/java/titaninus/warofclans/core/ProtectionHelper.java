package titaninus.warofclans.core;

import net.minecraft.server.network.ServerPlayerEntity;

import javax.swing.*;

public class ProtectionHelper {

    public void ReloadProtectionAfterBuyingFor(Territory target, WOCTeam source, FormattableTime timeToApply) {
        if (timeToApply != null) {
            if (timeToApply.SecondsTotal() > 0) {
                Timer timer = new Timer(1000, arg0 -> ActualBuyFor(target, source));
                timer.setRepeats(false); // Only execute once
                timer.start(); // Go go go
                return;
            }
        }
        ActualBuyFor(target, source);
    }

    private void ActualBuyFor(Territory target, WOCTeam source) {
        target.Buy(source);
    }


    public void ReloadProtectionAfterCapturingFor(Territory target, WOCTeam source, FormattableTime timeToApply) {
        if (timeToApply != null) {
            if (timeToApply.SecondsTotal() > 0) {
                Timer timer = new Timer(1000, arg0 -> ActualCaptureFor(target, source));
                timer.setRepeats(false); // Only execute once
                timer.start(); // Go go go
                return;
            }
        }
        ActualCaptureFor(target, source);
    }

    private void ActualCaptureFor(Territory target, WOCTeam source) {
        target.CaptureBy(source);
    }

    public void ReloadProtectionAfterCapturingBaseFor(Territory target, WOCTeam source, FormattableTime timeToApply) {
        if (timeToApply != null) {
            if (timeToApply.SecondsTotal() > 0) {
                Timer timer = new Timer(1000, arg0 -> ActualCaptureBaseFor(target, source));
                timer.setRepeats(false); // Only execute once
                timer.start(); // Go go go
                return;
            }
        }
        ActualCaptureBaseFor(target, source);
    }

    private void ActualCaptureBaseFor(Territory target, WOCTeam source) {
        target.CaptureBaseBy(source);
    }

    public void SetPlayerAdventureLike(ServerPlayerEntity player) {
        player.getAbilities().allowModifyWorld = false;
        player.getAbilities().invulnerable = true;
    }

    public void UnsetPlayerAdventureLike(ServerPlayerEntity player) {
        player.getAbilities().allowModifyWorld = true;
        player.getAbilities().invulnerable = false;
    }
}
