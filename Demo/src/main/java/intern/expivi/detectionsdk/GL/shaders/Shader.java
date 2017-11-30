package intern.expivi.detectionsdk.GL.shaders;

import android.opengl.GLES30;
import android.util.Log;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.StringReader;

import intern.expivi.detectionsdk.GL.Common;

/**
 * Created by Rick4 on 14-11-2017.
 */

public class Shader
{
    private String mName;
    private String mSource;
    private int mProgram;

    class ShaderProgramSource {
        public String VertexSource;
        public String FragmentSource;

        public ShaderProgramSource(String vertexSource, String fragmentSource) {
            VertexSource = vertexSource;
            FragmentSource = fragmentSource;
        }
    }

    /**
     * CreateFromFile: This method creates an instance of the Shader class by loading from a file
     * @param name: Name of the shader
     * @param filepath: Path to the shader file
     * @return shader
     */
    public static Shader CreateFromFile(String name, String filepath)
    {
        Common.Assert(!filepath.isEmpty(), "Filepath cannot be empty!");
        StringBuilder builder = new StringBuilder();
        File file = new File(filepath);

        Common.Assert(file.isFile(), "The filepath doesnt lead to a file!");
        Common.Assert(file.canRead(), "File is not readable");

        try {
            BufferedReader bufReader = new BufferedReader(new FileReader(file));
            String line;
            while ((line = bufReader.readLine()) != null)
            {
                builder.append(line).append("\n");
            }
        } catch(Exception e) { e.printStackTrace(); }

        return new Shader(name, builder.toString());
    }

    /**
     * CreateFromSource: This method creates an instance of the Shader class by loading from source
     * @param name: Name of the shader
     * @param source: The source of the shader
     * @return shader
     */
    public static Shader CreateFromSource(String name, String source)
    {
        Common.Assert(!source.isEmpty(), "Source is empty!");

        return new Shader(name, source);
    }

    /**
     * Shader: This class creates a shader from source
     * @param name: Name of the shader
     * @param source: The source of the shader
     */
    private Shader(String name, String source)
    {
        mName = name;
        mSource = source;
        mProgram = GLES30.glCreateProgram();
        ShaderProgramSource spc = Load(mSource);

        int vs = Create(GLES30.GL_VERTEX_SHADER, spc.VertexSource);
        int fs = Create(GLES30.GL_FRAGMENT_SHADER, spc.FragmentSource);

        // Link
        GLES30.glAttachShader(mProgram, vs);
        GLES30.glAttachShader(mProgram, fs);
        GLES30.glLinkProgram(mProgram);
        GLES30.glValidateProgram(mProgram);

        // Detach & Delete unnecessary items
        GLES30.glDetachShader(mProgram, vs);
        GLES30.glDetachShader(mProgram, fs);

        GLES30.glDeleteShader(vs);
        GLES30.glDeleteShader(fs);
    }

    /**
     * Bind: This method is used to bind the shader
     */
    public void Bind()
    {
        GLES30.glUseProgram(mProgram);
    }

    /**
     * Unbind: This method is used to unbind the shader
     */
    public void Unbind()
    {
        GLES30.glUseProgram(0);
    }

    /**
     * Create: This method creates, compiles and validates the shader by type
     * @param type: Type of the shader
     * @param source: Source of the shader
     * @return shader id
     */
    private int Create(int type, String source) {
        int id = GLES30.glCreateShader(type);
        GLES30.glShaderSource(id, source);
        GLES30.glCompileShader(id);

        // Check shader
        ValidateShader(id, type);

        return id;
    }

    /**
     * ValidateShader: This method validates the shader by type
     * @param id: The id of the shader
     * @param type: The shader type
     */
    private void ValidateShader(int id, int type) {
        int[] result = new int[1];
        GLES30.glGetShaderiv(id, GLES30.GL_COMPILE_STATUS, result, 0);

        if (result[0] == GLES30.GL_FALSE)
        {
            String msg = GLES30.glGetShaderInfoLog(id);
            Log.e("Shader", "Failed to compile " + (type == GLES30.GL_VERTEX_SHADER ? "vertex" : "fragment") + " shader!");
            Log.e("Shader", msg);
            Common.Assert(false, "");
        }
    }

    /**
     * Load: This method loads the source and split the vertex- & fragment shader.
     * @param source: Source of the shader
     * @return ShaderProgramSource struct
     */
    private ShaderProgramSource Load(String source)
    {
        Common.Assert(!source.isEmpty(), "Shader source cannot be empty!");
        StringBuilder[] builder = new StringBuilder[2];
        ShaderType type = ShaderType.NONE;

        try {
            BufferedReader bufReader = new BufferedReader(new StringReader(source));
            String line;
            while ((line = bufReader.readLine()) != null)
            {
                if (line.indexOf("#shader", 0) >= 0) {
                    if (line.indexOf("vertex", 8) >= 0) {
                        type = ShaderType.VERTEX;
                        builder[type.ToInt()] = new StringBuilder();
                    } else if (line.indexOf("fragment", 8) >= 0) {
                        type = ShaderType.FRAGMENT;
                        builder[type.ToInt()] = new StringBuilder();
                    }
                } else {
                    builder[type.ToInt()].append(line).append("\n");
                }
            }
        } catch(Exception e) { e.printStackTrace(); }

        return new ShaderProgramSource(builder[0].toString(), builder[1].toString());
    }

