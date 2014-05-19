import java.net.*;
import java.nio.ByteBuffer;


public interface Channel {
	public Channel accept() throws Exception;
	public boolean connect(InetSocketAddress address) throws Exception;
	public boolean close() throws Exception;
	public int read(ByteBuffer dst) throws Exception;
	public int write(ByteBuffer src) throws Exception;
}
