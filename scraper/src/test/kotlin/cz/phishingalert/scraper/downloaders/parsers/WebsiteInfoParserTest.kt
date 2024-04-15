package cz.phishingalert.scraper.downloaders.parsers

import cz.phishingalert.common.domain.Website
import org.junit.jupiter.api.Test

import org.junit.jupiter.api.Assertions.*
import java.time.LocalDate

class WebsiteInfoParserTest {

    @Test
    fun parseWhoIsCz() {
        val rawResponse ="""
        %  (c) 2006-2021 CZ.NIC, z.s.p.o.
        % 
        % Intended use of supplied data and information
        % 
        % Data contained in the domain name register, as well as information
        % supplied through public information services of CZ.NIC association,
        % are appointed only for purposes connected with Internet network
        % administration and operation, or for the purpose of legal or other
        % similar proceedings, in process as regards a matter connected
        % particularly with holding and using a concrete domain name.
        % 
        % Full text available at:
        % http://www.nic.cz/page/306/intended-use-of-supplied-data-and-information/
        % 
        % See also a search service at http://www.nic.cz/whois/
        % 
        % 
        % Whoisd Server Version: 3.14.0
        % Timestamp: Tue Apr 02 10:10:08 2024
        
        domain:       cvut.cz
        registrant:   CVUT-V-PRAZE
        admin-c:      CVUT-MN
        admin-c:      CVUT-JK
        admin-c:      CVUT-RT
        nsset:        NSS:R1S-CES-8079-FA:1
        keyset:       AUTO-UJ3Z52N0Z5FN8BUNSDW4PV5TD
        registrar:    REG-GRANSY
        registered:   10.03.1996 01:00:00
        changed:      24.11.2023 16:38:48
        expire:       10.10.2024
        
        contact:      CVUT-V-PRAZE
        org:          České vysoké učení technické v Praze
        name:         Michal Neuman
        address:      Jugoslávských partyzánů 1580/3
        address:      Praha 6
        address:      160 00
        address:      CZ
        registrar:    REG-GRANSY
        created:      04.11.2019 10:56:07
        changed:      07.11.2019 10:37:04
        
        contact:      CVUT-MN
        name:         Michal Neuman
        registrar:    REG-GRANSY
        created:      04.11.2019 09:39:01
        changed:      15.03.2022 14:03:00
        
        contact:      CVUT-JK
        name:         Jana Krupová
        address:      Jugoslávských partyzánů 1580/3
        address:      Praha 6
        address:      160 00
        address:      CZ
        registrar:    REG-GRANSY
        created:      04.11.2019 10:44:50
        
        contact:      CVUT-RT
        name:         Radek Trousílek
        address:      Jugoslávských partyzánů 1580/3
        address:      Praha 6
        address:      160 00
        address:      CZ
        registrar:    REG-GRANSY
        created:      04.11.2019 10:46:23
        
        nsset:        NSS:R1S-CES-8079-FA:1
        nserver:      ns.cvut.cz (147.32.1.20, 2001:718:2:2200::100)
        nserver:      nss.cvut.cz (147.32.1.9, 2001:718:2:2200::101)
        tech-c:       R1C-RT-8077-69
        tech-c:       R1C-MN-8075-4B
        tech-c:       SB:R1S-CES-8079-FA
        tech-c:       R1C-JK-8076-A4
        registrar:    REG-ONE
        created:      01.10.2007 02:00:00
        changed:      20.11.2009 08:05:37
        
        contact:      R1C-RT-8077-69
        name:         Radek Trousílek
        address:      Zikova 4
        address:      Praha 6
        address:      16636
        address:      CZ
        registrar:    REG-ONE
        created:      31.08.2006 13:15:00
        changed:      15.05.2018 21:32:00
        
        contact:      R1C-MN-8075-4B
        name:         Michal Neuman
        address:      Zikova 4
        address:      Praha 6
        address:      16636
        address:      CZ
        registrar:    REG-ONE
        created:      31.08.2006 13:05:00
        changed:      15.05.2018 21:32:00
        
        contact:      SB:R1S-CES-8079-FA
        org:          České vysoké učení technické v Praze
        name:         Michal Neuman
        address:      Zikova 4
        address:      Praha 6
        address:      16636
        address:      CZ
        registrar:    REG-GRANSY
        created:      31.08.2006 15:15:00
        changed:      21.07.2020 10:34:04
        
        contact:      R1C-JK-8076-A4
        name:         Jana Krupová
        registrar:    REG-ONE
        created:      31.08.2006 13:15:00
        changed:      22.02.2022 14:11:21
        
        keyset:       AUTO-UJ3Z52N0Z5FN8BUNSDW4PV5TD
        dnskey:       257 3 13 +XkJfh9Fh5wGJVpntLyKQRXkrRQhP+kL3lnJ1SQfG3XTjW9WBBknfO37yynPQtat8S+kIuOJZaSgkLmMfS8drQ==
        tech-c:       CZ-NIC
        registrar:    REG-CZNIC
        created:      24.11.2023 16:38:48
        
        contact:      CZ-NIC
        org:          CZ.NIC, z.s.p.o.
        name:         CZ.NIC, z.s.p.o.
        address:      Milesovska 1136/5
        address:      Praha 3
        address:      130 00
        address:      CZ
        registrar:    REG-CZNIC
        created:      17.10.2008 12:08:21
        changed:      15.05.2018 21:32:00
        
        
        """.trimIndent()
        val expected = Website(
            null,
            null,
            "CVUT-V-PRAZE",
            "REG-GRANSY",
            "CZ",
            LocalDate.of(1996,3, 10),
            LocalDate.of(2023,11, 24),
            LocalDate.of(2024,10, 10)
        )
        val parsed = WebsiteInfoParser.parseWhoIs(rawResponse)
        assertEquals(expected, parsed)
    }

