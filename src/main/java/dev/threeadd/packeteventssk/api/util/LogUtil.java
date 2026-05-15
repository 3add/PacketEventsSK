package dev.threeadd.packeteventssk.api.util;

import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.TextColor;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;

public class LogUtil {

    private static final MiniMessage MM = MiniMessage.miniMessage();

    private static final Component PREFIX = MM.deserialize("<color:#66BBFF>[</color><color:#9BD3FF>PacketEventsSK</color><color:#66BBFF>]</color>");
    private static final TextColor ERROR_COLOR = TextColor.color(0xFF5555);
    private static final TextColor WARN_COLOR = TextColor.color(0xFFCC00);

    public static void info(String format, Object... objects) {
        String log = String.format(format, objects);
        Bukkit.getConsoleSender().sendMessage(PREFIX.appendSpace().append(Component.text(log)));
    }

    public static void warning(String format, Object... objects) {
        String log = String.format(format, objects);
        Bukkit.getConsoleSender().sendMessage(PREFIX.appendSpace().append(Component.text(log).color(WARN_COLOR)));
    }

    public static void error(String format, Object... objects) {
        String log = String.format(format, objects);
        Bukkit.getConsoleSender().sendMessage(PREFIX.appendSpace().append(Component.text(log).color(ERROR_COLOR)));
    }

    public static void mini(String format, Object... objects) {
        String log = String.format(format, objects);
        Bukkit.getConsoleSender().sendMessage(PREFIX.appendSpace().append(MM.deserialize(log)));
    }
}
