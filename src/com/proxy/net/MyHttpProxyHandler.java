package com.proxy.net;

import java.net.InetSocketAddress;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.HttpContent;
import io.netty.handler.codec.http.HttpRequest;

public class MyHttpProxyHandler extends ChannelInboundHandlerAdapter {
	private HttpRequest request;
	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("http server active===================");
	}

	@Override
	public void channelInactive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("http server inactive===================");
		
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
		//data get method
		if(msg instanceof HttpRequest){
			request = (HttpRequest) msg;
			String uri = request.uri();
			
            System.out.println("Uri:" + uri);
    		//get client address
            if(request.headers().get("X-Forwarded-For")==null){
    			InetSocketAddress insocket = (InetSocketAddress) ctx.channel().remoteAddress();
    			String clientIP = insocket.getAddress().getHostAddress();
    			System.out.println("clientIP:"+clientIP);
    		}else{
    			System.out.println("proxyIp:"+request.headers().get("X-Forwarded-For"));
    		}
		}
		//data post method
		if(msg instanceof HttpContent){
			 HttpContent content = (HttpContent) msg;
	         ByteBuf buf = content.content();
	         System.out.println("conetent message:"+buf.toString());
	         buf.release();
		}
		//start service
		
	}
	
	@Override
	public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
		System.out.println("http server complete===================");
		super.channelReadComplete(ctx);
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		
		System.out.println("http server except==================="+cause);
		cause.printStackTrace();
		ctx.close();
	}
	
	
}
