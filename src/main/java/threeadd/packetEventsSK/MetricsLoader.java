package threeadd.packetEventsSK;

import ch.njol.skript.Skript;
import ch.njol.skript.util.Version;
import com.google.common.collect.HashBasedTable;
import com.google.common.collect.Table;
import org.bstats.bukkit.Metrics;
import org.bstats.charts.DrilldownPie;
import org.bstats.charts.SimplePie;

public class MetricsLoader {

    // Heavily inspired by SkBee's implmenentation of metrics
    // https://github.com/ShaneBeee/SkBee/blob/master/src/main/java/com/shanebeestudios/skbee/SkBee.java#L97
    // as of 10/01/2026
    public static void loadMetrics(PacketEventsSK plugin) { // plugin id: 28798
        Metrics metrics = new Metrics(plugin, 28798);
        metrics.addCustomChart(new SimplePie("skript_version", () -> Skript.getVersion().toString()));

        // New Metrics
        // Many of these are copied from Skript -> SkriptMetrics.class
        metrics.addCustomChart(new DrilldownPie("plugin_version_drilldown_pie", () -> {
            Version version = new Version(plugin.getPluginMeta().getVersion());
            Table<String, String, Integer> table = HashBasedTable.create(1,1);
            table.put(
                    version.getMajor() + "." + version.getMinor(), // upper label
                    version.toString(), // lower label
                    1 // weight
            );
            return table.rowMap();
        }));
        metrics.addCustomChart(new DrilldownPie("skript_version_drilldown_pie", () -> {
            Version version = Skript.getVersion();
            Table<String, String, Integer> table = HashBasedTable.create(1,1);
            table.put(
                    version.getMajor() + "." + version.getMinor(), // upper label
                    version.toString(), // lower label
                    1 // weight
            );
            return table.rowMap();
        }));
        metrics.addCustomChart(new DrilldownPie("minecraft_version_drilldown_pie", () -> {
            Version version = Skript.getMinecraftVersion();
            Table<String, String, Integer> table = HashBasedTable.create(1,1);

            if (version.getMajor() == 1) {
                // Minecraft 1.x.x versioning
                table.put(
                        version.getMajor() + "." + version.getMinor(), // upper label
                        version.toString(), // lower label
                        1 // weight
                );
            } else {
                // Minecraft (year).x.x versioning
                table.put(
                        "" + version.getMajor(), // upper label
                        version.toString(), // lower label
                        1 // weight
                );
            }
            return table.rowMap();
        }));
    }
}
