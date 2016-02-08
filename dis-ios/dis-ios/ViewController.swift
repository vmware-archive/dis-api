import UIKit

public class ViewController: UIViewController, UITableViewDelegate, UITableViewDataSource {
    
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
        
        notificationCenter.addObserver(self, selector: "load", name: UIApplicationWillEnterForegroundNotification, object: nil)
        
        tableView.dataSource = self
        tableView.delegate = self
        tableView.registerClass(UITableViewCell.self, forCellReuseIdentifier: "cell")
        tableView.addSubview(refreshControl)
    }
    
    public override func viewWillAppear(animated: Bool) {
        load()
    }
    
    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    public func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell
    {
        let cell = tableView.dequeueReusableCellWithIdentifier("cell")! as UITableViewCell
        cell.textLabel?.text = disruptions?[indexPath.row]
        return cell
    }
    
    public func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return disruptions?.count ?? 0
    }
    
    public func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        if disruptions?.count > 0 {
            tableView.separatorStyle = UITableViewCellSeparatorStyle.SingleLine
            return 1
        } else {
            tableView.separatorStyle = UITableViewCellSeparatorStyle.None;
            return 0
        }
    }
    
    func load() {
        refreshControl.beginRefreshing()
        tableView.setContentOffset(CGPoint(x: 0, y: tableView.contentOffset.y - refreshControl.frame.size.height), animated: true)
        fetchDisruptions()
    }
    
    func fetchDisruptions() {
        disruptionsService.getDisruptions(handleDisruptionsData, onError: handleFetchError)
    }
    
    func handleRefresh(refreshControl: UIRefreshControl) {
        fetchDisruptions()
    }
    
    func handleFetchError(error: String) {
        refreshControl.endRefreshing()
        showStatusMessage("Couldn't retrieve data from server :(")
    }
    
    func handleDisruptionsData(disruptionData: [String]) {
        tableView.backgroundView = nil
        
        if disruptionData.count > 0 {
            disruptions = disruptionData
            tableView.reloadData()
        } else {
            showStatusMessage("No Disruptions")
        }
        
        refreshControl.endRefreshing()
    }
    
    func showStatusMessage(message: String) {
        let size = view.bounds.size
        let messageLabel = UILabel(frame: CGRect(x: 0, y: 0, width: size.width, height: size.height))
        
        messageLabel.numberOfLines = 0;
        messageLabel.textAlignment = NSTextAlignment.Center;
        messageLabel.text = message
        
        tableView.backgroundView = messageLabel;
    }
}

