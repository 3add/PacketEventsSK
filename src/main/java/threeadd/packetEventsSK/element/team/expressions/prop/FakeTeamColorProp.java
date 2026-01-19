package threeadd.packetEventsSK.element.team.expressions.prop;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import ch.njol.util.coll.CollectionUtils;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.team.api.FakeTeam;
import threeadd.packetEventsSK.util.expressions.CustomPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Team - Team Color")
@Description("Represents the color of a fake team")
@Example("""
        command glowGreen:
            trigger:
                set glowing state of player to true for player
                create new fake team named player's name for players:
                    set the fake team color of the fake team to green
                    add player to fake team entities of the fake team
        """)
@Since("1.0.0")
public class FakeTeamColorProp extends CustomPropertyExpression<FakeTeam, Color> {

    static {
        PropertyExpression.register(FakeTeamColorProp.class, Color.class, "fake[ ]team[ ]color", "faketeam");
    }

    public FakeTeamColorProp() {
        super(Color.class, FakeTeam.class, true);
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected Color getOne(Event event) {
        FakeTeam team = getValueOrNull(0, FakeTeam.class, event);
        if (team == null) return null;
        return ColorRGB.fromBukkitColor(org.bukkit.Color.fromARGB(team.getScoreBoardInfo().getColor().value()));
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode.equals(Changer.ChangeMode.SET))
            return CollectionUtils.array(Color[].class);

        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {

        Color newColor = getDeltaValue(delta, Color.class);
        FakeTeam team = getValue(0, FakeTeam.class, event);

        NamedTextColor newTextColor = NamedTextColor.nearestTo(TextColor.color(newColor.asARGB()));
        team.consumeScoreBoardInfo(info -> info.setColor(newTextColor));
    }
}
