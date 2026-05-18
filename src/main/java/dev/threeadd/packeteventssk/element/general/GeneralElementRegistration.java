package dev.threeadd.packeteventssk.element.general;

import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.util.registry.element.SkriptElementRegistration;
import dev.threeadd.packeteventssk.element.general.effect.EffCancelPacket;
import dev.threeadd.packeteventssk.element.general.effect.EffFetchSkin;
import dev.threeadd.packeteventssk.element.general.effect.EffSendOrReceivePacket;
import dev.threeadd.packeteventssk.element.general.event.EvtPacketSendOrReceive;
import dev.threeadd.packeteventssk.element.general.expression.ExprSkinFromValue;
import dev.threeadd.packeteventssk.element.general.expression.prop.*;
import dev.threeadd.packeteventssk.element.general.field.meta.MetaFieldRegistry;
import dev.threeadd.packeteventssk.element.general.field.packet.PacketFieldRegistry;
import dev.threeadd.packeteventssk.element.general.section.SecExprNewMeta;
import dev.threeadd.packeteventssk.element.general.section.SecExprNewPacket;

public class GeneralElementRegistration implements SkriptElementRegistration {

    @Override
    public String identifier() {
        return "general";
    }

    @Override
    public void load(Registration reg) {

        // property registries (registered before the expr/sec using it)
        PacketFieldRegistry.INSTANCE.registerAll();
        MetaFieldRegistry.INSTANCE.registerAll();

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
        ExprMetaField.register(reg);
        ExprPacketField.register(reg);
        ExprPacketPacketType.register(reg);
        ExprPlayerSkin.register(reg);

        ExprSkinFromValue.register(reg);
        // end expressions

        // start sections
        SecExprNewMeta.register(reg);
        SecExprNewPacket.register(reg);
        // end sections

        // types
        Types.register(reg);
    }
}