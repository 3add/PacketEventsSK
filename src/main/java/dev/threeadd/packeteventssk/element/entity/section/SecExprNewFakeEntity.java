package dev.threeadd.packeteventssk.element.entity.section;

import ch.njol.skript.bukkitutil.EntityUtils;
import ch.njol.skript.entity.EntityData;
import ch.njol.skript.lang.Expression;
import com.github.retrooper.packetevents.protocol.entity.type.EntityType;
import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.field.BaseFieldRegistry;
import dev.threeadd.packeteventssk.api.field.FieldSchema;
import dev.threeadd.packeteventssk.api.field.doc.FieldDescriptionBuilder;
import dev.threeadd.packeteventssk.api.field.skript.AbstractSecExprNew;
import dev.threeadd.packeteventssk.element.entity.field.entity.FakeEntityFieldRegistry;
import io.github.retrooper.packetevents.util.SpigotConversionUtil;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class SecExprNewFakeEntity extends AbstractSecExprNew<EntityType, WrapperEntity> {

    public static void register(Registration reg) {
        buildValidators(FakeEntityFieldRegistry.INSTANCE);

        List<FieldSchema<EntityType, WrapperEntity>> schemas = new ArrayList<>(FakeEntityFieldRegistry.INSTANCE.getAllSchemas());
        schemas.sort(java.util.Comparator.comparingInt(s -> ((dev.threeadd.packeteventssk.api.field.FieldSchema<?, ?>) s).accessors().size()));

        String description = """
                Create a new fake entity from an entity type.
                ### Available Entities and their fields
                """
                + FieldDescriptionBuilder.buildHierarchical(
                schemas,
                schema -> "fake " + schema
                        .type().getName().getKey().toLowerCase(Locale.ENGLISH).replace("_", " ") + " entity",
                true);

        reg.newSimpleExpression(SecExprNewFakeEntity.class, WrapperEntity.class,
                        "[a] new fake %entitydata% entity")
                .name("Fake Entity - Create Fake Entity")
                .description(description)
                .examples("""
                        on load:
                            set {-notchSkin} to skin of player named "notch"
                        
                        command test5:
                            trigger:
                                set {_player} to a new fake player entity:
                                    name: "test"
                                    skin: skin of player
                                    location: location of player
                                    viewers: players
                        
                                wait 1 second
                                set fake entity skin of {_player} to {-notchSkin}
                        """)
                .since("1.0.0", "1.1.2 (changed to SectionExpression)",
                        "1.2.0 (dynamic type support)")
                .register();
    }

    @Override
    protected BaseFieldRegistry<EntityType, WrapperEntity> getRegistry() {
        return FakeEntityFieldRegistry.INSTANCE;
    }

    @Override
    protected @Nullable EntityType resolveType(Expression<?> typeExpr, Event event) {
        EntityData<?> data = (EntityData<?>) typeExpr.getSingle(event);
        if (data == null) return null;
        org.bukkit.entity.EntityType bukkit = EntityUtils.toBukkitEntityType(data);
        if (bukkit == null) return null;
        return SpigotConversionUtil.fromBukkitEntityType(bukkit);
    }

    @Override
    public Class<? extends WrapperEntity> getReturnType() {
        return WrapperEntity.class;
    }

    @Override
    protected String formatTypeName(@Nullable EntityType type) {
        return type != null ? type.getName().getKey().toLowerCase(Locale.ENGLISH).replace("_", " ") : "unknown";
    }

    @Override
    protected String categoryLabel() {
        return "fake entity";
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "a new fake " + formatTypeName(resolveType(this.typeExpr, event)) + " entity";
    }
}