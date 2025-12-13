## PacketEventsSK
PacketEventsSK is a skript addon that adds packet related features to Skript.
### Requirements
PacketEventsSK requires a 1.18+ bukkit server and [packetevents](https://modrinth.com/plugin/packetevents) a seperate plugin to be installed.

## Features
Here's a list of some of it's key features, more available on the wiki.
### Entities
```nginx
command spawn <text>:
  trigger:
    create packet text display entity:
      # using SkBee text components
      set the packet text of the packet entity to minimessage from arg-1 
      set the packet scale of the packet entity to vector(2, 2, 2)
      set the packet billboard of the packet entity to fixed

      add all players to packet receivers of the packet entity
      
      spawn packet entity at player
```
### Listening to packets
Packet Listeners can listen to both incomming and outgoing packets. 

They are processed asynchrounsly and thus **YOU CAN'T ACCESS THE MAIN BUKKIT API THROUGH NETTY PROCESSED STRUCTURES**, 
(basically just means that any expression accessing something minecraft related [except if provided by this addon] might cause errors)

If you want to access the bukkit api, process the packet synchrounsly though be aware, not processing on the netty threads removes the ability to cancel or modify the packets received/sent
```nginx
on chunk data packet send:
  # All Chunks will now have their block at 1, 1, 1 (relative coordinates) set to dirt
  set packet block 1, 1, 1 of the packet chunk data of the packet to dirt

on tab complete packet receive:
  send "%name of event-player% tried to tab complete!" to console
  packet cancel event
```

## Credits
- [PacketEvents](https://github.com/retrooper/packetevents) (all packets), [Install Here](https://modrinth.com/plugin/packetevents)
- [EntityLib](https://github.com/Tofaa2/EntityLib) (Packet Entity management)
- [SkBee](https://github.com/ShaneBeee/SkBee) (text components)

The project code will be uploaded once version 1 will release.
