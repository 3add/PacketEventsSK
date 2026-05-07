package dev.threeadd.packeteventssk.element.entity.expressions.prop;

import ch.njol.skript.expressions.base.PropertyExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.shanebeee.skr.Registration;
import me.tofaa.entitylib.EntityLib;
import me.tofaa.entitylib.wrapper.WrapperEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ExprVisibleFakeEntities extends PropertyExpression<Player, WrapperEntity> {

    public static void register(Registration reg) {
        reg.newPropertyExpression(ExprVisibleFakeEntities.class, WrapperEntity.class, "[visible] fake[ ]entities", "player")
                // TODO docs
                .since("1.0.1")
                .register();
    }

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        setExpr((Expression<? extends Player>) expressions[0]);
        return true;
    }

    @Override
    protected WrapperEntity[] get(Event event, Player[] source) {
        Set<UUID> playerUUIDs = Arrays.stream(source)
                .map(Player::getUniqueId)
                .collect(Collectors.toSet());

        return EntityLib.getApi().getAllEntities().stream()
                .filter(entity -> entity.getViewers().stream().anyMatch(playerUUIDs::contains))
                .toArray(WrapperEntity[]::new);
    }

    @Override
    public boolean isSingle() {
        return false;
    }

    @Override
    public Class<? extends WrapperEntity> getReturnType() {
        return WrapperEntity.class;
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return "visible fake entities of " + getExpr().toString(event, debug);
    }
}
