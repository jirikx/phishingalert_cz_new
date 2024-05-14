package cz.phishingalert.scraper.exporters.models

import cz.phishingalert.common.domain.*

/**
 * Take care of exporting given models to some destination
 */
interface ModelExporter {
    fun export(
        website: Website,
        dnsRecords: Collection<DnsRecord>,
        modules: Collection<ModuleInfo>,
        certs: Collection<SslCertificate>)

    fun export(
        phishingAccidentId: Int,
        website: Website,
        dnsRecords: Collection<DnsRecord>,
        modules: Collection<ModuleInfo>,
        certs: Collection<SslCertificate>)
}