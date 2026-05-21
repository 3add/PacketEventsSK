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
import com.google.common.primitives.Primitives;
import dev.threeadd.packeteventssk.api.general.packet.PacketSendOrReceiveEvent;
import dev.threeadd.packeteventssk.api.util.field.FieldAccessor;
import dev.threeadd.packeteventssk.api.util.field.FieldSchema;
import dev.threeadd.packeteventssk.element.general.event.EvtPacketSendOrReceive;
import dev.threeadd.packeteventssk.element.general.event.EvtPacketSendOrReceive.PacketSendOrReceiveParserData;
import dev.threeadd.packeteventssk.element.general.field.PacketFieldRegistry;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Array;
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

    private FieldAccessor<PacketWrapper<?>, ?> fieldAccessor;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        String fieldName = parseResult.regexes.getFirst().group().trim();
        PacketSendOrReceiveParserData data = getParser().getData(PacketSendOrReceiveParserData.class);

        if (getParser().isCurrentEvent(PacketSendOrReceiveEvent.class)) { // for the listening event
            PacketTypeCommon eventPacketType = data.getPacketType();
            if (eventPacketType == null) return false;

            FieldSchema<PacketTypeCommon, PacketWrapper<?>> schema = PacketFieldRegistry.INSTANCE.getSchema(eventPacketType);
            if (schema == null) {
                Skript.error("No fields are currently registered for the " + eventPacketType.getName().toLowerCase(Locale.ENGLISH).replace("_", " ") + " packet.");
                return false;
            }

            this.fieldAccessor = schema.getAccessor(fieldName);
            if (this.fieldAccessor == null) {
                Skript.error("The field '" + fieldName + "' does not exist in a " + eventPacketType.getName().toLowerCase(Locale.ENGLISH).replace("_", " ") + " packet.");
                return false;
            }

        } else { // more global check
            boolean isValidField = false;
            for (FieldSchema<PacketTypeCommon, PacketWrapper<?>> def : PacketFieldRegistry.INSTANCE.getAllSchemas()) {
                this.fieldAccessor = def.getAccessor(fieldName);
                if (this.fieldAccessor  != null) {
                    isValidField = true;
                    break;
                }
            }

            if (!isValidField) {
                Skript.error("The packet field '" + fieldName + "' is not registered or does not exist. Consider checking your spelling.");
                return false;
            }
        }

        setExpr((Expression<? extends PacketWrapper>) exprs[0]);
        return true;
    }

    @Override
    protected Object[] get(Event event, PacketWrapper[] source) {
        if (this.fieldAccessor == null) return null;

        List<Object> elements = new ArrayList<>();

        for (PacketWrapper<?> wrapper : source) {
            if (wrapper == null) continue;

            Object value = this.fieldAccessor.getter().apply(wrapper);
            if (value == null) continue;

            if (value.getClass().isArray()) {
                int len = Array.getLength(value);
                int i = 0;
                while (i < len) {
                    elements.add(Array.get(value, i++));
                }
            } else {
                elements.add(value);
            }
        }

        if (elements.isEmpty()) return null;

        Class<?> returnType = Primitives.wrap(getReturnType()); // wrap primitives to avoid java.lang.ClassCastException on arrays of primitives
        return elements.toArray((Object[]) Array.newInstance(returnType, 0));
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (this.fieldAccessor.setter() == null) {
            Skript.error("Cannot set " + this.fieldAccessor.name() + " because it is a read-only field.");
            return null;
        }

        if (mode != Changer.ChangeMode.SET) return null;

        PacketSendOrReceiveParserData data = getParser().getData(PacketSendOrReceiveParserData.class);

        if (getParser().isCurrentEvent(PacketSendOrReceiveEvent.class)) {
            if (data.getProcessType() != EvtPacketSendOrReceive.ProcessType.NETTY) {
                Skript.error("You can't alter packets in a " + (data.getProcessType() == null ? "unknown" : data.getProcessType().toString().toLowerCase(Locale.ENGLISH)) + " processed event, the packets have already been processed at that point. Use a netty processed event instead.");
                return null;
            } else if (data.getPriority() == PacketListenerPriority.MONITOR) {
                Skript.error("You can't alter packets when using the \"monitor\" listening priority.");
                return null;
            }
        }

        Class<?> expected = this.fieldAccessor.expectedType();
        Class<?> typeToAccept = expected.isArray() ? expected.getComponentType() : expected;

        if (expected.equals(typeToAccept)) {
            return new Class<?>[]{typeToAccept};
        }

        return null;
    }

    @SuppressWarnings({"unchecked"})
    @Override
    public void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        if (mode != Changer.ChangeMode.SET || delta == null || delta.length == 0 || this.fieldAccessor.setter() == null) return;

        for (PacketWrapper<?> wrapper : getExpr().getArray(event)) {
            if (wrapper == null) continue;

            Object newValue;
            Class<?> expected = this.fieldAccessor.expectedType();

            if (expected.isArray()) {
                Class<?> componentType = expected.getComponentType();
                Object typedArray = Array.newInstance(componentType, delta.length);
                for (int i = 0; i < delta.length; i++) {
                    Array.set(typedArray, i, delta[i]);
                }
                newValue = typedArray;
            } else {
                if (delta.length == 1) {
                    newValue = delta[0];
                } else {
                    newValue = delta;
                }
            }

            boolean isCompatible = expected == Object.class || expected.isInstance(newValue);
            if (!isCompatible && expected.isArray() && newValue.getClass().isArray()) {
                isCompatible = true;
            }

            if (!isCompatible) {
                Skript.warning("Cannot set the packet field '" + this.fieldAccessor.name() + "' to a value of type " + newValue.getClass().getSimpleName() + ". Expected type: " + expected.getSimpleName());
                continue;
            }

            ((BiConsumer<PacketWrapper<?>, Object>) this.fieldAccessor.setter()).accept(wrapper, newValue);
        }
    }

    @Override
    public boolean isSingle() {
        return !this.fieldAccessor.expectedType().isArray() && getExpr().isSingle();
    }

    @Override
    public Class<?> getReturnType() {
        Class<?> expected = this.fieldAccessor.expectedType();
        return expected.isArray() ? expected.getComponentType() : expected;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String wrapper = getExpr() != null ? getExpr().toString(event, debug) : "packet";
        return "packet field " + this.fieldAccessor.name() + " of " + wrapper;
    }
}