    @Test
    fun parseWhoIsPl() {
        val jsonStr = """
            DOMAIN NAME:           biedronka.pl
            registrant type:       organization
            nameservers:           ns1.domena.pl. [193.239.44.33]
                                   ns2.domena.pl. [91.234.176.240]
                                   ns3.domena.pl. [195.110.49.49]
            created:               2002.11.07 12:00:00
            last modified:         2023.11.15 12:47:47
            renewal date:          2025.11.06 13:00:00

            option created:        2024.03.27 12:22:16
            option expiration date:       2027.03.27 12:22:16

            dnssec:                Unsigned


            REGISTRAR:
            Domena.pl sp. z o.o.
            ul. Gdańska 119 
            85-022 Bydgoszcz
            Polska/Poland
            +48.52 3667777 
            +48.52 3667788
            bok@domena.pl
            www.domena.pl

            WHOIS database responses: https://dns.pl/en/whois

            WHOIS displays data with a delay not exceeding 15 minutes in relation to the .pl Registry system
        """.trimIndent()
        val expected = Website(
            null,
            null,
            "organization",
            "Domena.pl sp. z o.o.",
            "unknown",
            LocalDate.of(2002,11, 7),
            LocalDate.of(2023,11, 15),
            LocalDate.of(2027,3, 27)
        )
        val parsed = WebsiteInfoParser.parseWhoIs(jsonStr)
        assertEquals(expected, parsed)
    }

