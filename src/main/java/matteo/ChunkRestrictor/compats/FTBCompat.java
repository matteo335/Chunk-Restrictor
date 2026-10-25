package matteo.ChunkRestrictor.compats;

import dev.ftb.mods.ftbchunks.FTBChunksWorldConfig;
import matteo.ChunkRestrictor.Main;

import dev.ftb.mods.ftblibrary.math.ChunkDimPos;

import dev.ftb.mods.ftbteams.api.FTBTeamsAPI;
import dev.ftb.mods.ftbteams.api.Team;
import dev.ftb.mods.ftbteams.api.event.TeamCollectPropertiesEvent;
import dev.ftb.mods.ftbteams.api.event.TeamEvent;
import dev.ftb.mods.ftbteams.api.property.*;

import dev.ftb.mods.ftbchunks.data.ClaimedChunkManagerImpl;
import dev.ftb.mods.ftbchunks.api.ClaimedChunk;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.List;

public class FTBCompat {

    //FTB Teams values
    public static final EnumProperty CLAIM_ACCESS =
            new EnumProperty(
                    new ResourceLocation("chunk_restrictor", "claim_access"),
                    PrivacyMode.NAME_MAP
            );

    public static final BooleanProperty CLAIM_ACCESS_ENEMY =
            new BooleanProperty(
                    new ResourceLocation("chunk_restrictor", "claim_access_enemy"),
                    true
            );

    public static final StringListProperty CLAIM_ACCESS_BLACKLIST =
            new StringListProperty(
                    new ResourceLocation("chunk_restrictor", "claim_access_blacklist"),
                    Collections.emptyList()
            );

    public static final StringListProperty PLAYER_ACCESS_ALLIES =
            new StringListProperty(
                    new ResourceLocation("chunk_restrictor", "player_access_allies"),
                    Collections.emptyList()
            );

    public static final StringListProperty PLAYER_ACCESS_MEMBER =
            new StringListProperty(
                    new ResourceLocation("chunk_restrictor", "player_access_member"),
                    Collections.emptyList()
            );

    public static final StringListProperty PLAYER_ACCESS_OFFICER =
            new StringListProperty(
                    new ResourceLocation("chunk_restrictor", "player_access_officer"),
                    Collections.emptyList()
            );

    public static final StringListProperty PLAYER_ACCESS_ADMIN =
            new StringListProperty(
                    new ResourceLocation("chunk_restrictor", "player_access_admin"),
                    Collections.emptyList()
            );

    public static final StringListProperty CHUNK_ACCESS_PUBLIC =
            new StringListProperty(
                    new ResourceLocation("chunk_restrictor", "chunk_access_public"),
                    Collections.emptyList()
            );

    public static final StringListProperty CHUNK_ACCESS_ALLIES =
            new StringListProperty(
                    new ResourceLocation("chunk_restrictor", "chunk_access_allies"),
                    Collections.emptyList()
            );

    public static final StringListProperty CHUNK_ACCESS_MEMBER =
            new StringListProperty(
                    new ResourceLocation("chunk_restrictor", "chunk_access_member"),
                    Collections.emptyList()
            );

    public static final StringListProperty CHUNK_ACCESS_OFFICER =
            new StringListProperty(
                    new ResourceLocation("chunk_restrictor", "chunk_access_officer"),
                    Collections.emptyList()
            );

    public static final StringListProperty CHUNK_ACCESS_ADMIN =
            new StringListProperty(
                    new ResourceLocation("chunk_restrictor", "chunk_access_admin"),
                    Collections.emptyList()
            );

    public static final Consumer<TeamCollectPropertiesEvent> CLAIM_ACCESS_CONSUMER =
            property -> property.add(CLAIM_ACCESS);

    public static final Consumer<TeamCollectPropertiesEvent> CLAIM_ACCESS_ENEMY_CONSUMER =
            property -> property.add(CLAIM_ACCESS_ENEMY);

    public static final Consumer<TeamCollectPropertiesEvent> CLAIM_ACCESS_BLACKLIST_CONSUMER =
            property -> property.add(CLAIM_ACCESS_BLACKLIST);

