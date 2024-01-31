package org.experimental.protobuf.metro;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.google.protobuf.Any;
import com.google.protobuf.Message;

public class ServerMessage {
    public static final String HEADER_VERSION = "_ver_";

    /**
     * This is the version that is included in the message under HEADER_VERSION. It is used to navigate changes in the
     * application layer.
     * History:
     * Version 2 - 7.8.3 - Changed the response from services to be ServiceResponse, instead of a direct result object.
     * ServiceResponse looks at the version, and maintains the old behavior if necessary.
     */
    public static final String VERSION = "2";

    private ServerMessageHeader header;
    private byte[] sourceStream;    // this is a stand-in for the InputStreamProvider in the real ServerMessageHeader
    private Object source;  // New for this example. The object that will be serialized or deserialized later.

    public ServerMessage(ServerMessageHeader header, byte[] sourceStream) {
        this.header = header;
        this.sourceStream = sourceStream;
    }

    public ServerMessage(ServerMessageHeader header, Object source) {
        this.header = header;
        this.source = source;
    }

    public static ServerMessage createFor(String intentName, String codecName, byte[] msgBytes) {
        return new ServerMessage(new ServerMessageHeader(intentName, codecName), msgBytes);
    }

    // New for this example
    public static ServerMessage createFor(ServerMessageHeader header, Object source) {
        return new ServerMessage(header, source);
    }

    public void addHeaderValue(String key, String value) {
        this.header.headersValues.put(key, value);
    }

    public Map<String, String> getHeaderValues() {
        return Collections.unmodifiableMap(header.getHeadersValues());
    }

    public byte[] getSourceStream() {
        return this.sourceStream;
    }

    // New for this example
    public Object getSource() {
        return this.source;
    }

    public String getIntentName() {
        return header.intentName;
    }

    public int getIntentVersion() {
        return header.intentVersion;
    }

    public String getCodec() {
        return header.codecName;
    }

    @Override
    public String toString() {
        return "ServerMessage{" +
            "header=" + header +
            ", body bytes size=" + (sourceStream != null ? sourceStream.length : 0)+
            '}';
    }

    public static class ServerMessageHeader implements Serializable {
        private static final Set<Class<?>> WHITELIST =
            Set.of(ServerMessageHeader.class, String.class, Map.class, HashMap.class, Integer.class, Long.class);
        private String intentName;
        private String codecName;
        private Map<String, String> headersValues;
        private int intentVersion = 0;

        public ServerMessageHeader() {
            this.headersValues = new HashMap<>();
        }

        public ServerMessageHeader(String intent, String codec) {
            this();
            setIntentName(intent);
            this.codecName = codec;
            this.headersValues.put(HEADER_VERSION, VERSION);
        }

        public void setCodecName(String codecName) {
            this.codecName = codecName;
        }

        /**
         * Intents names can include the version, like "name|version". For compatibility, however, we don't transmit it
         * like that, we break it out into separate fields.
         */
        public void setIntentName(String intentName) {
            this.intentName = getBaseName(intentName);
            this.intentVersion = getVersion(intentName);
        }

        public void addHeaderValue(String key, String value) {
            headersValues.put(key, value);
        }

        public Map<String, String> getHeadersValues() {
            return headersValues;
        }

        @Override
        public String toString() {
            return "ServerMessageHeader{" +
                "intentName='" + intentName + '\'' +
                ", intentVersion=" + intentVersion +
                ", codecName='" + codecName + '\'' +
                ", headersValues=" + headersValues +
                '}';
        }
    }

    /**
     * Returns the name/id without version information.
     */
    public static String getBaseName(String intent) {
        int ndx = intent.lastIndexOf('|');
        if (ndx > 0) {
            return intent.substring(0, ndx);
        }
        return intent;
    }

    /**
     * Returns the version encoded in the name, or 0 if no version is present.
     */
    public static int getVersion(String versionedName) {
        int ndx = versionedName.lastIndexOf('|');
        if (ndx > 0) {
            try {
                return Integer.parseInt(versionedName.substring(ndx + 1));
            } catch (Exception e) {
                // Ignore, just return 0
            }
        }
        return 0;
    }
}
