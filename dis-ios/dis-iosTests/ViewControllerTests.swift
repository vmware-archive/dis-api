import XCTest
import Nimble
@testable import dis_ios

class ViewControllerTests: XCTestCase {
    
    class StubDisruptionsServiceSuccess: DisruptionsServiceProtocol {
        private let _disruptions: [String]
        
        init(disruptions: [String]) {
            _disruptions = disruptions
        }
        
        func getDisruptions(onSuccess: (disruptions: [String]) -> Void, onError: (error: String) -> Void) {
            onSuccess(disruptions: self._disruptions)
        }
    }
    
    class StubDisruptionServiceNetworkError: DisruptionsServiceProtocol {
        func getDisruptions(onSuccess: (disruptions: [String]) -> Void, onError: (error: String) -> Void) {
            return onError(error: "Couldn't retrieve data from server 💩")
        }
    }
    
    var viewController: ViewController!
    
    override func setUp() {
        let storyboard = UIStoryboard(name: "Main", bundle: NSBundle.mainBundle())
        viewController = storyboard.instantiateInitialViewController() as! ViewController
        
        let _ = viewController.view
    }
    
    func testDisruptionsAreRefreshedWhenAppEntersForeground() {
        viewController.disruptionsService = StubDisruptionsServiceSuccess(disruptions: ["Jubilee"])
        viewController.notificationCenter.postNotificationName(UIApplicationWillEnterForegroundNotification, object: nil)
                
        expect(self.viewController.disruptions.count).to(equal(1))
    }
    
    func testTableBackgroundViewIsNilWhenDisruptionsAreReturned() {
        viewController.disruptionsService = StubDisruptionsServiceSuccess(disruptions: ["Jubilee"])
        viewController.viewWillAppear(false)
        expect(self.viewController.tableView.backgroundView).to(beNil())
    }
    
    func testTableBackgroundViewHasMessageWhenThereAreNoDisruptions() {
        viewController.disruptionsService = StubDisruptionsServiceSuccess(disruptions: [])
        viewController.viewWillAppear(false)
        expect(self.viewController.tableView.backgroundView).to(beAKindOf(UIView.self))
        expect(self.viewController.errorViewLabel.text).to(equal("No Disruptions"))
    }
    
    func testTableBackgroundViewHasMessageWhenAnErrorIsReturned() {
        viewController.disruptionsService = StubDisruptionServiceNetworkError()
        viewController.viewWillAppear(false)
        expect(self.viewController.tableView.backgroundView).to(beAKindOf(UIView.self))
        expect(self.viewController.errorViewLabel.text).to(equal("Couldn't retrieve data from server 💩"))
    }

}
