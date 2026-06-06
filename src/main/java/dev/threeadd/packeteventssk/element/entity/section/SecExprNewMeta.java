package dev.threeadd.packeteventssk.element.entity.section;

import ch.njol.skript.Skript;
import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.field.BaseFieldRegistry;
import dev.threeadd.packeteventssk.api.field.FieldSchema;
import dev.threeadd.packeteventssk.api.field.doc.FieldDescriptionBuilder;
import dev.threeadd.packeteventssk.api.field.skript.AbstractSecExprNew;
import dev.threeadd.packeteventssk.element.entity.field.meta.MetaFieldRegistry;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.meta.EntityMeta;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SecExprNewMeta extends AbstractSecExprNew<EntityType, EntityMeta> {

    public static void register(Registration reg) {
        buildValidators(MetaFieldRegistry.INSTANCE);

        List<FieldSchema<EntityType, EntityMeta>> schemas = new ArrayList<>(MetaFieldRegistry.INSTANCE.getAllSchemas());
        schemas.sort(java.util.Comparator.comparingInt(s -> ((FieldSchema<?, ?>) s).accessors().size()));

        String description = """
                Create a new entity meta from an entity type.
                ### Available Metas and their fields
                """
                + FieldDescriptionBuilder.buildHierarchical(
                schemas,
                schema -> "fake " + schema
                        .type().getName().getKey().toLowerCase(Locale.ENGLISH).replace("_", " ") + " entity",
                false); // showRequired = false (all meta fields are optional)

        reg.newSimpleExpression(SecExprNewMeta.class, EntityMeta.class,
                        "[a] new [fake] %entitydata% [entity] meta [data]")
                .name("General - Create Meta")
                .description(description)
                .since("1.1.2", "1.2.0 (dynamic type support)")
                .examples("""
                        set {_meta} to text display meta data:
                            display content: " "
                            display text shadowed state: true
                            display billboard: center
                            display scale: vector(1.2,1.2,1.2)
                            display translation: vector(-0.03,0.65,0)
                            display background color: rgb(0, 128, 255)
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

    @Override
    protected BaseFieldRegistry<EntityType, EntityMeta> getRegistry() {
        return MetaFieldRegistry.INSTANCE;
    }

    @Override
    protected @Nullable EntityType resolveType(Expression<?> typeExpr, Event event) {
        EntityData<?> data = (EntityData<?>) typeExpr.getSingle(event);
        if (data == null) return null;
        org.bukkit.entity.EntityType bukkit = EntityUtils.toBukkitEntityType(data);
        if (bukkit == null) return null;
        return SpigotConversionUtil.fromBukkitEntityType(bukkit);
    }

    /**
     * Rejects abstract meta schemas (those with a null constructor) at parse time.
     * This is meta-specific behavior, not shared by the other registries.
     */
    @Override
    protected boolean validateSchema(FieldSchema<EntityType, EntityMeta> schema, EntityType type) {
        if (schema.constructor() == null) {
            Skript.error("Meta creation for " + formatTypeName(type) + " is abstract and cannot be instantiated directly. Use a concrete entity type instead.");
            return false;
        }
        return true;
    }

    @Override
    public Class<? extends EntityMeta> getReturnType() {
        return EntityMeta.class;
    }

    @Override
    protected String formatTypeName(@Nullable EntityType type) {
        return type != null ? type.getName().getKey().toLowerCase(Locale.ENGLISH).replace("_", " ") : "unknown";
    }

    @Override
    protected String categoryLabel() {
        return "meta";
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "a new " + formatTypeName(resolveType(this.typeExpr, event)) + " meta";
    }
}