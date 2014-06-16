import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.HashMap;


public class DataManager {
	private final int MAX_STORAGE = 1000;
	private HashMap<String, String> publicKeyPaths = new HashMap<String, String>();
	private HashMap<String, String> dataPaths = new HashMap<String, String>();
	private String pathPrivateKey = "PrivateKey_Alice";
	
	public DataManager() {
		
	}
	
	public byte[] getData(String id) {
		String path = dataPaths.get(id);
		return read(path);
	}
	
	public void putData(byte[] src, String id) {
		String path = dataPaths.get(id);
		write(src, path);
	}
	
	public byte[] getPublicKey(String id) {
		String path = publicKeyPaths.get(id);
		return read(path);
	}
	
	public void putPublicKey(byte[] src, String id) {
		String path = publicKeyPaths.get(id);
		write(src, path);
	}
	
	public byte[] getPrivateKey() {
		return read(pathPrivateKey);
	}
	
	public void putPrivateKey(byte[] src) {
		write(src, pathPrivateKey);
	}
	
	private byte[] read(String path) {
		FileInputStream inStream = null;
		try {
			inStream = new FileInputStream(new File(path));
			byte[] returnBytes = new byte[inStream.available()];
			int readBytes = inStream.read(returnBytes);
			if (returnBytes.length == readBytes) {
				return returnBytes;
			} else {
				return null;
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				inStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}
	
	private void write(byte[] src, String path) {
		FileOutputStream outStream = null;
		try {
			outStream = new FileOutputStream(new File(path));
			outStream.write(src);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				outStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public void setPathPublicKey(String id, String path) {
		publicKeyPaths.put(id, path);
	}
	
	public void setPathPrivateKey(String path) {
		pathPrivateKey = path;
	}
	
	public void setPathData(String id, String path) {
		dataPaths.put(id, path);
	}
}
