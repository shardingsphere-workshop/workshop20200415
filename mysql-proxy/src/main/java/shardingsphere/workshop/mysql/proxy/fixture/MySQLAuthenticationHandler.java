
package shardingsphere.workshop.mysql.proxy.fixture;

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import io.netty.channel.ChannelHandlerContext;
import org.apache.commons.codec.digest.DigestUtils;
import shardingsphere.workshop.mysql.proxy.fixture.packet.MySQLErrPacket;
import shardingsphere.workshop.mysql.proxy.fixture.packet.MySQLOKPacket;
import shardingsphere.workshop.mysql.proxy.fixture.packet.MySQLPacketPayload;
import shardingsphere.workshop.mysql.proxy.fixture.packet.constant.MySQLServerErrorCode;
import shardingsphere.workshop.mysql.proxy.fixture.packet.handshake.MySQLAuthPluginData;
import shardingsphere.workshop.mysql.proxy.fixture.packet.handshake.MySQLHandshakePacket;
import shardingsphere.workshop.mysql.proxy.fixture.packet.handshake.MySQLHandshakeResponse41Packet;

import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;

/**
 * Authentication engine for MySQL.
 */
public final class MySQLAuthenticationHandler {
    
    private final MySQLAuthPluginData authPluginData = new MySQLAuthPluginData();
    
    private final Map<String, String> authentication = ImmutableMap.of("root", "root");
    
    private static int currentId;
    
    /**
     * Handshake.
     *
     * @param context channel handler context
     */
    public void handshake(final ChannelHandlerContext context) {
        context.writeAndFlush(new MySQLHandshakePacket(nextId(), authPluginData));
    }
    
    /**
     * Get next connection ID.
     *
     * @return next connection ID
     */
    private static synchronized int nextId() {
        if (currentId >= Integer.MAX_VALUE) {
            currentId = 0;
        }
        return ++currentId;
    }
    
    /**
     * Authentication.
     *
     * @param context channel handler context
     * @param payload packet payload
     * @return auth finish or not
     */
    public boolean auth(final ChannelHandlerContext context, final MySQLPacketPayload payload) {
        MySQLHandshakeResponse41Packet response41 = new MySQLHandshakeResponse41Packet(payload);
        Optional<MySQLServerErrorCode> errorCode = login(response41);
        if (errorCode.isPresent()) {
            context.writeAndFlush(getMySQLErrPacket(errorCode.get(), context, response41));
        } else {
            context.writeAndFlush(new MySQLOKPacket(2));
        }
        return true;
    }
    
    private Optional<MySQLServerErrorCode> login(final MySQLHandshakeResponse41Packet response41) {
        if (!authentication.containsKey(response41.getUsername()) || !isPasswordRight(authentication.get(response41.getUsername()), response41.getAuthResponse())) {
            return Optional.of(MySQLServerErrorCode.ER_ACCESS_DENIED_ERROR);
        }
        return Optional.empty();
    }
    
    private boolean isPasswordRight(final String password, final byte[] authResponse) {
        return Strings.isNullOrEmpty(password) || Arrays.equals(getAuthCipherBytes(password), authResponse);
    }
    
    private byte[] getAuthCipherBytes(final String password) {
        byte[] sha1Password = DigestUtils.sha1(password);
        byte[] doubleSha1Password = DigestUtils.sha1(sha1Password);
        byte[] concatBytes = new byte[authPluginData.getAuthPluginData().length + doubleSha1Password.length];
        System.arraycopy(authPluginData.getAuthPluginData(), 0, concatBytes, 0, authPluginData.getAuthPluginData().length);
        System.arraycopy(doubleSha1Password, 0, concatBytes, authPluginData.getAuthPluginData().length, doubleSha1Password.length);
        byte[] sha1ConcatBytes = DigestUtils.sha1(concatBytes);
        return xor(sha1Password, sha1ConcatBytes);
    }
    
    private byte[] xor(final byte[] input, final byte[] secret) {
        final byte[] result = new byte[input.length];
        for (int i = 0; i < input.length; ++i) {
            result[i] = (byte) (input[i] ^ secret[i]);
        }
        return result;
    }
    
    private MySQLErrPacket getMySQLErrPacket(final MySQLServerErrorCode errorCode, final ChannelHandlerContext context, final MySQLHandshakeResponse41Packet response41) {
        if (MySQLServerErrorCode.ER_DBACCESS_DENIED_ERROR == errorCode) {
            return new MySQLErrPacket(2, MySQLServerErrorCode.ER_DBACCESS_DENIED_ERROR, response41.getUsername(), getHostAddress(context), response41.getDatabase());
        } else {
            return new MySQLErrPacket(2, MySQLServerErrorCode.ER_ACCESS_DENIED_ERROR, response41.getUsername(), getHostAddress(context),
                0 == response41.getAuthResponse().length ? "NO" : "YES");
        }
    }
    
    private String getHostAddress(final ChannelHandlerContext context) {
        SocketAddress socketAddress = context.channel().remoteAddress();
        return socketAddress instanceof InetSocketAddress ? ((InetSocketAddress) socketAddress).getAddress().getHostAddress() : socketAddress.toString();
    }
}
