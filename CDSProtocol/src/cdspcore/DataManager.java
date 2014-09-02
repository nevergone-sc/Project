package cdspcore;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;


public class DataManager {
	private int MAX_STORAGE = 65535;
	private HashMap<String, String> publicKeyPaths = new HashMap<String, String>();
	private String pathPrivateKey = "PrivateKey_Alice";
	private String pathPublicKeys = "PublicKeys";
	private String workingDirectory = "";
	
	public DataManager(String directory, int max) {
		workingDirectory = directory + "\\";
		MAX_STORAGE = max;
	}
	
	public DataManager(String directory) {
		workingDirectory = directory + "\\";
	}
	
	public byte[] getData(String idTo) throws IOException {
		String path = workingDirectory+ idTo;
		File file = new File(path);
		if (!file.exists()) {
			return null;
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
	/* deprecated
	public void putData(byte[] src, String idFrom, String idTo) {
		Pair<String, String> idPair = new Pair<String, String>(idFrom, idTo);
		String path = dataPaths.get(idPair);
		if (path == null) {
			// Create the file
			String filename = "Data_" + idFrom + "_To_" + idTo;
			File file = new File(filename);
			try {
				file.createNewFile();
				write(src, filename);
				dataPaths.put(idPair, filename);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} else {
			write(src, path);
		}
	}
	*/
	public byte[] getPublicKey(String id) throws IOException {
		String path = publicKeyPaths.get(id);
		return read(path);
	}
	
	public void putPublicKey(byte[] src, String id) throws IOException {
		String path = publicKeyPaths.get(id);
		write(src, path);
	}
	
	public byte[] getPrivateKey() throws IOException {
		return read(pathPrivateKey);
	}
	
	public void putPrivateKey(byte[] src) throws IOException {
		write(src, pathPrivateKey);
	}
	
	private byte[] read(String path) throws IOException {
		FileInputStream inStream = null;
		inStream = new FileInputStream(new File(path));
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
		outStream = new FileOutputStream(new File(path), true);
		outStream.write(src);
		outStream.close();
	}
	
	public void setPathPublicKey(String id, String path) {
		publicKeyPaths.put(id, path);
	}
	
	public void setPathPrivateKey(String path) {
		pathPrivateKey = path;
	}
	/*
	public void setPathData(String idFrom, String idTo, String path) {
		dataPaths.put(new Pair<String, String>(idFrom, idTo), path);
	}
	*/
	public void loadPublicKeysFromFile() {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new FileReader(new File(pathPublicKeys)));
			String readRow = reader.readLine();
			String[] row = new String[2];
			while (readRow != null) {
				row = readRow.split(";");
				publicKeyPaths.put(row[0], row[1]);
				readRow = reader.readLine();
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	public int maxStorage() {
		return MAX_STORAGE;
	}
}