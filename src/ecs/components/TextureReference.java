package ecs.components;

import ecs.Component;
import linalib.Vec2;
import linalib.Vec2Readable;
import linalib.Vec3;
import linalib.Vec3Readable;


public class TextureReference extends Component {

    private int location;
    private boolean hasTransparency;
    private Vec2Readable sizeOfAtlas;
    private Vec2Readable positionOnAtlas;
    private Vec2Readable repeatVector;
    private Vec3Readable dyeColor;

    public TextureReference(int location) {
        this(location, false);
    }

    public TextureReference(int location, boolean hasTransparency) {
        this(location, new Vec2(1), new Vec2(0), new Vec3(1), new Vec2(1));
    }

    public TextureReference(int location, Vec2Readable sizeOfAtlas, Vec2Readable atlasPosition, Vec3Readable dyeColor,
            Vec2Readable repeat) {
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

    public Vec2Readable getSizeOfAtlas() {
        return this.sizeOfAtlas;
    }

    public Vec2Readable getPositionOnAtlas() {
        return this.positionOnAtlas;
    }

    public Vec2Readable getRepeatVecor() {
        return this.repeatVector;
    }

    public Vec3Readable getDyeColor() {
        return this.dyeColor;
    }

}
