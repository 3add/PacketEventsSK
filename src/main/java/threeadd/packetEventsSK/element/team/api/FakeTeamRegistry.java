package threeadd.packetEventsSK.element.team.api;

import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.util.registry.Registry;

public class FakeTeamRegistry extends Registry<FakeTeam> {
    public final static FakeTeamRegistry INSTANCE = new FakeTeamRegistry();
    private FakeTeamRegistry() {}

    public @Nullable FakeTeam getByName(String name) {
        return getRegisteredItems().stream()
                .filter(team -> team.getName().equalsIgnoreCase(name))
                .findAny().orElse(null);
    }
}
