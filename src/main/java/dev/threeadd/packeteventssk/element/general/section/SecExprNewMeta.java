package dev.threeadd.packeteventssk.element.general.section;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.config.SectionNode;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.expressions.base.SectionExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.Literal;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.TriggerItem;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.shanebeee.skr.Registration;
import com.github.shanebeee.skr.skript.SimpleEntryValidator;
import dev.threeadd.packeteventssk.api.util.field.FieldAccessor;
import dev.threeadd.packeteventssk.api.util.field.FieldSchema;
import dev.threeadd.packeteventssk.api.util.field.ConstructionContext;
import dev.threeadd.packeteventssk.element.general.field.meta.MetaFieldRegistry;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.*;

public class SecExprNewMeta extends SectionExpression<EntityMeta> {

    private static EntryValidator VALIDATOR;

    public static void register(Registration reg) {

        SimpleEntryValidator builder = SimpleEntryValidator.builder();
        for (FieldSchema<EntityType, EntityMeta> def : MetaFieldRegistry.INSTANCE.getAllSchemas()) {
            for (FieldAccessor<EntityMeta, ?> field : def.accessors()) {
                builder.addOptionalEntry(field.name(), Object.class);

                for (String alias : field.aliases()) { // register aliases
                    builder.addOptionalEntry(alias, Object.class);
                }
            }
        }
        VALIDATOR = builder.build();

        StringBuilder description = new StringBuilder();
        description.append("Create a new meta from an entity type.\n\n");
        description.append("### Available Metas and their fields\n");

        Collection<FieldSchema<EntityType, EntityMeta>> schemas = MetaFieldRegistry.INSTANCE.getAllSchemas();
        for (FieldSchema<EntityType, EntityMeta> schema : schemas) {
            String fieldLines = schema.getReadableFields();
            if (!fieldLines.isEmpty()) {
                description.append("* **")
                        .append(schema.type().toString().toLowerCase(Locale.ENGLISH).replace("_", " "))
                        .append("** fields:\n")
                        .append(fieldLines)
                        .append("\n");
            }
        }

        reg.newSimpleExpression(SecExprNewMeta.class, EntityMeta.class, "[a] [new] %*entitydata% [entity] meta [data]")
                .name("General - Create Meta")
                .description(description.toString())
                .since("1.1.2")
                .register();
    }

    private final Map<String, Expression<?>> fieldExpressions = new HashMap<>();

    private EntityType type;
    private FieldSchema<EntityType, EntityMeta> schema;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions,
                        int matchedPattern,
                        Kleenean isDelayed,
                        SkriptParser.ParseResult parseResult,
                        @Nullable SectionNode sectionNode,
                        @Nullable List<TriggerItem> triggerItems) {

        if (!(expressions[0] instanceof Literal<?>)) { // shouldn't ever happen cause of our *
            Skript.error("The entity type needs to be a literal");
            return false;
        }

        Literal<EntityData<?>> entityDataLiteral = (Literal<EntityData<?>>) expressions[0];

        EntityData<?> skriptType = entityDataLiteral.getSingle();
        if (skriptType == null) return false;

        org.bukkit.entity.EntityType bukkitType = EntityUtils.toBukkitEntityType(skriptType);
        this.type = SpigotConversionUtil.fromBukkitEntityType(bukkitType);

        this.schema = MetaFieldRegistry.INSTANCE.getSchema(this.type);

        if (this.schema == null) {
            Skript.warning("Meta creation for " + bukkitType.toString().toLowerCase(Locale.ENGLISH).replace("_", " ") + " is not currently supported. Consider creating/handling it through reflection.");
            return true;
        }

        if (this.schema.constructor() == null) {
            Skript.error("Meta creation for " + bukkitType.toString().toLowerCase(Locale.ENGLISH).replace("_", " ") + " is abstract and thus can't be created. Use an extending entity instead.");
            return false;
        }
        
        boolean hasRequiredFields = this.schema.accessors().stream().anyMatch(field -> !field.isOptional());
        if (sectionNode == null) {
            if (hasRequiredFields) {
                Skript.error("You must provide a section with the required fields to create a " + this.type.getName() + " meta.");
                return false;
            }
            return true;
        }

        EntryContainer container = VALIDATOR.validate(sectionNode);
        if (container == null) {
            return false;
        }

        List<String> missingKeys = new ArrayList<>();
        for (FieldAccessor<EntityMeta, ?> field : this.schema.accessors()) {
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
            String metaName = this.type.getName().getKey().toLowerCase(Locale.ENGLISH).replace("_", " ");
            Skript.error("Missing required entries for " + metaName + " meta: " + String.join(", ", missingKeys));
            return false;
        }

        return true;
    }

    @Override
    protected EntityMeta @Nullable [] get(Event event) {
        return new EntityMeta[]{createMeta(event)};
    }

    private @Nullable EntityMeta createMeta(@NotNull Event event) {
        if (this.schema == null) return null;

        Map<String, Object> values = new HashMap<>();
        for (FieldAccessor<EntityMeta, ?> field : this.schema.accessors()) {
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

    @Override
    public Class<? extends EntityMeta> getReturnType() {
        return EntityMeta.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {;
        String entityData = (this.type != null ? this.type.getName().getKey().toLowerCase(Locale.ENGLISH).replace("_", " ") : "unknown");
        return String.format("a new %s meta", entityData);
    }
}