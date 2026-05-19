package dev.threeadd.packeteventssk.update;

import ch.njol.skript.Skript;
import ch.njol.skript.util.Version;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import dev.threeadd.packeteventssk.PacketEventsSK;
import dev.threeadd.packeteventssk.api.util.LogUtil;
import dev.threeadd.packeteventssk.config.Config;
import dev.threeadd.packeteventssk.config.Configurable;
import org.bukkit.Bukkit;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

// Highly inspired by SkBee's implementation https://github.com/ShaneBeee/SkBee/tree/master/src/main/java/com/shanebeestudios/skbee/api/util/update
public class UpdateChecker {

    private static final Version SERVER_VERSION = Skript.getMinecraftVersion();
    private static Version PLUGIN_VERSION;
    private static ModrinthVersion CURRENT_UPDATE_VERSION;

    public static void enable() {
        PLUGIN_VERSION = new Version(PacketEventsSK.getInstance().getPluginMeta().getVersion());

        Config config = PacketEventsSK.getInstance().getPluginConfig();
        if (config.getConfigValue(Configurable.UPDATE_CHECKER_ENABLED)) {
            Bukkit.getPluginManager().registerEvents(new JoinUpdateListener(), PacketEventsSK.getInstance());
            checkUpdate(config.getConfigValue(Configurable.UPDATE_CHECKER_ASYNC));
        } else {
            LogUtil.warning("Update checker is disabled");
        }
    }

    private static void checkUpdate(boolean async) {
        LogUtil.info("Checking for update...");
        getUpdateVersion(async).thenApply(modrinthVersion -> {
            LogUtil.warning("Plugin is not up to date!");
            LogUtil.mini(" - Current version: <red>v%s", PLUGIN_VERSION);
            LogUtil.mini(" - Available update: <green>v%s", modrinthVersion.getUpdateVersion());
            if (modrinthVersion.isServerSupported(SERVER_VERSION)) {
                LogUtil.mini(" - Download at: <blue>" + modrinthVersion.getUpdateLink());
            } else {
                LogUtil.mini(" - <red>Your server version (<yellow>%s<red>) does not support this update.", SERVER_VERSION);
                LogUtil.info(" - Supported Versions:");
                for (Version supportedVersion : modrinthVersion.getSupportedVersions()) {
                    LogUtil.info("   - " + supportedVersion.toString());
                }
            }
            return true;
        }).exceptionally(ignored -> {
            LogUtil.mini("<green>Plugin is up to date!");
            return true;
        });
    }

    protected static CompletableFuture<ModrinthVersion> getUpdateVersion(boolean async) {
        CompletableFuture<ModrinthVersion> updateVersionFuture = new CompletableFuture<>();
        if (CURRENT_UPDATE_VERSION != null) {
            updateVersionFuture.complete(CURRENT_UPDATE_VERSION);
        } else {
            CompletableFuture<ModrinthVersion> latestReleaseFuture = new CompletableFuture<>();
            if (async) {
                Bukkit.getScheduler().runTaskAsynchronously(PacketEventsSK.getInstance(), () -> {
                    ModrinthVersion latest = getLatestVersionFromModrinth();
                    if (latest == null) {
                        latestReleaseFuture.cancel(true);
                    } else {
                        latestReleaseFuture.complete(latest);
                    }
                });
            } else {
                ModrinthVersion latest = getLatestVersionFromModrinth();
                if (latest == null) {
                    latestReleaseFuture.cancel(true);
                } else {
                latestReleaseFuture.complete(latest);
                }
            }
            latestReleaseFuture.thenApply(version -> {
                if (version.getUpdateVersion().compareTo(PLUGIN_VERSION) <= 0) {
                    updateVersionFuture.cancel(true);
                } else {
                    CURRENT_UPDATE_VERSION = version;
                    updateVersionFuture.complete(CURRENT_UPDATE_VERSION);
                }
                return true;
            });
        }
        return updateVersionFuture;
    }

    private static ModrinthVersion getLatestVersionFromModrinth() {
        try {
            URL url = new URI("https://api.modrinth.com/v2/project/CmkvxP9q/version").toURL();
            BufferedReader reader = new BufferedReader(new InputStreamReader(url.openStream()));
            JsonArray elements = new Gson().fromJson(reader, JsonArray.class);
            JsonElement latestVersion = elements.get(0);
            return new ModrinthVersion(latestVersion);
        } catch (IOException | URISyntaxException e) {
            LogUtil.error("Checking for updates failed");
        }
        return null;
    }
}
