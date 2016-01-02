package me.flash1110.seventx.enums;

import me.flash1110.seventx.SevenTX;
import org.bukkit.ChatColor;

/**
 * Created by sandleraj on 1/1/16.
 */
public enum Colors {

    WHITE(ChatColor.WHITE, SevenTX.INSTANCE.getConfig().getInt("white.points")),
    LIGHT_GREEN(ChatColor.GREEN, SevenTX.INSTANCE.getConfig().getInt("light_green.points"));

    private ChatColor color;
    private int points;

    Colors(ChatColor color, int points) {
        this.color = color;
        this.points = points;
    }

    public int getPoints() {
        return points;
    }

    public ChatColor getColor() {
        return color;
    }

    public static Colors getColorFromInt(int point) {
        for (Colors color : values()) {
            if (color.points == point)
                return color;
        }
        return null;
    }

    public static Colors getColorFromChat(ChatColor color) {
        for (Colors c : values()) {
            if (c.color == color)
                return c;
        }
        return null;
    }

    public static Colors next(Colors color) {
        if (color.equals(Colors.WHITE)) {
            return LIGHT_GREEN;
        }

        return WHITE;
    }
}
