import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;


public class DataManager {
	private final int MAX_STORAGE;
	private HashMap<String, String> publicKeyPaths = new HashMap<String, String>();
	private HashMap<Pair<String, String>, String> dataPaths = new HashMap<Pair<String, String>, String>();
	private String pathPrivateKey = "PrivateKey_Alice";
	
	public DataManager(int max) {
		MAX_STORAGE = max;
	}
	
	public byte[] getData(String idFrom, String idTo) {
		String path = dataPaths.get(new Pair<String, String>(idFrom, idTo));
		if (path == null) return null;
		return read(path);
	}
	
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
	
	public void setPathData(String idFrom, String idTo, String path) {
		dataPaths.put(new Pair<String, String>(idFrom, idTo), path);
	}
	
	public void readPathsFromFile(String filepath) {
		// TODO:
	}
	
	public int maxStorage() {
		return MAX_STORAGE;
	}
	
	class Pair<L,R> {
		  private final L left;
		  private final R right;

		  public Pair(L left, R right) {
		    this.left = left;
		    this.right = right;
		  }

		  public L getLeft() { return left; }
		  public R getRight() { return right; }

		  @Override
		  public int hashCode() { return left.hashCode() ^ right.hashCode(); }

		  @Override
		  public boolean equals(Object o) {
		    if (o == null) return false;
		    if (!(o instanceof Pair)) return false;
		    @SuppressWarnings("unchecked")
			Pair<L, R> pairo = (Pair<L, R>) o;
		    return this.left.equals(pairo.getLeft()) &&
		           this.right.equals(pairo.getRight());
		  }

	}
}