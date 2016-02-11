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
        let data = "{\"disruptions\":[{\"line\":\"District\", \"status\":\"Minor Delays\", \"startTime\":\"12:25\", \"endTime\":\"12:55\"}]}".dataUsingEncoding(NSUTF8StringEncoding)
        stubRequest("GET", "http://localhost:8080/disruptions.json").andReturnRawResponse(data)
        
        var disruptions: [Disruption]? = nil
        var error: String? = nil
        
        let expectation = expectationWithDescription("")
        
        service.getDisruptions() { result in
            switch result {
            case .Success(let d):
                disruptions = d
            case .HTTPError(let e):
                error = e
            }
            
            expectation.fulfill()
        }
        
        self.waitForExpectationsWithTimeout(5.0) { _ in
            expect(disruptions).toNot(beNil())
            expect(disruptions?.count).to(equal(1))
            expect(disruptions?.first?.lineName).to(equal("District"))
            expect(disruptions?.first?.status).to(equal("Minor Delays"))
            expect(disruptions?.first?.startTime).to(equal("12:25"))
            expect(disruptions?.first?.endTime).to(equal("12:55"))
            expect(error).to(beNil())
        }
    }
    
    func testBrokenDisruptionItemsFromServerAreIgnored() {
        let data = "{\"disruptions\":[{\"line\":\"District\", \"status\":\"Minor Delays\"}, {\"line\":\"Northern\", \"status\":\"Minor Delays\"}, {\"goat\":\"Nowhere\", \"status\":\"Minor Delays\"}, {\"line\":\"Hammersmith & City\"}]}".dataUsingEncoding(NSUTF8StringEncoding)
        stubRequest("GET", "http://localhost:8080/disruptions.json").andReturnRawResponse(data)
        
        var disruptions: [Disruption]? = nil
        var error: String? = nil
        
        let expectation = expectationWithDescription("")
        
        service.getDisruptions() { result in
            switch result {
            case .Success(let d):
                disruptions = d
            case .HTTPError(let e):
                error = e
            }
            
            expectation.fulfill()
        }
        
        self.waitForExpectationsWithTimeout(5.0) { _ in
            expect(disruptions).toNot(beNil())
            expect(disruptions?.count).to(equal(3))
            expect(error).to(beNil())
        }
    }
    
    func testServiceTimesOut() {
        stubRequest("GET", "http://localhost:8080/disruptions.json").andFailWithError(timeoutError)
        
        var disruptions: [Disruption]? = nil
        var error: String? = nil
        let expectation = expectationWithDescription("wait for block")
        
        service.getDisruptions() { result in
            switch result {
            case .Success(let d):
                disruptions = d
            case .HTTPError(let e):
                error = e
            }
            
            expectation.fulfill()
        }
        
        self.waitForExpectationsWithTimeout(5.0) { _ in
            expect(disruptions).to(beNil())
            expect(error).to(equal("Couldn't retrieve data from server 💩"))
        }
    }
    
}

