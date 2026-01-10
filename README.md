## PacketEventsSK
PacketEventsSK is a skript addon that adds packet related features to Skript.
### Requirements
PacketEventsSK requires a 1.18+ bukkit server and [packetevents](https://modrinth.com/plugin/packetevents) a seperate plugin to be installed.
All packets that require a Text Component require [SkBee](https://modrinth.com/plugin/skbee) to be installed as well (you can use their expressions to work with text components)

## Features
Here's a list of some of it's key features, more available on the wiki.
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

They are processed asynchrounsly and thus **YOU CAN'T ACCESS THE MAIN BUKKIT API THROUGH NETTY PROCESSED STRUCTURES**, 
(basically just means that any expression accessing something minecraft related [except if provided by this addon] might cause errors)

If you want to access the bukkit api, process the packet synchrounsly though be aware, not processing on the netty threads removes the ability to cancel or modify the packets received/sent

Every single packet in packetevents can be listened to, for a list see [this](https://github.com/retrooper/packetevents/blob/a3dc1118f87b7bd1404203ec3b6f3b302c59b2b3/api/src/main/java/com/github/retrooper/packetevents/protocol/packettype/PacketType.java), consider that u have to define a side (send [sent by client, `PacketType.x.Server` in paceketevents] or receive [sent by client, `PacketType.x.Client` in paceketevents])
```applescript
on interact entity receive netty processed:
   if packet entity id of event-packet is not {-interactables::%player's uuid%}:
      stop
 
   send "Welcome %player's name%"
```

For more information on how to use the addon, please checkout it's wiki

## Credits
- [PacketEvents](https://github.com/retrooper/packetevents) (all packets), [Install Here](https://modrinth.com/plugin/packetevents)
- [SkBee](https://github.com/ShaneBeee/SkBee) (text components), [Install Here](https://modrinth.com/plugin/skbee)
- [EntityLib](https://github.com/Tofaa2/EntityLib) (Packet Entity management)

Join [the discord](https://discord.gg/vHbVvgJt) for community help or get in touch with contributors
