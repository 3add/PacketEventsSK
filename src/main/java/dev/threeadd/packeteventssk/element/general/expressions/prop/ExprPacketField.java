package dev.threeadd.packeteventssk.element.general.expressions.prop;

import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.general.PacketConstructorRegistry;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;

@SuppressWarnings("rawtypes")
public class ExprPacketField extends PropertyExpression<PacketWrapper, Object> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprPacketField.class, Object.class, "[packet] field <[a-zA-Z0-9_ ]+>", "packet")
                .name("General - Packet Field")
                .description("Gets a field's value from a packet by a it's name.")
                //TODO example
                .since("1.1.0")
                .register();
    }

    private String fieldName;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] exprs, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.fieldName = parseResult.regexes.getFirst().group().trim();

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
            PacketConstructorRegistry.PacketDefinition definition = PacketConstructorRegistry.getDefinition(type);

            if (definition == null) continue;

            PacketConstructorRegistry.PacketField targetField = null;
            for (PacketConstructorRegistry.PacketField field : definition.fields()) {
                if (field.name().equalsIgnoreCase(this.fieldName)) {
                    targetField = field;
                    break;
                }
            }

            if (targetField == null || targetField.getter() == null) continue;

            Object value = targetField.getter().apply(wrapper);
            if (value != null) {
                results.add(value);
            }
        }

        return results.isEmpty() ? null : results.toArray();
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