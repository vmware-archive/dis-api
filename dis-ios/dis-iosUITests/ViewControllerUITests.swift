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

        app = XCUIApplication()
        webServer = GCDWebServer()
    }

    override func tearDown() {
        super.tearDown()

        webServer.stop()

    }

    private func startWebServerWithResponse(response: String) {
        webServer.addDefaultHandlerForMethod("GET", requestClass: GCDWebServerRequest.self){ (request) -> GCDWebServerResponse! in

            return GCDWebServerDataResponse(
                data: response.dataUsingEncoding(NSUTF8StringEncoding, allowLossyConversion: false)!,
                contentType: "application/json")
        }

        do {
            try webServer!.startWithOptions([
                GCDWebServerOption_BindToLocalhost: true,
                GCDWebServerOption_Port: 8080,
                GCDWebServerOption_AutomaticallySuspendInBackground: false
                ])
        } catch let error {
            print("Server could not be started: \(error)")
        }
    }
    
    private func startWebServerWithTimeOutResponse() {
        webServer.addDefaultHandlerForMethod("GET", requestClass: GCDWebServerRequest.self){ (request) -> GCDWebServerResponse! in
            return GCDWebServerDataResponse(statusCode: 408)
        }
        
        do {
            try webServer!.startWithOptions([
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

        app.launch()
        
        expect(self.app.tables["No Disruptions"].exists).to(beTrue())
    }

    func testWhenThereAreDisruptionsItShowsDisruptedLines() {
        startWebServerWithResponse("{\"disruptions\":[{\"line\":\"District\"}]}")

        app.launch()

        let disruptionsTable = app.tables.elementBoundByIndex(0)

        expect(disruptionsTable).notTo(beNil())
        expect(disruptionsTable.cells.count).to(equal(1))
        expect(disruptionsTable.cells.staticTexts["District"].exists).to(beTrue())
    }
    
    func testWhenUserPullsDownOldDataIsClearedAndTableShowsNewData() {
        startWebServerWithResponse("{\"disruptions\":[{\"line\":\"District\"}]}")
        
        app.launch()
        
        webServer.stop()
        
        startWebServerWithResponse("{\"disruptions\":[{\"line\":\"Jubilee\"}]}")
        
        app.swipeDown()
        
        let disruptionsTable = app.tables.elementBoundByIndex(0)
        expect(disruptionsTable).notTo(beNil())
        expect(disruptionsTable.cells.count).to(equal(1))
        expect(disruptionsTable.staticTexts["Jubilee"].exists).to(beTrue())
        expect(disruptionsTable.staticTexts["District"].exists).to(beFalse())
    }
    
    func testWhenRequestTakesMoreThan10SecondsItShowsErrorMessage() {
        startWebServerWithTimeOutResponse()
        
        app.launch()
        
        expect(self.app.tables["Couldn't retrieve data from server 💩"].exists).to(beTrue())
    }
}
