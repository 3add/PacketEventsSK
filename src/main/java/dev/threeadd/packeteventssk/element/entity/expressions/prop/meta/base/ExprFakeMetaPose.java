package dev.threeadd.packeteventssk.element.entity.expressions.prop.meta.base;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import ch.njol.util.coll.CollectionUtils;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.entity.EntityMetaPropertyExpression;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.entity.Pose;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;

public class ExprFakeMetaPose extends EntityMetaPropertyExpression<EntityMeta, Pose> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprFakeMetaPose.class, Pose.class, "fake pose", "entitymeta")
                .name("Fake Entity Property - Entity Pose")
                .description("The entity pose of a fake entity (drawing bow, sleeping, etc)")
                .examples("""
                        command sleepify <integer>:
                            trigger:
                                set {_entity} to fake entity with id arg-1
                                if {_entity} is not set:
                                    send "Couldn't find that entity"
                                    stop
                        
                                set fake pose of {_entity} to sleeping pose
                        """)
                .since("1.0.0")
                .register();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends EntityMeta>) expressions[0]);
        return true;
    }

    @Override
    protected Pose @Nullable [] getMetaProp(Event event, EntityMeta meta) {
        return new Pose[]{SpigotConversionUtil.toBukkitPose(meta.getPose())};
    }

    @Override
    public Class<?> @Nullable [] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET) {
            return CollectionUtils.array(Pose.class);
        }
        return null;
    }

    @Override
    protected void changeMeta(Event event, EntityMeta meta, Object @Nullable [] delta, Changer.ChangeMode mode) {
        if (delta == null || delta.length != 1 || !(delta[0] instanceof Pose newPose)) {
            throw new IllegalStateException("Unexpected delta value " + (delta != null ? Arrays.toString(delta) : "none"));
        }

        if (mode != Changer.ChangeMode.SET) { // should always be the case
            return;
        }

        meta.setPose(SpigotConversionUtil.fromBukkitPose(newPose));
    }

    @Override
    protected Class<EntityMeta> getMetaClass() {
        return EntityMeta.class;
    }

    @Override
    public Class<? extends Pose> getReturnType() {
        return Pose.class;
    }

    @Override
    protected String getPropertyName() {
        return "fake pose";
    }
}

