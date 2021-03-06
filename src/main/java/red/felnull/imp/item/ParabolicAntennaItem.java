package red.felnull.imp.item;

import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;

public class ParabolicAntennaItem extends Item {
    private final ResourceLocation texLocation;
    private final int writeSpeedMagnification;

    public ParabolicAntennaItem(Properties properties, ResourceLocation textuer, int writeSpeedMagnification) {
        super(properties);
        this.texLocation = textuer;
        this.writeSpeedMagnification = writeSpeedMagnification;
    }

    public ResourceLocation getAntennaTextuer() {
        return texLocation;
    }

    public int getWriteSpeedMagnification() {
        return writeSpeedMagnification;
    }
}
