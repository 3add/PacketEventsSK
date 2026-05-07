package dev.threeadd.packeteventssk.element.team.effects;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.team.FakeTeam;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffDeleteTeam extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffDeleteTeam.class, "(delete|destroy|remove) fake[ ]team[s] %faketeams%")
                .name("Fake Team - Delete Fake Team")
                .description("Used to delete a fake team that's viewable by at least 1 player.")
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

    private Expression<FakeTeam> teamExpr;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.teamExpr = (Expression<FakeTeam>) expressions[0];
        return true;
    }

    @Override
    protected void execute(Event event) {
        FakeTeam[] fakeTeams = teamExpr.getAll(event);
        if (fakeTeams == null) return;

        for (FakeTeam fakeTeam : fakeTeams) {
            fakeTeam.destroy();
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String teams = teamExpr.toString(event, debug);
        return String.format("delete fake teams %s", teams);
    }
}
