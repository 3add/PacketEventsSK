package dev.threeadd.packeteventssk.element.team;

import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.element.team.effects.EffDeleteTeam;
import dev.threeadd.packeteventssk.element.team.expressions.ExprFakeTeamFromName;
import dev.threeadd.packeteventssk.element.team.expressions.prop.ExprFakeTeamColor;
import dev.threeadd.packeteventssk.element.team.expressions.prop.ExprFakeTeamEntities;
import dev.threeadd.packeteventssk.element.team.expressions.prop.ExprFakeTeamName;
import dev.threeadd.packeteventssk.element.team.expressions.prop.ExprFakeTeamReceivers;
import dev.threeadd.packeteventssk.element.team.sections.EffSecCreateFakeTeam;
import dev.threeadd.packeteventssk.api.util.registry.element.SkriptElementRegistration;

public class TeamElementRegistration implements SkriptElementRegistration {

    @Override
    public String identifier() {
        return "team";
    }

    @Override
    public void load(Registration reg) {

        // start effects
        EffDeleteTeam.register(reg);
        // end effects

        // start expressions
        ExprFakeTeamColor.register(reg);
        ExprFakeTeamEntities.register(reg);
        ExprFakeTeamName.register(reg);
        ExprFakeTeamReceivers.register(reg);

        ExprFakeTeamFromName.register(reg);
        // end expressions

        // start sections
        EffSecCreateFakeTeam.register(reg);
        // end sections

        // types
        Types.register(reg);
    }
}
