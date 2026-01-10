package threeadd.packetEventsSK.element.entity.expressions.prop.base;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.entity.Pose;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.element.entity.api.MetaPropertyExpression;

@SuppressWarnings("unused")
@Name("Fake Entity Property - Entity Pose")
@Description("The entity pose of a fake entity (drawing bow, sleeping, etc)")
@Example("""
        command sleepify <integer>:
            trigger:
                set {_entity} to fake entity with id arg-1
                if {_entity} is not set:
                    send "Couldn't find that entity"
                    stop
        
                set fake pose of {_entity} to sleeping pose
        """)
@Since("1.0.0")
public class PoseProp extends MetaPropertyExpression<EntityMeta, Pose> {

    static {
        PropertyExpression.register(PoseProp.class, Pose.class, "fake[ ]pose", "fakeentity/fakeentitymeta");
    }

    @SuppressWarnings("unused")
    public PoseProp() {
        super(Pose.class, EntityMeta.class, Changer.ChangeMode.SET);
    }

    @Override
    protected @Nullable Pose get(EntityMeta meta) {
        return SpigotConversionUtil.toBukkitPose(meta.getPose());
    }

    @Override
    protected void change(EntityMeta meta, Changer.ChangeMode mode, Object[] delta) {
        Pose newPose = getDeltaValue(delta, Pose.class);
        meta.setPose(SpigotConversionUtil.fromBukkitPose(newPose));
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "pose of fake entity";
    }
}

