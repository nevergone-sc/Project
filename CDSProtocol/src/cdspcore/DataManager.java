package cdspcore;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;

/* DataManger takes care of all related disk files and provides functionalities to 
 * read from and write to those files
 */
public class DataManager {
	private int MAX_STORAGE = 65535;
	private HashMap<String, String> publicKeyPaths = new HashMap<String, String>();
	private String pathPrivateKey = "";
	private String workingDirectory = "";
	
	public DataManager(String directory, int max) {
		workingDirectory = directory + "\\";
		MAX_STORAGE = max;
	}
	
	public DataManager(String directory) {
		workingDirectory = directory + "\\";
	}
	
	public byte[] getData(String idTo) throws IOException {
		String path = workingDirectory + idTo;
		File file = new File(path);
		if (!file.exists()) {
			throw new IOException("Data File Not Found");
		}
		return read(path);
	}
	
	public void putData(byte[] src, String idTo) throws IOException {
		String path = workingDirectory + idTo;
		File file = new File(path);
		if (!file.exists()) {
			file.createNewFile();
		}
		write(src, path);
	}
	
	public void deleteData(String idTo) {
		String path = workingDirectory + idTo;
		File file = new File(path);
		if (file.exists()) {
			file.delete();
		}
	}

	public byte[] getPublicKey(String id) throws IOException {
		String path = publicKeyPaths.get(id);
		if (path == null) {
			throw new IOException("Public Key File Not Found: " + id);
		}
		return read(path);
	}
	
	public void putPublicKey(byte[] src, String id) throws IOException {
		String path = publicKeyPaths.get(id);
		if (path == null) {
			throw new IOException("Public Key File Not Found: " + id);
		}
		write(src, path);
	}
	
	public byte[] getPrivateKey() throws IOException {
		File file = new File(pathPrivateKey);
		if (!file.exists()) {
			throw new IOException("Private Key File Not Found: " + pathPrivateKey);
		} else {
			return read(pathPrivateKey);
		}
	}
	
	public void putPrivateKey(byte[] src) throws IOException {
		File file = new File(pathPrivateKey);
		if (!file.exists()) {
			throw new IOException("Private Key File Not Found: " + pathPrivateKey);
		} else {
			write(src, pathPrivateKey);
		}
	}
	
	private byte[] read(String path) throws IOException {
		FileInputStream inStream = null;
		File file = new File(path);
		if (!file.exists()) {
			throw new IOException("Error when read file " + path);
		}
		inStream = new FileInputStream(file);
		byte[] returnBytes = new byte[inStream.available()];
		int readBytes = inStream.read(returnBytes);
		inStream.close();
		if (returnBytes.length == readBytes) {
			return returnBytes;
		} else {
			return null;
		}
	}
	
	private void write(byte[] src, String path) throws IOException {
		FileOutputStream outStream = null;
		File file = new File(path);
		if (!file.exists()) {
			throw new IOException("Error when write file " + path);
		}
		outStream = new FileOutputStream(file, true);
		outStream.write(src);
		outStream.close();
	}
	
	public void setPathPublicKey(String id, String path) {
		publicKeyPaths.put(id, path);
	}
	
	public void setPathPrivateKey(String path) {
		pathPrivateKey = path;
	}
	
	public int maxStorage() {
		return MAX_STORAGE;
	}
}