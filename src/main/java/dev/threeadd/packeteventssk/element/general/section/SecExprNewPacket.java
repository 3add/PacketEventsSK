package dev.threeadd.packeteventssk.element.general.section;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.*;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.shanebeee.skr.Registration;
import com.github.shanebeee.skr.skript.SimpleEntryValidator;
import dev.threeadd.packeteventssk.api.general.PacketConstructorRegistry;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.*;

public class SecExprNewPacket extends SectionExpression<PacketWrapper<?>> {

    private static EntryValidator VALIDATOR;

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void register(Registration reg) {

        SimpleEntryValidator builder = SimpleEntryValidator.builder();
        for (PacketConstructorRegistry.PacketDefinition def : PacketConstructorRegistry.getAllDefinitions()) {
            for (PacketConstructorRegistry.PacketField<?> field : def.fields()) {
                builder.addOptionalEntry(field.name(), Object.class);
            }
        }
        VALIDATOR = builder.build();

        reg.newSimpleExpression(SecExprNewPacket.class, (Class) PacketWrapper.class, "[a] [new] %packettype%")
                .name("General - New Packet")
                .description("Create a new packet from a packet type. This section is a data block, not an execution block.")
                // TODO Example
                .since("1.0.0", "1.1.0 (changed to SectionExpression) and large changes")
                .register();
    }

    private Literal<PacketTypeCommon> packetTypeLiteral;

    private final Map<String, Expression<?>> fieldExpressions = new HashMap<>();
    private PacketConstructorRegistry.PacketDefinition definition;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions,
                        int matchedPattern,
                        Kleenean isDelayed,
                        SkriptParser.ParseResult parseResult,
                        @Nullable SectionNode sectionNode,
                        @Nullable List<TriggerItem> triggerItems) {

        if (!(expressions[0] instanceof Literal<?>)) {
            Skript.error("The packet type needs to be a literal");
            return false;
        }

        this.packetTypeLiteral = (Literal<PacketTypeCommon>) expressions[0];

        PacketTypeCommon type = this.packetTypeLiteral.getSingle();
        this.definition = PacketConstructorRegistry.getDefinition(type);

        if (this.definition == null) {
            Skript.error("Packet creation for " + type.getName() + " is not currently supported.");
            return false;
        }

        boolean hasRequiredFields = this.definition.fields().stream().anyMatch(field -> !field.isOptional());
        if (sectionNode == null) {
            if (hasRequiredFields) {
                Skript.error("You must provide a section with the required fields to create a " + type.getName() + " packet.");
                return false;
            }
            return true;
        }

        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) {
            return false;
        }

        List<String> missingKeys = new ArrayList<>();
        boolean hasTypeError = false;

        for (PacketConstructorRegistry.PacketField<?> field : this.definition.fields()) {
            String key = field.name();
            Class<?> expectedType = field.expectedType();

            Expression<?> expr = container.getOptional(key, Object.class, false);

            if (expr == null) {
                if (!field.isOptional()) {
                    missingKeys.add(key);
                }
                continue;
            }

            Class<?> baseType = expectedType.isArray() ? expectedType.getComponentType() : expectedType;
            Expression<?> converted = expr.getConvertedExpression(baseType);

            if (converted == null) {
                Skript.error("The value for '" + key + "' must be of type " + expectedType.getSimpleName() + ".");
                hasTypeError = true;
                continue;
            }

            this.fieldExpressions.put(key, converted);
        }

        if (!missingKeys.isEmpty()) {
            String packetName = type.getName().toLowerCase(Locale.ENGLISH).replace("_", " ");
            Skript.error("Missing required entries for " + packetName + " packet: " + String.join(", ", missingKeys));
            return false;
        }

        return !hasTypeError;
    }

    @Override
    protected PacketWrapper<?> @Nullable [] get(Event event) {
        return new PacketWrapper[]{createPacket(event)};
    }

    private @Nullable PacketWrapper<?> createPacket(@NotNull Event event) {
        if (this.definition == null) return null;

        Map<String, Object> values = new HashMap<>();

        for (PacketConstructorRegistry.PacketField<?> field : this.definition.fields()) {
            Expression<?> expr = this.fieldExpressions.get(field.name());

            if (expr == null) {
                continue;
            }

            Object value;
            if (field.expectedType().isArray() || !expr.isSingle()) {
                Object[] array = expr.getArray(event);
                value = (array == null || array.length == 0) ? null : array;
            } else {
                value = expr.getSingle(event);
            }

            if (value == null) {
                if (!field.isOptional()) {
                    return null;
                }
                continue;
            }
            values.put(field.name(), value);
        }

        return this.definition.constructor().apply(new PacketConstructorRegistry.PacketValues(values));
    }

    @Override
    public boolean isSingle() {
        return true;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    @Override
    public Class<? extends PacketWrapper<?>> getReturnType() {
        return (Class) PacketWrapper.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        PacketTypeCommon type = this.packetTypeLiteral.getSingle();
        String packetType = (type != null ? type.getName() : "unknown");
        return String.format("a new %s packet", packetType);
    }
}