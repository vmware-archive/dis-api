import XCTest
import Nimble
@testable import dis_ios

class ViewControllerTests: XCTestCase {
    
    class MockDisruptionsService: DisruptionsServiceProtocol {
        private var disruptions: [String]!
        
        init(disruptions: [String]) {
            self.disruptions = disruptions
        }
        
        func getDisruptions(onSuccess: (disruptions: [String]) -> Void, onError: (error: String) -> Void) {
            onSuccess(disruptions: self.disruptions)
        }
    }
    
    class MockNSNotificationCenter: NSNotificationCenter {
        var observerCount = 0
        var postCount = 0
        var lastPostedNotificationName:String?
        
        override func addObserver(observer: AnyObject, selector aSelector: Selector, name aName: String?, object anObject: AnyObject?) {
            observerCount++
        }
        
        override func postNotificationName(aName: String?, object anObject: AnyObject?) {
            lastPostedNotificationName = aName!
            postCount++
        }
    }
    
    var viewController: ViewController!
    var mockNotificationcenter: MockNSNotificationCenter!
    
    override func setUp() {
        mockNotificationcenter = MockNSNotificationCenter()

        let storyboard = UIStoryboard(name: "Main", bundle: NSBundle.mainBundle())
        viewController = storyboard.instantiateInitialViewController() as! ViewController
        
        let _ = viewController.view
    }
    
    func testDisruptionsAreRefreshedWhenAppEntersForeground() {
        viewController.viewDidLoad()
        
        viewController.disruptionsService = MockDisruptionsService(disruptions: ["Jubilee"])
        viewController.notificationCenter.postNotificationName(UIApplicationWillEnterForegroundNotification, object: nil)
                
        expect(self.viewController.disruptions).notTo(beNil())
        expect(self.viewController.disruptions?.count).to(equal(1))
    }    
}
