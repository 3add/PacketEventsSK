package dev.threeadd.packeteventssk.element.team.expressions;

import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.team.FakeTeam;
import dev.threeadd.packeteventssk.api.team.FakeTeamRegistry;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class ExprFakeTeamFromName extends SimpleExpression<FakeTeam> {

    public static void register(Registration reg) {
        reg.newSimpleExpression(ExprFakeTeamFromName.class, FakeTeam.class, "fake[ ]team (from|named|with name) %string%")
                .name("Fake Team - From Name")
                .description("Retrieve a fake team from it's name")
                .examples("""
                        command deleteNow <text>:
                            trigger:
                                if fake team named arg-1 isn't set:
                                    send "That team doesn't even exist silly!"
                                    stop
                        
                                delete fake team named arg-1
                                send "Deleted the %arg-1% team"
                        """)
                .since("1.0.0")
                .register();
    }

    private Expression<String> nameExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.nameExpr = (Expression<String>) expressions[0];
        return true;
    }

    @Override
    protected FakeTeam @Nullable [] get(Event event) {
        String name = this.nameExpr.getSingle(event);
        if (name == null) return null;

        FakeTeam team = FakeTeamRegistry.INSTANCE.getByName(name);
        if (team == null) return null;

        return new FakeTeam[]{team};
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @Override
    public Class<? extends FakeTeam> getReturnType() {
        return FakeTeam.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String teamName = nameExpr.toString(event, debug);
        return String.format("fake team with name %s", teamName);
    }
}
