package dev.threeadd.packeteventssk.element.general.expression.prop;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.event.PacketListenerPriority;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.general.packet.PacketSendOrReceiveEvent;
import dev.threeadd.packeteventssk.api.util.field.FieldAccessor;
import dev.threeadd.packeteventssk.api.util.field.FieldSchema;
import dev.threeadd.packeteventssk.element.general.event.EvtPacketSendOrReceive;
import dev.threeadd.packeteventssk.element.general.event.EvtPacketSendOrReceive.PacketSendOrReceiveParserData;
import dev.threeadd.packeteventssk.element.general.field.PacketFieldRegistry;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

@SuppressWarnings("rawtypes")
public class ExprPacketField extends PropertyExpression<PacketWrapper, Object> {

    public static void register(Registration reg) {

        StringBuilder description = new StringBuilder();
        description.append("Gets or sets a field's value from a packet by its name.\n\n");
        description.append("### Available Packets and their fields\n");

        Collection<FieldSchema<PacketTypeCommon, PacketWrapper<?>>> schemas = PacketFieldRegistry.INSTANCE.getAllSchemas();
        for (FieldSchema<PacketTypeCommon, PacketWrapper<?>> schema : schemas) {
            String fieldLines = schema.getReadableFields();
            if (!fieldLines.isEmpty()) {
                description.append("* **")
                        .append(schema.type().getName().toLowerCase(Locale.ENGLISH).replace("_", " "))
                        .append("** fields:\n")
                        .append(fieldLines)
                        .append("\n");
            }
        }

        reg.newPropertyExpression(ExprPacketField.class, Object.class, "[fake] packet [field] <[a-zA-Z0-9_ ]+>", "packet")
                .name("General - Packet Field")
                .description(description.toString())
                .examples("""
                        on clientbound entity metadata netty processed:
                            set {_meta} to entity meta of event-packet
                            set fake glowing state of {_meta} to true
                            set entity meta of event-packet to {_meta}
                        """)
                .since("1.1.0", "1.1.1 (fixed bugs)")
                .register();
    }

    private String fieldName;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.fieldName = parseResult.regexes.getFirst().group().trim();

        PacketSendOrReceiveParserData data = getParser().getData(PacketSendOrReceiveParserData.class);

        if (getParser().isCurrentEvent(PacketSendOrReceiveEvent.class)) { // for the listening event

            PacketTypeCommon eventPacketType = data.getPacketType();
            if (eventPacketType == null) return false; // shouldn't ever happen

            FieldSchema<PacketTypeCommon, PacketWrapper<?>> def = PacketFieldRegistry.INSTANCE.getSchema(eventPacketType);

            if (def == null) {
                Skript.error("No fields are currently registered for the " + eventPacketType.getName().toLowerCase(Locale.ENGLISH).replace("_", " ") + " packet.");
                return false;
            }

            if (def.getAccessor(this.fieldName) == null) {
                Skript.error("The field '" + this.fieldName + "' does not exist in a " + eventPacketType.getName().toLowerCase(Locale.ENGLISH).replace("_", " ") + " packet.");
                return false;
            }
        } else { // more global check
            boolean isValidField = false;
            for (FieldSchema<PacketTypeCommon, PacketWrapper<?>> def : PacketFieldRegistry.INSTANCE.getAllSchemas()) {
                if (def.getAccessor(this.fieldName) != null) {
                    isValidField = true;
                    break;
                }
            }

            if (!isValidField) {
                Skript.error("The packet field '" + this.fieldName + "' is not registered or does not exist. Consider checking your spelling.");
                return false;
            }
        }

