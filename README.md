<!--suppress HtmlDeprecatedAttribute --> 
<div align="center">
  <h2>PacketEventsSK</h2>
  <h3>PacketEventsSK is a skript addon that adds packet related features to Skript.</h3>
  
  <a href="https://discord.gg/CzQ863nxDB"><img src="https://img.shields.io/discord/1459606994389307463?color=5562e9&logo=discord&logoColor=white&style=for-the-badge"></a>
  <img src="https://img.shields.io/github/license/3add/PacketEventsSK?style=for-the-badge&logo=github">
  <a href="https://bstats.org/plugin/bukkit/PacketEventsSK/28798"><img src="https://img.shields.io/bstats/servers/28798?style=for-the-badge"></a>
  <a href="https://modrinth.com/plugin/packeteventssk"><img src="https://img.shields.io/modrinth/dt/packeteventssk?style=for-the-badge&logo=modrinth&logoColor=white&label=downloads"></a>
    
  <a href="https://skdocs.org/?addon=PacketEventsSK">
    <img src="https://camo.githubusercontent.com/1623c33b83fe153048358ded38409eb55182c29a98158e78b1aeca2ad9575e32/687474703a2f2f736b646f63732e6f72672f76696577646f63732e706e67" alt="SkDocs">
  </a>
  
  <a href="http://skripthub.net/docs/?addon=PacketEventsSK">
    <img src="http://skripthub.net/static/addon/ViewTheDocsButton.png" alt="SkriptHubViewTheDocs">
  </a>
</div>

## Requirements
PacketEventsSK requires the following to run:

- **Minecraft 1.18+** Paper-compatible server  
- **[PacketEvents](https://modrinth.com/plugin/packetevents)** installed as a separate paper plugin

Optionally install [SkBee](https://modrinth.com/plugin/skbee/versions) for the NBT Compound hook.
> [!CAUTION]
> PacketEventsSK attempts to support the latest versions of skript and minecraft:
> - **Paper 1.21.10+**
> - **Skript 2.15.0+**
> - **SkBee 1.17.2+**
>
> There are many more versions that work, but you won't receive support for those.
## Features
PacketEventsSK adds advanced packet functionality to Skript, including:

- Fake entity creation and manipulation (includes tracking and metadata management)
- Interception and modification of incoming and outgoing packets (manipulation and cancellation)
- SkBee integration for NBT Compound creation and manipulation
- Access to all PacketEvents packet types
- Metadata wrappers and packet utilities  

Learn more in the **[Wiki](https://github.com/3add/PacketEventsSK/wiki)**.
## Credits
### PacketEventsSK
The main project.

- [All Contributors](https://github.com/3add/PacketEventsSK/graphs/contributors)
- [Install Here](https://modrinth.com/plugin/packeteventssk)
### PacketEvents
Packet management library.

- [GitHub Repository](https://github.com/retrooper/packetevents)
- [Install Here](https://modrinth.com/plugin/packetevents)
### EntityLib
Fake entity management and metadata wrappers.

- [GitHub Repository](https://github.com/Tofaa2/EntityLib)
- **Included with PacketEventsSK**
### SkBee
SkBee provides the NBT Compound hook for fields using NBT.

- [GitHub Repository](https://github.com/ShaneBeee/SkBee)
- [Install Here](https://modrinth.com/plugin/skbee/versions)
### Other
Other elements use common types provided by other frequently used addons.
Such as:
- Entity Pose (provided by: SkBee)
  
These elements will not register without SkBee present.
## Support
Need help, found a bug, or want to contribute? Join [the Discord](https://discord.gg/CzQ863nxDB)! (alternatively open a PR or an issue)
