package dev.threeadd.packeteventssk.element.entity.expressions.prop;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.SkriptParser;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.util.expressions.CustomPropertyExpression;

import java.util.UUID;

@SuppressWarnings("unused")
@Name("Fake Entity - Entity UUID")
@Description("Get the entity uuid used to represent this fake entity")
@Example("""
        command spawnfakeplayer:
            trigger:
                create new fake player entity at player for all players:
                    set fake skin of the fake entity to player's skin
                    add the packet entity uuid of the fake entity to {-uuid::*}

        command lookup <text>:
            trigger:
                loop {-uuid::*}:
                    if {-uuid::*} contains arg-1 parsed as uuid:
                        send "Found %fake entity with uuid loop-value%"
        """)
@Since("1.0.0")
public class UuidProp extends CustomPropertyExpression<WrapperEntity, UUID> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(UuidProp.class, UUID.class)
                        .addPatterns(
                                "[the] fake uuid of %fakeentity%",
                                "%fakeentity%'s fake uuid"
                        )
                        .build()
        );
    }

    @SuppressWarnings("unused")
    public UuidProp() {
        super(UUID.class, WrapperEntity.class, true);
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected @Nullable UUID getProperty(WrapperEntity input) {
        return input.getUuid();
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "uuid of fake entity";
    }
}
