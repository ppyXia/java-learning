package examples.sqlgateway.message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;


public class MessageRouter extends SimpleChannelInboundHandler<RequestMessage> {
    private Map<MessageType, BiFunction<Map<String, String>, ChannelHandlerContext, Void>> handlers = new HashMap<>();

    protected void channelRead0(ChannelHandlerContext ctx, RequestMessage msg) throws Exception {
        // 先不考虑没找到handler的情况,也就是get不会返回null
        handlers.get(msg.getType()).apply(msg.getPara(), ctx);
    }

    public void registerHandler(MessageType type, BiFunction<Map<String, String>, ChannelHandlerContext, Void> function) {
        handlers.put(type, function);
    }
}
