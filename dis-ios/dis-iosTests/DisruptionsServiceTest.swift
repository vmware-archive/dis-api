import XCTest
import UIKit
import Nimble
import Nocilla
@testable import dis_ios

class DisruptionsServiceTest: XCTestCase {
    
    let timeoutError = NSError(domain: NSURLErrorDomain, code: NSURLErrorTimedOut, userInfo: nil)
    
    var service: DisruptionsService!
    
    override func setUp() {
        super.setUp()
        LSNocilla.sharedInstance().start()
        service = DisruptionsService()
    }
    
    override func tearDown() {
        super.tearDown()
        LSNocilla.sharedInstance().clearStubs()
        LSNocilla.sharedInstance().stop()
    }
    
    func testServiceSendsDataOnSuccess() {
        let data = "{\"disruptions\":[{\"line\":\"District\", \"status\":\"Minor Delays\"}]}".dataUsingEncoding(NSUTF8StringEncoding)
        stubRequest("GET", "http://localhost:8080/disruptions.json").andReturnRawResponse(data)
        
        var disruptions: [Disruption]? = nil
        var error: String? = nil
        
        let expectation = expectationWithDescription("")
        
        service.getDisruptions({ d in
            disruptions = d
            expectation.fulfill()
        },
        onError: { e in
            error = e
            expectation.fulfill()
        })
        
        self.waitForExpectationsWithTimeout(5.0) { _ in
            expect(disruptions).toNot(beNil())
            expect(disruptions?.count).to(equal(1))
            expect(disruptions?.first?.lineName).to(equal("District"))
            expect(disruptions?.first?.status).to(equal("Minor Delays"))
            expect(error).to(beNil())
        }
    }
    
    func testServiceTimesOut() {
        stubRequest("GET", "http://localhost:8080/disruptions.json").andFailWithError(timeoutError)
        
        var disruptions: [Disruption]? = nil
        var error: String? = nil
        let expectation = expectationWithDescription("wait for block")
        
        service.getDisruptions({ d in
            disruptions = d
            expectation.fulfill()
            
        }, onError: { e in
            error = e
            expectation.fulfill()
        })
        
        self.waitForExpectationsWithTimeout(5.0) { _ in
            expect(disruptions).to(beNil())
            expect(error).to(equal("Couldn't retrieve data from server 💩"))
        }
    }
    
}

