package dev.threeadd.packeteventssk.element.team.expressions.prop;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.SimplePropertyExpression;
import ch.njol.skript.util.Color;
import ch.njol.skript.util.ColorRGB;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.team.FakeTeam;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextColor;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprFakeTeamColor extends SimplePropertyExpression<FakeTeam, Color> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeTeamColor.class, Color.class, "fake[ ]team colo[u]r", "faketeam")
                .name("Fake Team - Team Color")
                .description("Represents the color of a fake team")
                .examples("""
                        command glowGreen:
                            trigger:
                                set glowing state of player to true for player
                                create new fake team named player's name for players:
                                    set the fake team color of the fake team to green
                                    add player to fake team entities of the fake team
                        """)
                .since("1.0.0")
                .register();
    }

    @Override
    public @Nullable Color convert(FakeTeam team) {
        return ColorRGB.fromBukkitColor(org.bukkit.Color.fromARGB(team.getScoreBoardInfo().getColor().value()));
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Color.class);
        }
        return null;
    }

    @Override
    public void change(Event event, Object @Nullable [] delta, Changer.ChangeMode mode) {
        FakeTeam[] teams = getExpr().getAll(event);

        if (delta == null || delta.length != 1 || !(delta[0] instanceof Color newColor)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        for (FakeTeam team : teams) {
            team.getScoreBoardInfo().setColor(NamedTextColor.nearestTo(TextColor.color(newColor.asARGB())));
        }
    }

    @Override
    public Class<? extends Color> getReturnType() {
        return Color.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake team color";
    }
}
