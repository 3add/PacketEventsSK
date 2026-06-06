package dev.threeadd.packeteventssk.element.general.section;

import ch.njol.skript.lang.Expression;
import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.field.BaseFieldRegistry;
import dev.threeadd.packeteventssk.api.field.doc.FieldDescriptionBuilder;
import dev.threeadd.packeteventssk.api.field.skript.AbstractSecExprNew;
import dev.threeadd.packeteventssk.element.general.field.PacketFieldRegistry;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class SecExprNewPacket extends AbstractSecExprNew<PacketTypeCommon, PacketWrapper<?>> {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void register(Registration reg) {
        buildValidators(PacketFieldRegistry.INSTANCE);

        String description = """
                Create a new packet from a packet type.
                ### Available Packets and their fields
                """ +
                FieldDescriptionBuilder.buildFlat(
                        PacketFieldRegistry.INSTANCE.getAllSchemas(),
                        schema -> {
                            String side = schema.type().getSide().equals(PacketSide.SERVER)
                                    ? "clientbound" : "serverbound";
                            return side + " " + schema.type().getName()
                                    .toLowerCase(Locale.ENGLISH).replace("_", " ") + " packet";
                        });

        reg.newSimpleExpression(SecExprNewPacket.class, (Class) PacketWrapper.class,
                        "[a] new %*packettype%")
                .name("General - New Packet")
                .description(description)
                .examples("""
                        command killTargetForMe:
                            trigger:
                                set {_packet} to a new clientbound destroy entities packet:
                                    entity ids: protocol id of target entity
                        
                                silently send packet {_packet} to the player
                        """)
                .since("1.0.0", "1.1.0 (changed to SectionExpression)",
                        "1.2.0 (dynamic type support)")
                .register();
    }

    @Override
    protected BaseFieldRegistry<PacketTypeCommon, PacketWrapper<?>> getRegistry() {
        return PacketFieldRegistry.INSTANCE;
    }

    @Override
    protected @Nullable PacketTypeCommon resolveType(Expression<?> typeExpr, Event event) {
        return (PacketTypeCommon) typeExpr.getSingle(event);
    }

    @Override
    protected boolean onMissingSchema(PacketTypeCommon type) {
        return false; // hard error, can't return an empty packet
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Class<? extends PacketWrapper<?>> getReturnType() {
        return (Class) PacketWrapper.class;
    }

    @Override
    protected String formatTypeName(@Nullable PacketTypeCommon type) {
        return type != null ? type.getName().toLowerCase(Locale.ENGLISH).replace("_", " ") : "unknown";
    }

    @Override
    protected String categoryLabel() {
        return "packet";
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "a new " + this.formatTypeName(this.typeExpr.getSingle(event)) + " packet";
    }
}