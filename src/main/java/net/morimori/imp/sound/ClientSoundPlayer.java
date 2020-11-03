package net.morimori.imp.sound;

import java.nio.file.Paths;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import net.minecraft.client.Minecraft;
import net.morimori.imp.client.handler.RenderHandler;
import net.morimori.imp.file.ClientFileReceiver;
import net.morimori.imp.file.FileReceiverBuffer;
import net.morimori.imp.file.ServerFileSender;
import net.morimori.imp.packet.ClientResponseMessage;
import net.morimori.imp.packet.PacketHandler;
import net.morimori.imp.sound.PlayData.PlayDatasTypes;
import net.morimori.imp.util.SoundHelper;

public class ClientSoundPlayer {
	public static ClientSoundPlayer INSTANS;

	private Map<String, SoundRinger> ringSounds = new HashMap<String, SoundRinger>();
	public Map<String, INewSoundPlayer> playdSounds = new HashMap<String, INewSoundPlayer>();
	private Map<String, Boolean> loopSound = new HashMap<String, Boolean>();
	public Map<String, Boolean> caplSound = new HashMap<String, Boolean>();
	private Set<WorldSoundKey> dwonloadwait = new HashSet<WorldSoundKey>();

	private static Minecraft mc = Minecraft.getInstance();

	public void tick() {
		try {
			ringtick();
			playedtick();
		} catch (Exception e) {
			RenderHandler.expations.put("Exception : " + e, 200);
			e.printStackTrace();
		}
	}

	public SoundRinger getRingSound(String key) {
		if (ringSounds.containsKey(key)) {
			return ringSounds.get(key);
		}
		return null;
	}

	public void addRingSound(String key, SoundRinger sr) {
		if (!ringSounds.containsKey(key)) {
			ringSounds.put(key, sr);
		}
	}

	public void removeRingSound(String key) {
		if (ringSounds.containsKey(key)) {
			ringSounds.get(key).stopRing();
			remringmaps.add(key);
		}
	}

	private Set<String> remringmaps = new HashSet<String>();

	private void ringtick() {

		if (mc.player == null) {
			for (Entry<String, SoundRinger> rs : ringSounds.entrySet()) {
				rs.getValue().stopRing();
			}
			ringSounds.clear();
			try {
				SoundRinger.players.entrySet().forEach(n -> n.getValue().close());
			} catch (Exception e) {
			}
			SoundRinger.players.clear();
		}

		remringmaps.forEach(n -> ringSounds.remove(n));
		remringmaps.clear();

		for (Entry<String, SoundRinger> rs : ringSounds.entrySet()) {
			SoundRinger sr = rs.getValue();

			if (sr.isFinish()) {
				if (playdSounds.get(rs.getKey()).isLoop()) {
					loopSound.put(rs.getKey(), true);
					removeRingSound(rs.getKey());
				} else {
					caplSound.put(rs.getKey(), false);
					removeRingSound(rs.getKey());
				}

			}

			if (!sr.isRing() && !sr.isFinish()) {

				boolean flag = false;
				if (caplSound.containsKey(rs.getKey())) {
					flag = caplSound.get(rs.getKey());
				}

				if(flag)
				sr.startRing();
			}
		}
	}

	private Set<String> removespke = new HashSet<String>();

	private void playedtick() {
		if (mc.player == null) {
			playdSounds.clear();
		} else {
			PacketHandler.INSTANCE.sendToServer(new ClientResponseMessage(2, 0, "null"));
		}

		removespke.forEach(n -> playdSounds.remove(n));
		removespke.clear();

		for (Entry<String, INewSoundPlayer> rs : playdSounds.entrySet()) {

			if (!ringSounds.containsKey(rs.getKey()) && rs.getValue().isPlayed()) {

				SoundRinger sr = null;
				if (rs.getValue().getSound().type == PlayDatasTypes.FILE) {
					sr = new SoundRinger(rs.getValue().getSound().path);
				} else if (rs.getValue().getSound().type == PlayDatasTypes.WORLD) {
					WorldSoundKey wsk = rs.getValue().getSound().wsk;

					if (!wsk.isClientExistence()) {
						sr = new WorldSoundRinger(wsk);
						addDwonload(wsk);
					} else {
						sr = new SoundRinger(wsk.getClientPath());
					}

				} else if (rs.getValue().getSound().type == PlayDatasTypes.URL_STREAM) {
					sr = new StreamSoundRinger(rs.getValue().getSound().url);
				}
				boolean flag = false;
				if (loopSound.containsKey(rs.getKey())) {
					flag = loopSound.get(rs.getKey());
				}
				sr.setPotision(!flag ? rs.getValue().getPosition() : 0);
				addRingSound(rs.getKey(), sr);

			}

			if (ringSounds.containsKey(rs.getKey()) && rs.getValue().isPlayed()) {

				float vold = SoundHelper.getVolumeFromSoundPos(rs.getValue().getSoundPos(),
						rs.getValue().getVolume() * SoundHelper.getOptionVolume());

				getRingSound(rs.getKey()).setVolume(vold >= 1 ? 1 : vold);

			}

			if (!rs.getValue().canExistence()) {
				removeRingSound(rs.getKey());
			}

			if (ringSounds.containsKey(rs.getKey()) && !rs.getValue().isPlayed()
					|| ringSounds.containsKey(rs.getKey()) && !rs.getValue().canExistence()) {

				removeRingSound(rs.getKey());
			}

			if (!rs.getValue().canExistence()) {
				removespke.add(rs.getKey());
			}

		}

	}

	public void finishedDownload(WorldSoundKey wsk) {
		dwonloadwait.remove(wsk);
	}

	public void addDwonload(WorldSoundKey wsk) {

		if (ClientFileReceiver.receiverBufer.size() >= ServerFileSender.MaxSendCont) {
			return;
		}

		boolean flag = false;
		for (Entry<Integer, FileReceiverBuffer> f : ClientFileReceiver.receiverBufer.entrySet()) {
			if (Paths.get(f.getValue().filepath).getParent().toFile().getName().equals(wsk.getFolder())) {
				if (Paths.get(f.getValue().filepath).toFile().getName().equals(wsk.getName())) {
					flag = true;
					break;
				}
			}
		}

		if (!flag) {
			PacketHandler.INSTANCE.sendToServer(new ClientResponseMessage(4, 0, wsk.getFolder() + ":" + wsk.getName()));
		}

	}
}
