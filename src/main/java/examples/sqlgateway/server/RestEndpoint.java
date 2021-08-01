package examples.sqlgateway.server;


import examples.sqlgateway.handlers.SqlExecutorHandlers;
import examples.sqlgateway.message.MessageDecoder;
import examples.sqlgateway.message.MessageRouter;
import examples.sqlgateway.message.MessageType;
import examples.sqlgateway.message.RequestMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.handler.stream.ChunkedWriteHandler;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;


public class RestEndpoint {
    public static void main(String[] args) {
        RestEndpoint server = new RestEndpoint();
        server.start();
    }

    public final static Charset DEFAULT_CHARSET = StandardCharsets.UTF_8;
    private final static String url = "127.0.0.1";
    private final static Integer port = 8080;

    // 在这里实现netty的逻辑

    public void start() {
        // 通过ServerBootstrap启动服务端
        ServerBootstrap server = new ServerBootstrap();
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        // 1.定义两个线程组，用来处理客户端通道的accept和读写事件
        // bossGroup用来处理accept事件，workerGroup用来处理通道的读写事件
        // partenGroup是mainRector，workerGroup是subReactor
        server.group(bossGroup, workerGroup);


        // 2.绑定服务端channel
        server.channel(NioServerSocketChannel.class);

        // 3.绑定handler，处理读写的http请求
        server.childHandler(new ChannelInitializer<SocketChannel>() {
            protected void initChannel(SocketChannel ch) throws Exception {
                MessageRouter router = new MessageRouter();
                router.registerHandler(MessageType.EXECUTE_SQL, new SqlExecutorHandlers());
                // 对解析出来的数据进行处理分类，给相应的Message处理
                ch.pipeline().addLast("http-decoder", new HttpRequestDecoder());
                ch.pipeline().addLast("http-aggregator", new HttpObjectAggregator(65536));
                ch.pipeline().addLast("http-encoder", new HttpResponseEncoder());
                ch.pipeline().addLast("http-chunked", new ChunkedWriteHandler());
                ch.pipeline().addLast("http-request-decoder", new MessageDecoder());
                ch.pipeline().addLast("http-request-handler", router);
            }
        });

        // 4.监控端口，一直轮询
        try {
            ChannelFuture channelFuture = server.bind(url, port).sync();
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

}