    @Test
    fun parseRDAPCz() {
        val jsonStr = """
            {"objectClassName": "domain", "rdapConformance": ["rdap_level_0", "fred_version_0"], "handle": "cvut.cz", "ldhName": "cvut.cz", "links": [{"value": "https://rdap.nic.cz/domain/cvut.cz", "rel": "self", "href": "https://rdap.nic.cz/domain/cvut.cz", "type": "application/rdap+json"}], "port43": "whois.nic.cz", "events": [{"eventAction": "registration", "eventDate": "1996-03-10T00:00:00+00:00"}, {"eventAction": "expiration", "eventDate": "2024-10-09T22:00:00+00:00"}, {"eventAction": "last changed", "eventDate": "2023-11-24T15:38:48+00:00"}, {"eventAction": "transfer", "eventDate": "2020-03-24T10:46:05+00:00"}], "entities": [{"objectClassName": "entity", "handle": "CVUT-V-PRAZE", "roles": ["registrant"], "links": [{"value": "https://rdap.nic.cz/entity/CVUT-V-PRAZE", "rel": "self", "href": "https://rdap.nic.cz/entity/CVUT-V-PRAZE", "type": "application/rdap+json"}]}, {"objectClassName": "entity", "handle": "REG-GRANSY", "roles": ["registrar"]}, {"objectClassName": "entity", "handle": "CVUT-JK", "roles": ["administrative"], "links": [{"value": "https://rdap.nic.cz/entity/CVUT-JK", "rel": "self", "href": "https://rdap.nic.cz/entity/CVUT-JK", "type": "application/rdap+json"}]}, {"objectClassName": "entity", "handle": "CVUT-MN", "roles": ["administrative"], "links": [{"value": "https://rdap.nic.cz/entity/CVUT-MN", "rel": "self", "href": "https://rdap.nic.cz/entity/CVUT-MN", "type": "application/rdap+json"}]}, {"objectClassName": "entity", "handle": "CVUT-RT", "roles": ["administrative"], "links": [{"value": "https://rdap.nic.cz/entity/CVUT-RT", "rel": "self", "href": "https://rdap.nic.cz/entity/CVUT-RT", "type": "application/rdap+json"}]}], "status": ["active"], "nameservers": [{"objectClassName": "nameserver", "handle": "ns.cvut.cz", "ldhName": "ns.cvut.cz", "links": [{"value": "https://rdap.nic.cz/nameserver/ns.cvut.cz", "rel": "self", "href": "https://rdap.nic.cz/nameserver/ns.cvut.cz", "type": "application/rdap+json"}], "ipAddresses": {"v4": ["147.32.1.20"], "v6": ["2001:718:2:2200::100"]}}, {"objectClassName": "nameserver", "handle": "nss.cvut.cz", "ldhName": "nss.cvut.cz", "links": [{"value": "https://rdap.nic.cz/nameserver/nss.cvut.cz", "rel": "self", "href": "https://rdap.nic.cz/nameserver/nss.cvut.cz", "type": "application/rdap+json"}], "ipAddresses": {"v4": ["147.32.1.9"], "v6": ["2001:718:2:2200::101"]}}], "fred_nsset": {"objectClassName": "fred_nsset", "handle": "NSS:R1S-CES-8079-FA:1", "links": [{"value": "https://rdap.nic.cz/fred_nsset/NSS:R1S-CES-8079-FA:1", "rel": "self", "href": "https://rdap.nic.cz/fred_nsset/NSS:R1S-CES-8079-FA:1", "type": "application/rdap+json"}], "nameservers": [{"objectClassName": "nameserver", "handle": "ns.cvut.cz", "ldhName": "ns.cvut.cz", "links": [{"value": "https://rdap.nic.cz/nameserver/ns.cvut.cz", "rel": "self", "href": "https://rdap.nic.cz/nameserver/ns.cvut.cz", "type": "application/rdap+json"}], "ipAddresses": {"v4": ["147.32.1.20"], "v6": ["2001:718:2:2200::100"]}}, {"objectClassName": "nameserver", "handle": "nss.cvut.cz", "ldhName": "nss.cvut.cz", "links": [{"value": "https://rdap.nic.cz/nameserver/nss.cvut.cz", "rel": "self", "href": "https://rdap.nic.cz/nameserver/nss.cvut.cz", "type": "application/rdap+json"}], "ipAddresses": {"v4": ["147.32.1.9"], "v6": ["2001:718:2:2200::101"]}}]}, "secureDNS": {"zoneSigned": true, "delegationSigned": true, "keyData": [{"flags": 257, "protocol": 3, "algorithm": 13, "publicKey": "+XkJfh9Fh5wGJVpntLyKQRXkrRQhP+kL3lnJ1SQfG3XTjW9WBBknfO37yynPQtat8S+kIuOJZaSgkLmMfS8drQ=="}], "maxSigLife": 1209600}, "fred_keyset": {"objectClassName": "fred_keyset", "handle": "AUTO-UJ3Z52N0Z5FN8BUNSDW4PV5TD", "links": [{"value": "https://rdap.nic.cz/fred_keyset/AUTO-UJ3Z52N0Z5FN8BUNSDW4PV5TD", "rel": "self", "href": "https://rdap.nic.cz/fred_keyset/AUTO-UJ3Z52N0Z5FN8BUNSDW4PV5TD", "type": "application/rdap+json"}], "dns_keys": [{"flags": 257, "protocol": 3, "algorithm": 13, "publicKey": "+XkJfh9Fh5wGJVpntLyKQRXkrRQhP+kL3lnJ1SQfG3XTjW9WBBknfO37yynPQtat8S+kIuOJZaSgkLmMfS8drQ=="}]}, "notices": [{"title": "Disclaimer", "description": ["(c) 2023 CZ.NIC, z.s.p.o.", "Intended use of supplied data and information", "Data contained in the domain name register, as well as information supplied through public information services of CZ.NIC association, are appointed only for purposes connected with Internet network administration and operation, or for the purpose of legal or other similar proceedings, in process as regards a matter connected particularly with holding and using a concrete domain name."]}]}
        """.trimIndent()
        val expected = Website(
            null,
            null,
            "CVUT-V-PRAZE",
            "REG-GRANSY",
            "unknown",
            LocalDate.of(1996,3, 10),
            LocalDate.of(2023,11, 24),
            LocalDate.of(2024,10, 9)
        )
        val parsed = WebsiteInfoParser.parseRDAP(jsonStr)
        assertEquals(expected, parsed)
    }

