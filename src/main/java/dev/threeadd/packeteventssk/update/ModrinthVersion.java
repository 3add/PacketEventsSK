package dev.threeadd.packeteventssk.update;

import ch.njol.skript.util.Version;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.ArrayList;
import java.util.List;

// Highly inspired by SkBee's implementation https://github.com/ShaneBeee/SkBee/tree/master/src/main/java/com/shanebeestudios/skbee/api/util/update
public class ModrinthVersion {

    private final Version updateVersion;
    private final List<Version> supportedVersions = new ArrayList<>();

    public ModrinthVersion(JsonElement jsonElement) {
        JsonObject json = jsonElement.getAsJsonObject();
        this.updateVersion = new Version(json.get("version_number").getAsString());
        JsonArray gameVersions = json.getAsJsonArray("game_versions");
        gameVersions.forEach(version -> this.supportedVersions.add(new Version(version.getAsString())));
    }

    public Version getUpdateVersion() {
        return this.updateVersion;
    }

    public String getUpdateLink() {
        return "https://modrinth.com/plugin/packeteventssk/version/" + this.updateVersion;
    }

    public List<Version> getSupportedVersions() {
        return this.supportedVersions;
    }

    public boolean isServerSupported(Version serverVersion) {
        return this.supportedVersions.contains(serverVersion);
    }
}