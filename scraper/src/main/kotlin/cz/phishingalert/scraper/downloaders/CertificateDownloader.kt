package cz.phishingalert.scraper.downloaders

import cz.phishingalert.scraper.domain.SslCertificate
import org.springframework.stereotype.Component
import java.net.URL
import java.security.MessageDigest
import java.security.cert.X509Certificate
import javax.net.ssl.*

private const val HTTPS_PORT = 443

@Component
class CertificateDownloader : Downloader() {
    override fun download(url: URL) {
        val socketFactory = SSLSocketFactory.getDefault()
        val sslSocket = socketFactory.createSocket(url.host, HTTPS_PORT) as SSLSocket

        // Connect and get the X.509 SSL certificates
        try {
            sslSocket.startHandshake()
            val certificates = sslSocket.session.peerCertificates.filterIsInstance<X509Certificate>().toTypedArray()

            for (cert in certificates) {
                logger.info(
                    SslCertificate(
                        calculateThumbprint(cert), cert.version.toString(), cert.serialNumber.toString(),
                        cert.sigAlgName, cert.issuerX500Principal.toString(), cert.notBefore, cert.notAfter,
                        cert.subjectX500Principal.toString(), cert.publicKey.toString(), cert.issuerUniqueID?.toString(),
                        cert.subjectUniqueID?.toString(), cert.signature.toString()
                    ).toString()
                )
            }
        } catch (ex: SSLHandshakeException) {
            logger.warn("CertificateDownloader: SSL Handshake failed!")
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun calculateThumbprint(cert: X509Certificate): String {
        val messageDigest = MessageDigest.getInstance("SHA-1")
        messageDigest.update(cert.encoded)
        return messageDigest.digest().toHexString()
    }
}