package threeadd.packetEventsSK.element.team.expressions.prop;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.event.Event;
import threeadd.packetEventsSK.element.team.api.FakeTeam;
import threeadd.packetEventsSK.util.expressions.CustomPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Team - Team Name")
@Description("Represents the name of a fake team")
@Example("""
        command glowGreen:
            trigger:
                set glowing state of player to true for player
                create new fake team named player's name for players:
                    set the fake team color of the fake team to green
                    add player to fake team entities of the fake team
        """)
@Since("1.0.0")
public class FakeTeamNameProp extends CustomPropertyExpression<FakeTeam, String> {

    static {
        PropertyExpression.register(FakeTeamNameProp.class, String.class, "fake[ ]team[ ]name", "faketeam");
    }

    public FakeTeamNameProp() {
        super(String.class, FakeTeam.class, true);
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected String getOne(Event event) {
        return getValue(0, FakeTeam.class, event).getName();
    }
}
