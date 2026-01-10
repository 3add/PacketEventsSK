package threeadd.packetEventsSK.element.team.expressions;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.lang.ExpressionType;
import ch.njol.skript.lang.SkriptParser;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.team.api.FakeTeam;
import threeadd.packetEventsSK.element.team.api.FakeTeamRegistry;
import threeadd.packetEventsSK.element.team.expressions.prop.FakeTeamName;
import threeadd.packetEventsSK.util.expressions.CustomExpression;

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
public class FakeTeamWithName extends CustomExpression<FakeTeam> {

    static {
        Skript.registerExpression(FakeTeamWithName.class, FakeTeam.class, ExpressionType.SIMPLE,
                "fake[ ]team (named|with name) %string%");
    }

    @SuppressWarnings("unused")
    public FakeTeamWithName() {
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
