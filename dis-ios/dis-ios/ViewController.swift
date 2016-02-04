//
//  ViewController.swift
//  dis-ios
//
//  Created by Pivotal on 2/2/16.
//  Copyright © 2016 Pivotal. All rights reserved.
//

import UIKit

public class ViewController: UIViewController {

    @IBOutlet public weak var noDisruptionsLabel: UILabel!
    
    public func disruptionsService() -> DisruptionsService {
        return DisruptionsService()
    }
    
    override public func viewDidLoad() {
        super.viewDidLoad()
        
        self.disruptionsService().getDisruptions(){ (data: Bool) in
            self.noDisruptionsLabel.text = data ? "" : "No Disruptions"
        }
    }

    override public func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }


}

