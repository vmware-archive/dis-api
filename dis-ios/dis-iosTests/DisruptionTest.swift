import Nimble
import SwiftyJSON
import XCTest
@testable import dis_ios

class DisruptionTest: XCTestCase {
    
    override func setUp() {
        super.setUp()
    }
    
    func testValidJSON() {
        
        let disruption = Disruption(json: JSON([
            "line": ["name": "District", "foregroundColor": "#000000", "backgroundColor": "#FFFFFF"],
            "status": "Minor Delays",
            "startTime": "12:25",
            "endTime": "12:55"
        ]))
        
        expect(disruption?.status).to(equal("Minor Delays"))
        expect(disruption?.startTime).to(equal("12:25"))
        expect(disruption?.endTime).to(equal("12:55"))
        expect(disruption?.line).toNot(beNil())

    }

    func testMissingLineInvalidJSON() {
        let disruptionWithNilLine = Disruption(json: JSON([
            "line": nil,
            "status": "Minor Delays",
            "startTime": "12:25",
            "endTime": "12:55"
            ]))
        
        expect(disruptionWithNilLine).to(beNil())
        
        let disruptionWithoutLineKey = Disruption(json: JSON([
            "status": "Minor Delays",
            "startTime": "12:25",
            "endTime": "12:55"
            ]))
        
        expect(disruptionWithoutLineKey).to(beNil())
    }

    func testMissingOptionalValues() {
        let disruption = Disruption(json: JSON([
            "line": ["name": "District", "foregroundColor": "#000000", "backgroundColor": "#FFFFFF"],
            "status": nil,
            "startTime": nil,
            "endTime": nil
        ]))
        
        expect(disruption?.line.name).to(equal("District"))
        expect(disruption?.status).to(beNil())
        expect(disruption?.startTime).to(beNil())
        expect(disruption?.endTime).to(beNil())

    }
    
}
