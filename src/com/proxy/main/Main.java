package com.proxy.main;

import com.proxy.net.MyHttpProxyHandler;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpRequestEncoder;

public class Main {

	public static void main(String[] args) {
		ServerBootstrap b = new ServerBootstrap();
		EventLoopGroup bossGroup =  new NioEventLoopGroup();
		EventLoopGroup childGroup = new NioEventLoopGroup();
		try{
			b.group(bossGroup, childGroup).channel(NioServerSocketChannel.class).childHandler(new ChannelInitializer<Channel>() {
	
				@Override
				protected void initChannel(Channel ch) throws Exception {
					ChannelPipeline pipeline = ch.pipeline();
					pipeline.addLast("decoder",new HttpRequestDecoder());
					pipeline.addLast("encoder",new HttpRequestEncoder());
					pipeline.addLast("handler",new MyHttpProxyHandler());
				}
			}).option(ChannelOption.SO_BACKLOG, 128).childOption(ChannelOption.SO_KEEPALIVE, true);
			ChannelFuture f = b.bind(8080).sync();
			System.out.println("==================server start success===============");
			f.channel().closeFuture().sync();
		}catch(Exception e){
			e.printStackTrace();
		}finally {
			childGroup.shutdownGracefully();
			bossGroup.shutdownGracefully();
		}
	}
}
