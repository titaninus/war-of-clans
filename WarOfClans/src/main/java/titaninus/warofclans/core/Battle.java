package titaninus.warofclans.core;

import titaninus.warofclans.core.interfaces.LifecycleEventable;
import titaninus.warofclans.gamelogic.GameMaster;

import java.time.LocalDateTime;

public class Battle implements LifecycleEventable {
    public LocalDateTime StartTime;
    public FormattableTime TimeBeforeFinish;

    private int _secondsRemaining;

    private boolean _isActive;
    private boolean _inPause;

    public Battle(LocalDateTime now, FormattableTime until) {
        StartTime = now;
        TimeBeforeFinish = until;
        _secondsRemaining = until.SecondsTotal();
        _isActive = true;
    }

    public FormattableTime GetTimeBeforeEnds() {
        if (_isActive) {
            return FormattableTime.FromTotalSeconds(_secondsRemaining);
        }
        return new FormattableTime();
    }

    @Override
    public void Update() {
        if (!_isActive || _inPause) {
            return;
        }

        _secondsRemaining -= 1;
        if (_secondsRemaining == 0) {
            End();
        }
    }

    @Override
    public void End() {
        _isActive = false;
        GameMaster.Instance().InternalStopBattle(this);
    }

    @Override
    public void Pause() {
        _inPause = true;
    }

    @Override
    public void Continue() {
        _inPause = false;
    }

    @Override
    public void Start() {
        _isActive = true;
    }
}
