import UIKit

public class ViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
    @IBOutlet public weak var noDisruptionsLabel: UILabel!
    @IBOutlet public weak var tableView: UITableView!
    
    public var disruptions: [String]?
    
    public lazy var notificationCenter: NSNotificationCenter = {
        return NSNotificationCenter.defaultCenter()
    }()
    
    public lazy var disruptionsService: DisruptionsServiceProtocol = {
        return DisruptionsService()
    }()
    
    lazy var refreshControl: UIRefreshControl = {
        let refreshControl = UIRefreshControl()
        refreshControl.addTarget(self, action: "handleRefresh:", forControlEvents: UIControlEvents.ValueChanged)
        
        return refreshControl
    }()
    
    override public func viewDidLoad() {
        super.viewDidLoad()
        
        self.notificationCenter.addObserver(self, selector: "load", name: UIApplicationWillEnterForegroundNotification, object: nil)
        
        self.tableView.dataSource = self
        self.tableView.delegate = self
        self.tableView.registerClass(UITableViewCell.self, forCellReuseIdentifier: "cell")
        self.tableView.addSubview(self.refreshControl)
    }
    
    public override func viewWillAppear(animated: Bool) {
        self.load()
    }
    
    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    public func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell
    {
        let cell = self.tableView.dequeueReusableCellWithIdentifier("cell")! as UITableViewCell
        cell.textLabel?.text = self.disruptions?[indexPath.row]
        return cell
    }
    
    public func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return self.disruptions?.count ?? 0
    }
    
    public func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        return 1
    }
    
    func load() {
        self.refreshControl.beginRefreshing()
        self.tableView.setContentOffset(CGPoint(x: 0, y: self.tableView.contentOffset.y - self.refreshControl.frame.size.height), animated: true)
        self.fetchDisruptions()
    }
    
    func fetchDisruptions() {
        self.disruptionsService.getDisruptions(){ (disruptions: [String]) in
            if disruptions.count > 0 {
                self.noDisruptionsLabel.text = ""
                self.disruptions = disruptions
                self.tableView.reloadData()
            } else {
                self.noDisruptionsLabel.text = "No Disruptions"
                self.tableView.hidden = true
            }
            
            self.refreshControl.endRefreshing()
        }
    }
    
    func handleRefresh(refreshControl: UIRefreshControl) {
        self.fetchDisruptions()
    }
    
}

