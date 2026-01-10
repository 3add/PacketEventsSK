package threeadd.packetEventsSK.element.team.api;

import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.NameTagVisibility;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.OptionData;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.ScoreBoardTeamInfo;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.TeamMode;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.format.NamedTextColor;
import oshi.annotation.concurrent.Immutable;
import threeadd.packetEventsSK.util.UserManager;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;
import java.util.function.Consumer;

import static com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerTeams.CollisionRule;

public class FakeTeam {

    private final List<UUID> viewingPlayers = new ArrayList<>();
    private final String name;
    private final ScoreBoardTeamInfo info;

    // Note that players use player names and entities use their UUID, see https://minecraft.wiki/w/Java_Edition_protocol/Packets#Update_Teams
    private final List<String> entities = new ArrayList<>();

    public FakeTeam(String name) {
        this.name = name;
        this.info = new ScoreBoardTeamInfo(Component.text(name), null, null,
                NameTagVisibility.ALWAYS, CollisionRule.ALWAYS, NamedTextColor.WHITE, OptionData.NONE);
        FakeTeamRegistry.INSTANCE.register(this);
    }

    public String getName() {
        return name;
    }

    @Immutable
    public ScoreBoardTeamInfo getScoreBoardInfo() {
        return info;
    }

    public void addViewers(UUID... viewerIds) {
        viewingPlayers.addAll(List.of(viewerIds));
        for (UUID viewerId : viewerIds) {
            UserManager.getUser(viewerId).sendPacket(toCreatePacket());
        }
    }

    public void removeViewer(UUID... viewerIds) {
        viewingPlayers.removeAll(List.of(viewerIds));
        for (UUID viewerId : viewerIds) {
            UserManager.getUser(viewerId).sendPacket(toDestroyPacket());
        }
    }

    public void clearViewers() {
        sendPacketToViewers(toDestroyPacket());
        viewingPlayers.clear();
    }

    public List<UUID> getViewingPlayers() {
        return Collections.unmodifiableList(viewingPlayers);
    }

    public void consumeScoreBoardInfo(Consumer<ScoreBoardTeamInfo> infoConsumer) {
        infoConsumer.accept(info);
        sendPacketToViewers(toUpdatePacket());
    }

    // The entity id is a player name for a player and a UUID for any other entity
    public void addEntities(String... entityIds) {
        entities.addAll(List.of(entityIds));
        sendPacketToViewers(toEntitiesAddPacket(entityIds));
    }

    public void removeEntity(String... entityIds) {
        entities.removeAll(List.of(entityIds));
        sendPacketToViewers(toEntitiesRemovePacket(entityIds));
    }

    public void clearEntities() {
        sendPacketToViewers(toEntitiesRemovePacket(entities.toArray(new String[0])));
        entities.clear();
    }

    public List<String> getEntities() {
        return Collections.unmodifiableList(entities);
    }

    public void destroy() {
        FakeTeamRegistry.INSTANCE.unRegister(this);
        sendPacketToViewers(toDestroyPacket());
    }

    public WrapperPlayServerTeams toCreatePacket() {
        return new WrapperPlayServerTeams(name, TeamMode.CREATE, info, entities);
    }

    private WrapperPlayServerTeams toUpdatePacket() {
        return new WrapperPlayServerTeams(name, TeamMode.UPDATE, info, entities);
    }

    private WrapperPlayServerTeams toEntitiesAddPacket(String... ids) {
        return new WrapperPlayServerTeams(name, TeamMode.ADD_ENTITIES, info, ids);
    }

    private WrapperPlayServerTeams toEntitiesRemovePacket(String... ids) {
        return new WrapperPlayServerTeams(name, TeamMode.REMOVE_ENTITIES, info, ids);
    }

    private WrapperPlayServerTeams toDestroyPacket() {
        return new WrapperPlayServerTeams(name, TeamMode.REMOVE, info, entities);
    }

    private void sendPacketToViewers(PacketWrapper<?> packet) {
        viewingPlayers.stream()
                .map(UserManager::getUser)
                .forEach(user -> user.sendPacket(packet));
    }
}
