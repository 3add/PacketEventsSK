package dev.threeadd.packeteventssk.element.entity.expressions;

import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.SkriptParser;
import dev.threeadd.packeteventssk.util.expressions.CustomExpression;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.ArrayList;
import java.util.List;

// TODO docs
@Since("1.0.1")
public class ExprFakeEntities extends CustomExpression<WrapperEntity> {

  public static void register(SyntaxRegistry registry) {
    registry.register(
            SyntaxRegistry.EXPRESSION,
            DefaultSyntaxInfos.Expression.builder(ExprFakeEntities.class, WrapperEntity.class)
                    .addPatterns("fake[ ]entities")
                    .build()
    );
  }

  public ExprFakeEntities() {
    super(WrapperEntity.class, false);
  }

  @Override
  protected boolean initialize(SkriptParser.ParseResult parseResult) {
    return true;
  }

  @Override
  protected @Nullable List<WrapperEntity> getMany(Event currentEvent) {
    return new ArrayList<>(EntityLib.getApi().getAllEntities());
  }

  @Override
  public String toString(@Nullable Event event, boolean debug) {
    return "fake entities";
  }
}
