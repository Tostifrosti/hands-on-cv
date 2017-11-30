package intern.expivi.detectionsdk.GL.shaders;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Rick4 on 14-11-2017.
 */

public class ShaderManager
{
    private static List<Shader> mShaders = new ArrayList<Shader>();

    /**
     * Get: This static method retrieves the shader by name
     * @param name: Name of the shader
     * @return shader
     */
    public static Shader Get(String name)
    {
        for (int i=0; i < mShaders.size(); i++) {
            if (mShaders.get(i).GetName().equals(name))
                return mShaders.get(i);
        }
        return null;
    }

    /**
     * Add: This static method saves the shader
     * @param shader: Shader instance
     */
    public static void Add(Shader shader)
    {
        mShaders.add(shader);
    }

    /**
     * Clean: This static method destroys all the shader instances that are saved.
     */
    public static void Clean() {
        for (int i = 0; i < mShaders.size(); i++)
        {
            try {
                mShaders.get(i).finalize();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
        mShaders.clear();
    }
}
