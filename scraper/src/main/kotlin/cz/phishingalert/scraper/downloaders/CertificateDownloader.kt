package cz.phishingalert.scraper.downloaders

import cz.phishingalert.scraper.domain.SslCertificate
import cz.phishingalert.scraper.utils.DateParser
import org.springframework.stereotype.Component
import java.net.URL
import java.security.MessageDigest
import java.security.cert.X509Certificate
import javax.net.ssl.*

private const val HTTPS_PORT = 443

@Component
class CertificateDownloader : Downloader<SslCertificate>() {
    override fun download(url: URL): List<SslCertificate> {
        val socketFactory = SSLSocketFactory.getDefault()
        val sslSocket = socketFactory.createSocket(url.host, HTTPS_PORT) as SSLSocket

        // Connect and get the X.509 SSL certificates
        try {
            sslSocket.startHandshake()
            val certificates = sslSocket.session.peerCertificates.filterIsInstance<X509Certificate>().toTypedArray()
            val parsedCertificates = certificates.map {
                SslCertificate(
                    0, calculateThumbprint(it), it.version.toString(), it.serialNumber.toString(),
                    it.sigAlgName, it.issuerX500Principal.toString(), DateParser.toLocalDateTime(it.notBefore),
                    DateParser.toLocalDateTime(it.notAfter), it.subjectX500Principal.toString(), it.publicKey.toString(),
                    it.issuerUniqueID?.toString(), it.subjectUniqueID?.toString(), it.signature.toString()
                )
            }.toList()

            return parsedCertificates
        } catch (ex: SSLHandshakeException) {
            logger.warn("CertificateDownloader: SSL Handshake failed!")
            return emptyList()
        } finally {
            sslSocket.close()
        }
    }

    @OptIn(ExperimentalStdlibApi::class)
    fun calculateThumbprint(cert: X509Certificate): String {
        val messageDigest = MessageDigest.getInstance("SHA-1")
        messageDigest.update(cert.encoded)
        return messageDigest.digest().toHexString()
    }
}