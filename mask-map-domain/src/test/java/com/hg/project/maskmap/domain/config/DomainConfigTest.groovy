package com.hg.project.maskmap.domain.config

import com.hg.project.maskmap.domain.client.MaskDataClient
import com.hg.project.maskmap.domain.entity.Pharmacy
import com.hg.project.maskmap.domain.service.DataPortalService
import com.hg.project.maskmap.domain.service.MapService
import org.junit.Ignore
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.data.mongodb.core.MongoTemplate

@SpringBootTest(classes = DomainConfig.class)
class DomainConfigTest extends spock.lang.Specification {
    @Autowired
    private DataPortalService dataPortalService
    @Autowired
    private MongoTemplate mongoTemplate
    @Autowired
    private MapService mapService;

    @Ignore
    def "test"() {
        when:
        def serviceKey = ""
        def pageNo = 2
        def numOfRows = 1000

        while (true) {
            def data = dataPortalService.getEntirePharmarcy(serviceKey, pageNo, numOfRows)
            def pharmacy = dataPortalService.convert(data.getBody().items.item)

            for (Pharmacy a : pharmacy) {
                mongoTemplate.save(a)
            }

            if (data.body.items.item.size() < 1000) {
                break
            }
            pageNo++
            System.out.println("page no is " + pageNo)
        }
        then:
        true
    }

    @Autowired
    private MaskDataClient maskDataClient
    def "update code"() {
        when:
        def actual = maskDataClient.getStore(1)
        then:
        true

    }

    def "조회"() {
        when:
        def actual = mapService.findByGeoLocation(127.1150501, 37.325271, 1_000)

        then:
        actual.size() > 1
    }
}
