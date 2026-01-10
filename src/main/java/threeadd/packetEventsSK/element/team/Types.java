package threeadd.packetEventsSK.element.team;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import threeadd.packetEventsSK.element.team.api.FakeTeam;

public class Types {
    static {
        Classes.registerClass(new ClassInfo<>(FakeTeam.class, "faketeam")
                .user("fake[ -]?team")
                .name("Fake Team - Fake Team")
                .description("A fake team viewable by at least 1 player")
                .examples("""
                        command glowGreen:
                            trigger:
                                set glowing state of player to true for player
                                create new fake team named player's name for players:
                                    set the fake team color of the fake team to green
                                    add player to fake team entities of the fake team
                        """)
                .since("1.0")
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(FakeTeam team, int flags) {
                        return team.getName() + " fake team";
                    }

                    @Override
                    public String toVariableNameString(FakeTeam entity) {
                        return "faketeam:" + entity.hashCode();
                    }
                })
        );
    }
}
