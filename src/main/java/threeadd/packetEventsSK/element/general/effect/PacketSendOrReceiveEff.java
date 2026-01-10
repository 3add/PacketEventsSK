package threeadd.packetEventsSK.element.general.effect;

import ch.njol.skript.Skript;
import ch.njol.skript.doc.Description;
import ch.njol.skript.doc.Example;
import ch.njol.skript.doc.Name;
import ch.njol.skript.doc.Since;
import ch.njol.skript.lang.SkriptParser;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;
import threeadd.packetEventsSK.util.effect.CustomEffect;

import java.util.List;

@SuppressWarnings("unused")
@Name("General - Send/Receive Packet")
@Description("Used to force the server to send or receive a packet [silently].")
@Example("""
        command killTargetForMe:
            trigger:
                create a new destroy entities send packet:
                    add target entity of player to packet entities of the packet
                    send packet the packet to the player
        """)
@Since("1.0.0")
public class PacketSendOrReceiveEff extends CustomEffect {

    static {
        Skript.registerEffect(PacketSendOrReceiveEff.class,
                "[:(silently|default)] :(send|receive) packet %packets% (to|from) %player%");
    }

    private boolean isSilent = false;
    private boolean isSend = true;

    @Override
    protected boolean initialize(SkriptParser.ParseResult parseResult) {

        if (parseResult.hasTag("receive"))
            isSend = false;

        if (parseResult.hasTag("silently"))
            isSilent = true;

        return true;
    }

    @SuppressWarnings("unchecked")
    @Override
    protected void execute(Event event) {
        List<PacketWrapper<?>> packets = getValuesOrNull(0, (Class<PacketWrapper<?>>) (Class<?>) PacketWrapper.class, event);
        List<Player> targets = getValuesOrNull(1, Player.class, event);

        if (packets == null || targets == null) return;

        for (Player target : targets) {
            User user = PacketEvents.getAPI().getPlayerManager().getUser(target);

            for (PacketWrapper<?> packet : packets) {
                handlePacket(user, packet, isSend, isSilent);
            }
        }
    }

    private void handlePacket(User user, PacketWrapper<?> packet, boolean isSend, boolean isSilent) {
        if (isSend) {
            if (isSilent) {
                user.sendPacketSilently(packet);
            } else {
                user.sendPacket(packet);
            }
        } else
        if (isSilent) {
            user.receivePacketSilently(packet);
        } else {
            user.receivePacket(packet);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        return (isSend ? "send" : "receive") + " packets " + (isSilent ? "silently" : "not silently");
    }
}
