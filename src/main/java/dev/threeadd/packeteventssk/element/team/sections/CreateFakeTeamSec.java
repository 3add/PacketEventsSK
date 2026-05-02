package dev.threeadd.packeteventssk.element.team.sections;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.api.team.FakeTeam;

import java.util.List;
import java.util.WeakHashMap;

@SuppressWarnings("unused")
@Name("Fake Team - Create Fake Team")
@Description("Used to create a fake team with a name")
@Examples("""
        command glowGreen:
            trigger:
                create new fake team named player's name for player and store it in {_team}:
                    set {_team}'s fake team color to green
                    add player to {_team}'s fake team entities
        """)
@Since("1.0.0")
public class CreateFakeTeamSec extends EffectSection {

    private static final WeakHashMap<Event, FakeTeam> lastTeams = new WeakHashMap<>();

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.SECTION,
                SyntaxInfo.builder(CreateFakeTeamSec.class)
                        .supplier(CreateFakeTeamSec::new)
                        .addPatterns("(make|create) [a] [new] fake[ ]team (with name|named) %string% [for %-players%] [and store (it|the result) in %-objects%]:")
                        .build()
        );
    }

    public static FakeTeam getLastTeam(Event event) {
        return lastTeams.get(event);
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
            if (!Changer.ChangerUtils.acceptsChange(storeExpr, Changer.ChangeMode.SET, FakeTeam.class)) {
                Skript.error(storeExpr.toString(null, Skript.debug()) + " cannot be set to store a fake team");
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

        lastTeams.put(event, team);

        if (storeExpr != null) {
            storeExpr.change(event, new Object[]{team}, Changer.ChangeMode.SET);
        }

        return walk(event, true);
    }

    private @Nullable FakeTeam createTeam(@NotNull Event event) {
        String name = nameExpr.getSingle(event);
        if (name == null) return null;

        FakeTeam team = new FakeTeam(name);

        if (playerExpr != null) {
            Player[] players = playerExpr.getArray(event);
            for (Player player : players) {
                team.addViewers(player.getUniqueId());
            }
        }

        return team;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "create fake team";
    }
}