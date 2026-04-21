package dev.threeadd.packeteventssk.element.team;

import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.element.team.effects.EffDeleteTeam;
import dev.threeadd.packeteventssk.element.team.expressions.ExprFakeTeamWithName;
import dev.threeadd.packeteventssk.element.team.expressions.ExprLastFakeTeam;
import dev.threeadd.packeteventssk.element.team.expressions.prop.FakeTeamColorProp;
import dev.threeadd.packeteventssk.element.team.expressions.prop.FakeTeamEntitiesProp;
import dev.threeadd.packeteventssk.element.team.expressions.prop.FakeTeamNameProp;
import dev.threeadd.packeteventssk.element.team.expressions.prop.FakeTeamReceiversProp;
import dev.threeadd.packeteventssk.element.team.sections.CreateFakeTeamSec;

public class TeamModule {

    public static void registerAll(SyntaxRegistry registry) {
        CreateFakeTeamSec.register(registry);

        ExprLastFakeTeam.register(registry);
        ExprFakeTeamWithName.register(registry);
        FakeTeamColorProp.register(registry);
        FakeTeamEntitiesProp.register(registry);
        FakeTeamNameProp.register(registry);
        FakeTeamReceiversProp.register(registry);

        EffDeleteTeam.register(registry);

        Types.register();
    }
}
