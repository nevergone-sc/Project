package cdspcore;
import java.io.IOException;
import java.net.*;
import java.nio.ByteBuffer;

public interface Channel {
	public Channel accept() throws IOException;
	public boolean connect(InetSocketAddress address) throws IOException;
	public boolean close() throws IOException;
	public int read(ByteBuffer dst) throws IOException;
	public int write(ByteBuffer src) throws IOException;
}
