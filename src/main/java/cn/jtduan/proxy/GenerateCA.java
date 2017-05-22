package cn.jtduan.proxy;

import net.lightbody.bmp.mitm.RootCertificateGenerator;
import net.lightbody.bmp.mitm.manager.ImpersonatingMitmManager;
import java.io.File;

import org.littleshoot.proxy.HttpProxyServer;
import org.littleshoot.proxy.impl.DefaultHttpProxyServer;

/**
 * Created by jintaoduan on 17/5/22.
 */
public class GenerateCA {
    public static void main(String[] args) {

        // create a dynamic CA root certificate generator using default settings (2048-bit RSA keys)
        RootCertificateGenerator rootCertificateGenerator = RootCertificateGenerator.builder().build();

        // save the dynamically-generated CA root certificate for installation in a browser
        rootCertificateGenerator.saveRootCertificateAsPemFile(new File("my-ca.cer"));
        rootCertificateGenerator.savePrivateKeyAsPemFile(new File("my-key.pem"), "123456");

        // tell the MitmManager to use the root certificate we just generated
        ImpersonatingMitmManager mitmManager = ImpersonatingMitmManager.builder()
                .rootCertificateSource(rootCertificateGenerator)
                .build();

        // tell the HttpProxyServerBootstrap to use the new MitmManager
        HttpProxyServer proxyServer = DefaultHttpProxyServer.bootstrap()
                .withManInTheMiddle(mitmManager)
                .start();

        // make your requests to the proxy server
        //...

        proxyServer.abort();
    }
}
