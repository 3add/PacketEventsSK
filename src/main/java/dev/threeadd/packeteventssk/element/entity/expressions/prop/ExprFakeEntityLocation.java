package dev.threeadd.packeteventssk.element.entity.expressions.prop;

import ch.njol.skript.expressions.base.SimplePropertyExpression;
import com.github.shanebeee.skr.Registration;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.util.Vector;
import org.jetbrains.annotations.Nullable;
import dev.threeadd.packeteventssk.api.util.ConversionUtil;

public class ExprFakeEntityLocation extends SimplePropertyExpression<WrapperEntity, Vector> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeEntityLocation.class, Vector.class, "fake [entity] location", "fakeentity")
                .name("Fake Entity - Entity Location")
                .description("Used to get the entity location as a vector, this is a vector and not a regular location.")
                .examples("""
                        command tp <integer>:
                            trigger:
                                set {_entity} to fake entity with id arg-1
                                if {_entity} is not set:
                                    send "Couldn't find that entity"
                                    stop
                        
                                set {_locVector} to fake entity location of {_entity}
                        
                                #https://skripthub.net/docs/?id=10168
                                set {_loc} to {_locVector} to location in world of player
                        
                                teleport player to {_loc}
                        """)
                .since("1.0.0")
                .register();
    }

    @Override
    public @Nullable Vector convert(WrapperEntity entity) {
        return ConversionUtil.toBukkitVector(entity.getLocation());
    }

    @Override
    public Class<? extends Vector> getReturnType() {
        return Vector.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake entity location";
    }
}