        setExpr((Expression<? extends PacketWrapper>) exprs[0]);
        return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    @Override
    protected Object[] get(Event event, PacketWrapper[] source) {
        if (this.fieldName == null) return null;

        List<Object> results = new ArrayList<>();

        for (PacketWrapper<?> wrapper : source) {
            if (wrapper == null) continue;

            PacketTypeCommon type = wrapper.getPacketTypeData().getPacketType();
            FieldSchema<PacketTypeCommon, PacketWrapper<?>> definition = PacketFieldRegistry.INSTANCE.getSchema(type);

            if (definition == null) continue;

            FieldAccessor<PacketWrapper<?>, ?> targetField = definition.getAccessor(this.fieldName);

            if (targetField == null || targetField.getter() == null) continue;

            Object value = targetField.getter().apply(wrapper);
            if (value != null) {
                results.add(value);
            }
        }

        return results.isEmpty() ? null : results.toArray();
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (mode == Changer.ChangeMode.SET && this.fieldName != null) {
            PacketSendOrReceiveParserData data = getParser().getData(PacketSendOrReceiveParserData.class);

            if (getParser().isCurrentEvent(PacketSendOrReceiveEvent.class)) {
                if (data.getProcessType() != EvtPacketSendOrReceive.ProcessType.NETTY) {
                    Skript.error("You can't alter packets in a " + (data.getProcessType() == null ? "unknown" : data.getProcessType().toString().toLowerCase(Locale.ENGLISH)) + " processed event, the packets have already been processed at that point. Use a netty processed event instead.");
                    return null;
                } else if (data.getPriority() == PacketListenerPriority.MONITOR) {
                    Skript.error("You can't alter packets when using the \"monitor\" listening priority.");
                    return null;
                }

                PacketTypeCommon eventPacketType = data.getPacketType();
                if (eventPacketType != null) {
                    FieldSchema<PacketTypeCommon, PacketWrapper<?>> def = PacketFieldRegistry.INSTANCE.getSchema(eventPacketType);
                    if (def != null) {
                        FieldAccessor<PacketWrapper<?>, ?> field = def.getAccessor(this.fieldName);
                        if (field != null) {
                            return new Class<?>[]{field.expectedType()};
                        }
                    }
                }
            }

            List<Class<?>> acceptedTypes = new ArrayList<>();
            for (FieldSchema<PacketTypeCommon, PacketWrapper<?>> def : PacketFieldRegistry.INSTANCE.getAllSchemas()) {
                FieldAccessor<PacketWrapper<?>, ?> field = def.getAccessor(this.fieldName);
                if (field != null) {
                    Class<?> expected = field.expectedType();
                    if (!acceptedTypes.contains(expected)) {
                        acceptedTypes.add(expected);
                    }
                }
            }

            if (!acceptedTypes.isEmpty()) {
                return acceptedTypes.toArray(new Class<?>[0]);
            }
        }
        return null;
    }

    @SuppressWarnings({"unchecked", "UnstableApiUsage"})
    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        if (mode != Changer.ChangeMode.SET || delta == null || delta.length == 0 || this.fieldName == null) return;

        for (PacketWrapper<?> wrapper : getExpr().getArray(event)) {
            if (wrapper == null) continue;

            PacketTypeCommon type = wrapper.getPacketTypeData().getPacketType();
            FieldSchema<PacketTypeCommon, PacketWrapper<?>> definition = PacketFieldRegistry.INSTANCE.getSchema(type);

            if (definition == null) continue;

            FieldAccessor<PacketWrapper<?>, ?> targetField = definition.getAccessor(this.fieldName);

            if (targetField == null || targetField.setter() == null) continue;

            Object newValue;
            if (delta.length == 1) {
                newValue = delta[0];
            } else {
                newValue = delta; // array
            }

            Class<?> expected = targetField.expectedType();
            boolean isCompatible = expected == Object.class || expected.isInstance(newValue);

            if (!isCompatible && expected.isArray() && newValue.getClass().isArray()) {
                isCompatible = true;
            }

            if (!isCompatible) {
                Skript.warning("Cannot set the packet field '" + this.fieldName + "' to a value of type " + newValue.getClass().getSimpleName() + ". Expected type: " + expected.getSimpleName());
                continue;
            }

            ((BiConsumer<PacketWrapper<?>, Object>) targetField.setter()).accept(wrapper, newValue);
        }
    }

    @Override
    public Class<?> getReturnType() {
        return Object.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String wrapper = getExpr() != null ? getExpr().toString(event, debug) : "packet";
        return "packet field " + fieldName + " of " + wrapper;
    }
}