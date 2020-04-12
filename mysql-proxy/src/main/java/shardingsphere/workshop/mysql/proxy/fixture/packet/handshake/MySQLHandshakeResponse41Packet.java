
package shardingsphere.workshop.mysql.proxy.fixture.packet.handshake;

import shardingsphere.workshop.mysql.proxy.fixture.packet.MySQLPacketPayload;
import shardingsphere.workshop.mysql.proxy.fixture.packet.constant.MySQLCapabilityFlag;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

/**
 * Handshake response above MySQL 4.1 packet protocol.
 * 
 * @see <a href="https://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::HandshakeResponse41">HandshakeResponse41</a>
 */
@RequiredArgsConstructor
@Setter
@Getter
public final class MySQLHandshakeResponse41Packet {
    
    private final int maxPacketSize;
    
    private final int characterSet;
    
    private final String username;
    
    private byte[] authResponse;
    
    private int capabilityFlags;
    
    private String database;
    
    private String authPluginName;
    
    public MySQLHandshakeResponse41Packet(final MySQLPacketPayload payload) {
        capabilityFlags = payload.readInt4();
        maxPacketSize = payload.readInt4();
        characterSet = payload.readInt1();
        payload.skipReserved(23);
        username = payload.readStringNul();
        authResponse = readAuthResponse(payload);
        database = readDatabase(payload);
        authPluginName = readAuthPluginName(payload);
    }
    
    private byte[] readAuthResponse(final MySQLPacketPayload payload) {
        if (0 != (capabilityFlags & MySQLCapabilityFlag.CLIENT_PLUGIN_AUTH_LENENC_CLIENT_DATA.getValue())) {
            return payload.readStringLenencByBytes();
        }
        if (0 != (capabilityFlags & MySQLCapabilityFlag.CLIENT_SECURE_CONNECTION.getValue())) {
            int length = payload.readInt1();
            return payload.readStringFixByBytes(length);
        }
        return payload.readStringNulByBytes();
    }
    
    private String readDatabase(final MySQLPacketPayload payload) {
        return 0 != (capabilityFlags & MySQLCapabilityFlag.CLIENT_CONNECT_WITH_DB.getValue()) ? payload.readStringNul() : null;
    }
    
    private String readAuthPluginName(final MySQLPacketPayload payload) {
        // TODO Should check CLIENT_PLUGIN_AUTH both client capabilityFlags and server capability
        return null;
    }
}
