package intern.expivi.detectionsdk.GL.shaders;

/**
 * Created by Rick4 on 16-11-2017.
 */

public class ShaderFactory
{
    /**
     * CreateBasicShader: This static method returns a basic shader
     * @param name: Name of the shader
     */
    public static void CreateBasicShader(String name)
    {
        Shader shader = Shader.CreateFromSource(name, basicShaderSource);
        ShaderManager.Add(shader);
    }
    /**
     * CreateBasicColorShader: This static method returns a basic shader with a uniform for color
     * @param name: Name of the shader
     */
    public static void CreateBasicColorShader(String name)
    {
        Shader shader = Shader.CreateFromSource(name, basicColorShaderSource);
        ShaderManager.Add(shader);
    }

    private static final String basicShaderSource =
            "#shader vertex \n"
                    + "#version 300 es \n"
                    + "layout(location=0) in vec3 a_Position; \n"
                    + "layout(location=1) in vec3 a_Color; \n"
                    + "uniform mat4 u_MVPMatrix; \n"
                    + "out vec4 v_Color; \n"
                    + "void main() \n"
                    + "{ \n"
                    + "gl_Position = u_MVPMatrix * vec4(a_Position, 1.0f); \n"
                    + "v_Color = vec4(a_Color.r, a_Color.g, a_Color.b, 1.0f); \n"
                    + "} \n"
            + "#shader fragment \n"
                    + "#version 300 es \n"
                    + "in vec4 v_Color; \n"
                    + "out vec4 outColor; \n"
                    + "void main() \n"
                    + "{ \n"
                    + "outColor = v_Color; \n"
                    + "} \n";
    private static final String basicColorShaderSource =
            "#shader vertex \n"
                    + "#version 300 es \n"
                    + "layout(location=0) in vec3 a_Position; \n"
                    + "uniform mat4 u_MVPMatrix; \n"
                    + "void main() \n"
                    + "{ \n"
                    + "gl_Position = u_MVPMatrix * vec4(a_Position, 1.0f); \n"
                    + "} \n"
            + "#shader fragment \n"
                    + "#version 300 es \n"
                    + "uniform vec4 u_Color; \n"
                    + "out vec4 outColor; \n"
                    + "void main() \n"
                    + "{ \n"
                    + "outColor = u_Color; \n"
                    + "} \n";


    /*private final String vertexShaderUniformColor =
                      "uniform mat4 u_MVPMatrix;      \n"        // A constant representing the combined model/view/projection matrix
                    + "uniform vec4 u_Color;          \n"

                    + "attribute vec4 a_Position;     \n"        // Per-vertex position information we will pass in.

                    + "varying vec4 v_Color;          \n"        // This will be passed into the fragment shader.

                    + "void main()                    \n"        // The entry point for our vertex shader.
                    + "{                              \n"
                    + "   v_Color = u_Color;          \n"        // Pass the color through to the fragment shader.
                    // It will be interpolated across the triangle.
                    + "   gl_Position = u_MVPMatrix   \n"    // gl_Position is a special variable used to store the final position.
                    + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                    + "}                              \n";    // normalized screen coordinates.

    private final String baseVertexShader =
                    "uniform mat4 u_MVPMatrix;      \n"        // A constant representing the combined model/view/projection matrix.

                    + "attribute vec4 a_Position;     \n"        // Per-vertex position information we will pass in.
                    + "attribute vec4 a_Color;        \n"        // Per-vertex color information we will pass in.

                    + "varying vec4 v_Color;          \n"        // This will be passed into the fragment shader.

                    + "void main()                    \n"        // The entry point for our vertex shader.
                    + "{                              \n"
                    + "   v_Color = a_Color;          \n"        // Pass the color through to the fragment shader.
                    // It will be interpolated across the triangle.
                    + "   gl_Position = u_MVPMatrix   \n"    // gl_Position is a special variable used to store the final position.
                    + "               * a_Position;   \n"     // Multiply the vertex by the matrix to get the final point in
                    + "}                              \n";    // normalized screen coordinates.

    private final String baseFragmentShader =
            "precision mediump float;       \n"        // Set the default precision to medium. We don't need as high of a
                    // precision in the fragment shader.
                    + "varying vec4 v_Color;          \n"        // This is the color from the vertex shader interpolated across the
                    // triangle per fragment.
                    + "void main()                    \n"        // The entry point for our fragment shader.
                    + "{                              \n"
                    + "   gl_FragColor = v_Color;     \n"        // Pass the color directly through the pipeline.
                    + "}                              \n";*/
}
