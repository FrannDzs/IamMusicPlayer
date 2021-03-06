package red.felnull.imp.client.handler;

import net.minecraft.nbt.CompoundNBT;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import red.felnull.imp.client.data.MusicUploadData;
import red.felnull.imp.client.data.MusicUploader;
import red.felnull.imp.data.IMPWorldData;
import red.felnull.otyacraftengine.api.event.common.ResponseEvent;
import red.felnull.otyacraftengine.api.event.common.SenderEvent;
import red.felnull.otyacraftengine.data.SendReceiveLogger;

public class MusicUploadHandler {

    @SubscribeEvent
    public static void onSendRun(SenderEvent.Client.Run e) {
        if (e.getLocation().equals(IMPWorldData.SERVER_MUSIC_DATA)) {
            MusicUploader.instance().setProgress(e.getName(), e.getParsent());
        }
    }


    @SubscribeEvent
    public static void onResopnse(ResponseEvent.Server e) {
        if (e.getLocation().equals(IMPWorldData.SEND_MUSIC_RESPONSE)) {
            CompoundNBT tag = e.getData();
            SendReceiveLogger.SRResult res = SendReceiveLogger.SRResult.valueOf(tag.getString("result"));
            if (res == SendReceiveLogger.SRResult.SUCCESS) {
                if (tag.getString("state").equals("unziped")) {
                    MusicUploader.instance().setState(e.getMessage(), MusicUploadData.UploadState.UNZIPPING);
                } else if (tag.getString("state").equals("complete")) {
                    MusicUploader.instance().setState(e.getMessage(), MusicUploadData.UploadState.COMPLETION);
                }
            } else {
                MusicUploader.instance().setState(e.getMessage(), MusicUploadData.UploadState.ERROR);
            }
        }

    }
}
