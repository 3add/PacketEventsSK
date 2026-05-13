package dev.threeadd.packeteventssk.element.general.effect;

import ch.njol.skript.lang.Effect;
import ch.njol.skript.lang.Expression;
import ch.njol.skript.lang.SkriptParser;
import ch.njol.util.Kleenean;
import com.github.retrooper.packetevents.PacketEvents;
import com.github.retrooper.packetevents.protocol.player.User;
import com.github.retrooper.packetevents.wrapper.PacketWrapper;
import com.github.shanebeee.skr.Registration;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.Nullable;

public class EffSendOrReceivePacket extends Effect {

    public static void register(Registration reg) {
        reg.newEffect(EffSendOrReceivePacket.class, "[(:(silently|default))] (:(send|receive)) packet %packets% (to|from) %players%")
                .name("General - Send/Receive Packet")
                .description("Used to force the server to send or receive a packet, optionally silently. (silent means it won't trigger on packet send or receive)")
                .examples("""
                        command killTargetForMe:
                            trigger:
                                set {_packet} to a new clientbound destroy entities packet:
                                    entity ids: protocol id of target entity
                        
                                silently send packet {_packet} to the player
                        """)
                .since("1.0.0")
                .register();
    }

    private Expression<PacketWrapper<?>> packetWrapperExpr;
    private Expression<Player> playerExpr;
    private boolean isSilent = false;
    private boolean isSend = true;

    @SuppressWarnings("unchecked")
    @Override
    public boolean init(Expression<?>[] expressions, int matchedPattern, Kleenean isDelayed, SkriptParser.ParseResult parseResult) {
        this.packetWrapperExpr = (Expression<PacketWrapper<?>>) expressions[0];
        this.playerExpr = (Expression<Player>) expressions[1];

        if (parseResult.hasTag("receive")) {
            isSend = false;
        }

        if (parseResult.hasTag("silently")) {
            isSilent = true;
        }

        return true;
    }

    @Override
    protected void execute(Event event) {
        PacketWrapper<?>[] packets = packetWrapperExpr.getAll(event);
        Player[] targets = playerExpr.getAll(event);
        if (packets == null || packets.length == 0 || targets == null) return;

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
        } else if (isSilent) {
            user.receivePacketSilently(packet);
        } else {
            user.receivePacket(packet);
        }
    }

    @Override
    public String toString(@Nullable Event event, boolean debug) {
        String silentPrefix = isSilent ? "silently " : "";
        String action = isSend ? "send packet" : "receive packet";
        String packetName = packetWrapperExpr.toString(event, debug);
        String direction = isSend ? "to" : "from";
        String targetPlayers = playerExpr.toString(event, debug);
        return String.format("%s%s %s %s %s", silentPrefix, action, packetName, direction, targetPlayers);
    }
}