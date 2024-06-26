package cz.phishingalert.scraper.exporters.models

import cz.phishingalert.common.domain.*
import org.springframework.context.annotation.Profile
import org.springframework.stereotype.Component

/**
 * Take care of exporting given models into the output stream (printing them)
 */
@Component
@Profile("command-line")
class OutputStreamExporter : ModelExporter {
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

    override fun export(
        phishingAccidentId: Int,
        website: Website,
        dnsRecords: Collection<DnsRecord>,
        modules: Collection<ModuleInfo>,
        certs: Collection<SslCertificate>
    ) {
        println("Phishing accident id is $phishingAccidentId")
        export(website, dnsRecords, modules, certs)
    }
}