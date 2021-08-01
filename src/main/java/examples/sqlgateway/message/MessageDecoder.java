package examples.sqlgateway.message;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.QueryStringDecoder;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MessageDecoder extends SimpleChannelInboundHandler<FullHttpRequest> {
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest msg) throws Exception {
        // 这里假定http请求是get请求
        // request-type: execute_sql, paras: sql context
        QueryStringDecoder decoder = new QueryStringDecoder(msg.uri());
        Map<String, String> paras = new HashMap();
        for (Map.Entry<String, List<String>> entry : decoder.parameters().entrySet()) {
            paras.put(entry.getKey(), entry.getValue().get(0));
        }
        RequestMessage mss = new RequestMessage(paras.getOrDefault("request-type", "default"), paras);
        ctx.fireChannelRead(mss);
    }


}
