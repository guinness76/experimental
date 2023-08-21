package experimental.avro;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import org.apache.avro.Schema;
import org.apache.avro.Schema.Field;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericData.Record;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericDatumWriter;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.BinaryDecoder;
import org.apache.avro.io.BinaryEncoder;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.io.DecoderFactory;
import org.apache.avro.io.EncoderFactory;
import org.apache.avro.specific.SpecificDatumReader;
import org.apache.avro.specific.SpecificDatumWriter;

/**
 * This example uses the protocolheader.avsc file to define what the data actually is. You don't get type safety with
 * this directly in the IDE. If you put in the wrong type, you won't know until you hit a runtime exception.
 * Also, both the writer and reader require access to the same schema to do their thing.
 */
public class AvroDynamicExample {
    public static void main(String[] args) throws Exception {
        boolean fileWrite = false;
        boolean fileRead = true;

        Schema schema = new Schema.Parser().parse(
            new File("C:\\development\\experimental\\src\\main\\java\\org\\experimental\\avro\\protocolheader.avsc"));

        GenericRecord msg = buildMsg(schema);
        byte[] bytes = writeToBytes(msg, schema);
        System.out.println("Message size in bytes=" + bytes.length);

        File outFile = new File("C:\\development\\experimental\\src\\main\\java\\org\\experimental\\avro\\temp\\avro-dynamic.dat");
        if (fileWrite) {
            writeToFile(bytes, outFile);
        }

        GenericRecord decodedMsg = null;
        if (fileRead) {
            decodedMsg = readFromFile(outFile, schema);
        } else {
            decodedMsg = readFromBytes(bytes, schema);
        }
        System.out.println("Decoded msg=" + decodedMsg);
    }

    static GenericRecord buildMsg(Schema schema) {
        GenericRecord msg = new GenericData.Record(schema);
        Field headerFld = msg.getSchema().getField("header");
        GenericRecord header = new GenericData.Record(headerFld.schema());

        header.put("magic", 0x4941);
        header.put("version", 1);
        header.put("messageId", 100);
        header.put("opCode", 1);
        header.put("senderId", "server1");
        header.put("senderURL", "http://localhost:8088/main");
        header.put("targetAddress", "server2");
        msg.put("header", header);

        Field bodyFld = msg.getSchema().getField("body");
        GenericRecord body = new GenericData.Record(bodyFld.schema());
        // TODO This is all well and good, but we need to figure out how to stringify Java objects. We will likely
        // need .avsc files for every single Java object that needs to be serialized
        String bodyStr = "This is an avro generic message";
        body.put("msgBody", bodyStr);
        msg.put("body", body);
        return msg;
    }

    static byte[] writeToBytes(GenericRecord theMsg, Schema schema) throws IOException {
        DatumWriter<GenericRecord> datumWriter = new GenericDatumWriter<>(schema);

        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        BinaryEncoder encoder = EncoderFactory.get().binaryEncoder(bos, null);
        datumWriter.write(theMsg, encoder);
        encoder.flush();
        return bos.toByteArray();
    }

    static GenericRecord readFromBytes(byte[] bytes, Schema schema) throws IOException {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(schema);

        BinaryDecoder decoder = DecoderFactory.get().binaryDecoder(bytes, null);
        return datumReader.read(null, decoder);
    }

    static void writeToFile(byte[] bytes, File theFile) throws IOException {
        FileOutputStream fos = new FileOutputStream(theFile);
        fos.write(bytes);
        fos.flush();
        fos.close();
    }

    static GenericRecord readFromFile(File theFile, Schema schema) throws Exception {
        FileInputStream fis = new FileInputStream(theFile);
        byte[] fileBytes = fis.readAllBytes();
        return readFromBytes(fileBytes, schema);
    }
}
