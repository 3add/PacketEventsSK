package dev.threeadd.packeteventssk.api.team;

import org.jetbrains.annotations.Nullable;
import dev.threeadd.packeteventssk.util.registry.Registry;

public class FakeTeamRegistry extends Registry<FakeTeam> {
    public final static FakeTeamRegistry INSTANCE = new FakeTeamRegistry();
    private FakeTeamRegistry() {}

    public @Nullable FakeTeam getByName(String name) {
        return getRegisteredItems().stream()
                .filter(team -> team.getName().equalsIgnoreCase(name))
                .findAny().orElse(null);
    }
}
