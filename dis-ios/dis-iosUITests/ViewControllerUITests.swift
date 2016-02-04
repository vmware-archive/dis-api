import XCTest
import Nimble
import GCDWebServer
import SwiftyJSON

class ViewControllerUITests: XCTestCase {
    
    var webServer: GCDWebServer?
    
    override func setUp() {
        super.setUp()
        
        continueAfterFailure = false

        self.webServer = GCDWebServer()
    }
    
    override func tearDown() {
        super.tearDown()
        
        self.webServer!.stop()
    }
    
    private func startWebServerWithResponse(response: String) {
        self.webServer!.addDefaultHandlerForMethod("GET", requestClass: GCDWebServerRequest.self){ (request) -> GCDWebServerResponse! in
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
        
        XCUIApplication().launch()
        
        expect(XCUIApplication().staticTexts["No Disruptions"].exists).to(beTrue())
    }
    
    func testWhenThereAreDisruptionsItDoesNotSayNoDisruptions() {
        startWebServerWithResponse("{\"disruptions\":[{\"line\":\"District\",\"startTime\":\"15:25\",\"status\":\"Part Suspended\"}]}")
        
        XCUIApplication().launch()
        
        expect(XCUIApplication().staticTexts["No Disruptions"].exists).to(beFalse())
    }
}
