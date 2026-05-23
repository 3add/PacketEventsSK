package dev.threeadd.packeteventssk.element.entity.section;

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
import dev.threeadd.packeteventssk.api.util.field.ConstructionContext;
import dev.threeadd.packeteventssk.api.util.field.FieldAccessor;
import dev.threeadd.packeteventssk.api.util.field.FieldSchema;
import dev.threeadd.packeteventssk.element.entity.field.meta.MetaFieldRegistry;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.lang.entry.EntryContainer;
import org.skriptlang.skript.lang.entry.EntryValidator;

import java.util.*;
import java.util.stream.Collectors;

public class SecExprNewMeta extends SectionExpression<EntityMeta> {

    private static final Map<FieldSchema<EntityType, EntityMeta>, EntryValidator> VALIDATORS = new HashMap<>();

    public static void register(Registration reg) {

        for (FieldSchema<EntityType, EntityMeta> def : MetaFieldRegistry.INSTANCE.getAllSchemas()) {
            SimpleEntryValidator builder = SimpleEntryValidator.builder();
            for (FieldAccessor<EntityMeta, ?> field : def.accessors()) {
                builder.addOptionalEntry(field.name(), Object.class);

                for (String alias : field.aliases()) { // register aliases
                    builder.addOptionalEntry(alias, Object.class);
                }
            }
            VALIDATORS.put(def, builder.build());
        }

        StringBuilder description = new StringBuilder();
        description.append("Create a new meta from an entity type.\n\n");
        description.append("### Available Metas and their fields\n");

        List<FieldSchema<EntityType, EntityMeta>> schemas = new ArrayList<>(MetaFieldRegistry.INSTANCE.getAllSchemas());
        schemas.sort(Comparator.comparingInt(schema -> schema.accessors().size()));
        for (FieldSchema<EntityType, EntityMeta> schema : schemas) {

            Set<String> myFields = schema.accessors().stream()
                    .map(FieldAccessor::name)
                    .collect(Collectors.toSet());

            FieldSchema<EntityType, EntityMeta> parentSchema = null;
            int maxSubsetSize = -1;

            for (FieldSchema<EntityType, EntityMeta> other : schemas) {
                if (other == schema) continue;
                Set<String> otherFields = other.accessors().stream()
                        .map(FieldAccessor::name)
                        .collect(Collectors.toSet());

                if (myFields.containsAll(otherFields) && myFields.size() > otherFields.size()) {
                    if (otherFields.size() > maxSubsetSize) {
                        maxSubsetSize = otherFields.size();
                        parentSchema = other;
                    }
                }
            }

            Set<String> parentFieldNames = parentSchema != null
                    ? parentSchema.accessors().stream().map(FieldAccessor::name).collect(Collectors.toSet())
                    : Collections.emptySet();

            StringBuilder fieldLines = new StringBuilder();
            for (FieldAccessor<EntityMeta, ?> field : schema.accessors()) {
                if (!parentFieldNames.contains(field.name())) {
                    fieldLines.append("  - `").append(field.name());
                    if (field.aliases().length > 0) {
                        fieldLines.append(" (").append(String.join(", ", field.aliases())).append(")");
                    }

                    fieldLines.append("`\n");
                }
            }

            if (!fieldLines.isEmpty()) {
                String typeName = "fake " + schema.type().getName().getKey().toLowerCase(Locale.ENGLISH).replace("_", " ") + " entity";
                description.append("* **").append(typeName).append("** fields:\n");

                if (parentSchema != null) {
                    String parentName = "fake " + parentSchema.type().getName().getKey().toLowerCase(Locale.ENGLISH).replace("_", " ") + " entity";
                    description.append("  - *(Inherits all fields from **").append(parentName).append("**)*\n");
                }

                description.append(fieldLines).append("\n");
            }
        }

        reg.newSimpleExpression(SecExprNewMeta.class, EntityMeta.class, "[a] [new] [fake] %*entitydata% [entity] meta [data]")
                .name("General - Create Meta")
                .description(description.toString())
                .since("1.1.2")
                .examples("""
                        set {_meta} to text display meta data:
                            display content: " "
                            display text shadowed state: true
                            display billboard: center
                            display scale: vector(1.2,1.2,1.2)
                            display translation: vector(-0.03,0.65,0)
                            display background color: rgb(random integer between 0 and 255, random integer between 0 and 255, random integer between 0 and 255)
                            display view range: 1
                            display transform interpolation duration: 2 ticks
                            display interpolation delay: 0 ticks

                        set {_entity} to a new fake text display entity:
                            viewers: all players
                            location: location of player ~ vector(0,0.5,0)
                            meta: {_meta}
                        """)
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
                Skript.error("You must provide a section with the required fields to create a " + bukkitType.toString().toLowerCase(Locale.ENGLISH).replace("_", " ") + " meta.");
                return false;
            }
            return true;
        }

        EntryValidator validator = VALIDATORS.get(this.schema);
        if (validator == null) {
            Skript.error("No validator found for " + bukkitType.toString().toLowerCase(Locale.ENGLISH).replace("_", " ") + " meta.");
            return false;
        }

        EntryContainer container = validator.validate(sectionNode);
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
    public String toString(@Nullable Event event, boolean debug) {
        String entityData = (this.type != null ? this.type.getName().getKey().toLowerCase(Locale.ENGLISH).replace("_", " ") : "unknown");
        return String.format("a new %s meta", entityData);
    }
}