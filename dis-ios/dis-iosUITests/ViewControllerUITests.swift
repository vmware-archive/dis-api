import XCTest
import Nimble

class ViewControllerUITests: XCTestCase {
    
    override func setUp() {
        super.setUp()
        
        continueAfterFailure = false
        XCUIApplication().launch()
    }
    
    override func tearDown() {
        super.tearDown()
    }
    
    func testWhenThereAreNoDisruptionsItSaysNoDisruptions() {
        expect(XCUIApplication().staticTexts["No Disruptions"].exists).to(beTrue())
    }    
}
