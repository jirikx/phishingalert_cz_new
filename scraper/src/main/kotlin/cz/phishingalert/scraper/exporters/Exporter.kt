package cz.phishingalert.scraper.exporters

import cz.phishingalert.common.domain.*

/**
 * Take care of exporting given entities to some destination
 */
interface Exporter {
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