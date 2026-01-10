package threeadd.packetEventsSK.element.general.api;

import ch.njol.skript.Skript;
import ch.njol.skript.classes.Changer;
import ch.njol.skript.expressions.base.EventValueExpression;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import org.skriptlang.skript.registration.SyntaxRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import threeadd.packetEventsSK.PacketEventsSK;
import threeadd.packetEventsSK.util.expressions.CustomPropertyExpression;
import threeadd.packetEventsSK.element.general.structures.StructPacketEvent.PacketEventParserData;
import threeadd.packetEventsSK.element.general.structures.StructPacketEvent.ProcessWay;

import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;

@SuppressWarnings("unused")
public abstract class PacketPropertyExpression<Wrapper extends PacketWrapper<?>, Return> extends CustomPropertyExpression<Wrapper, Return> {

    private static final Logger log = LoggerFactory.getLogger(PacketPropertyExpression.class);
    private final EnumSet<Changer.ChangeMode> changeModes;
    private final PacketTypeCommon packetType;
    private final boolean isThreadSafe;
    private final Class<? extends PacketPropertyExpression<?, ?>> alternative;

    @SuppressWarnings({"unused", "unchecked"})
    public PacketPropertyExpression(Class<Return> returnType, PacketTypeCommon packetType,
                                    boolean isSingle, boolean isThreadSafe,
                                    @Nullable Class<? extends PacketPropertyExpression<?, ?>> alternative,
                                    Changer.ChangeMode... changeModes) {
        super(returnType, (Class<Wrapper>) (Class<?>) PacketWrapper.class, isSingle);
        this.packetType = packetType;
        this.isThreadSafe = isThreadSafe;
        this.alternative = alternative;
        this.changeModes = EnumSet.copyOf(Arrays.asList(changeModes));
    }

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {

        ProcessWay processWay = getParser().getData(PacketEventParserData.class).getProcessWay();
        if (processWay != null && !processWay.equals(ProcessWay.SYNC) && !isThreadSafe) {

            if (alternative == null) {
                Skript.error("This property is not thread safe, you can't retrieve/alter it from a netty/async processed event, there is no alternative.");
                return false;
            }

            @SuppressWarnings("UnstableApiUsage")
            String pattern = PacketEventsSK.getLoader().getAddon().syntaxRegistry().syntaxes(SyntaxRegistry.EXPRESSION).stream()
                    .filter(expression -> expression.type().equals(alternative))
                    .findAny()
                    .orElseThrow(() -> new IllegalStateException("Couldn't find expression element for loaded expression"))
                    .patterns().stream()
                    .findFirst()
                    .orElseThrow(() -> new IllegalStateException("Couldn't find any patterns for the alternative expression."));

            Skript.error("This property is not thread safe, you can't retrieve/alter it from a netty/async processed event, here's the alternative: " + pattern);
            return false;
        }

        return true;
    }

    @SuppressWarnings("UnstableApiUsage")
    private boolean isInvalidWrapper(PacketWrapper<?> wrapper) {
        if (wrapper.getPacketTypeData().getPacketType() == null
                || !wrapper.getPacketTypeData().getPacketType().equals(packetType)) {

            log.warn("{} doesn't have a {} property", wrapper.getPacketTypeData().getPacketType(), returnType.getSimpleName());
            return true;
        }

        return false;
    }

    @Override
    protected final List<Return> getProperties(Wrapper input) {
        if (isInvalidWrapper(input)) return null;
        return getMany(input);
    }

    @Nullable
    protected List<Return> getMany(Wrapper wrapper) {
        return null;
    }

    @Override
    protected final Return getProperty(Wrapper input) {
        if (isInvalidWrapper(input)) return null;
        return get(input);
    }

    @Nullable
    protected Return get(Wrapper wrapper) {
        return null;
    }

    @Override
    public Class<?>[] acceptChange(Changer.ChangeMode mode) {
        if (changeModes.contains(mode))
            return new Class[] { returnType };

        return null;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final void change(Event event, Object[] delta, Changer.ChangeMode mode) {
        Expression<PacketWrapper<?>> input = getExpressionOrNull(0, Expression.class);
        if (input == null) return;

        ProcessWay processWay = getParser().getData(PacketEventParserData.class).getProcessWay();
        if (input instanceof EventValueExpression<PacketWrapper<?>>) {
            if (processWay != null && !processWay.equals(ProcessWay.SYNC)) {
                Skript.error("Cannot alter the on a non-sync thread");
                return;
            }
        }

        PacketWrapper<?> rawValue = input.getSingle(event);
        if (rawValue == null) return;
        if (isInvalidWrapper(rawValue)) return;

        Wrapper value = (Wrapper) rawValue;
        change(value, mode, delta);

        if (event instanceof PacketTriggerEvent triggerEvent)
            triggerEvent.getEvent().markForReEncode(true);
    }

    protected abstract void change(Wrapper wrapper, Changer.ChangeMode mode, Object[] delta);
}
