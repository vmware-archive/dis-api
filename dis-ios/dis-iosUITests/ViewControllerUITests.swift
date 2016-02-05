import XCTest
import Nimble
import GCDWebServer
import SwiftyJSON

class ViewControllerUITests: XCTestCase {

    var webServer: GCDWebServer!
    var app: XCUIApplication!

    override func setUp() {
        super.setUp()

        continueAfterFailure = false

        self.app = XCUIApplication()
        self.webServer = GCDWebServer()
    }

    override func tearDown() {
        super.tearDown()

        self.webServer!.stop()

    }

    private func startWebServerWithResponse(response: String) {
        self.webServer.addDefaultHandlerForMethod("GET", requestClass: GCDWebServerRequest.self){ (request) -> GCDWebServerResponse! in
            return GCDWebServerDataResponse(
                data: response.dataUsingEncoding(NSUTF8StringEncoding, allowLossyConversion: false)!,
                contentType: "application/json")
        }

        do {
            try self.webServer!.startWithOptions([
                GCDWebServerOption_BindToLocalhost: true,
                GCDWebServerOption_Port: 8080,
                GCDWebServerOption_AutomaticallySuspendInBackground: false
                ])
        } catch let error {
            print("Server could not be started: \(error)")
        }
    }

    func testWhenThereAreNoDisruptionsItSaysNoDisruptions() {
        startWebServerWithResponse("{\"disruptions\":[]}")

        self.app.launch()

        expect(self.app.staticTexts["No Disruptions"].exists).to(beTrue())
    }

    func testWhenThereAreDisruptionsItDoesNotSayNoDisruptions() {
        startWebServerWithResponse("{\"disruptions\":[{\"line\":\"District\",\"startTime\":\"15:25\",\"status\":\"Part Suspended\"}]}")

        self.app.launch()

        expect(self.app.staticTexts["No Disruptions"].exists).to(beFalse())
    }

    func testWhenThereAreDisruptionsItShowsDisruptedLines() {
        startWebServerWithResponse("{\"disruptions\":[{\"line\":\"District\"}]}")

        self.app.launch()

        let disruptionsTable = self.app!.tables.elementBoundByIndex(0)

        expect(disruptionsTable).notTo(beNil())
        expect(disruptionsTable.cells.count).to(equal(1))
        expect(disruptionsTable.cells.staticTexts["District"].exists).to(beTrue())
    }
    
    func testWhenUserPullsDownisruptionsTableIsRefreshed() {
        startWebServerWithResponse("{\"disruptions\":[{\"line\":\"District\"}]}")
        
        self.app.launch()
        
        startWebServerWithResponse("{\"disruptions\":[{\"line\":\"Jubilee\"}]}")
        
        pullToRefresh(fromText: "District")
        
        let disruptionsTable = self.app!.tables.elementBoundByIndex(0)
        expect(disruptionsTable).notTo(beNil())
        expect(disruptionsTable.cells.count).to(equal(1))
        expect(disruptionsTable.cells.staticTexts["Jubilee"].exists).to(beTrue())
        
    }
    
    func pullToRefresh(fromText fromText: String) {
        let firstCell = self.app.staticTexts[fromText]
        let start = firstCell.coordinateWithNormalizedOffset(CGVectorMake(0, 0))
        let finish = firstCell.coordinateWithNormalizedOffset(CGVectorMake(0, 8))
        start.pressForDuration(0, thenDragToCoordinate: finish)
    }
}
