import UIKit

public class ViewController: UITableViewController {
    
    @IBOutlet var errorView: UIView!
    @IBOutlet weak var errorViewLabel: UILabel!
    
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
        tableView.tableFooterView = UIView(frame: CGRect.zero)
    }
    
    public override func viewWillAppear(animated: Bool) {
        load()
    }
    
    public override func viewWillDisappear(animated: Bool) {
        super.viewWillDisappear(animated)
        notificationCenter.removeObserver(self)
        refreshControl?.endRefreshing()
    }
    
    func load() {
        refreshControl?.beginRefreshing()
        fetchDisruptions()
    }
    
    func handleRefresh(refreshControl: UIRefreshControl) {
        fetchDisruptions()
    }
    
    // MARK: - Delegate/DataSource
    
    public override func tableView(tableView: UITableView, cellForRowAtIndexPath indexPath: NSIndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCellWithIdentifier("DisruptionCell")! as UITableViewCell
        cell.textLabel?.text = disruptions[indexPath.row]
        return cell
    }
    
    public override func tableView(tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return disruptions.count
    }
    
    // MARK: - Private
    
    private func fetchDisruptions() {
        disruptionsService.getDisruptions(handleDisruptionsData, onError: handleFetchError)
    }
    
    private func handleFetchError(error: String) {
        refreshControl?.endRefreshing()
        showStatusMessage("Couldn't retrieve data from server 💩")
    }
    
    private func handleDisruptionsData(disruptionData: [String]) {
        tableView.backgroundView = nil
        
        if disruptionData.count > 0 {
            disruptions = disruptionData
            tableView.reloadData()
        } else {
            showStatusMessage("No Disruptions")
        }
        
        refreshControl?.endRefreshing()
    }
    
    private func showStatusMessage(message: String) {
        errorViewLabel.text = message
        tableView.backgroundView = errorView
    }
}

