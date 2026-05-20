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

        reg.newType(EntityMeta.class, "entitymeta")
                .user("fake ?entit(y|ies) meta")
                .name("General - Entity Meta")
                .description("The entity meta of a minecraft entity (this can both represent a fake entity's meta or a real entity's meta, but is mostly used for fake entities since the only use for real entities is for packet intercepting).")
                .examples("""
                        command spawn:
                            trigger:
                                set {_zombie} to a new fake zombie entity:
                                    players: all players
                                    location: location of player

                                set glowing state of field meta of {_zombie} to 2
                        """)
                .since("1.1.0")
                .parser(new Parser<>() {
                    @Override
                    public boolean canParse(ParseContext context) {
                        return false;
                    }

                    @Override
                    public String toString(EntityMeta meta, int flags) {
                        return "entity meta";
                    }

                    @Override
                    public String toVariableNameString(EntityMeta meta) {
                        return "entitymeta:" + meta.hashCode();
                    }
                })
                .register();
    }
}
