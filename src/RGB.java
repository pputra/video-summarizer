public class RGB {
    private byte r;
    private byte g;
    private byte b;

    public RGB(byte r, byte g, byte b) {
        this.r = r;
        this.g = g;
        this.b = b;
    }

    public RGB(RGB otherRGB) {
        this.r = otherRGB.r;
        this.g = otherRGB.g;
        this.b = otherRGB.b;
    }

    public byte getR() {
        return r;
    }

    public byte getG() {
        return g;
    }

    public byte getB() {
        return b;
    }

    public void setR(byte r) {
        this.r = r;
    }

    public void setG(byte g) {
        this.g = g;
    }

    public void setB(byte b) {
        this.b = b;
    }

    @Override
    public String toString() {
        return "RGB{" +
                "r=" + r +
                ", g=" + g +
                ", b=" + b +
                '}';
    }
}