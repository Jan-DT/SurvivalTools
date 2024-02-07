<div align="center">

# `⛏ Jan's Survival Tools`
**A minimal server mod that includes some useful features for vanilla-like survival servers**

</div>

## Installation
1. Download and run the [Fabric installer](https://fabricmc.net/use).
1. Download the mod jar from the [releases page](https://github.com/jan-dt/SurvivalTools/releases)
   and move it to the mods folder (`.minecraft/mods`).
<!--1. Download the [Fabric API](https://minecraft.curseforge.com/projects/fabric)
   and move it to the mods folder (`.minecraft/mods`).-->

## Contributing
1. Clone the repository
   ```
   git clone https://github.com/jan-dt/SurvivalTools.git
   cd SurvivalTools
   ```
1. Generate the Minecraft / Fabric source code
   ```
   ./gradlew genSources
   ```
    - Note: on Windows, use `gradlew` rather than `./gradlew`.
1. Import the project into your preferred IDE.
    1. If you use IntelliJ (the preferred option), you can simply import the project as a Gradle project.
    1. If you use Eclipse, you need to `./gradlew eclipse` before importing the project as an Eclipse project.
1. Edit the code
1. After testing in the IDE, build a JAR to test whether it works outside the IDE too
   ```
   ./gradlew build
   ```
   The mod JAR may be found in the `build/libs` directory
1. [Create a pull request](https://help.github.com/en/articles/creating-a-pull-request)
   so that your changes can be integrated into the mod
    - Note: for large / breaking contributions, create an issue before creating a pull request

### License

This contribution is licensed under [Mozilla Public License v2.0](LICENSE) (or <https://www.mozilla.org/en-US/MPL/2.0/>).
