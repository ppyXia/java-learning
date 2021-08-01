package examples.sqlgateway.result;

import org.apache.flink.runtime.rest.messages.ResponseBody;

public class PrintTestResult implements ResponseBody {
    private String response;

    public PrintTestResult(String response) {
        this.response = response;
    }

    public String getResponse() {
        return response;
    }
}
