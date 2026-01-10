package threeadd.packetEventsSK.element.general.expressions.prop.server;

import ch.njol.skript.classes.Changer;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import com.github.retrooper.packetevents.protocol.packettype.PacketType;
import com.github.retrooper.packetevents.wrapper.play.server.WrapperPlayServerDestroyEntities;
import threeadd.packetEventsSK.element.general.api.PacketPropertyExpression;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
@Name("General - Destroy Entities Packet - Entity Ids")
@Description("Represents the entity ids of an destroy entities packet.")
@Example("""
        command killTargetForMe:
            trigger:
                create a new destroy entities send packet:
                    add (entity id of target entity of player) to packet entity ids of the packet
                    send packet the packet to the player
        """)
@Since("1.0.0")
public class DestroyEntitiesEntityIds extends PacketPropertyExpression<WrapperPlayServerDestroyEntities, Integer> {

    static {
        PropertyExpression.register(DestroyEntitiesEntityIds.class, Integer.class,
                "packet[ ]entity[ ]ids", "packet");
    }

    public DestroyEntitiesEntityIds() {
        super(Integer.class, PacketType.Play.Server.DESTROY_ENTITIES, false, true, null,
                Changer.ChangeMode.SET, Changer.ChangeMode.ADD, Changer.ChangeMode.REMOVE, Changer.ChangeMode.REMOVE_ALL, Changer.ChangeMode.RESET);
    }

    @Override
    protected List<Integer> getMany(WrapperPlayServerDestroyEntities input) {
        return Arrays.stream(input.getEntityIds()).boxed().toList();
    }

    @Override
    protected void change(WrapperPlayServerDestroyEntities wrapper, Changer.ChangeMode mode, Object[] delta) {

        List<Integer> currentIds = new ArrayList<>(Arrays.stream(wrapper.getEntityIds()).boxed().toList());
        List<Integer> deltaIds = new ArrayList<>();

        if (mode.equals(Changer.ChangeMode.SET) ||
                mode.equals(Changer.ChangeMode.ADD) ||
                mode.equals(Changer.ChangeMode.REMOVE)) {

            deltaIds = getDeltaValues(delta, Integer.class).stream().toList();
        }

        switch (mode) {
            case SET -> {
                wrapper.setEntityIds(convertToPrimitiveArray(deltaIds));
            }

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
        return "entity ids of entities destroy packet";
    }
}
