package titaninus.warofclans.core;

import net.minecraft.util.DyeColor;
import net.minecraft.util.Formatting;

public enum TeamColor {
    Red,
    Blue,
    Yellow,
    Green;

    public Formatting Convert() {
        switch (this) {
            case Red -> {
                return Formatting.RED;
            }
            case Blue -> {
                return Formatting.BLUE;
            }
            case Yellow -> {
                return Formatting.YELLOW;
            }
            case Green -> {
                return Formatting.GREEN;
            }
        }
        return Formatting.GRAY;
    }

    public String getColorCode() {
        switch (this) {
            case Red -> {
                return "§c";
            }
            case Blue -> {
                return "§9";
            }
            case Yellow -> {
                return "§e";
            }
            case Green -> {
                return "§a";
            }
        }
        return "§0";
    }

    public DyeColor toDyeColor() {
        switch (this) {
            case Red -> {
                return DyeColor.RED;
            }
            case Blue -> {
                return DyeColor.BLUE;
            }
            case Yellow -> {
                return DyeColor.YELLOW;
            }
            case Green -> {
                return DyeColor.GREEN;
            }
        }
        return null;
    }
}
