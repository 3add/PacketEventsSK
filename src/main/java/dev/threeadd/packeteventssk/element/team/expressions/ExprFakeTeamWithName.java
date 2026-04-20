package dev.threeadd.packeteventssk.element.team.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.element.team.api.FakeTeam;
import dev.threeadd.packeteventssk.element.team.api.FakeTeamRegistry;
import dev.threeadd.packeteventssk.util.expressions.CustomExpression;

@SuppressWarnings("unused")
@Name("Fake Team - From Name")
@Description("Retrieve a fake team from it's name")
@Example("""
        command deleteNow <text>:
            trigger:
                if fake team named arg-1 isn't set:
                    send "That team doesn't even exist silly!"
                    stop
        
                delete fake team named arg-1
                send "Deleted the %arg-1% team"
        """)
@Since("1.0.0")
public class ExprFakeTeamWithName extends CustomExpression<FakeTeam> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                DefaultSyntaxInfos.Expression.builder(ExprFakeTeamWithName.class, FakeTeam.class)
                        .addPatterns("fake[ ]team (named|with name) %string%")
                        .build()
        );

    }

    @SuppressWarnings("unused")
    public ExprFakeTeamWithName() {
        super(FakeTeam.class, true);
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable FakeTeam getOne(Event currentEvent) {
        String name = getValueOrNull(0, String.class, currentEvent);
        if (name == null) return null;

        return FakeTeamRegistry.INSTANCE.getByName(name);
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "fake team from name";
    }
}
