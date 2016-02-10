//
//  Disruption.swift
//  dis-ios
//
//  Created by Pivotal on 2/10/16.
//  Copyright © 2016 Pivotal. All rights reserved.
//

import SwiftyJSON

public struct Disruption {
    
    let lineName: String
    let status: String?
    
    init?(json: JSON) {
        guard let lineName = json["line"].string else {
            return nil
        }

        self.lineName = lineName
        self.status = json["status"].string
    }

}
