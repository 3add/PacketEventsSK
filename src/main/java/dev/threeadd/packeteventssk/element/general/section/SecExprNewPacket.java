package dev.threeadd.packeteventssk.element.general.section;

import ch.njol.skript.Skript;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.shanebeee.skr.Registration;
import com.github.shanebeee.skr.skript.SimpleEntryValidator;
import dev.threeadd.packeteventssk.api.util.field.ConstructionContext;
import dev.threeadd.packeteventssk.api.util.field.FieldAccessor;
import dev.threeadd.packeteventssk.api.util.field.FieldSchema;
import dev.threeadd.packeteventssk.element.general.field.packet.PacketFieldRegistry;
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
        for (FieldSchema<PacketTypeCommon, PacketWrapper<?>> def : PacketFieldRegistry.INSTANCE.getAllSchemas()) {
            for (FieldAccessor<PacketWrapper<?>, ?> field : def.accessors()) {
                builder.addOptionalEntry(field.name(), Object.class);

                for (String alias : field.aliases()) { // register aliases
                    builder.addOptionalEntry(alias, Object.class);
                }
            }
        }
        VALIDATOR = builder.build();

        StringBuilder description = new StringBuilder();
        description.append("Create a new packet from a packet type.\n\n");
        description.append("### Available Packets and their fields\n");

        Collection<FieldSchema<PacketTypeCommon, PacketWrapper<?>>> schemas = PacketFieldRegistry.INSTANCE.getAllSchemas();
        for (FieldSchema<PacketTypeCommon, PacketWrapper<?>> schema : schemas) {
            String fieldLines = schema.getReadableFields();
            if (!fieldLines.isEmpty()) {
                description.append("* **")
                        .append(schema.type().toString().toLowerCase(Locale.ENGLISH).replace("_", " "))
                        .append("** fields:\n")
                        .append(fieldLines)
                        .append("\n");
            }
        }

        reg.newSimpleExpression(SecExprNewPacket.class, (Class) PacketWrapper.class, "[a] [new] %*packettype%")
                .name("General - New Packet")
                .description(description.toString())
                // TODO example
                .since("1.0.0", "1.1.0 (changed to SectionExpression) and large changes")
                .register();
    }

    private final Map<String, Expression<?>> fieldExpressions = new HashMap<>();

    private PacketTypeCommon type;
    private FieldSchema<PacketTypeCommon, PacketWrapper<?>> schema;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions,
                        int matchedPattern,
                        Kleenean isDelayed,
                        SkriptParser.ParseResult parseResult,
                        @Nullable SectionNode sectionNode,
                        @Nullable List<TriggerItem> triggerItems) {

        if (!(expressions[0] instanceof Literal<?>)) { // shouldn't ever happen cause of our *
            Skript.error("The packet type needs to be a literal");
            return false;
        }

        Literal<PacketTypeCommon> packetTypeLiteral = (Literal<PacketTypeCommon>) expressions[0];

        this.type = packetTypeLiteral.getSingle();
        this.schema = PacketFieldRegistry.INSTANCE.getSchema(this.type);

        if (this.schema == null) {
            Skript.error("Packet creation for " + this.type.getName() + " is not currently supported. Consider creating/handling it through reflection.");
            return false; // can't return an empty packet so this expr can't be used
        }

        boolean hasRequiredFields = this.schema.accessors().stream().anyMatch(field -> !field.isOptional());
        if (sectionNode == null) {
            if (hasRequiredFields) {
                Skript.error("You must provide a section with the required fields to create a " + this.type.getName() + " packet.");
                return false;
            }

            return true;
        }

        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) {
            return false;
        }

        List<String> missingKeys = new ArrayList<>();
        for (FieldAccessor<PacketWrapper<?>, ?> field : this.schema.accessors()) {
            String key = field.name();
            Class<?> expectedType = field.expectedType();

            Expression<?> expr = container.getOptional(key, Object.class, false);

            if (expr == null) {
                for (String alias : field.aliases()) {
                    expr = container.getOptional(alias, Object.class, false);
                    if (expr != null) {
                        break;
                    }
                }
            }

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
                return false;
            }

            this.fieldExpressions.put(key, converted);
        }

        if (!missingKeys.isEmpty()) {
            String packetName = type.getName().toLowerCase(Locale.ENGLISH).replace("_", " ");
            Skript.error("Missing required entries for " + packetName + " packet: " + String.join(", ", missingKeys));
            return false;
        }

        return true;
    }

    @Override
    protected PacketWrapper<?> @Nullable [] get(Event event) {
        return new PacketWrapper[]{createPacket(event)};
    }

    private @Nullable PacketWrapper<?> createPacket(@NotNull Event event) {
        if (this.schema == null) return null;

        Map<String, Object> values = new HashMap<>();
        for (FieldAccessor<PacketWrapper<?>, ?> field : this.schema.accessors()) {
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

        return this.schema.constructor().apply(new ConstructionContext<>(this.type, this.schema.accessors(), values));
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
        String packetType = (this.type != null ? this.type.getName().toLowerCase(Locale.ENGLISH).replace("_", " ") : "unknown");
        return String.format("a new %s packet", packetType);
    }
}