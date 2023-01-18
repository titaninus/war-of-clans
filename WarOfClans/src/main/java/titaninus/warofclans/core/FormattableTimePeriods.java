package titaninus.warofclans.core;

import java.time.Period;
import java.util.ArrayList;

public class FormattableTimePeriods {
    public ArrayList<FormattableTime> Periods = new ArrayList<>();

    public static FormattableTimePeriods TryParse(String text) {
        var result = new FormattableTimePeriods();
        var tokens = text.split("\\|");
        for (var t : tokens) {
            if (FormattableTime.IsParseable(t)) {
                var time = FormattableTime.TryParse(t);
                result.Periods.add(time);
            } else {
                return null;
            }
        }
        return result;
    }

    public void Shift(FormattableTime shiftAmount) {
        Shift(shiftAmount, false);
    }

    public void Shift(FormattableTime shiftAmount, boolean inverse) {
        if (Periods.size() < 2) {
            throw new IllegalArgumentException("Cannot shift single period");
        }
        if (inverse) {
            try {
                Periods.get(0).Add(shiftAmount);
                Periods.get(Periods.size() - 1).Sub(shiftAmount);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Shift is greater than last period");
            }
        } else {
            try {
                Periods.get(0).Sub(shiftAmount);
                Periods.get(Periods.size() - 1).Add(shiftAmount);
            } catch (IllegalArgumentException e) {
                throw new IllegalArgumentException("Shift is greater than first period");
            }
        }
    }
}
