//                    def record = 'subdomain.example.com'
//                    def domain = 'example.com'
//                    def apiToken = 'YOUR_API_TOKEN'
//                    def ipAddress = '192.0.2.1'
//                    DnsUtils.checkAndCreateDnsRecord(record, domain, apiToken, ipAddress)

package org.akarintitech

import static groovyx.net.http.HttpBuilder.configure

class DnsUtils {
    static void checkAndCreateDnsRecord(String record, String domain, String apiToken, String ipAddress) {
        def zoneId = getZoneId(domain, apiToken)
        if (!zoneId) {
            println "Zone ID not found for domain: $domain"
            return
        }

        def recordExists = checkDnsRecord(zoneId, record, apiToken)
        if (recordExists) {
            println "DNS record already exists for record: $record"
        } else {
            createDnsRecord(zoneId, record, apiToken, ipAddress)
            println "DNS record created for record: $record"
        }
    }

    static def getZoneId(String domain, String apiToken) {
        def response = configure {
            request.uri = 'https://api.cloudflare.com/client/v4/zones'
            request.headers['Authorization'] = "Bearer $apiToken"
            request.headers['Content-Type'] = 'application/json'
        }.get {
            request.uri.query = [name: domain]
        }

        def zone = response.result.find { it.name == domain }
        return zone?.id
    }

    static def checkDnsRecord(String zoneId, String record, String apiToken) {
        def response = configure {
            request.uri = "https://api.cloudflare.com/client/v4/zones/$zoneId/dns_records"
            request.headers['Authorization'] = "Bearer $apiToken"
            request.headers['Content-Type'] = 'application/json'
        }.get {
            request.uri.query = [type: 'A', name: record]
        }

        return response.result.any { it.name == record }
    }

    static void createDnsRecord(String zoneId, String record, String apiToken, String ipAddress) {
        configure {
            request.uri = "https://api.cloudflare.com/client/v4/zones/$zoneId/dns_records"
            request.headers['Authorization'] = "Bearer $apiToken"
            request.headers['Content-Type'] = 'application/json'
        }.post {
            request.body = [
                type: 'A',
                name: record,
                content: ipAddress,
                ttl: 3600,
                proxied: false
            ]
        }
    }
}
