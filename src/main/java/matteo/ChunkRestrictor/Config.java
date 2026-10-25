package matteo.ChunkRestrictor;

import net.minecraftforge.common.ForgeConfigSpec;

import java.util.List;

public class Config {

    public static ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
    public static ForgeConfigSpec spec;

    public static ForgeConfigSpec.ConfigValue<List<? extends String>> restricted;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> exceptionRestricted;
    public static ForgeConfigSpec.ConfigValue<List<? extends String>> exceptionFTB;

    static {
        builder.comment("Read the mod description. Turn off the compats via the Chunk-Restrictor-Compats file located inside the regular config folder");

        restricted = builder.defineListAllowEmpty("Restricted chunks", List.of(), object -> object instanceof String);
        exceptionRestricted = builder.defineListAllowEmpty("Allow these usernames from bypassing Restricted Chunks", List.of(), object -> object instanceof String);
        exceptionFTB = builder.defineListAllowEmpty("Allow these usernames from bypassing the FTB Chunks restrictions", List.of(), object -> object instanceof String);

        spec = builder.build();
    }
}
