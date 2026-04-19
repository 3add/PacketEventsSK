package threeadd.packetEventsSK.element.team.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.team.api.FakeTeam;
import threeadd.packetEventsSK.element.team.sections.CreateFakeTeamSec;

@Name("Fake Team - Last Fake Team")
@Description("Represents the last fake team created in the current event")
@Examples({
        """
        command test:
            trigger:
                create new fake team named player's name for player:
                    set the fake team color of the fake team to green
                    add player to fake team entities of the fake team
                
                broadcast "%the last fake team%"
        """})
@Since("1.0.0")
public class ExprLastFakeTeam extends SimpleExpression<FakeTeam> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprLastFakeTeam.class, FakeTeam.class)
                        .supplier(ExprLastFakeTeam::new)
                        .addPatterns("[the] [last] fake team")
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern,
                        Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected FakeTeam @Nullable [] get(@NotNull Event event) {
        FakeTeam team = CreateFakeTeamSec.getLastTeam(event);
        if (team == null) return null;
        return new FakeTeam[]{team};
    }

    @Override
    public boolean isSingle() { return true; }

    @Override
    public @NotNull Class<? extends FakeTeam> getReturnType() { return FakeTeam.class; }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) { return "the last fake team"; }
}