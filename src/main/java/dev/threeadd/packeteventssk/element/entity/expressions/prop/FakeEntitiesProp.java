package dev.threeadd.packeteventssk.element.entity.expressions.prop;

import ch.njol.skript.lang.SkriptParser;
import dev.threeadd.packeteventssk.util.expressions.CustomExpression;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.DefaultSyntaxInfos;
import org.skriptlang.skript.registration.SyntaxRegistry;

import java.util.List;

public class FakeEntitiesProp extends CustomExpression<WrapperEntity> {

  public static void register(SyntaxRegistry registry) {
    registry.register(
            SyntaxRegistry.EXPRESSION,
            DefaultSyntaxInfos.Expression.builder(FakeEntitiesProp.class, WrapperEntity.class)
                    .addPatterns(
                            "[visible] fake[ ]entities of %player%",
                            "%player%'s [visible] fake[ ]entities"
                    )
                    .build()
    );
  }

  public FakeEntitiesProp() {
    super(WrapperEntity.class, false);
  }

  @Override
  protected boolean initialize(SkriptParser.ParseResult parseResult) {
    return true;
  }

  @Override
  protected @Nullable List<WrapperEntity> getMany(Event currentEvent) {
    List<Player> players = getValues(0, Player.class, currentEvent);

    if (players.isEmpty()) {
      return null;
    }

    return EntityLib.getApi().getAllEntities().stream()
            .filter(entity -> players.stream().anyMatch(player -> entity.getViewers().contains(player.getUniqueId())))
            .distinct()
            .toList();
  }

  @Override
  public String toString(@Nullable Event event, boolean debug) {
    return "fake entities of player";
  }
}
