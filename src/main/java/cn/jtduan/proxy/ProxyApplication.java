package cn.jtduan.proxy;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;

import org.littleshoot.proxy.HttpFilters;
import org.littleshoot.proxy.HttpFiltersAdapter;
import org.littleshoot.proxy.HttpFiltersSourceAdapter;
import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.extras.SelfSignedMitmManager;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.http.DefaultHttpRequest;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpMethod;
import io.netty.handler.codec.http.HttpObject;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;
import io.netty.util.AttributeKey;
import net.lightbody.bmp.mitm.PemFileCertificateSource;
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;

@SpringBootApplication
public class ProxyApplication {

    public static void main(String[] args) {
//		SpringApplication.run(ProxyApplication.class, args);
        PemFileCertificateSource fileCertificateSource = new PemFileCertificateSource(
                new File("my-ca.cer"),    // the PEM-encoded certificate file
                new File("my-key.pem"),    // the PEM-encoded private key file
                "123456");                      // the password for the private key -- can be null if the private key is not encrypted

        HttpProxyServer server =
                DefaultHttpProxyServer.bootstrap()
                        .withAddress(new InetSocketAddress("0.0.0.0", 8080))
                        .withManInTheMiddle(ImpersonatingMitmManager.builder().rootCertificateSource(fileCertificateSource).trustAllServers(true).build())
                        .withFiltersSource(new HttpFiltersSourceAdapter() {

                            @Override
                            public HttpFilters filterRequest(HttpRequest originalRequest) {
                                return super.filterRequest(originalRequest);
                            }

                            @Override
                            public int getMaximumResponseBufferSizeInBytes() {
                                return 10 * 1024 * 1024;
                            }

                            @Override
                            public int getMaximumRequestBufferSizeInBytes() {
                                return 10 * 1024 * 1024;
                            }

                            public HttpFilters filterRequest(HttpRequest originalRequest, ChannelHandlerContext ctx) {
                                String uri = originalRequest.getUri();
                                if (originalRequest.getMethod() == HttpMethod.CONNECT) {
                                    if (ctx != null) {
                                        String prefix = "https://" + uri.replaceFirst(":443$", "");
                                        ctx.channel().attr(AttributeKey.valueOf("connected_url")).set(prefix);
                                    }
                                    return new HttpFiltersAdapter(originalRequest);
                                }
                                String connectedUrl = (String) ctx.channel().attr(AttributeKey.valueOf("connected_url")).get();

                                return new HttpFiltersAdapter(originalRequest) {
                                    @Override
                                    public HttpResponse clientToProxyRequest(HttpObject httpObject) {
                                        if (httpObject instanceof FullHttpRequest) {
                                            FullHttpRequest request = (FullHttpRequest) httpObject;///1 ///7
//                                            if (request.headers().get("host").contains("weixin")) {
//                                                System.out.println("=======" + request.getUri());
//                                            }
                                            System.out.println((connectedUrl == null ? "" : connectedUrl) + request.getUri());
                                        }
                                        return null;
                                    }
                                };
                            }
                        })
                        .start();
    }
}
