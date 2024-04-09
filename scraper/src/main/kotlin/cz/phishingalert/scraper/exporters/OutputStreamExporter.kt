package cz.phishingalert.scraper.exporters

import cz.phishingalert.scraper.domain.*
import org.springframework.stereotype.Component

/**
 * Take care of exporting given entities into the output stream (printing them)
 */
@Component
class OutputStreamExporter : Exporter {
    override fun export(
        website: Website,
        dnsRecords: Collection<DnsRecord>,
        modules: Collection<ModuleInfo>,
        certs: Collection<SslCertificate>
    ) {
        println(website)

        for (dnsRecord in dnsRecords)
            println(dnsRecord)

        for (module in modules)
            println(module)

        for (cert in certs)
            println(cert)
    }
}