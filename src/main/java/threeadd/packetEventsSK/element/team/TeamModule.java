package threeadd.packetEventsSK.element.team;

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

    private static final String BASE_PACKAGE = "threeadd.packetEventsSK.element.team";

    // Team syntax classes register in static blocks, so initialize them once here.
    private static final String[] STATIC_REGISTRATION_CLASSES = {
            BASE_PACKAGE + ".Types"

    };

    public static void registerAll(SyntaxRegistry registry) {

        CreateFakeTeamSec.register(registry);

        ExprLastFakeTeam.register(registry);
        ExprFakeTeamWithName.register(registry);
        FakeTeamColorProp.register(registry);
        FakeTeamEntitiesProp.register(registry);
        FakeTeamNameProp.register(registry);
        FakeTeamReceiversProp.register(registry);

        EffDeleteTeam.register(registry);

        for (String className : STATIC_REGISTRATION_CLASSES) {
            forceInitialize(className);
        }
    }

    private static void forceInitialize(String className) {
        try {
            Class.forName(className, true, TeamModule.class.getClassLoader());
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("Unable to initialize team syntax class: " + className, exception);
        }
    }
}
