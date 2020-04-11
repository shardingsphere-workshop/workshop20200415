
package shardingsphere.workshop.mysql.proxy.fixture.packet.handshake;

import com.google.common.primitives.Bytes;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Random;

/**
 * Auth plugin data for MySQL.
 * 
 * <p>
 *     The auth-plugin-data is the concatenation of strings auth-plugin-data-part-1 and auth-plugin-data-part-2.
 *     The auth-plugin-data-part-1's length is 8; The auth-plugin-data-part-2's length is 12.
 * </p>
 */
@RequiredArgsConstructor
@Getter
public final class MySQLAuthPluginData {
    
    private static final byte[] SEED = {
        'a', 'b', 'e', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z',
        'A', 'B', 'C', 'D', 'E', 'F', 'G', 'H', 'I', 'J', 'K', 'L', 'M', 'N', 'O', 'P', 'Q', 'R', 'S', 'T', 'U', 'V', 'W', 'X', 'Y', 'Z',
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', };
    
    private final Random random = new Random();
    
    private final byte[] authPluginDataPart1;
    
    private final byte[] authPluginDataPart2;
    
    public MySQLAuthPluginData() {
        authPluginDataPart1 = generateRandomBytes(8);
        authPluginDataPart2 = generateRandomBytes(12);
    }
    
    private byte[] generateRandomBytes(final int length) {
        byte[] result = new byte[length];
        for (int i = 0; i < length; i++) {
            result[i] = SEED[random.nextInt(SEED.length)];
        }
        return result;
    }
    
    /**
     * Get auth plugin data.
     * 
     * @return auth plugin data
     */
    public byte[] getAuthPluginData() {
        return Bytes.concat(authPluginDataPart1, authPluginDataPart2);
    }
}
