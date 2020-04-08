
package shardingsphere.workshop.database.csv.handler;

import au.com.bytecode.opencsv.CSVReader;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.apache.shardingsphere.sql.parser.sql.statement.dml.SelectStatement;
import shardingsphere.workshop.database.csv.CSVLogicSchema;
import shardingsphere.workshop.database.mysql.packet.MySQLEofPacket;
import shardingsphere.workshop.database.mysql.packet.MySQLPacket;
import shardingsphere.workshop.database.mysql.packet.constant.MySQLColumnType;
import shardingsphere.workshop.database.mysql.packet.query.MySQLColumnDefinition41Packet;
import shardingsphere.workshop.database.mysql.packet.query.MySQLFieldCountPacket;
import shardingsphere.workshop.database.mysql.packet.query.MySQLTextResultSetRowPacket;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

/**
 * CSV backend handler.
 */
@RequiredArgsConstructor
public final class CSVQueryBackendHandler implements QueryBackendHandler {
    
    private final CSVLogicSchema logicSchema = CSVLogicSchema.getInstance();
    
    private final SelectStatement sqlStatement;
    
    private Iterator<Object[]> iterator;
    
    private int currentSequenceId;
    
    @Override
    public Collection<MySQLPacket> execute() {
        String tableName = sqlStatement.getSimpleTableSegments().iterator().next().getTableName().getIdentifier().getValue();
        prepareQueryData(tableName);
        return createHeaderPackets(tableName);
    }
    
    @SneakyThrows
    private void prepareQueryData(final String tableName) {
        List<Object[]> data = new LinkedList<>();
        try (CSVReader csvReader = logicSchema.getCSVReader(tableName)) {
            csvReader.readNext();
            data.addAll(csvReader.readAll());
        }
        iterator = data.iterator();
    }
    
    private Collection<MySQLPacket> createHeaderPackets(final String tableName) {
        Collection<CSVLogicSchema.ColumnMetaData> columns =  logicSchema.getMetadata().get(tableName);
        Collection<MySQLPacket> result = new LinkedList<>();
        result.add(new MySQLFieldCountPacket(++currentSequenceId, columns.size()));
        CSVLogicSchema.getInstance().getMetadata().get(tableName).forEach(columnMetaData -> {
            result.add(new MySQLColumnDefinition41Packet(++currentSequenceId, 0, logicSchema.getSchemaName(), tableName, tableName,
                columnMetaData.getName(), columnMetaData.getName(), 100, MySQLColumnType.valueOfJDBCType(columnMetaData.getDataType()), 0));
        });
        result.add(new MySQLEofPacket(++currentSequenceId));
        return result;
    }
    
    @Override
    public boolean next() {
        return iterator.hasNext();
    }
    
    @Override
    public MySQLPacket getQueryData() {
        return new MySQLTextResultSetRowPacket(++currentSequenceId, Arrays.asList(iterator.next()));
    }
}
