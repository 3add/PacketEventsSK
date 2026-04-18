package threeadd.packetEventsSK.element.general;

import org.skriptlang.skript.registration.SyntaxRegistry;
import threeadd.packetEventsSK.element.general.effect.EffCancelPacket;
import threeadd.packetEventsSK.element.general.effect.EffFetchSkin;
import threeadd.packetEventsSK.element.general.effect.EffSendOrReceivePacket;
import threeadd.packetEventsSK.element.general.expressions.ExprSkin;
import threeadd.packetEventsSK.element.general.expressions.prop.EntityIdProp;
import threeadd.packetEventsSK.element.general.expressions.prop.PlayerSkinProp;
import threeadd.packetEventsSK.element.general.expressions.prop.client.*;
import threeadd.packetEventsSK.element.general.expressions.prop.server.*;
import threeadd.packetEventsSK.element.general.structures.PacketEventStruct;

public class GeneralModule {

    private static final String BASE_PACKAGE = "threeadd.packetEventsSK.element.general";

    // These classes register syntax through static initializers and must be initialized once.
    private static final String[] STATIC_REGISTRATION_CLASSES = {
            BASE_PACKAGE + ".Types",
            BASE_PACKAGE + ".section.CreatePacketSec",
    };

    public static void registerAll(SyntaxRegistry registry) {

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

        EffCancelPacket.register(registry);
        EffFetchSkin.register(registry);
        EffSendOrReceivePacket.register(registry);

        PacketEventStruct.register(registry);

        for (String className : STATIC_REGISTRATION_CLASSES) {
            forceInitialize(className);
        }
    }

    private static void forceInitialize(String className) {
        try {
            Class.forName(className, true, GeneralModule.class.getClassLoader());
        } catch (ClassNotFoundException exception) {
            throw new IllegalStateException("Unable to initialize general syntax class: " + className, exception);
        }
    }
}
