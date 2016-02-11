import Foundation
import SwiftyJSON
import UIKit

public class DisruptionsService: DisruptionsServiceProtocol {
    
    public func getDisruptions(completion: (result: Result<[Disruption]>) -> Void) {
        #if TEST
            let url = NSURL(string: "http://localhost:8080/disruptions.json")!
        #else
            let url = NSURL(string: "https://pivotal-london-dis-digest-test.s3.amazonaws.com/disruptions.json")!
        #endif
        
        
        let request = NSURLRequest(URL: url, cachePolicy: .ReloadIgnoringLocalCacheData, timeoutInterval: 10)
        
        let task = NSURLSession.sharedSession().dataTaskWithRequest(request) { data, response, error in
            
            let result: Result<[Disruption]>
            
            if let data = data {
                var json = JSON(data: data)
                let disruptions = json["disruptions"].arrayValue.flatMap { Disruption(json: $0) }
//                let newJson = JSON(
//                    data: "{\"line\":\"Hammersmith & City\", \"status\":\"Major cat-related rain snow sleet delays\", \"startTime\":\"12:25\", \"endTime\":\"12:55\"}".dataUsingEncoding(NSUTF8StringEncoding)!
//                )
//                disruptions.append(Disruption(json: newJson)!)
                result = Result.Success(disruptions)
            } else {
                result = Result.HTTPError(message: "Couldn't retrieve data from server 💩")
            }
            
            dispatch_async(dispatch_get_main_queue()) {
                completion(result: result)
            }
        }
        task.resume()
    }
    
}

