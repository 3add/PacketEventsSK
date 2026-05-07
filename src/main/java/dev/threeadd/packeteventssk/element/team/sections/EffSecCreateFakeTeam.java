package dev.threeadd.packeteventssk.element.team.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.team.FakeTeam;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EffSecCreateFakeTeam extends EffectSection {

    public static void register(Registration reg) {
        reg.newSection(EffSecCreateFakeTeam.class, "(make|create) [a] [new] fake[ ]team (with name|named) %string% [for %-players%] [and store (it|the result) in %-objects%]")
                .name("Fake Team - Create Fake Team")
                .description("""
                       Create a new fake team with a name
                       This creates its own internal event, which means previous event-values will not work.
                       """)
                .examples("""
                        command glowGreen:
                            trigger:
                                create new fake team named player's name for player and store it in {_team}:
                                    set {_team}'s fake team color to green
                                    add player to {_team}'s fake team entities
                        """)
                .since("1.0.0")
                .register();

        reg.newEventValue(CreateFakeTeamEvent.class, FakeTeam.class)
                .converter(CreateFakeTeamEvent::getFakeTeam)
                .register();
    }

    private Expression<String> nameExpr;
    private @Nullable Expression<Player> playerExpr;
    private @Nullable Expression<Object> storeExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions,
                        int matchedPattern,
                        Kleenean isDelayed,
                        SkriptParser.ParseResult parseResult,
                        @Nullable SectionNode sectionNode,
                        @Nullable List<TriggerItem> triggerItems) {

        this.nameExpr = (Expression<String>) expressions[0];

        if (expressions[1] != null) {
            this.playerExpr = (Expression<Player>) expressions[1];
        }

        if (expressions[2] != null) {
            this.storeExpr = (Expression<Object>) expressions[2];
            if (!Changer.ChangerUtils.acceptsChange(this.storeExpr, Changer.ChangeMode.SET, FakeTeam.class)) {
                Skript.error(this.storeExpr.toString(null, Skript.debug()) + " cannot be set to store a fake team");
                return false;
            }
        }

        if (sectionNode != null) {
            loadOptionalCode(sectionNode);
        }
        return true;
    }

    @Override
    protected TriggerItem walk(@NotNull Event event) {
        FakeTeam team = createTeam(event);
        if (team == null) {
            return getNext();
        }

        if (this.storeExpr != null) {
            this.storeExpr.change(event, new Object[]{team}, Changer.ChangeMode.SET);
        }

        return walk(event, true);
    }

    private @Nullable FakeTeam createTeam(@NotNull Event event) {
        String name = this.nameExpr.getSingle(event);
        if (name == null) return null;

        FakeTeam team = new FakeTeam(name);

        if (this.playerExpr != null) {
            Player[] players = this.playerExpr.getAll(event);
            if (players != null) {
                for (Player player : players) {
                    team.addViewers(player.getUniqueId());
                }
            }
        }

        return team;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String name = nameExpr.toString(event, debug);
        String playersPart = playerExpr != null ? " for " + playerExpr.toString(event, debug) : "";
        String storePart = storeExpr != null ? " and store it in " + storeExpr.toString(event, debug) : "";
        return String.format("create fake team with name %s%s%s", name, playersPart, storePart);
    }

    public static class CreateFakeTeamEvent extends Event {
        private final FakeTeam fakeTeam;

        public CreateFakeTeamEvent(FakeTeam fakeTeam) {
            this.fakeTeam = fakeTeam;
        }

        public FakeTeam getFakeTeam() {
            return fakeTeam;
        }

        @Override
        public @NotNull HandlerList getHandlers() {
            throw new IllegalStateException();
        }
    }
}