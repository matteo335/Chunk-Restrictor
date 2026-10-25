package matteo.ChunkRestrictor;

import matteo.ChunkRestrictor.compats.FTBCompat;

import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.phys.Vec3;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.server.ServerStartedEvent;

import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.LogManager;

import java.util.*;
import java.util.stream.Collectors;

@Mod("chunk_restrictor")
@Mod.EventBusSubscriber(value = Dist.DEDICATED_SERVER)
public class Main {

    //Values
    public static Logger LOGGER = LogManager.getLogger("Chunk-Restrictor");
    public static boolean UnsafeFTBCompat = false;
    public static boolean FTBChunks;
    public static List<ChunkPos> restrictedChunks;
    public static List<? extends String> exceptionRestricted;
    public static List<? extends String> exceptionFTB;


    //Mod setup
    public Main() {
        ModLoadingContext.get().registerConfig(ModConfig.Type.SERVER, Config.spec);

        if (JsonConfig.FILE.exists()) {
            JsonConfig.readJsonConfig();
            FTBChunks = (JsonConfig.getValues().get("FTBChunksCompat").FTBChunksCompat() && ModList.get().isLoaded("ftbchunks"));

        } else {
            FTBChunks = ModList.get().isLoaded("ftbchunks");
            JsonConfig.createJsonConfig();
        }

        if (FTBChunks) {
            FTBCompat.load();
        }
    }

    //World setup
    @SubscribeEvent
    public static void serverLoad(ServerStartedEvent event) {
        restrictedChunks = Config.restricted.get().stream().map(Main::parseRestricted).collect(Collectors.toList());
        exceptionRestricted = Config.exceptionRestricted.get();
        exceptionFTB = Config.exceptionFTB.get();

        LOGGER.info("Config values: {}", restrictedChunks);
        if (FTBChunks) {
            if (!(ModList.get().isLoaded("ftbchunks") && ModList.get().isLoaded("ftblibrary") && ModList.get().isLoaded("ftbteams"))) {
                UnsafeFTBCompat = true;
                LOGGER.error("The FTB Chunks compat is enabled, but required mod(s) are somehow missing!\nftbchunks: {}\nftblibrary: {}\nftbteams: {}", ModList.get().isLoaded("ftbchunks"), ModList.get().isLoaded("ftblibrary"), ModList.get().isLoaded("ftbteams"));
            }
        }
    }

    public static ChunkPos parseRestricted(String string) {
        if (string == null || string.isBlank()) {
            return null;
        }

        String chunk = string;
        chunk = chunk.replace(",", " ").replace("  ", " ").replaceAll("[^0-9- ]", "").trim();
        String[] number = chunk.split(" ");

        if (number.length != 2) {
            LOGGER.error("Someone pasted these chunk coordinates wrongly! \"{}\"\nThe mod interpreted the chunk coordinates as follow: \"{}\"\nUse this calculator to help you! https://minecraft.wiki/w/Calculators/Chunk_coordinates", string, chunk);

            if (number.length == 1) {
                LOGGER.error("There must be a space between the X and Z values!");
            }

            if (number.length == 3) {
                LOGGER.error("The Y coordinate must not be included");
            }
            return null;
        }

        return new ChunkPos(Integer.parseInt(number[0]), Integer.parseInt(number[1]));
    }

    //Live
    @SubscribeEvent
    public static void tick(TickEvent.PlayerTickEvent event) {
        if (event.phase == TickEvent.Phase.END) {
            CompoundTag data = event.player.getPersistentData();
            ChunkPos chunkPos = event.player.chunkPosition();
            Vec3 position = event.player.position();

            if (data.get("lastChunkX") != null && data.get("lastChunkZ") != null) {
                if (!(chunkPos.x == data.getInt("lastChunkX") && chunkPos.z == data.getInt("lastChunkZ"))) {

                    if (!exceptionRestricted.contains(event.player.getName().getString()) || restrictedChunks.contains(chunkPos)) {
                        teleport(event.player);

                    } else if (FTBChunks && !UnsafeFTBCompat && !exceptionFTB.contains(event.player.getName().getString())) {
                        if (!FTBCompat.allowFTBClaimAccess(event.player)) {
                            teleport(event.player);

                        } else {
                            data.putDouble("lastPositionX", position.x);
                            data.putDouble("lastPositionY", position.y);
                            data.putDouble("lastPositionZ", position.z);
                            data.putInt("lastChunkX", chunkPos.x);
                            data.putInt("lastChunkZ", chunkPos.z);
                            data.putInt("lastValidFTBChunkX", chunkPos.x);
                            data.putInt("lastValidFTBChunkZ", chunkPos.z);
                        }
                    } else {
                        data.putDouble("lastPositionX", position.x);
                        data.putDouble("lastPositionY", position.y);
                        data.putDouble("lastPositionZ", position.z);
                        data.putInt("lastChunkX", chunkPos.x);
                        data.putInt("lastChunkZ", chunkPos.z);
                    }
                } else {
                    data.putDouble("lastPositionX", position.x);
                    data.putDouble("lastPositionY", position.y);
                    data.putDouble("lastPositionZ", position.z);
                }
            } else {
                data.putDouble("lastPositionX", position.x);
                data.putDouble("lastPositionY", position.y);
                data.putDouble("lastPositionZ", position.z);
                data.putInt("lastChunkX", chunkPos.x);
                data.putInt("lastChunkZ", chunkPos.z);
            }
        }
    }

    //Utils
    public static void teleport(Player player) {
        if (!player.hasPermissions(4)) {
            CompoundTag data = player.getPersistentData();
            if (FTBChunks) {

            } else {
                player.teleportTo(data.getDouble("lastPositionX"), data.getDouble("lastPositionY"), data.getDouble("lastPositionZ"));
            }
            player.displayClientMessage(Component.literal("§cYou cannot enter this chunk!"), true);
        }
    }
}