    /**
     * GetUniformLocation: This method returns the uniform location by the given name
     * @param name: Name of the uniform
     * @return uniform location
     */
    private int GetUniformLocation(String name) {
        int id = GLES30.glGetUniformLocation(mProgram, name);
        Common.Assert(id != -1, "Uniform location not found!");
        return id;
    }

    /**
     * GetAttributeLocation: This method retrieves the attribute location by name
     * @param name: Name of the attribute
     * @return attribute location
     */
    public int GetAttributeLocation(String name) {
        int id = GLES30.glGetAttribLocation(mProgram, name);
        Common.Assert(id != -1, "Attribute not found!");
        return id;
    }

    /**
     * BindAttributeLocation: This method binds the given attribute location by name
     * @param name: Name of the attribute
     * @param location: Location of the attrbute
     */
    public void BindAttributeLocation(String name, int location)
    {
        Common.Assert(location >= 0, "Location must be unsigned.");
        GLES30.glBindAttribLocation(mProgram, location, name);
    }

    /**
     * EnableAttribute: This method enables the vertex attribute array by name.
     * @param name: Name of the attribute
     */
    public void EnableAttribute(String name) {
        int id = GetAttributeLocation(name);
        GLES30.glEnableVertexAttribArray(id);
    }

    /**
     * DisableAttribute: This method disables the vertex attribute array by name.
     * @param name: Name of the attribute
     */
    public void DisableAttribute(String name) {
        int id = GetAttributeLocation(name);
        GLES30.glDisableVertexAttribArray(id);
    }

    /**
     * SetUniformMatrix4: This method sets the uniform matrix by name
     * @param name: Name of the uniform
     * @param mat: The new matrix values ( float[4x4] )
     */
    public void SetUniformMatrix4(String name, float[] mat) {
        GLES30.glUniformMatrix4fv(GetUniformLocation(name), 1, false, mat, 0);
    }
    /**
     * SetUniformMatrix3: This method sets the uniform matrix by name
     * @param name: Name of the uniform
     * @param mat: The new matrix values ( float[3x3] )
     */
    public void SetUniformMatrix3(String name, float[] mat) {
        GLES30.glUniformMatrix3fv(GetUniformLocation(name), 1, false, mat, 0);
    }

    /**
     * SetUniform1i: This method sets the uniform integer
     * @param name: Name of the uniform
     * @param v0: The new integer value
     */
    public void SetUniform1i(String name, int v0) {
        GLES30.glUniform1i(GetUniformLocation(name), v0);
    }

    /**
     * SetUniform1f: This method sets the uniform float
     * @param name: Name of the uniform
     * @param v0: The new float value
     */
    public void SetUniform1f(String name, float v0) {
        GLES30.glUniform1f(GetUniformLocation(name), v0);
    }

    /**
     * SetUniform2f: This method sets the uniform floats
     * @param name: Name of the uniform
     * @param v0: The new float value
     * @param v1: The new float value
     */
    public void SetUniform2f(String name, float v0, float v1) {
        GLES30.glUniform2f(GetUniformLocation(name), v0, v1);
    }
    /**
     * SetUniform3f: This method sets the uniform floats
     * @param name: Name of the uniform
     * @param v0: The new float value
     * @param v1: The new float value
     * @param v2: The new float value
     */
    public void SetUniform3f(String name, float v0, float v1, float v2) {
        GLES30.glUniform3f(GetUniformLocation(name), v0, v1, v2);
    }

    /**
     * SetUniform3fv: This method sets the uniform floats by matrix
     * @param name: Name of the uniform
     * @param count: Amount of matrices
     * @param v: The new matrix values ( float[3x3] )
     */
    public void SetUniform3fv(String name, int count, float[] v) {
        GLES30.glUniform3fv(GetUniformLocation(name), count, v, 0);
    }

    /**
     * SetUniform4f: This method sets the uniform floats
     * @param name: Name of the uniform
     * @param v0: The new float value
     * @param v1: The new float value
     * @param v2: The new float value
     * @param v3: The new float value
     */
    public void SetUniform4f(String name, float v0, float v1, float v2, float v3)
    {
        GLES30.glUniform4f(GetUniformLocation(name), v0, v1, v2, v3);
    }

    /**
     * SetUniform4fv: This method set the uniform float by matrix
     * @param name: Name of the uniform
     * @param count: Amount of matrices
     * @param v: The new matrix values ( float[4x4] )
     */
    public void SetUniform4fv(String name, int count, float[] v)
    {
        GLES30.glUniform4fv(GetUniformLocation(name), count, v, 0);
    }

    /**
     * GetName: This method returns the name of the shader
     * @return shader name
     */
    public String GetName() {
        return mName;
    }

    /**
     * finalize: This method finalizes the method by destroying all the objects within this instance.
     * @throws Throwable: Could throw an exception
     */
    @Override
    public void finalize() throws Throwable
    {
        if (mProgram != - 1)
        {
            Unbind();
            GLES30.glDeleteProgram(mProgram);
            mProgram = -1;
        }
        super.finalize();
    }
}
