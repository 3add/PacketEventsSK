package dev.threeadd.packeteventssk.element.general.section;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.config.SectionNode;
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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class SecCreatePacket extends Section {

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {

        SimpleEntryValidator builder = SimpleEntryValidator.builder();
        for (PacketConstructorRegistry.PacketDefinition def : PacketConstructorRegistry.getAllDefinitions()) {
            for (PacketConstructorRegistry.PacketField field : def.fields()) {
                builder.addOptionalEntry(field.name(), Object.class);
            }
        }
        VALIDATOR = builder.build();

        reg.newSection(SecCreatePacket.class, VALIDATOR, "(make|create) [a] [new] %packettype% [and store (it|the result) in %-objects%]")
                .name("General - Create Packet")
                .description("Create a new packet from a packet type. This section is a data block, not an execution block.")
                .since("1.0.0")
                .register();
    }

    private Literal<PacketTypeCommon> packetTypeLiteral;
    private @Nullable Expression<Object> storeExpr;

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

        this.packetTypeLiteral = (Literal<PacketTypeCommon>) expressions[0];

        if (expressions[1] != null) {
            this.storeExpr = (Expression<Object>) expressions[1];
            if (!Changer.ChangerUtils.acceptsChange(this.storeExpr, Changer.ChangeMode.SET, PacketWrapper.class)) {
                Skript.error("Cannot set to store a packet.");
                return false;
            }
        }

        PacketTypeCommon type = packetTypeLiteral.getSingle();
        this.definition = PacketConstructorRegistry.getDefinition(type);

        if (this.definition == null) {
            Skript.error("Packet creation for " + type.getName() + " is not currently supported.");
            return false;
        }

        if (sectionNode == null) {
            Skript.error("You must provide a section with the required fields to create a " + type.getName() + " packet!");
            return false;
        }

        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) {
            return false;
        }

        List<String> missingKeys = new ArrayList<>();
        boolean hasTypeError = false;

        for (PacketConstructorRegistry.PacketField field : definition.fields()) {
            String key = field.name();
            Class<?> expectedType = field.expectedType();

            Expression<?> expr = container.getOptional(key, Object.class, false);

            // Handle missing fields
            if (expr == null) {
                if (!field.isOptional()) {
                    missingKeys.add(key); // Only flag as missing if it's strictly required
                }
                continue;
            }

            // Handle type conversion
            if (expr instanceof UnparsedLiteral literal) {
                expr = literal.getConvertedExpression(expectedType);
                if (expr == null) {
                    Skript.error("The value for '" + key + "' must be of type " + expectedType.getSimpleName() + ".");
                    hasTypeError = true;
                    continue;
                }
            }

            fieldExpressions.put(key, expr);
        }

        if (!missingKeys.isEmpty()) {
            String packetName = type.getName().toLowerCase(Locale.ENGLISH).replace("_", " ");
            Skript.error("Missing required entries for " + packetName + " packet: " + String.join(", ", missingKeys));
            return false;
        }

        if (hasTypeError) {
            return false;
        }

        return true;
    }

    @Override
    protected TriggerItem walk(@NotNull Event event) {
        PacketWrapper<?> packet = createPacket(event);

        if (packet != null && this.storeExpr != null) {
            this.storeExpr.change(event, new Object[]{packet}, Changer.ChangeMode.SET);
        }

        return getNext();
    }

    private @Nullable PacketWrapper<?> createPacket(@NotNull Event event) {
        if (definition == null) return null;

        Map<String, Object> values = new HashMap<>();

        for (PacketConstructorRegistry.PacketField field : definition.fields()) {
            Expression<?> expr = fieldExpressions.get(field.name());

            if (expr == null) {
                continue; // Skip optional missing fields entirely
            }

            Object value = expr.getSingle(event);

            if (value == null) {
                if (!field.isOptional()) {
                    return null; // Required value returned null at runtime
                }
                continue; // Treat null as missing for optional fields
            }
            values.put(field.name(), value);
        }

        return definition.constructor().apply(new PacketConstructorRegistry.PacketValues(values));
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        if (packetTypeLiteral == null) return "create packet section";
        PacketTypeCommon type = this.packetTypeLiteral.getSingle();
        String packetType = (type != null ? type.getName() : "unknown");
        return String.format("create %s packet", packetType);
    }
}