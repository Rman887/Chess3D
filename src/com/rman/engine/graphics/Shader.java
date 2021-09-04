package com.rman.engine.graphics;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.FloatBuffer;
import java.util.HashMap;

import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL20;
import org.lwjgl.util.vector.Matrix3f;
import org.lwjgl.util.vector.Matrix4f;
import org.lwjgl.util.vector.Vector2f;
import org.lwjgl.util.vector.Vector3f;
import org.lwjgl.util.vector.Vector4f;

import com.rman.engine.Log;

public class Shader {
	private int programID;
	
	private int vertexShaderID;
	private int fragmentShaderID;
	
	private HashMap<String, Integer> uniformLocs;
	
	private FloatBuffer matrix3fBuffer;
	private FloatBuffer matrix4fBuffer;
	
	public Shader(URL vertexShaderLoc, URL fragmentShaderLoc) {
		vertexShaderID = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		fragmentShaderID = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		
		StringBuilder vertexShaderCode = new StringBuilder();
		try {
			vertexShaderCode = readShaderFile(vertexShaderLoc);
		} catch (IOException ioe) {
			Log.logError(String.format("Error reading file %s:", vertexShaderLoc.toString()), ioe);
		}

		StringBuilder fragmentShaderCode = new StringBuilder();
		try {
			fragmentShaderCode = readShaderFile(fragmentShaderLoc);
		} catch (IOException ioe) {
			Log.logError(String.format("Error reading file %s:", fragmentShaderLoc.toString()), ioe);
		}
		
		compileShader(vertexShaderID, vertexShaderCode, vertexShaderLoc.toString());
		compileShader(fragmentShaderID, fragmentShaderCode, fragmentShaderLoc.toString());
		
		linkProgram();

		GL20.glDeleteShader(vertexShaderID);
		GL20.glDeleteShader(fragmentShaderID);
		
		uniformLocs = new HashMap<String, Integer>();
		matrix3fBuffer = BufferUtils.createFloatBuffer(9);
		matrix4fBuffer = BufferUtils.createFloatBuffer(16);
	}
	
	public void useProgram() {
		GL20.glUseProgram(programID);
	}
	
	public int getProgramID() {
		return programID;
	}
	
	public void addUniform(String name) {
		if (name.isEmpty())
			throw new IllegalArgumentException("Uniform variable name is not valid");
		uniformLocs.put(name, GL20.glGetUniformLocation(programID, name));
	}
	
	public void updateUniform(String name, int value) {
		if (!uniformLocs.containsKey(name))
			addUniform(name);
		GL20.glUniform1i(uniformLocs.get(name), value);
	}
	
	public void updateUniform(String name, float value) {
		GL20.glUniform1f(uniformLocs.get(name), value);
	}
	
	public void updateUniform(String name, Vector2f value) {
		GL20.glUniform2f(uniformLocs.get(name), value.getX(), value.getY());
	}
	
	public void updateUniform(String name, Vector3f value) {
		GL20.glUniform3f(uniformLocs.get(name), value.getX(), value.getY(), value.getZ());
	}
	
	public void updateUniform(String name, Vector4f value) {
		GL20.glUniform4f(uniformLocs.get(name), value.getX(), value.getY(), value.getZ(), value.getW());
	}
	
	public void updateUniform(String name, Matrix3f value, boolean transpose) {
		value.store(matrix3fBuffer);
		matrix3fBuffer.flip();
		GL20.glUniformMatrix3(uniformLocs.get(name), transpose, matrix3fBuffer);
	}
	
	public void updateUniform(String name, Matrix4f value, boolean transpose) {
		value.store(matrix4fBuffer);
		matrix4fBuffer.flip();
		GL20.glUniformMatrix4(uniformLocs.get(name), transpose, matrix4fBuffer);
	}
	
	public void deleteProgram() {
		GL20.glDeleteProgram(programID);
	}
	
	private StringBuilder readShaderFile(URL fileLoc) throws IOException {
		BufferedReader input = new BufferedReader(new InputStreamReader(fileLoc.openStream()));
		StringBuilder shaderCode = new StringBuilder();
		String line = "";
		
		while ((line = input.readLine()) != null)
			shaderCode.append(line).append("\n");
		
		input.close();
		return shaderCode;
	}
	
	private void compileShader(int shaderID, CharSequence shaderCode, String shaderName) {
		GL20.glShaderSource(shaderID, shaderCode);
		GL20.glCompileShader(shaderID);
		
		int result = GL20.glGetShaderi(shaderID, GL20.GL_COMPILE_STATUS);
		int infoLogLength = GL20.glGetShaderi(shaderID, GL20.GL_INFO_LOG_LENGTH);
		if (infoLogLength > 1)
			Log.logError(result + "\n" + String.format("Unable to compile vertex shader %s:\n%s", shaderName, GL20.glGetShaderInfoLog(shaderID, infoLogLength)));
	}
	
	private void linkProgram() {
		programID = GL20.glCreateProgram();
		GL20.glAttachShader(programID, vertexShaderID);
		GL20.glAttachShader(programID, fragmentShaderID);
		GL20.glLinkProgram(programID);

		// Check the program
		int result = GL20.glGetProgrami(programID, GL20.GL_LINK_STATUS);
		int infoLogLength = GL20.glGetProgrami(programID, GL20.GL_INFO_LOG_LENGTH);
		if (infoLogLength > 1)
			Log.logError(result + "\n" + "Unable to link shader program " + programID + ":\n" + GL20.glGetProgramInfoLog(programID, infoLogLength));
	}
}
