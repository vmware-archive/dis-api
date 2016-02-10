import XCTest
import UIKit
import Nimble
import Nocilla
@testable import dis_ios

class DisruptionsServiceTests: XCTestCase {
    
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
    
    func testServiceTimesOut() {
        stubRequest("GET", "http://localhost:8080/disruptions.json").andFailWithError(timeoutError)
        
        var disruptions: [String]? = nil
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

