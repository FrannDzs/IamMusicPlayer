package red.felnull.imp.data;

import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import red.felnull.imp.client.data.MusicSourceClientReferencesType;
import red.felnull.imp.client.data.MusicUploader;
import red.felnull.imp.client.util.YoutubeUtils;
import red.felnull.imp.handler.MusicReceiveHandler;
import red.felnull.imp.music.resource.PlayImage;
import red.felnull.imp.music.resource.PlayList;
import red.felnull.imp.music.resource.PlayLocation;
import red.felnull.imp.music.resource.PlayMusic;
import red.felnull.imp.packet.PacketHandler;
import red.felnull.imp.packet.PlayMusicCreateRequestMessage;
import red.felnull.imp.util.MusicUtils;
import red.felnull.imp.util.PathUtils;
import red.felnull.otyacraftengine.api.DataSendReceiverManager;
import red.felnull.otyacraftengine.data.SendReceiveLogger;
import red.felnull.otyacraftengine.util.IKSGFileLoadUtil;
import red.felnull.otyacraftengine.util.IKSGPlayerUtil;
import red.felnull.otyacraftengine.util.IKSGStringUtil;

import java.io.File;
import java.net.URL;
import java.nio.file.Paths;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PlayMusicManeger {
    private static PlayMusicManeger INSTANCE;

    public static void init() {
        INSTANCE = new PlayMusicManeger();
    }

    public static PlayMusicManeger instance() {
        return INSTANCE;
    }

    public static final Map<String, PlayMusicEntry> waitingDownloads = new HashMap<>();

    @OnlyIn(Dist.CLIENT)
    public void createPlayMusicRequest(String name, PlayList playList, PlayImage image, byte[] imageData, PlayLocation location, MusicSourceClientReferencesType uploadtype, String pathOrURL, String artist, String album, String year, String genre) {
        PlayListCreateRequestThread plcr = new PlayListCreateRequestThread(name, playList, image, imageData, location, uploadtype, pathOrURL, artist, album, year, genre);
        plcr.start();
    }

    public class PlayListCreateRequestThread extends Thread {
        private final String name;
        private final PlayList playList;
        private final PlayImage image;
        private final byte[] imageData;
        private final PlayLocation location;
        private final MusicSourceClientReferencesType uploadtype;
        private final String pathOrURL;
        private final String artist;
        private final String album;
        private final String year;
        private final String genre;

        private PlayListCreateRequestThread(String name, PlayList playList, PlayImage image, byte[] imageData, PlayLocation location, MusicSourceClientReferencesType uploadtype, String pathOrURL, String artist, String album, String year, String genre) {
            this.name = name;
            this.playList = playList;
            this.image = image;
            this.imageData = imageData;
            this.location = location;
            this.uploadtype = uploadtype;
            this.pathOrURL = pathOrURL;
            this.artist = artist;
            this.album = album;
            this.year = year;
            this.genre = genre;
        }

        @Override
        public void run() {
            if (playList == PlayList.ALL)
                return;

            long leth = 0;

            if (uploadtype == MusicSourceClientReferencesType.LOCAL_FILE) {
                MusicUploader.instance().startUpload(name, Paths.get(pathOrURL), location.getIdOrURL(), image, imageData);
                File file = PathUtils.getIMPTmpFolder().resolve(location.getIdOrURL() + "-tmp").toFile();
                try {
                    leth = MusicUtils.getMillisecondDuration(file);
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
                IKSGFileLoadUtil.deleteFile(file);
            } else if (uploadtype == MusicSourceClientReferencesType.URL) {
                try {
                    leth = MusicUtils.getMillisecondDuration(new URL(location.getIdOrURL()));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            } else if (uploadtype == MusicSourceClientReferencesType.YOUTUBE) {
                try {
                    leth = MusicUtils.getMillisecondDuration(new URL(YoutubeUtils.getYoutubeMa4DirectLink(location.getIdOrURL())));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }

            PacketHandler.INSTANCE.sendToServer(new PlayMusicCreateRequestMessage(name, playList.getUUID(), image, location, artist, album, year, genre, leth));

            if (image.getImageType() == PlayImage.ImageType.IMGAE)
                DataSendReceiverManager.instance().sendToServer(IMPWorldData.IMAGE, image.getName(), imageData);
        }
    }


    public void createPlayMusic(ServerPlayerEntity player, PlayList playList, String name, PlayImage image, PlayLocation musicLocation, String artist, String album, String year, String genre, long lengthInMilliseconds) {
        String plUUID = UUID.randomUUID().toString();
        PlayMusic playMusic = new PlayMusic(plUUID, name, image, IKSGPlayerUtil.getUserName(player), IKSGPlayerUtil.getUUID(player), IKSGStringUtil.getTimeStamp(), musicLocation, artist, album, year, genre, lengthInMilliseconds);
        if (musicLocation.getLocationType() != PlayLocation.LocationType.WORLD_FILE || (musicLocation.getLocationType() == PlayLocation.LocationType.WORLD_FILE && MusicReceiveHandler.downloadble.containsKey(musicLocation.getIdOrURL()) && MusicReceiveHandler.downloadble.get(musicLocation.getIdOrURL()) == SendReceiveLogger.SRResult.SUCCESS)) {
            PlayMusic.addPlayMusic(playMusic);
            playMusic.addPlayMusicToPlayList(playList);
            if (musicLocation.getLocationType() == PlayLocation.LocationType.WORLD_FILE)
                MusicReceiveHandler.downloadble.remove(musicLocation.getIdOrURL());
        } else if (musicLocation.getLocationType() == PlayLocation.LocationType.WORLD_FILE) {
            waitingDownloads.put(musicLocation.getIdOrURL(), new PlayMusicEntry(playMusic, playList));
        }
    }

    public CompoundNBT getAllPlayMusicNBT(ServerPlayerEntity playerEntity, PlayList list) {
        CompoundNBT tag = new CompoundNBT();
        if (list.equals(PlayList.ALL)) {
            List<PlayList> plist = PlayList.getJoinedPlayLists(playerEntity);
            plist.forEach(n -> {
                List<PlayMusic> jpl = PlayMusic.getIncludedPlayMusics(n);
                jpl.forEach(n2 -> tag.put(n2.getUUID(), n2.write(new CompoundNBT())));
            });
            return tag;
        } else {
            List<PlayMusic> jpl = PlayMusic.getIncludedPlayMusics(list);
            jpl.forEach(n -> tag.put(n.getUUID(), n.write(new CompoundNBT())));
            return tag;
        }
    }

    public class PlayMusicEntry {
        private final PlayMusic music;
        private final PlayList list;

        public PlayMusicEntry(PlayMusic music, PlayList list) {
            this.music = music;
            this.list = list;
        }

        public PlayMusic getMusic() {
            return music;
        }

        public PlayList getList() {
            return list;
        }
    }

}

