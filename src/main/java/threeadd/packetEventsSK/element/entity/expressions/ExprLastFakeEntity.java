package threeadd.packetEventsSK.element.entity.expressions;

import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Examples;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.skript.lang.util.SimpleExpression;
import ch.njol.util.Kleenean;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxInfo;
import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.entity.sections.CreateFakeEntitySec;

@Name("Fake Entity - Last Fake Entity")
@Description("Retrieves the last fake entity created in the current event, this only works inside a create fake entity section")
@Examples(
        """
        command test:
            trigger:
                spawn a new fake text display entity at player for players:
                    set fake display content of fake entity to "<RAINBOW>HEYY IM FOLLLOWING YOU"
                    set fake display billboard of fake entity to center
         
                    set fake display teleport interpolation duration of fake entity to 1 second
         
                    loop 5 times:
                        teleport fake entity the fake entity to player
                        wait 1 second
         
                    kill fake entity the fake entity
        """
)
@Since("1.0.0")
public class ExprLastFakeEntity extends SimpleExpression<WrapperEntity> {

    public static void register(SyntaxRegistry registry) {
        registry.register(
                SyntaxRegistry.EXPRESSION,
                SyntaxInfo.Expression.builder(ExprLastFakeEntity.class, WrapperEntity.class)
                        .supplier(ExprLastFakeEntity::new)
                        .addPatterns("[the] [last] fake entity")
                        .build()
        );
    }

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern,
                        Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        return true;
    }

    @Override
    protected WrapperEntity @Nullable [] get(@NotNull Event event) {
        WrapperEntity entity = CreateFakeEntitySec.getLastEntity(event);
        if (entity == null) return null;
        return new WrapperEntity[]{entity};
    }

    @Override
    public boolean isSingle() { return true; }

    @Override
    public @NotNull Class<? extends WrapperEntity> getReturnType() { return WrapperEntity.class; }

    @Override
    public @NotNull String toString(@Nullable Event event, boolean debug) { return "the last fake entity"; }
}