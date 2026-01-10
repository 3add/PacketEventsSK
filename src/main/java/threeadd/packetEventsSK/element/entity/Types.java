package threeadd.packetEventsSK.element.entity;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import me.tofaa.entitylib.wrapper.WrapperEntity;

@SuppressWarnings("unused")
public class Types {

    static {
        Classes.registerClass(new ClassInfo<>(WrapperEntity.class, "fakeentity")
                .user("fake[ -]?entity")
                .name("Fake Entity - Fake Entity")
                .description("A fake entity viewable by at least 1 player")
                .examples("""
                        command cloneMe:
                            trigger:
                                create new fake player entity at player for all players:
                                    set packet skin of the fake entity to player's skin
                        """)
                .since("1.0")
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(WrapperEntity entity, int flags) {
                        return entity.getEntityType().getName().getKey() + " fake entity with id " + entity.getEntityId();
                    }

                    @Override
                    public String toVariableNameString(WrapperEntity entity) {
                        return "fakeentity:" + entity.hashCode();
                    }
                })
        );
    }
}
