package intern.expivi.detectionsdk.GL.shaders;

/**
 * Created by Rick4 on 14-11-2017.
 */

public enum ShaderType
{
    NONE(-1),
    VERTEX(0),
    FRAGMENT(1);
    private final int code;

    ShaderType(int code) {
        this.code = code;
    }

    public int ToInt() {
        return code;
    }
    public String ToString() {
        return String.valueOf(code);
    }
}