    public static final Consumer<TeamCollectPropertiesEvent> PLAYER_ACCESS_ALLIES_CONSUMER =
            property -> property.add(PLAYER_ACCESS_ALLIES);

    public static final Consumer<TeamCollectPropertiesEvent> PLAYER_ACCESS_MEMBER_CONSUMER =
            property -> property.add(PLAYER_ACCESS_MEMBER);

    public static final Consumer<TeamCollectPropertiesEvent> PLAYER_ACCESS_OFFICER_CONSUMER =
            property -> property.add(PLAYER_ACCESS_OFFICER);

    public static final Consumer<TeamCollectPropertiesEvent> PLAYER_ACCESS_ADMIN_CONSUMER =
            property -> property.add(PLAYER_ACCESS_ADMIN);

    public static Consumer<TeamCollectPropertiesEvent> CHUNK_ACCESS_PUBLIC_CONSUMER =
            property -> property.add(CHUNK_ACCESS_PUBLIC);

    public static Consumer<TeamCollectPropertiesEvent> CHUNK_ACCESS_ALLIES_CONSUMER =
            property -> property.add(CHUNK_ACCESS_ALLIES);

    public static Consumer<TeamCollectPropertiesEvent> CHUNK_ACCESS_MEMBER_CONSUMER =
            property -> property.add(CHUNK_ACCESS_MEMBER);

    public static Consumer<TeamCollectPropertiesEvent> CHUNK_ACCESS_OFFICER_CONSUMER =
            property -> property.add(CHUNK_ACCESS_OFFICER);

    public static Consumer<TeamCollectPropertiesEvent> CHUNK_ACCESS_ADMIN_CONSUMER =
            property -> property.add(CHUNK_ACCESS_ADMIN);

    //FTB server settings values
  //  public static Consumer<TeamCollectPropertiesEvent> GET_LAST_NOCLAIM_POSITION =
//            property -> property.add(NO_CLA)


    //Setup
    public static void load() {
        TeamEvent.COLLECT_PROPERTIES.register(CLAIM_ACCESS_CONSUMER);
        TeamEvent.COLLECT_PROPERTIES.register(CLAIM_ACCESS_BLACKLIST_CONSUMER);
        TeamEvent.COLLECT_PROPERTIES.register(CLAIM_ACCESS_ENEMY_CONSUMER);

        TeamEvent.COLLECT_PROPERTIES.register(PLAYER_ACCESS_ALLIES_CONSUMER);
        TeamEvent.COLLECT_PROPERTIES.register(PLAYER_ACCESS_MEMBER_CONSUMER);
        TeamEvent.COLLECT_PROPERTIES.register(PLAYER_ACCESS_OFFICER_CONSUMER);
        TeamEvent.COLLECT_PROPERTIES.register(PLAYER_ACCESS_ADMIN_CONSUMER);

        TeamEvent.COLLECT_PROPERTIES.register(CHUNK_ACCESS_PUBLIC_CONSUMER);
        TeamEvent.COLLECT_PROPERTIES.register(CHUNK_ACCESS_ALLIES_CONSUMER);
        TeamEvent.COLLECT_PROPERTIES.register(CHUNK_ACCESS_MEMBER_CONSUMER);
        TeamEvent.COLLECT_PROPERTIES.register(CHUNK_ACCESS_OFFICER_CONSUMER);
        TeamEvent.COLLECT_PROPERTIES.register(CHUNK_ACCESS_ADMIN_CONSUMER);

        FTBChunksWorldConfig.CONFIG.addBoolean("get_last_noclaim_position", true);
    }


