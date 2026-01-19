package threeadd.packetEventsSK.element.general.expressions.prop.server;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import org.bukkit.entity.Entity;
import threeadd.packetEventsSK.element.general.api.PacketPropertyExpression;
import threeadd.packetEventsSK.util.ConversionUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

@SuppressWarnings("unused")
@Name("General - Destroy Entities Packet - Entities")
@Description("""
        Represents the entities of an destroy entities packet.
        **This property is NOT thread safe.**
        """)
@Example("""
        command killTargetForMe:
            trigger:
                create a new destroy entities send packet:
                    add target entity of player to packet entities of the packet
                    send packet the packet to the player
        """)
@Since("1.0.0")
public class DestroyEntitiesEntitiesProp extends PacketPropertyExpression<WrapperPlayServerDestroyEntities, Entity> {

    static {
        PropertyExpression.register(DestroyEntitiesEntitiesProp.class, Entity.class,
                "packet[ ]entities", "packet");
    }

    public DestroyEntitiesEntitiesProp() {
        super(Entity.class, PacketType.Play.Server.DESTROY_ENTITIES, false, false, DestroyEntitiesEntityIdsProp.class,
                Changer.ChangeMode.SET, Changer.ChangeMode.ADD, Changer.ChangeMode.REMOVE, Changer.ChangeMode.REMOVE_ALL, Changer.ChangeMode.RESET);
    }

    @Override
    protected List<Entity> getMany(WrapperPlayServerDestroyEntities input) {
        return Arrays.stream(input.getEntityIds())
                .mapToObj(ConversionUtil::getEntityById)
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    protected void change(WrapperPlayServerDestroyEntities wrapper, Changer.ChangeMode mode, Object[] delta) {

        List<Integer> currentIds = new ArrayList<>(Arrays.stream(wrapper.getEntityIds()).boxed().toList());
        List<Integer> deltaIds = new ArrayList<>();

        if (mode.equals(Changer.ChangeMode.SET) ||
                mode.equals(Changer.ChangeMode.ADD) ||
                mode.equals(Changer.ChangeMode.REMOVE)) {

            deltaIds = getDeltaValues(delta, Entity.class).stream()
                    .map(Entity::getEntityId)
                    .toList();
        }

        switch (mode) {
            case SET -> wrapper.setEntityIds(convertToPrimitiveArray(deltaIds));

            case ADD -> {
                currentIds.addAll(deltaIds);
                wrapper.setEntityIds(convertToPrimitiveArray(currentIds));
            }

            case REMOVE -> {
                currentIds.removeAll(deltaIds);
                wrapper.setEntityIds(convertToPrimitiveArray(currentIds));
            }

            case RESET, REMOVE_ALL -> wrapper.setEntityIds(new int[0]);
        }
    }

    private int[] convertToPrimitiveArray(List<Integer> list) {
        return list.stream()
                .mapToInt(i -> i)
                .toArray();
    }

    @Override
    public String toString() {
        return "entities of entities destroy packet";
    }
}
