import UIKit

public class ViewController: UITableViewController {
    
    public var disruptions: [String] = []
    
    public lazy var notificationCenter: NSNotificationCenter = {
        return NSNotificationCenter.defaultCenter()
    }()
    
    public lazy var disruptionsService: DisruptionsServiceProtocol = {
        return DisruptionsService()
    }()
    
    override public func viewDidLoad() {
        super.viewDidLoad()
        
        notificationCenter.addObserver(self, selector: "load", name: UIApplicationWillEnterForegroundNotification, object: nil)
        refreshControl = UIRefreshControl()
        refreshControl?.addTarget(self, action: "handleRefresh:", forControlEvents: .ValueChanged)
        tableView.addSubview(refreshControl!)
    }
    
    public override func viewWillAppear(animated: Bool) {
        load()
    }

    
    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
    }
    
    public override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("DisruptionCell")! as UITableViewCell
        cell.textLabel?.text = disruptions[indexPath.row]
        return cell
    }
    
    public override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return disruptions.count
    }
    
    public override func numberOfSectionsInTableView(tableView: UITableView) -> Int {
        if disruptions.count > 0 {
            tableView.separatorStyle = .SingleLine
            return 1
        } else {
            tableView.separatorStyle = .None;
            return 0
        }
    }
    
    func load() {
        refreshControl?.beginRefreshing()
        fetchDisruptions()
    }
    
    func fetchDisruptions() {
        disruptionsService.getDisruptions(handleDisruptionsData, onError: handleFetchError)
    }
    
    func handleRefresh(refreshControl: UIRefreshControl) {
        fetchDisruptions()
    }
    
    func handleFetchError(error: String) {
        refreshControl?.endRefreshing()
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
        
        refreshControl?.endRefreshing()
    }
    
    func showStatusMessage(message: String) {
        let size = view.bounds.size
        let messageLabel = UILabel(frame: CGRect(x: 0, y: 0, width: size.width, height: size.height))
        
        messageLabel.numberOfLines = 0;
        messageLabel.textAlignment = .Center;
        messageLabel.text = message
        
        tableView.backgroundView = messageLabel;
    }
}

