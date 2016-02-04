import XCTest
import Nimble
import GCDWebServer

class ViewControllerUITests: XCTestCase {
    
    var webServer: GCDWebServer?

    override func setUp() {
        super.setUp()
        
        continueAfterFailure = false
        XCUIApplication().launch()
        self.webServer = GCDWebServer()
        
    }
    
    override func tearDown() {
        super.tearDown()
        webServer!.stop()
    }
    
    func testWhenThereAreNoDisruptionsItSaysNoDisruptions() {
        
        self.webServer!.addHandlerForMethod("GET", path: "/disruptions.json", requestClass: GCDWebServerRequest.self){ (request) -> GCDWebServerResponse! in
            return GCDWebServerDataResponse(JSONObject: "{\"disruptions\":[]}")
        }
        
        self.webServer!.startWithPort(8080, bonjourName: nil)
        expect(XCUIApplication().staticTexts["No Disruptions"].exists).to(beTrue())
    }
    
    func testWhenThereAreDisruptionsItDoesNotSayNoDisruptions() {
        
        self.webServer!.addHandlerForMethod("GET", path: "/disruptions.json", requestClass: GCDWebServerRequest.self){ (request) -> GCDWebServerResponse! in
            return GCDWebServerDataResponse(JSONObject: "{\"disruptions\":[{\"line\":\"District\",\"startTime\":\"15:25\",\"status\":\"Part Suspended\"}]}")
        }
        
        self.webServer!.startWithPort(8080, bonjourName: nil)
        
        expect(XCUIApplication().staticTexts["No Disruptions"].exists).to(beFalse())
    }
}
