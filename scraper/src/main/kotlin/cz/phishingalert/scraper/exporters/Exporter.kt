package cz.phishingalert.scraper.exporters

import cz.phishingalert.scraper.domain.DnsRecord
import cz.phishingalert.scraper.domain.ModuleInfo
import cz.phishingalert.scraper.domain.SslCertificate
import cz.phishingalert.scraper.domain.Website

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