//
//  DisruptionsService.swift
//  dis-ios
//
//  Created by Pivotal on 2/3/16.
//  Copyright © 2016 Pivotal. All rights reserved.
//

import Foundation
import Alamofire
import SwiftyJSON

public class DisruptionsService {
    func getDisruptions(onSuccess: (data: Bool) -> Void){
        var url = ""

        #if TEST
            url = "http://localhost:8080/disruptions.json"
        #else
            url = "https://pivotal-london-dis-digest.s3.amazonaws.com/disruptions.json"
        #endif
        
        Alamofire.request(.GET, url).validate().responseJSON { response in
            switch response.result {
            case .Success:
                if let value = response.result.value {
                    let json = JSON(value)["disruptions"]
                    
                    onSuccess(data: json.array?.count > 0)
                }
            case .Failure(let error):
                print(error)
            }
        }
        
        onSuccess(data: false)
    }
}