    @Test
    fun parseRDAPCom() {
        val jsonStr = """
            {"objectClassName":"domain","handle":"1675047635_DOMAIN_COM-VRSN","ldhName":"BAELDUNG.COM","links":[{"value":"https:\/\/rdap.verisign.com\/com\/v1\/domain\/BAELDUNG.COM","rel":"self","href":"https:\/\/rdap.verisign.com\/com\/v1\/domain\/BAELDUNG.COM","type":"application\/rdap+json"},{"value":"https:\/\/rdap.namecheap.com\/domain\/BAELDUNG.COM","rel":"related","href":"https:\/\/rdap.namecheap.com\/domain\/BAELDUNG.COM","type":"application\/rdap+json"}],"status":["client transfer prohibited"],"entities":[{"objectClassName":"entity","handle":"1068","roles":["registrar"],"publicIds":[{"type":"IANA Registrar ID","identifier":"1068"}],"vcardArray":["vcard",[["version",{},"text","4.0"],["fn",{},"text","NameCheap, Inc."]]],"entities":[{"objectClassName":"entity","roles":["abuse"],"vcardArray":["vcard",[["version",{},"text","4.0"],["fn",{},"text",""],["tel",{"type":"voice"},"uri","tel:+1.6613102107"],["email",{},"text","abuse@namecheap.com"]]]}]}],"events":[{"eventAction":"registration","eventDate":"2011-09-02T11:54:38Z"},{"eventAction":"expiration","eventDate":"2026-09-02T11:54:38Z"},{"eventAction":"last changed","eventDate":"2023-08-03T15:16:04Z"},{"eventAction":"last update of RDAP database","eventDate":"2024-04-01T18:29:50Z"}],"secureDNS":{"delegationSigned":false},"nameservers":[{"objectClassName":"nameserver","ldhName":"LAKAS.NS.CLOUDFLARE.COM"},{"objectClassName":"nameserver","ldhName":"MEERA.NS.CLOUDFLARE.COM"}],"rdapConformance":["rdap_level_0","icann_rdap_technical_implementation_guide_0","icann_rdap_response_profile_0"],"notices":[{"title":"Terms of Use","description":["Service subject to Terms of Use."],"links":[{"href":"https:\/\/www.verisign.com\/domain-names\/registration-data-access-protocol\/terms-service\/index.xhtml","type":"text\/html"}]},{"title":"Status Codes","description":["For more information on domain status codes, please visit https:\/\/icann.org\/epp"],"links":[{"href":"https:\/\/icann.org\/epp","type":"text\/html"}]},{"title":"RDDS Inaccuracy Complaint Form","description":["URL of the ICANN RDDS Inaccuracy Complaint Form: https:\/\/icann.org\/wicf"],"links":[{"href":"https:\/\/icann.org\/wicf","type":"text\/html"}]}]}
        """.trimIndent()
        val expected = Website(
            null,
            null,
            "\"BAELDUNG.COM\"",
            "1068",
            "unknown",
            LocalDate.of(2011,9, 2),
            LocalDate.of(2023,8, 3),
            LocalDate.of(2026,9, 2)
        )
        val parsed = WebsiteInfoParser.parseRDAP(jsonStr)
        assertEquals(expected, parsed)
    }

    @Test
    fun parseWrongRDAP() {
        val str = "this is not a valid rdap json"
        val parsed = WebsiteInfoParser.parseRDAP(str)
        assertNull(parsed)
    }
}