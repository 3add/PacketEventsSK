package dev.threeadd.packeteventssk.element.general;

import dev.threeadd.packeteventssk.element.general.expressions.prop.client.*;
import dev.threeadd.packeteventssk.element.general.expressions.prop.server.*;
import org.skriptlang.skript.bukkit.lang.eventvalue.EventValueRegistry;
import org.skriptlang.skript.registration.SyntaxRegistry;
import dev.threeadd.packeteventssk.element.general.effect.EffCancelPacket;
import dev.threeadd.packeteventssk.element.general.effect.EffFetchSkin;
import dev.threeadd.packeteventssk.element.general.effect.EffSendOrReceivePacket;
import dev.threeadd.packeteventssk.element.general.expressions.ExprLastPacket;
import dev.threeadd.packeteventssk.element.general.expressions.ExprSkin;
import dev.threeadd.packeteventssk.element.general.expressions.prop.EntityIdProp;
import dev.threeadd.packeteventssk.element.general.expressions.prop.PlayerSkinProp;
import dev.threeadd.packeteventssk.element.general.section.CreatePacketSec;
import dev.threeadd.packeteventssk.element.general.structures.PacketEventStruct;

public class GeneralModule {

    public static void registerAll(SyntaxRegistry registry, EventValueRegistry eventValueRegistry) {

        CreatePacketSec.register(registry);

        InteractEntityClickTypeProp.register(registry);
        InteractEntityEntityIdProp.register(registry);
        InteractEntityEntityProp.register(registry);
        PlayerDiggingBlockDataProp.register(registry);
        PlayerDiggingDigActionProp.register(registry);

        AckBlockChangeBlockDataProp.register(registry);
        DestroyEntitiesEntitiesProp.register(registry);
        DestroyEntitiesEntityIdsProp.register(registry);
        EntityMetaDataEntityMetadataProp.register(registry);
        GameTestHighlightPosAbsBlockProp.register(registry);
        TabCompleteTabCompletionsProp.registry(registry);

        EntityIdProp.register(registry);
        PlayerSkinProp.register(registry);

        ExprSkin.register(registry);
        ExprLastPacket.register(registry);

        EffCancelPacket.register(registry);
        EffFetchSkin.register(registry);
        EffSendOrReceivePacket.register(registry);

        PacketEventStruct.register(registry);
        PacketEventStruct.registerEventValues(eventValueRegistry);

        Types.register();
    }
}
