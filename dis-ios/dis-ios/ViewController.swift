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
    
    override public func viewDidLoad() {
        super.viewDidLoad()
        
        self.notificationCenter.addObserver(self, selector: "loadDisruptions", name: UIApplicationWillEnterForegroundNotification, object: nil)
        
        self.tableView.dataSource = self
        self.tableView.delegate = self
        self.tableView.registerClass(UITableViewCell.self, forCellReuseIdentifier: "cell")
    }
    
    public override func viewWillAppear(animated: Bool) {
        self.loadDisruptions()
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
    
    public func loadDisruptions() {
        self.disruptionsService.getDisruptions(){ (disruptions: [String]) in
            if disruptions.count > 0 {
                self.noDisruptionsLabel.text = ""
                self.disruptions = disruptions
                self.tableView.reloadData()
            } else {
                self.noDisruptionsLabel.text = "No Disruptions"
                self.tableView.hidden = true
            }
        }
    }
    
}

