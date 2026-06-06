package dev.threeadd.packeteventssk.element.general.expression.prop;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.general.packet.PacketSendOrReceiveEvent;
import dev.threeadd.packeteventssk.api.field.BaseFieldRegistry;
import dev.threeadd.packeteventssk.api.field.FieldAccessor;
import dev.threeadd.packeteventssk.api.field.FieldSchema;
import dev.threeadd.packeteventssk.api.field.doc.FieldDescriptionBuilder;
import dev.threeadd.packeteventssk.api.field.skript.AbstractExprField;
import dev.threeadd.packeteventssk.element.general.event.EvtPacketSendOrReceive;
import dev.threeadd.packeteventssk.element.general.event.EvtPacketSendOrReceive.PacketSendOrReceiveParserData;
import dev.threeadd.packeteventssk.element.general.field.PacketFieldRegistry;
import org.jetbrains.annotations.Nullable;

import java.util.Locale;

public class ExprPacketField extends AbstractExprField<PacketTypeCommon, PacketWrapper<?>> {

    public static void register(Registration reg) {

        String description = """
                Gets or sets a field's value from a packet by its name.
                ### Available Packets and their fields
                """
                + FieldDescriptionBuilder.buildFlat(
                PacketFieldRegistry.INSTANCE.getAllSchemas(),
                schema -> {
                    String side = schema.type().getSide().equals(PacketSide.SERVER)
                            ? "clientbound" : "serverbound";
                    return side + " " + schema.type().getName()
                            .toLowerCase(Locale.ENGLISH).replace("_", " ") + " packet";
                });

        reg.newPropertyExpression(ExprPacketField.class, Object.class,
                        "packet [field] <[a-zA-Z0-9_ ]+>", "packet")
                .name("General - Packet Field")
                .description(description)
                .examples("""
                        on clientbound entity metadata:
                            set {_meta} to packet meta of event-packet
                            set meta glowing state of {_meta} to true
                            set packet meta of event-packet to {_meta}
                        """)
                .since("1.1.0", "1.1.1 (fixed bugs)")
                .register();
    }

    private boolean eventSpecificMode = false;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {

        String name = parseResult.regexes.getFirst().group().trim();
        PacketSendOrReceiveParserData data = getParser().getData(PacketSendOrReceiveParserData.class);

        boolean isPacketEvent = getParser().isCurrentEvent(PacketSendOrReceiveEvent.class);
        PacketTypeCommon eventType = isPacketEvent ? data.getPacketType() : null;

        if (eventType != null) {
            FieldSchema<PacketTypeCommon, PacketWrapper<?>> schema = PacketFieldRegistry.INSTANCE.getSchema(eventType);
            if (schema == null) {
                Skript.error("No fields are currently registered for the " + eventType.getName().toLowerCase(Locale.ENGLISH).replace("_", " ") + " packet.");
                return false;
            }
            FieldAccessor<PacketWrapper<?>, ?> accessor = schema.getAccessor(name);
            if (accessor == null) {
                Skript.error("The field '" + name + "' does not exist in a " + eventType.getName().toLowerCase(Locale.ENGLISH).replace("_", " ") + " packet.");
                return false;
            }
            this.hintAccessor = accessor;
            this.fieldName = name;
            this.eventSpecificMode = true;
            setExpr((Expression<? extends PacketWrapper<?>>) exprs[0]);
            return true;
        }

        return super.init(exprs, matchedPattern, isDelayed, parseResult);
    }

    @Override
    @SuppressWarnings({"UnstableApiUsage"})
    protected @Nullable FieldAccessor<PacketWrapper<?>, ?> resolveAccessor(PacketWrapper<?> wrapper) {
        if (this.eventSpecificMode) return this.hintAccessor; // already exact

        PacketTypeCommon type = wrapper.getPacketTypeData().getPacketType();
        if (type == null) return null;
        FieldSchema<PacketTypeCommon, PacketWrapper<?>> schema = PacketFieldRegistry.INSTANCE.getSchema(type);
        if (schema == null) return null;
        return schema.getAccessor(this.fieldName);
    }

    @Override
    public @Nullable Class<?>[] acceptChange(Changer.ChangeMode mode) {
        Class<?>[] base = super.acceptChange(mode);
        if (base == null) return null;

        PacketSendOrReceiveParserData data = getParser().getData(PacketSendOrReceiveParserData.class);
        if (getParser().isCurrentEvent(PacketSendOrReceiveEvent.class)) {
            if (data.getProcessType() != EvtPacketSendOrReceive.ProcessType.NETTY) {
                String procName = data.getProcessType() == null
                        ? "unknown" : data.getProcessType().toString().toLowerCase(Locale.ENGLISH);
                Skript.error("You can't alter packets in a " + procName
                        + " processed event, the packet has already been processed. Use a netty processed event instead.");
                return null;
            }
            if (data.getPriority() == PacketListenerPriority.MONITOR) {
                Skript.error("You can't alter packets when using the \"monitor\" listening priority.");
                return null;
            }
        }
        return base;
    }

    @Override
    protected BaseFieldRegistry<PacketTypeCommon, PacketWrapper<?>> getRegistry() {
        return PacketFieldRegistry.INSTANCE;
    }

    @Override
    protected String categoryLabel() {
        return "packet";
    }
}