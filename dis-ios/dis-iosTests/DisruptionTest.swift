import Nimble
import SwiftyJSON
import XCTest
@testable import dis_ios

class DisruptionTest: XCTestCase {
    
    override func setUp() {
        super.setUp()
    }
    
    func testValidJSON() {
        let json = JSON(data: "{\"line\":\"District\", \"status\":\"Minor Delays\"}".dataUsingEncoding(NSUTF8StringEncoding)!)
        let disruption = Disruption(json: json)
        
        expect(disruption?.lineName).to(equal("District"))
        expect(disruption?.status).to(equal("Minor Delays"))
    }

    func testMissingLineInvalidJSON() {
        let json = JSON(data: "{\"goat\":\"District\", \"status\":\"Minor Delays\"}".dataUsingEncoding(NSUTF8StringEncoding)!)
        let disruption = Disruption(json: json)
        
        expect(disruption).to(beNil())
    }

    func testMissingStatusInvalidJSON() {
        let json = JSON(data: "{\"line\":\"District\"}".dataUsingEncoding(NSUTF8StringEncoding)!)
        let disruption = Disruption(json: json)
        
        expect(disruption?.lineName).to(equal("District"))
        expect(disruption?.status).to(beNil())
    }
    
}
