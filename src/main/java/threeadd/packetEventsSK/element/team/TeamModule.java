package threeadd.packetEventsSK.element.team;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.registrations.Classes;
import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.team.effects.EffDeleteTeam;
import threeadd.packetEventsSK.element.team.expressions.ExprFakeTeamWithName;
import threeadd.packetEventsSK.element.team.expressions.ExprLastFakeTeam;
import threeadd.packetEventsSK.element.team.expressions.prop.FakeTeamColorProp;
import threeadd.packetEventsSK.element.team.expressions.prop.FakeTeamEntitiesProp;
import threeadd.packetEventsSK.element.team.expressions.prop.FakeTeamNameProp;
import threeadd.packetEventsSK.element.team.expressions.prop.FakeTeamReceiversProp;
import threeadd.packetEventsSK.element.team.sections.CreateFakeTeamSec;

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
