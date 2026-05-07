package dev.threeadd.packeteventssk.element.team.expressions.prop;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.team.FakeTeam;
import org.jspecify.annotations.Nullable;

public class ExprFakeTeamName extends SimplePropertyExpression<FakeTeam, String> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeTeamName.class, String.class, "fake[ ]team name", "faketeam")
                .name("Fake Team - Team Name")
                .description("Represents the name of a fake team")
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
    public @Nullable String convert(FakeTeam team) {
        return team.getName();
    }

    @Override
    protected String getPropertyName() {
        return "fake team name";
    }

    @Override
    public Class<? extends String> getReturnType() {
        return String.class;
    }
}
