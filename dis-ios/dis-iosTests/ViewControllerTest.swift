import XCTest
import Nimble
@testable import dis_ios

extension Disruption {
    
    init(lineName: String, status: String) {
        self.lineName = lineName
        self.status = status
    }
    
}

class ViewControllerTest: XCTestCase {
    
    class StubDisruptionsServiceSuccess: DisruptionsServiceProtocol {
        func getDisruptions(onSuccess: (disruptions: [Disruption]) -> Void, onError: (error: String) -> Void) {
            onSuccess(disruptions: [
                Disruption(lineName: "Northern", status: "404 train not found"),
                Disruption(lineName: "Jubilee", status: "Regicide imminent"),
                Disruption(lineName: "Hammersmith & City", status: "Lost to the Gunners"),
            ])
        }
    }
    
    class StubDisruptionsServiceSuccessNoDisruptions: DisruptionsServiceProtocol {
        func getDisruptions(onSuccess: (disruptions: [Disruption]) -> Void, onError: (error: String) -> Void) {
            onSuccess(disruptions: [])
        }
    }
    
    class StubDisruptionsServiceNetworkError: DisruptionsServiceProtocol {
        func getDisruptions(onSuccess: (disruptions: [Disruption]) -> Void, onError: (error: String) -> Void) {
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
        viewController.disruptionsService = StubDisruptionsServiceSuccess()
        viewController.notificationCenter.postNotificationName(UIApplicationWillEnterForegroundNotification, object: nil)
                
        expect(self.viewController.tableView.numberOfRowsInSection(0)).to(equal(3))
    }
    
    func testTableBackgroundViewIsNilWhenDisruptionsAreReturned() {
        viewController.disruptionsService = StubDisruptionsServiceSuccess()
        viewController.viewWillAppear(false)
        expect(self.viewController.tableView.backgroundView).to(beNil())
    }
    
    func testTableViewDataSourceRespondsCorrectlyWhenDisruptionsAreReturned() {
        viewController.disruptionsService = StubDisruptionsServiceSuccess()
        viewController.viewWillAppear(false)
        
        expect(self.viewController.tableView.numberOfRowsInSection(0)).to(equal(3))

        let cell0 = self.viewController.tableView.cellForRowAtIndexPath(NSIndexPath(forRow: 0, inSection: 0))
        expect(cell0?.textLabel?.text).to(equal("Northern"))
        expect(cell0?.detailTextLabel?.text).to(equal("404 train not found"))
        
        let cell1 = self.viewController.tableView.cellForRowAtIndexPath(NSIndexPath(forRow: 1, inSection: 0))
        expect(cell1?.textLabel?.text).to(equal("Jubilee"))
        expect(cell1?.detailTextLabel?.text).to(equal("Regicide imminent"))

        let cell2 = self.viewController.tableView.cellForRowAtIndexPath(NSIndexPath(forRow: 2, inSection: 0))
        expect(cell2?.textLabel?.text).to(equal("Hammersmith & City"))
        expect(cell2?.detailTextLabel?.text).to(equal("Lost to the Gunners"))
    }
    
    func testTableBackgroundViewHasMessageWhenThereAreNoDisruptions() {
        viewController.disruptionsService = StubDisruptionsServiceSuccessNoDisruptions()
        viewController.viewWillAppear(false)
        expect(self.viewController.tableView.backgroundView).to(beAKindOf(UIView.self))
        expect(self.viewController.errorViewLabel.text).to(equal("No Disruptions"))
    }
    
    func testTableBackgroundViewHasMessageWhenAnErrorIsReturned() {
        viewController.disruptionsService = StubDisruptionsServiceNetworkError()
        viewController.viewWillAppear(false)
        expect(self.viewController.tableView.backgroundView).to(beAKindOf(UIView.self))
        expect(self.viewController.errorViewLabel.text).to(equal("Couldn't retrieve data from server 💩"))
    }
    
    func testRefreshControllerEndsRefreshingWhenViewDisappears() {
        self.viewController.refreshControl!.beginRefreshing()
        
        viewController.viewWillDisappear(false)
        expect(self.viewController.refreshControl?.refreshing).to(beFalse())
    }

}
