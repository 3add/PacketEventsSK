## PacketEventsSK
PacketEventsSK is a skript addon that adds packet related features to Skript.
### Requirements
PacketEventsSK requires a 1.18+ bukkit server and [packetevents](https://modrinth.com/plugin/packetevents) a seperate plugin to be installed.
> [!CAUTION]  
> **PacketEventsSK will only support the latest stable paper (1.21.11) and skript version (2.15.0). It might work with older versions but those are not supported.**

## Features
Here's a list of some of it's key features, more available on [The Wiki](https://github.com/3add/PacketEventsSK/wiki).
### Entities
```applescript
command test:
    trigger:
        create new fake block display entity at player for players:
            set fake display block data of the fake entity to dirt[]
            wait 2 seconds
            kill fake entity the fake entity
```
### Listening to packets
Packet Listeners can listen to both incomming and outgoing packets.

Every single packet in packetevents can be listened to, for a list see [Packet Types](https://skdocs.org/docs?id=5lplg), for some examples please checkout [The Wiki](https://github.com/3add/PacketEventsSK/wiki).
```applescript
on interact entity receive netty processed:
   if packet entity id of event-packet is not {-interactables::%player's uuid%}:
      stop
 
   send "Welcome %player's name%"
```

For more information on how to use the addon, please checkout it's wiki

## Credits
- [PacketEvents](https://github.com/retrooper/packetevents) (Packet management), [Install Here](https://modrinth.com/plugin/packetevents).
- [EntityLib](https://github.com/Tofaa2/EntityLib) (Fake Entity management, Metadata Wrappers) [**Included**].

Join [the discord](https://discord.gg/CzQ863nxDB) for community help or get in touch with contributors
