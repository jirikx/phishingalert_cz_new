package cz.phishingalert.scraper.downloaders
import cz.phishingalert.scraper.domain.DnsRecord
import org.springframework.stereotype.Component
import org.xbill.DNS.AAAARecord
import org.xbill.DNS.ARecord
import org.xbill.DNS.Address
import org.xbill.DNS.CNAMERecord
import org.xbill.DNS.MXRecord
import org.xbill.DNS.NSRecord
import org.xbill.DNS.Name
import org.xbill.DNS.Record
import org.xbill.DNS.Type
import org.xbill.DNS.lookup.LookupResult
import org.xbill.DNS.lookup.LookupSession
import java.net.URL
import java.net.UnknownHostException


@Component
class DnsDownloader(
    val dnsTypes: List<Int> = listOf(Type.A, Type.AAAA, Type.MX, Type.CNAME, Type.NS)
) : Downloader {
    override fun download(url: URL) {
        val session = LookupSession.defaultBuilder().build()
        val domainName = Name.fromString("${url.host}.")

        for (type in dnsTypes) {
            session.lookupAsync(domainName, type)
                .handle { answers: LookupResult, ex: Throwable? ->
                    if (ex == null)
                        for (dnsRecord in answers.records)
                            handleRecord(dnsRecord)
                    else
                        ex.printStackTrace()
                }
                .toCompletableFuture()
                .get()
        }
    }

    fun handleRecord(rec: Record) {
        val addressFromName: (Name) -> String = {
            try {
                Address.getByName(it.toString()).hostAddress
            } catch (ex: UnknownHostException) {
                "unknown"
            }
        }

        val res = when (rec) {
            is ARecord -> DnsRecord(0, rec.address.hostName, Type.A, rec.address.hostAddress, rec.ttl)
            is AAAARecord -> DnsRecord(0, rec.address.hostName, Type.AAAA, rec.address.hostAddress, rec.ttl)
            is MXRecord -> DnsRecord(0, rec.target.toString(), Type.MX, addressFromName(rec.target), rec.ttl, rec.priority) //println( "Host ${rec.target} has preference ${rec.priority} and TTL ${rec.ttl}, IP: " + Address.getByName(rec.target.toString()).hostAddress)
            is CNAMERecord -> DnsRecord(0, rec.target.toString(), Type.CNAME, addressFromName(rec.target), rec.ttl)
            is NSRecord -> DnsRecord(0, rec.target.toString(), Type.NS, Address.getByName(rec.target.toString()).hostAddress, rec.ttl)
            else -> return
        }

        println(res.toString())
        return
    }
}