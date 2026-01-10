package threeadd.packetEventsSK.element.team.sections;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.Expression;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import threeadd.packetEventsSK.element.team.api.FakeTeam;
import threeadd.packetEventsSK.util.section.ReturningSection;

@SuppressWarnings("unused")
@Name("Fake Team - Create Fake Team")
@Description("Used to create a fake team with a name")
@Example("""
        command glowGreen:
            trigger:
                set glowing state of player to true for player
                create new fake team named player's name for players:
                    set the fake team color of the fake team to green
                    add player to fake team entities of the fake team
        """)
public class SecCreateFakeTeam extends ReturningSection<FakeTeam> {

    private static final Logger log = LoggerFactory.getLogger(SecCreateFakeTeam.class);

    public static class FakeTeamBuilder extends LastBuilderExpression<FakeTeam, SecCreateFakeTeam> {}

    static {
        register(
                SecCreateFakeTeam.class,
                FakeTeam.class,
                FakeTeamBuilder.class,
                new String[]{
                        "[the] fake[ ]team [builder]"
                },
                "(make|create) [a] [new] fake[ ]team (with name|named) %string% [for %-players%]"
        );
    }

    private Expression<String> nameExpr;
    private Expression<Player> playerExpr;

    @SuppressWarnings("unchecked")
    @Override
    protected boolean initialize() {
        this.nameExpr = (Expression<String>) exprs[0];

        if (exprs[1] != null)
            this.playerExpr = (Expression<Player>) exprs[1];

        return true;
    }

    @Override
    public FakeTeam createNewValue(@NotNull Event event) {
        String name = nameExpr.getSingle(event);
        FakeTeam team = new FakeTeam(name);

        if (playerExpr != null) {
            Player[] players = playerExpr.getArray(event);
            for (Player player : players)
                team.addViewers(player.getUniqueId());
        }

        return team;
    }

    @Override
    public String toString(Event e, boolean debug) { return "create fake team"; }
}