package examples.sqlgateway.handlers;

import examples.sqlgateway.server.RestEndpoint;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpHeaderNames;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.HttpVersion;
import io.netty.handler.codec.http.LastHttpContent;

import org.apache.flink.runtime.rest.messages.ResponseBody;
import org.apache.flink.runtime.rest.util.RestMapperUtils;

import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.core.JsonProcessingException;
import org.apache.flink.shaded.jackson2.com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.StringWriter;
import java.util.Map;

public class HandlerUtils {
    // 对象和json转换器
    private static final ObjectMapper mapper = RestMapperUtils.getStrictObjectMapper();

    public static <P extends ResponseBody> void sendResponse(
            ChannelHandlerContext channelHandlerContext,
            HttpRequest httpRequest,
            P response,
            HttpResponseStatus statusCode,
            Map<String, String> headers) {
        try {
            String message = mapper.writeValueAsString(response);
            sendResponse(channelHandlerContext, httpRequest, message, statusCode, headers);
        } catch (JsonProcessingException e) {
            System.out.println(String.format("Internal server error. Could not map response to JSON. " + e));
            sendErrorResponce();
        }
    }

    private static void sendResponse(
            ChannelHandlerContext channelHandlerContext,
            HttpRequest httpRequest,
            String message,
            HttpResponseStatus statusCode,
            Map<String, String> headers) {
        // 组装status
        FullHttpResponse responce = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, statusCode);

        // 组装headers
        for (Map.Entry<String, String> header :headers.entrySet()) {
            responce.headers().set(header.getKey(), header.getValue());
        }
        responce.headers().set(HttpHeaderNames.CONTENT_TYPE, "application/json; charset=UTF-8");

        // 组装(headers + context + tails)
        byte[] mssBytes = message.getBytes(RestEndpoint.DEFAULT_CHARSET);
        ByteBuf buf = Unpooled.copiedBuffer(mssBytes);
        responce.content().writeBytes(buf);
        // 监控关闭channel结果就能显示了，否则不显示
        channelHandlerContext.writeAndFlush(responce).addListener(ChannelFutureListener.CLOSE);;

        // 设置活跃时间,根据httpRequest的keealive字段做二次处理

    }

    public static void sendErrorResponce() {
        // todo
    }
}