    //Utils
    public static boolean allowFTBClaimAccess(Player player) {
        try {
            if (!player.hasPermissions(4)) {
                ClaimedChunk claim = ClaimedChunkManagerImpl.getInstance().getChunk(new ChunkDimPos(player));

                if (claim == null) {
                    return true;
                }

                Team team = FTBTeamsAPI.api().getManager().getTeamByID(claim.getTeamData().getTeam().getId()).orElse(null);

                if (team.getRankForPlayer(player.getUUID()).isOwner()) {
                    return true;
                }

                if (team.getProperty(CLAIM_ACCESS_BLACKLIST).stream().anyMatch(string -> string.equals(player.getName().getString()))) {
                    return false;
                }

                List<String> chunkAccessAllies = team.getProperty(CHUNK_ACCESS_ALLIES);
                List<String> playerAccessAllies = team.getProperty(PLAYER_ACCESS_ALLIES);

                if (chunkAccessAllies != null && playerAccessAllies != null) {
                    if (team.getRankForPlayer(player.getUUID()).isAllyOrBetter() || playerAccessAllies.stream().anyMatch(string -> string.equals(player.getName().getString()))) {
                        if (chunkAccessAllies.stream().map(Main::parseRestricted).toList().contains(player.chunkPosition())) {
                            return true;
                        }
                    }
                }

                List<String> chunkAccessMember = team.getProperty(CHUNK_ACCESS_MEMBER);
                List<String> playerAccessMember = team.getProperty(PLAYER_ACCESS_MEMBER);

                if (chunkAccessMember != null && playerAccessMember != null) {
                    if (team.getRankForPlayer(player.getUUID()).isMemberOrBetter() || playerAccessMember.stream().anyMatch(string -> string.equals(player.getName().getString()))) {
                        if (chunkAccessMember.stream().map(Main::parseRestricted).toList().contains(player.chunkPosition())) {
                            return true;
                        }
                    }
                }

                List<String> chunkAccessOfficer = team.getProperty(CHUNK_ACCESS_OFFICER);
                List<String> playerAccessOfficer = team.getProperty(PLAYER_ACCESS_OFFICER);

                if (chunkAccessOfficer != null && playerAccessOfficer != null) {
                    if (team.getRankForPlayer(player.getUUID()).isOfficerOrBetter() || playerAccessOfficer.stream().anyMatch(string -> string.equals(player.getName().getString()))) {
                        if (chunkAccessOfficer.stream().map(Main::parseRestricted).toList().contains(player.chunkPosition())) {
                            return true;
                        }
                    }
                }

                List<String> chunkAccessAdmin = team.getProperty(CHUNK_ACCESS_ADMIN);
                List<String> playerAccessAdmin = team.getProperty(PLAYER_ACCESS_ADMIN);

                if (chunkAccessAdmin != null && playerAccessAdmin != null) {
                    if (playerAccessAdmin.stream().anyMatch(string -> string.equals(player.getName().getString()))) {
                        if (chunkAccessAdmin.stream().map(Main::parseRestricted).toList().contains(player.chunkPosition())) {
                            return true;
                        }
                    }
                }

                if (team.getProperty(CLAIM_ACCESS_ENEMY) && team.getRankForPlayer(player.getUUID()).isEnemyOrWorse()) {
                    return true;
                }

                List<String> chunkAccessPublic = team.getProperty(CHUNK_ACCESS_PUBLIC);
                if (chunkAccessPublic != null) {
                    if (chunkAccessPublic.stream().map(Main::parseRestricted).toList().contains(player.chunkPosition())) {
                        return true;
                    }
                }

                PrivacyMode claim_access = PrivacyMode.NAME_MAP.get(team.getProperty(CLAIM_ACCESS));

                if (claim_access == PrivacyMode.PUBLIC) {
                    return true;
                }

                if (claim_access == PrivacyMode.ALLIES) {
                    return (team.getRankForPlayer(player.getUUID()).isAllyOrBetter() || playerAccessAllies.stream().anyMatch(string -> string.equals(player.getName().getString())));
                }

                if (claim_access == PrivacyMode.PRIVATE) {
                    return (team.getRankForPlayer(player.getUUID()).isMemberOrBetter() || playerAccessMember.stream().anyMatch(string -> string.equals(player.getName().getString())));
                }
            }

            return false;
        } catch (Exception exception) {
            throw new RuntimeException("""
                    Chunk Restrictor failed to execute its own FTB Compat.
                    You are likely using versions of the FTB mods that are not compatible with my compat.
                    Please report send this log to the developer of Chunk Restrictor, you should be able to solve this issue by using these versions:
                    ftb-chunks-forge-2001.3.6.jar
                    ftb-library-forge-2001.2.12.jar
                    ftb-teams-forge-2001.3.2.jar
                    """, exception);
        }
    }
}
