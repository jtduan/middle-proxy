//package cn.jtduan.proxy;
//
//import io.netty.buffer.ByteBuf;
//import io.netty.buffer.Unpooled;
//import io.netty.handler.codec.http.DefaultFullHttpResponse;
//import io.netty.handler.codec.http.HttpHeaders;
//import io.netty.handler.codec.http.HttpObject;
//import io.netty.handler.codec.http.HttpRequest;
//import io.netty.handler.codec.http.HttpResponse;
//import io.netty.handler.codec.http.HttpResponseStatus;
//import io.netty.handler.codec.http.HttpVersion;
//
//import java.io.UnsupportedEncodingException;
//
//import org.littleshoot.proxy.HttpFiltersAdapter;
//
///**
// * Created by jintaoduan on 17/5/19.
// */
//public class AnswerRequestFilter extends HttpFiltersAdapter {
//
//
//    public AnswerRequestFilter(HttpRequest originalRequest) {
//        super(originalRequest, null);
//    }
//
//    @Override
//    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
//        ByteBuf buffer = null;
//        try {
//            buffer = Unpooled.wrappedBuffer("Hello World".getBytes("UTF-8"));
//        } catch (UnsupportedEncodingException e) {
//            e.printStackTrace();
//        }
//        HttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK, buffer);
//        HttpHeaders.setContentLength(response, buffer.readableBytes());
//        HttpHeaders.setHeader(response, HttpHeaders.Names.CONTENT_TYPE, "text/html");
//        return response;
//    }
//}
