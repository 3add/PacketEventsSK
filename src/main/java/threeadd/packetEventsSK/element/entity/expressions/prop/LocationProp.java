package threeadd.packetEventsSK.element.entity.expressions.prop;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.SkriptParser;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.bukkit.util.Vector;
import org.skriptlang.skript.registration.SyntaxInfo;
import threeadd.packetEventsSK.util.ConversionUtil;
import threeadd.packetEventsSK.util.expressions.CustomPropertyExpression;
import org.skriptlang.skript.registration.SyntaxRegistry;

@SuppressWarnings("unused")
@Name("Fake Entity - Entity Location")
@Description("Used to get the entity location as a vector, this is a vector and not a regular location.")
@Example("""
        command tp <integer>:
            trigger:
                set {_entity} to fake entity with id arg-1
                if {_entity} is not set:
                    send "Couldn't find that entity"
                    stop
        
                set {_locVector} to fake entity location of {_entity}
        
                #https://skripthub.net/docs/?id=10168
                set {_loc} to {_locVector} to location in world of player
        
                teleport player to {_loc}
        """)
@Since("1.0.0")
public class LocationProp extends CustomPropertyExpression<WrapperEntity, Vector> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
               SyntaxInfo.Expression.builder(LocationProp.class, Vector.class)
                        .addPatterns(
                                "[the] fake entity location of %fakeentity%",
                                "%fakeentity%'s fake entity location"
                        )
                        .build()
        );
    }

    public LocationProp() {
        super(Vector.class, WrapperEntity.class, true);
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected Vector getOne(Event event) {
        WrapperEntity entity = getValueOrNull(0, WrapperEntity.class, event);
        if (entity == null) return null;

        return ConversionUtil.toBukkitVector(entity.getLocation());
    }
}
