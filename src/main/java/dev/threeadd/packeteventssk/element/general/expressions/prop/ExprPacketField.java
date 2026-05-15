package dev.threeadd.packeteventssk.element.general.expressions.prop;

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
import dev.threeadd.packeteventssk.api.general.PacketConstructorRegistry;
import dev.threeadd.packeteventssk.api.general.PacketConstructorRegistry.PacketDefinition;
import dev.threeadd.packeteventssk.api.general.PacketConstructorRegistry.PacketField;
import dev.threeadd.packeteventssk.api.general.PacketSendOrReceiveEvent;
import dev.threeadd.packeteventssk.element.general.event.EvtPacketSendOrReceive;
import dev.threeadd.packeteventssk.element.general.event.EvtPacketSendOrReceive.PacketSendOrReceiveParserData;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.function.BiConsumer;

@SuppressWarnings("rawtypes")
public class ExprPacketField extends PropertyExpression<PacketWrapper, Object> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprPacketField.class, Object.class, "[packet] field <[a-zA-Z0-9_ ]+>", "packet")
                .name("General - Packet Field")
                .description("Gets a field's value from a packet by its name.")
                //TODO example
                .since("1.1.0", "1.1.1 (fixed bugs)")
                .register();
    }

    private String fieldName;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.fieldName = parseResult.regexes.getFirst().group().trim();

        PacketSendOrReceiveParserData data = getParser().getData(PacketSendOrReceiveParserData.class);

        if (data.getPacketType() != null) { // for the listening event

            PacketTypeCommon eventPacketType = data.getPacketType();
            PacketDefinition def = PacketConstructorRegistry.getDefinition(eventPacketType);

            if (def == null) {
                Skript.error("No fields are currently registered for the " + eventPacketType.getName().toLowerCase(Locale.ENGLISH).replace("_", " ") + " packet.");
                return false;
            }

            if (def.getField(this.fieldName) == null) {
                Skript.error("The field '" + this.fieldName + "' does not exist in a " + eventPacketType.getName().toLowerCase(Locale.ENGLISH).replace("_", " ") + " packet.");
                return false;
            }
        } else { // more global check
            boolean isValidField = false;
            for (PacketDefinition def : PacketConstructorRegistry.getAllDefinitions()) {
                if (def.getField(this.fieldName) != null) {
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
            PacketDefinition definition = PacketConstructorRegistry.getDefinition(type);

            if (definition == null) continue;

            PacketField<?> targetField = definition.getField(this.fieldName);

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
        if (mode == Changer.ChangeMode.SET) {
            PacketSendOrReceiveParserData data = getParser().getData(PacketSendOrReceiveParserData.class);

            if (data.getPacketType() != null) {
                if (data.getProcessType() != EvtPacketSendOrReceive.ProcessType.NETTY) {
                    Skript.error("You can't alter packets in a " + (data.getProcessType() == null ? "unknown" : data.getProcessType().toString().toLowerCase(Locale.ENGLISH)) + " processed event, the packets have already been processed at that point. Use a netty processed event instead.");
                    return null;
                } else if (data.getPriority() == PacketListenerPriority.MONITOR) { // Note: Swapped to == so it matches the error string
                    Skript.error("You can't alter packets when using the \"monitor\" listening priority.");
                    return null;
                }
            }

            return new Class[]{Object[].class};
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
            PacketDefinition definition = PacketConstructorRegistry.getDefinition(type);

            if (definition == null) continue;

            PacketField<?> targetField = definition.getField(this.fieldName);

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

            if (event instanceof PacketSendOrReceiveEvent.NettyPacketEvent packetEvent) {
                packetEvent.setModified(true);
            }
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