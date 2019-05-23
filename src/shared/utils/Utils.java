package shared.utils;

import javax.swing.text.html.Option;
import java.math.BigInteger;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Optional;

public class Utils {
	public static String getLocalAddress() throws SocketException, UnknownHostException {
		String ip = "";
		try(final DatagramSocket socket = new DatagramSocket()){
			socket.connect(InetAddress.getByName("8.8.8.8"), 10002);
			ip = socket.getLocalAddress().getHostAddress();
		}
		return ip;
	}

	public static Optional<String> sha256( String data ){
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			byte[] bytes = md.digest(data.getBytes());
			BigInteger signum = new BigInteger(1 , bytes);
			String hash = signum.toString(16);
			StringBuilder builder = new StringBuilder(hash);
			builder.reverse();
			while( builder.length() < 32 ){
				builder.append("0");
			}
			builder.reverse();
			return Optional.of( builder.toString() );
		} catch (NoSuchAlgorithmException e) {
			return Optional.empty();
		}
	}

	public static <K,V> HashMap<K,V> getPartialMap( HashMap<K,V> data , Iterable<K> keys ){
		HashMap<K,V> partial = new HashMap<>();
		for( K key : keys ){
			partial.put( key , data.get(key) );
		}
		return partial;
	}
}
