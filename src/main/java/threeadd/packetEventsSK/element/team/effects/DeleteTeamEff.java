package threeadd.packetEventsSK.element.team.effects;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.SkriptParser;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.team.api.FakeTeam;
import threeadd.packetEventsSK.util.effect.CustomEffect;

@SuppressWarnings("unused")
@Name("Fake Team Team - Delete Fake Team")
@Description("Used to delete a fake team that's viewable by at least 1 player.")
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
public class DeleteTeamEff extends CustomEffect {

    static {
        Skript.registerEffect(DeleteTeamEff.class,
                "(delete|destroy|remove) fake[ ]team %faketeam%");
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected void execute(Event event) {
        FakeTeam team = getValueOrNull(0, FakeTeam.class, event);
        if (team == null) return;

        team.destroy();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "delete fake team";
    }
}
