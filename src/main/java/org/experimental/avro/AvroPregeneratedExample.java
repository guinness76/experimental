package experimental.avro;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;

import org.apache.avro.Schema;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;
import org.experimental.avro.GatewayNetworkMsg;
import org.experimental.avro.MessageBody;
import org.experimental.avro.ProtocolHeader;

/**
 * This example uses the protocolheader.avsc file to generate Java classes. The Java classes can then be used to
 * more easily serialize and deserialize the actual message. Run `compile-avro.sh` to build or rebuild the
 * GatewayNetworkMsg class and related classes. The avro-tools jar used to generate the Java classes
 * was manually downloaded from search.maven.org.
 */
public class AvroPregeneratedExample {
    public static void main(String[] args) throws Exception {
        boolean fileWrite = true;
        boolean fileRead = true;

        Schema schema = new Schema.Parser().parse(
            new File("C:\\development\\experimental\\src\\main\\java\\org\\experimental\\avro\\protocolheader.avsc"));
        GatewayNetworkMsg msg = buildMsg();
        byte[] bytes = writeToBytes(msg, schema);
        System.out.println("Message size in bytes=" + bytes.length);

        File outFile = new File("C:\\development\\experimental\\src\\main\\java\\org\\experimental\\avro\\temp\\avro-pregenerated.dat");
        if (fileWrite) {
            writeToFile(bytes, outFile);
        }

        GatewayNetworkMsg decodedMsg = null;
        if (fileRead) {
            decodedMsg = readFromFile(outFile, schema);
        } else {
            decodedMsg = readFromBytes(bytes, schema);
        }

        System.out.println("Decoded msg=" + decodedMsg);

        // Ugh. To use Strings, you have to cast them, even though they are Strings in the .avsc file.
        // CharSequence newFieldVal = decodedMsg.getHeader().getNewFieldX();
        // Integer newFieldVal = decodedMsg.getHeader().getNewFieldY();
        // System.out.println("Avro Pregenerated: new field value reported as: '" + newFieldVal + "'");
    }

    static GatewayNetworkMsg buildMsg() {
        ProtocolHeader header = new ProtocolHeader();
        header.setMagic(0x4941);
        header.setVersion(1);
        header.setMessageId(100);
        header.setOpCode(1);
        header.setSenderId("server1");
        header.setSenderURL("http://localhost:8088/main");
        header.setTargetAddress("server2");
        // header.setNewFieldX("BBB");
        // header.setNewFieldY(42);

        MessageBody body = new MessageBody();
        String bodyStr = "This is an avro message";
        body.setMsgBody(bodyStr);

        GatewayNetworkMsg msg = new GatewayNetworkMsg();
        msg.setHeader(header);
        msg.setBody(body);
        return msg;
    }

    static byte[] writeToBytes(GatewayNetworkMsg theMsg, Schema theSchema) throws IOException {
        DatumWriter<GatewayNetworkMsg> datumWriter =
            new SpecificDatumWriter<>(theSchema);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(bos, null);
        datumWriter.write(theMsg, encoder);
        encoder.flush();

        return bos.toByteArray();
    }

    static GatewayNetworkMsg readFromBytes(byte[] bytes, Schema schema) throws IOException {
        // This doesn't work if the schema used to create the incoming byte stream is out of date
        // DatumReader<GatewayNetworkMsg> datumReader =
        //     new SpecificDatumReader<>(GatewayNetworkMsg.class);
        //
        // Schema origSchema = new Schema.Parser().parse(
        //     new File("C:\\development\\experimental\\src\\main\\java\\org\\experimental\\avro\\protocolheader-orig.avsc"));

        // You need both the original schema and the updated schema to read a byte stream created with
        // the original schema.
        // BUT: you have to know that you are using different schemas. If you try to call this with the original
        // schema and the new schema, it sets the message body value to the newly added field!
        DatumReader<GatewayNetworkMsg> datumReader =
            new SpecificDatumReader<>(schema);
        // DatumReader<GatewayNetworkMsg> datumReader =
        //     new SpecificDatumReader<>(origSchema, schema);

        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
        return datumReader.read(null, decoder);
    }

    static void writeToFile(byte[] bytes, File theFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(theFile);
        fos.write(bytes);
        fos.flush();
        fos.close();
    }

    static GatewayNetworkMsg readFromFile(File theFile, Schema schema) throws Exception {
        FileInputStream fis = new FileInputStream(theFile);
        byte[] fileBytes = fis.readAllBytes();
        return readFromBytes(fileBytes, schema);
    }
}
