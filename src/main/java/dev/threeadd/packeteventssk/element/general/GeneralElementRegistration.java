package dev.threeadd.packeteventssk.element.general;

import com.github.shanebeee.skr.Registration;
import dev.threeadd.packeteventssk.api.general.packet.definition.MainPacketDefinitions;
import dev.threeadd.packeteventssk.api.general.packet.definition.SkBeePacketDefinitions;
import dev.threeadd.packeteventssk.api.util.LogUtil;
import dev.threeadd.packeteventssk.api.util.registry.element.SkriptElementRegistration;
import dev.threeadd.packeteventssk.element.general.effect.EffCancelPacket;
import dev.threeadd.packeteventssk.element.general.effect.EffFetchSkin;
import dev.threeadd.packeteventssk.element.general.effect.EffSendOrReceivePacket;
import dev.threeadd.packeteventssk.element.general.event.EvtPacketSendOrReceive;
import dev.threeadd.packeteventssk.element.general.expressions.ExprSkinFromValue;
import dev.threeadd.packeteventssk.element.general.expressions.prop.ExprEntityId;
import dev.threeadd.packeteventssk.element.general.expressions.prop.ExprPacketField;
import dev.threeadd.packeteventssk.element.general.expressions.prop.ExprPacketPacketType;
import dev.threeadd.packeteventssk.element.general.expressions.prop.ExprPlayerSkin;
import dev.threeadd.packeteventssk.element.general.section.SecExprNewPacket;

public class GeneralElementRegistration implements SkriptElementRegistration {

    @Override
    public String identifier() {
        return "general";
    }

    @Override
    public void load(Registration reg) {

        // start definitions
        MainPacketDefinitions.register();

        try {
            Class<?> nbtApiClass = Class.forName("com.shanebeestudios.skbee.api.nbt.NBTApi");
            boolean enabled = (boolean) nbtApiClass.getMethod("isEnabled").invoke(null);
            if (enabled) {
                LogUtil.info("Hooked into SkBee NBT using NBT-API");
                SkBeePacketDefinitions.register();
            }
        } catch (ClassNotFoundException ignored) {
            LogUtil.error("SkBee not found, PacketEventsSK elements depending on NBT will not be registered");
        } catch (Exception e) {
            throw new IllegalStateException("Failed to hook into SkBee NBT", e);
        }
        // end definitions

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
        ExprPacketPacketType.register(reg);
        ExprPlayerSkin.register(reg);

        ExprSkinFromValue.register(reg);
        // end expressions

        // start sections
        SecExprNewPacket.register(reg);
        // end sections

        // types
        Types.register(reg);
    }
}