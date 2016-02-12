import Nimble
import SwiftyJSON
import XCTest
@testable import dis_ios

class LineTest: XCTestCase {

    override func setUp() {
        super.setUp()
    }
    
    override func tearDown() {
        super.tearDown()
    }

    func testValidJSON() {
        let json = JSON(["name": "District", "foregroundColor": "#FF0000", "backgroundColor": "#00FF00"])
        
        let line = Line(json: json)
        
        expect(line?.name).to(equal("District"))
        expect(line?.foregroundColor).to(equal(UIColor.redColor()))
        expect(line?.backgroundColor).to(equal(UIColor.greenColor()))
    }
    
    func testLineNamePresence() {
        let lineWithBlankName = Line(json: JSON(["name": "", "foregroundColor": "#FF0000", "backgroundColor": "#00FF00"]))
        expect(lineWithBlankName).to(beNil())
        
        let lineWithNilName = Line(json: JSON(["name": nil, "foregroundColor": "#FF0000", "backgroundColor": "#00FF00"]))
        expect(lineWithNilName).to(beNil())
        
        let lineWithMissingNameKey = Line(json: JSON(["foregroundColor": "#FF0000", "backgroundColor": "#00FF00"]))
        expect(lineWithMissingNameKey).to(beNil())
    }

}
