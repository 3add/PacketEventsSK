package dev.threeadd.packeteventssk.element.entity;

import ch.njol.skript.classes.ClassInfo;
import ch.njol.skript.classes.Parser;
import ch.njol.skript.lang.ParseContext;
import ch.njol.skript.registrations.Classes;
import dev.threeadd.packeteventssk.util.registry.EnumWrapper;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.entity.Pose;

@SuppressWarnings("unused")
public class Types {

    public static void register() {
        Classes.registerClass(new ClassInfo<>(WrapperEntity.class, "fakeentity")
                .user("fake[ -]?entity")
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
                        return entity.getEntityType().getName().getKey() + " fake entity with id " + entity.getEntityId();
                    }

                    @Override
                    public String toVariableNameString(WrapperEntity entity) {
                        return "fakeentity:" + entity.hashCode();
                    }
                })
        );

        if (Classes.getExactClassInfo(Pose.class) == null) {
            EnumWrapper<Pose> POSE_ENUM = new EnumWrapper<>(Pose.class, null, "pose");
            Classes.registerClass(POSE_ENUM.getClassInfo("pose")
                    .user("poses?")
                    .name("Pose")
                    .description("The pose of an entity (standing, sleeping, swimming, etc)")
                    .examples("""
                            command sleepify <integer>:
                                trigger:
                                    set {_entity} to fake entity with id arg-1
                                    if {_entity} is not set:
                                        send "Couldn't find that entity"
                                        stop
                            
                                    set fake pose of {_entity} to sleeping pose
                                    send "Entity is now sleeping!"
                            """)
                    .since("INSERT VERSION")
            );
        }
    }
}
