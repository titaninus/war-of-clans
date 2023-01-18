package titaninus.warofclans.core;

import java.time.LocalDateTime;
import java.util.regex.Pattern;

// 1d24h35m20
public class FormattableTime {
    public static final String formatRegex = "^((?<day>\\d+)d)?((?<hour>\\d+)h)?((?<minute>\\d+)m)?((?<sec>\\d+)s?)?$";
    public transient Pattern formatPattern = Pattern.compile(formatRegex, Pattern.CASE_INSENSITIVE);
    public int Days;
    public int Hours;
    public int Minutes;
    public int Seconds;

    public FormattableTime() {

    }

    public FormattableTime(FormattableTime first) {
        Days = first.Days;
        Hours = first.Hours;
        Minutes = first.Minutes;
        Seconds = first.Seconds;
        Reformat();
    }

    public static FormattableTime FromTotalSeconds(int secondsRemaining) {
        var time = new FormattableTime();
        time.Seconds = secondsRemaining;
        time.Reformat();
        return time;
    }

    public int SecondsTotal() { return Days * 24 * 60 * 60 + Hours * 60 * 60 + Minutes * 60 + Seconds; };

    public static boolean IsParseable(String text) {
        return Pattern.matches(formatRegex, text);
    }

    public static FormattableTime FromTime(LocalDateTime source) {
        var result = new FormattableTime();
        result.Hours = source.getHour();
        result.Minutes = source.getMinute();
        result.Seconds = source.getSecond();
        result.Days = source.getDayOfYear();
        result.Reformat();
        return result;
    }

    public void Add(FormattableTime other) {
        Days += other.Days;
        Hours += other.Hours;
        Minutes += other.Minutes;
        Seconds += other.Seconds;
        Reformat();
    }

    public static FormattableTime Add(FormattableTime first, FormattableTime second) {
        var result = new FormattableTime(first);
        result.Add(second);
        return result;
    }

    public void Sub(FormattableTime other) {
        if (SecondsTotal() < other.SecondsTotal()) {
            throw new IllegalArgumentException("second time is greater than this");
        }
        Days = 0;
        Hours = 0;
        Minutes = 0;
        Seconds = SecondsTotal() - other.SecondsTotal();
        Reformat();
    }

    public static FormattableTime Sub(FormattableTime first, FormattableTime second) {
        var result = new FormattableTime(first);
        result.Sub(second);
        return result;
    }

    public static FormattableTime TryParse(String text) {
        var time = new FormattableTime();
        if (!Pattern.matches(formatRegex, text)) {
            return null;
        }
        var matcher = time.formatPattern.matcher(text);
        if (!matcher.matches()) {
            return null;
        }
        var days = matcher.group("day");
        if (days != null) {
            time.Days = Integer.parseInt(days);
        }
        var hours = matcher.group("hour");
        if (hours != null) {
            time.Hours  = Integer.parseInt(hours);
        }
        var minutes = matcher.group("minute");
        if (minutes != null) {
            time.Minutes  = Integer.parseInt(minutes);
        }
        var seconds = matcher.group("sec");
        if (seconds != null) {
            time.Seconds  = Integer.parseInt(seconds);
        }
        time.Reformat();
        return time;
    }

    private void Reformat() {
        Minutes += Seconds / 60;
        Seconds = Seconds % 60;

        Hours += Minutes / 60;
        Minutes = Minutes % 60;

        Days += Hours / 24;
        Hours = Hours % 24;
    }

    @Override
    public String toString() {
        return String.format("%sd%sh%sm%s", Days, Hours, Minutes, Seconds);
    }
}
