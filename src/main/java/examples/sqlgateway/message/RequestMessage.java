package examples.sqlgateway.message;

import java.util.Map;

public class RequestMessage {
    private MessageType type;
    private Map<String, String> para;

    public RequestMessage(String type, Map<String, String> para) {
        this.type = MessageType.valueOf(type);
        this.para = para;
    }

    public MessageType getType() {
        return type;
    }

    public Map<String, String> getPara() {
        return para;
    }
}
