package dev.threeadd.packeteventssk.element.general;

import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.element.general.effect.EffCancelPacket;
import dev.threeadd.packeteventssk.element.general.effect.EffFetchSkin;
import dev.threeadd.packeteventssk.element.general.effect.EffSendOrReceivePacket;
import dev.threeadd.packeteventssk.element.general.event.EvtPacketSendOrReceive;
import dev.threeadd.packeteventssk.element.general.expressions.ExprSkinFromValue;
import dev.threeadd.packeteventssk.element.general.expressions.prop.ExprEntityId;
import dev.threeadd.packeteventssk.element.general.expressions.prop.ExprPacketField;
import dev.threeadd.packeteventssk.element.general.expressions.prop.ExprPlayerSkin;
import dev.threeadd.packeteventssk.element.general.section.SecCreatePacket;
import dev.threeadd.packeteventssk.api.util.registry.element.SkriptElementRegistration;

public class GeneralElementRegistration implements SkriptElementRegistration {

    @Override
    public String identifier() {
        return "general";
    }

    @Override
    public void load(Registration reg) {

        // start effects
        EffCancelPacket.register(reg);
        EffFetchSkin.register(reg);
        EffSendOrReceivePacket.register(reg);
        // end effects

        // start events
        EvtPacketSendOrReceive.register(reg);
        // end events

        // start expressions
        ExprEntityId.register(reg);
        ExprPacketField.register(reg);
        ExprPlayerSkin.register(reg);

        ExprSkinFromValue.register(reg);
        // end expressions

        // start sections
        SecCreatePacket.register(reg);
        // end sections

        // types
        Types.register(reg);
    }
}