package intern.expivi.detectionsdk.GL.shaders;

import android.opengl.GLES20;
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

        Shader shader = new Shader(name, builder.toString());

        return shader;
    }

    public static Shader CreateFromSource(String name, String source)
    {
        Common.Assert(!source.isEmpty(), "Source is empty!");

        Shader shader = new Shader(name, source);

        return shader;
    }


    public Shader(String name, String source)
    {
        mName = name;
        mSource = source;
        mProgram = GLES20.glCreateProgram();
        ShaderProgramSource spc = Load(mSource);

        int vs = Create(GLES20.GL_VERTEX_SHADER, spc.VertexSource);
        int fs = Create(GLES20.GL_FRAGMENT_SHADER, spc.FragmentSource);

        // Link
        GLES20.glAttachShader(mProgram, vs);
        GLES20.glAttachShader(mProgram, fs);
        GLES20.glLinkProgram(mProgram);
        GLES20.glValidateProgram(mProgram);

        // Detach & Delete unnecessary items
        GLES20.glDetachShader(mProgram, vs);
        GLES20.glDetachShader(mProgram, fs);

        GLES20.glDeleteShader(vs);
        GLES20.glDeleteShader(fs);
    }

    public void Bind()
    {
        GLES20.glUseProgram(mProgram);
    }
    public void Unbind()
    {
        GLES20.glUseProgram(0);
    }

    private int Create(int type, String source) {
        int id = GLES20.glCreateShader(type);
        GLES20.glShaderSource(id, source);
        GLES20.glCompileShader(id);

        // Check shader
        ValidateShader(id, type);

        return id;
    }
    private void ValidateShader(int id, int type) {
        int[] result = new int[1];
        GLES20.glGetShaderiv(id, GLES20.GL_COMPILE_STATUS, result, 0);

        if (result[0] == GLES20.GL_FALSE)
        {
            //int[] length = new int[1];
            //GLES20.glGetShaderiv(id, GLES20.GL_INFO_LOG_LENGTH, length, 0);
            String msg = GLES20.glGetShaderInfoLog(id);
            Log.e("Shader", "Failed to compile " + (type == GLES20.GL_VERTEX_SHADER ? "vertex" : "fragment") + " shader!");
            Log.e("Shader", msg);
            Common.Assert(false, "");
        }
    }

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

    private int GetUniformLocation(String name) {
        int id = GLES20.glGetUniformLocation(mProgram, name);
        Common.Assert(id != -1, "Uniform location not found!");
        return id;
    }
    public int GetAttributeLocation(String name) {
        int id = GLES20.glGetAttribLocation(mProgram, name);
        Common.Assert(id != -1, "Attribute not found!");
        return id;
    }
    public void BindAttributeLocation(String name, int location)
    {
        Common.Assert(location >= 0, "Location must be unsigned.");
        GLES20.glBindAttribLocation(mProgram, location, name);
    }
    public void EnableAttribute(String name) {
        int id = GetAttributeLocation(name);
        GLES20.glEnableVertexAttribArray(id);
    }
    public void DisableAttribute(String name) {
        int id = GetAttributeLocation(name);
        GLES20.glDisableVertexAttribArray(id);
    }

    public void SetUniformMatrix4(String name, float[] mat) {
        GLES20.glUniformMatrix4fv(GetUniformLocation(name), 1, false, mat, 0);
    }
    public void SetUniformMatrix3(String name, float[] mat) {
        GLES20.glUniformMatrix3fv(GetUniformLocation(name), 1, false, mat, 0);
    }
    public void SetUniform1i(String name, int v0) {
        GLES20.glUniform1i(GetUniformLocation(name), v0);
    }
    public void SetUniform1f(String name, float v0) {
        GLES20.glUniform1f(GetUniformLocation(name), v0);
    }
    public void SetUniform2f(String name, float v0, float v1) {
        GLES20.glUniform2f(GetUniformLocation(name), v0, v1);
    }
    public void SetUniform3f(String name, float v0, float v1, float v2) {
        GLES20.glUniform3f(GetUniformLocation(name), v0, v1, v2);
    }
    public void SetUniform3fv(String name, int count, float[] v) {
        GLES20.glUniform3fv(GetUniformLocation(name), count, v, 0);
    }
    public void SetUniform4f(String name, float v0, float v1, float v2, float v3)
    {
        GLES20.glUniform4f(GetUniformLocation(name), v0, v1, v2, v3);
    }
    public void SetUniform4fv(String name, int count, float[] v)
    {
        GLES20.glUniform4fv(GetUniformLocation(name), count, v, 0);
    }

    public String GetName() {
        return mName;
    }

    @Override
    public void finalize() throws Throwable
    {
        if (mProgram != - 1)
        {
            Unbind();
            GLES20.glDeleteProgram(mProgram);
            mProgram = -1;
        }
        super.finalize();
    }
}
