package dev.threeadd.packeteventssk.element.entity;

import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import com.github.shanebeee.skr.Registration;
import me.tofaa.entitylib.meta.EntityMeta;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.skriptlang.skript.lang.converter.Converters;

import java.util.Locale;

public class Types {

    public static void register(Registration reg) {
        reg.newType(WrapperEntity.class, "fakeentity")
                .user("fake ?entit(y|ies)")
                .name("Fake Entity - Fake Entity")
                .description("A fake entity viewable by at least 1 player")
                .examples("""
                        command cloneMe:
                            trigger:
                                create new fake player entity at player for all players:
                                    set fake skin of the fake entity to player's skin
                        """)
                .since("1.0.0")
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(WrapperEntity entity, int flags) {
                        return "fake " + entity.getEntityType().getName().getKey().toLowerCase(Locale.ENGLISH).replace("_", " ") + " entity";
                    }

                    @Override
                    public String toVariableNameString(WrapperEntity entity) {
                        return "fakeentity:" + entity.getUuid().toString().toLowerCase(Locale.ENGLISH);
                    }
                })
                .register();

        Converters.registerConverter(WrapperEntity.class, EntityMeta.class, WrapperEntity::getEntityMeta);
    }
}
