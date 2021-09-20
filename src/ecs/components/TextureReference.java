package ecs.components;

import ecs.Component;
import linalib.flt.FVec2Readable;
import linalib.flt.FVec3Readable;
import linalib.flt.*;

public class TextureReference extends Component {

    private int location;
    private boolean hasTransparency;
    private FVec2Readable sizeOfAtlas;
    private FVec2Readable positionOnAtlas;
    private FVec2Readable repeatVector;
    private FVec3Readable dyeColor;

    public TextureReference(int location) {
        this(location, false);
    }

    public TextureReference(int location, boolean hasTransparency) {
        this(location, new FVec2(1), new FVec2(0), new FVec3(1), new FVec2(1));
    }

    public TextureReference(int location, FVec2Readable sizeOfAtlas, FVec2Readable atlasPosition, FVec3Readable dyeColor,
            FVec2Readable repeat) {
        this.location = location;
        this.sizeOfAtlas = sizeOfAtlas;
        this.positionOnAtlas = atlasPosition;
        this.repeatVector = repeat;
        this.dyeColor = dyeColor;
    }

    public int getLocation() {
        return this.location;
    }

    public boolean hasTransparency() {
        return this.hasTransparency;
    }

    public FVec2Readable getSizeOfAtlas() {
        return this.sizeOfAtlas;
    }

    public FVec2Readable getPositionOnAtlas() {
        return this.positionOnAtlas;
    }

    public FVec2Readable getRepeatVecor() {
        return this.repeatVector;
    }

    public FVec3Readable getDyeColor() {
        return this.dyeColor;
    }

}
