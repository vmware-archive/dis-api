import XCTest
import Nimble
@testable import dis_ios

extension Disruption {
    
    init(lineName: String, status: String, startTime: String, endTime: String) {
        self.lineName = lineName
        self.status = status
        self.startTime = startTime
        self.endTime = endTime
    }
    
}

class DisruptionListViewControllerTest: XCTestCase {
    
    class StubDisruptionServiceSuccess: DisruptionServiceProtocol {
        func getDisruptions(completion: (result: Result<[Disruption]>) -> Void) {
            completion(result: .Success([
                Disruption(lineName: "Northern", status: "404 train not found", startTime: "12:25", endTime: "12:55"),
                Disruption(lineName: "Jubilee", status: "Regicide imminent", startTime: "12:50", endTime: "13:10"),
                Disruption(lineName: "Hammersmith & City", status: "Lost to the Gunners", startTime: "13:05", endTime: "13:45"),
            ]))
        }
    }
    
    class StubDisruptionServiceSuccessNoDisruptions: DisruptionServiceProtocol {
        func getDisruptions(completion: (result: Result<[Disruption]>) -> Void) {
            completion(result: .Success([]))
        }
    }
    
    class StubDisruptionServiceNetworkError: DisruptionServiceProtocol {
        func getDisruptions(completion: (result: Result<[Disruption]>) -> Void) {
            completion(result: .HTTPError(message: "Couldn't retrieve data from server 💩"))
        }
    }
    
    var viewController: DisruptionListViewController!
    
    override func setUp() {
        let storyboard = UIStoryboard(name: "Main", bundle: NSBundle.mainBundle())
        viewController = storyboard.instantiateInitialViewController() as! DisruptionListViewController
        
        let _ = viewController.view
    }
    
    func testDisruptionsAreRefreshedWhenAppEntersForeground() {
        viewController.disruptionsService = StubDisruptionServiceSuccess()
        viewController.notificationCenter.postNotificationName(UIApplicationWillEnterForegroundNotification, object: nil)
                
        expect(self.viewController.tableView.numberOfRowsInSection(0)).to(equal(3))
    }
    
    func testTableBackgroundViewIsNilWhenDisruptionsAreReturned() {
        viewController.disruptionsService = StubDisruptionServiceSuccess()
        viewController.viewWillAppear(false)
        expect(self.viewController.tableView.backgroundView).to(beNil())
    }
    
    func testTableViewDataSourceRespondsCorrectlyWhenDisruptionsAreReturned() {
        viewController.disruptionsService = StubDisruptionServiceSuccess()
        viewController.viewWillAppear(false)
        
        expect(self.viewController.tableView.numberOfRowsInSection(0)).to(equal(3))

        let cell0 = self.viewController.tableView.cellForRowAtIndexPath(NSIndexPath(forRow: 0, inSection: 0)) as! DisruptionCell
        expect(cell0.lineNameLabel?.text).to(equal("Northern"))
        expect(cell0.statusLabel?.text).to(equal("404 train not found"))
        expect(cell0.startTimeLabel?.text).to(equal("12:25"))
        expect(cell0.endTimeLabel?.text).to(equal("12:55"))
        
        let cell1 = self.viewController.tableView.cellForRowAtIndexPath(NSIndexPath(forRow: 1, inSection: 0)) as! DisruptionCell
        expect(cell1.lineNameLabel?.text).to(equal("Jubilee"))
        expect(cell1.statusLabel?.text).to(equal("Regicide imminent"))
        expect(cell1.startTimeLabel?.text).to(equal("12:50"))
        expect(cell1.endTimeLabel?.text).to(equal("13:10"))
        
        let cell2 = self.viewController.tableView.cellForRowAtIndexPath(NSIndexPath(forRow: 2, inSection: 0)) as! DisruptionCell
        expect(cell2.lineNameLabel?.text).to(equal("Hammersmith & City"))
        expect(cell2.statusLabel?.text).to(equal("Lost to the Gunners"))
        expect(cell2.startTimeLabel?.text).to(equal("13:05"))
        expect(cell2.endTimeLabel?.text).to(equal("13:45"))
        
    }
    
    func testTableBackgroundViewHasMessageWhenThereAreNoDisruptions() {
        viewController.disruptionsService = StubDisruptionServiceSuccessNoDisruptions()
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
    
    func testRefreshControllerEndsRefreshingWhenViewDisappears() {
        self.viewController.refreshControl!.beginRefreshing()
        
        viewController.viewWillDisappear(false)
        expect(self.viewController.refreshControl?.refreshing).to(beFalse())
    }

}
