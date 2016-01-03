package me.flash1110.seventx.enums;

import org.bukkit.ChatColor;

/**
 * Created by sandleraj on 1/1/16.
 */
public enum Colors {

    GRAY(ChatColor.WHITE, 0),
    DARK_GRAY(ChatColor.GREEN, 250),
    WHITE(ChatColor.DARK_GREEN, 500),
    YELLOW(ChatColor.YELLOW, 1000),
    LIGHT_PURPLE(ChatColor.LIGHT_PURPLE, 1500),
    GREEN(ChatColor.GREEN, 2750),
    AQUA(ChatColor.AQUA, 3000),
    BLUE(ChatColor.BLUE, 5000),
    DARK_BLUE(ChatColor.DARK_BLUE, 7500),
    DARK_GREEN(ChatColor.DARK_GREEN, 10000),
    DARK_PURPLE(ChatColor.DARK_PURPLE, 12500),
    DARK_AQUA(ChatColor.DARK_AQUA, 15000),
    GOLD(ChatColor.GOLD, 20000),
    DARK_RED(ChatColor.DARK_RED, 35000),
    RED(ChatColor.RED, 50000);

    /*
    Gray
Dark Gray
White
Yellow
Light purple
Green
Aqua
Blue
Dark blue
Dark green
Dark purple
Dark Aqua
Gold
Dark red
Red
     */

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
            if (color.points <= point)
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

    public static Colors getColor(int points) {
        if (points >= 50000) {
            return RED;
        } else if (points >= 35000) {
            return DARK_RED;
        } else if (points >= 20000) {
            return GOLD;
        } else if (points >= 15000) {
            return DARK_AQUA;
        } else if (points >= 12500) {
            return DARK_PURPLE;
        } else if (points >= 10000) {
            return DARK_GREEN;
        } else if (points >= 7500) {
            return DARK_BLUE;
        } else if (points >= 5000) {
            return BLUE;
        } else if (points >= 3000) {
            return AQUA;
        } else if (points >= 2750) {
            return GREEN;
        } else if (points >= 1500) {
            return LIGHT_PURPLE;
        } else if (points >= 1000) {
            return YELLOW;
        } else if (points >= 500) {
            return WHITE;
        } else if (points >= 250) {
            return DARK_GRAY;
        } else {
            return GRAY;
        }
    }
}
