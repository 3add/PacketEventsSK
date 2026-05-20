package dev.threeadd.packeteventssk.element.general.condition;

import ch.njol.skript.conditions.base.PropertyCondition;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.protocol.PacketSide;
import com.github.retrooper.packetevents.protocol.packettype.PacketTypeCommon;
import com.github.shanebeee.skr.Registration;

public class CondPacketTypeIsBound extends PropertyCondition<PacketTypeCommon> {

    public static void register(Registration reg) {
        reg.newPropertyCondition(CondPacketTypeIsBound.class, PropertyType.BE, "(:(client|server))[ ]bound", "packettypes")
                .register();
    }

    private boolean isClientbound = false;

    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {

        if (!super.init(expressions, matchedPattern, isDelayed, parseResult)) {
            return false;
        }

        isClientbound = parseResult.hasTag("client");

        return true;
    }

    @Override
    public boolean check(PacketTypeCommon value) {
        // bad naming from packetevents
        if (isClientbound) return value.getSide() == PacketSide.SERVER;
        else return value.getSide() == PacketSide.CLIENT;
    }

    @Override
    protected String getPropertyName() {
        return isClientbound ? "clientbound" : "serverbound";
    }
}
