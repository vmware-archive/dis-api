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

        self.webServer.stop()

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
    
    private func startWebServerWithTimeOutResponse() {
        self.webServer.addDefaultHandlerForMethod("GET", requestClass: GCDWebServerRequest.self){ (request) -> GCDWebServerResponse! in
            return GCDWebServerDataResponse(statusCode: 408)
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
    
    func testWhenUserPullsDownOldDataIsClearedAndTableShowsNewData() {
        startWebServerWithResponse("{\"disruptions\":[{\"line\":\"District\"}]}")
        
        self.app.launch()
        
        self.webServer.stop()
        
        startWebServerWithResponse("{\"disruptions\":[{\"line\":\"Jubilee\"}]}")
        
        pullToRefresh(fromText: "District")
        
        let disruptionsTable = self.app!.tables.elementBoundByIndex(0)
        expect(disruptionsTable).notTo(beNil())
        expect(disruptionsTable.cells.count).to(equal(1))
        expect(disruptionsTable.cells.staticTexts["Jubilee"].exists).to(beTrue())
        expect(disruptionsTable.cells.staticTexts["District"].exists).to(beFalse())
    }
    
    func testWhenRequestTakesMoreThan10SecondsItShowsErrorMessage() {
        startWebServerWithTimeOutResponse()
        
        self.app.launch()
        
        expect(self.app.staticTexts["Couldn't retrieve data from server :("].exists).to(beTrue())
    }
        
    func pullToRefresh(fromText fromText: String) {
        // http://stackoverflow.com/questions/31301798/replicate-pull-to-refresh-in-xctest-ui-testing
        let firstCell = self.app.staticTexts[fromText]
        let start = firstCell.coordinateWithNormalizedOffset(CGVectorMake(0, 1)) // make sure you don't go too high and get the notification center!
        let finish = firstCell.coordinateWithNormalizedOffset(CGVectorMake(0, 10)) // make sure this number is big enough!
        start.pressForDuration(0, thenDragToCoordinate: finish)
    }
}
