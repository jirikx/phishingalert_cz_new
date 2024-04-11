package cz.phishingalert.scraper.exporters

import cz.phishingalert.common.domain.DnsRecord
import cz.phishingalert.common.domain.ModuleInfo
import cz.phishingalert.common.domain.SslCertificate
import cz.phishingalert.common.domain.Website

/**
 * Take care of exporting given entities to some destination
 */
interface Exporter {
    fun export(
        website: Website,
        dnsRecords: Collection<DnsRecord>,
        modules: Collection<ModuleInfo>,
        certs: Collection<SslCertificate>)
}