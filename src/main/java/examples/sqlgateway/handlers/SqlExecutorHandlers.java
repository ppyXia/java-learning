package examples.sqlgateway.handlers;

import examples.sqlgateway.result.PrintTestResult;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponseStatus;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

public class SqlExecutorHandlers implements BiFunction<Map<String, String>, ChannelHandlerContext, Void> {
    @Override
    public Void apply(Map<String, String> stringStringMap, ChannelHandlerContext ctx) {

        HandlerUtils.sendResponse(
                ctx,
                null,
                new PrintTestResult(String.format("receive execute sql:%s", stringStringMap.get("sql"))),
                HttpResponseStatus.OK,
                new HashMap<>());
        return null;
    }
}
