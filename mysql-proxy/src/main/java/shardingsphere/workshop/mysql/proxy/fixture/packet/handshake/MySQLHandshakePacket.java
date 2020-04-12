
package shardingsphere.workshop.mysql.proxy.fixture.packet.handshake;

import shardingsphere.workshop.mysql.proxy.fixture.packet.MySQLPacketPayload;
import shardingsphere.workshop.mysql.proxy.fixture.packet.constant.MySQLCapabilityFlag;
import shardingsphere.workshop.mysql.proxy.fixture.packet.constant.MySQLServerInfo;
import shardingsphere.workshop.mysql.proxy.fixture.packet.constant.MySQLStatusFlag;
import shardingsphere.workshop.mysql.proxy.fixture.packet.MySQLPacket;
import lombok.Getter;

/**
 * Handshake packet protocol for MySQL.
 * 
 * @see <a href="https://dev.mysql.com/doc/internals/en/connection-phase-packets.html#packet-Protocol::Handshake">Handshake</a>
 */
@Getter
public final class MySQLHandshakePacket implements MySQLPacket {
    
    private final int protocolVersion = MySQLServerInfo.PROTOCOL_VERSION;
    
    private final String serverVersion;
    
    private final int connectionId;
    
    private final int capabilityFlagsLower;
    
    private final int characterSet;
    
    private final MySQLStatusFlag statusFlag;
    
    private final MySQLAuthPluginData authPluginData;
    
    private int capabilityFlagsUpper;
    
    private String authPluginName;
    
    public MySQLHandshakePacket(final int connectionId, final MySQLAuthPluginData authPluginData) {
        this.serverVersion = MySQLServerInfo.SERVER_VERSION;
        this.connectionId = connectionId;
        this.capabilityFlagsLower = MySQLCapabilityFlag.calculateHandshakeCapabilityFlagsLower();
        this.characterSet = MySQLServerInfo.CHARSET;
        this.statusFlag = MySQLStatusFlag.SERVER_STATUS_AUTOCOMMIT;
        this.capabilityFlagsUpper = MySQLCapabilityFlag.calculateHandshakeCapabilityFlagsUpper();
        this.authPluginData = authPluginData;
        this.authPluginName = null;
    }
    
    @Override
    public void write(final MySQLPacketPayload payload) {
        payload.writeInt1(protocolVersion);
        payload.writeStringNul(serverVersion);
        payload.writeInt4(connectionId);
        payload.writeStringNul(new String(authPluginData.getAuthPluginDataPart1()));
        payload.writeInt2(capabilityFlagsLower);
        payload.writeInt1(characterSet);
        payload.writeInt2(statusFlag.getValue());
        payload.writeInt2(capabilityFlagsUpper);
        payload.writeInt1(isClientPluginAuth() ? authPluginData.getAuthPluginData().length : 0);
        payload.writeReserved(10);
        writeAuthPluginDataPart2(payload);
        writeAuthPluginName(payload);
    }
    
    private void writeAuthPluginDataPart2(final MySQLPacketPayload payload) {
        if (isClientSecureConnection()) {
            payload.writeStringNul(new String(authPluginData.getAuthPluginDataPart2()));
        }
    }
    
    private void writeAuthPluginName(final MySQLPacketPayload payload) {
        if (isClientPluginAuth()) {
            payload.writeStringNul(authPluginName);
        }
    }
    
    private boolean isClientSecureConnection() {
        return 0 != (capabilityFlagsLower & MySQLCapabilityFlag.CLIENT_SECURE_CONNECTION.getValue());
    }
    
    private boolean isClientPluginAuth() {
        return 0 != ((capabilityFlagsUpper << 16) & MySQLCapabilityFlag.CLIENT_PLUGIN_AUTH.getValue());
    }
    
    @Override
    public int getSequenceId() {
        return 0;
    }
}
