import Foundation
import SwiftyJSON

public enum DisruptionsDataKeys : String {
    case Root = "disruptions"
    case Line = "line"
}

public class DisruptionsService: DisruptionsServiceProtocol {
    
    public func getDisruptions(onSuccess: (disruptions: [String]) -> Void, onError: (error: String) -> Void) {

        #if TEST
            let url = NSURL(string: "http://localhost:8080/disruptions.json")!
        #else
            let url = NSURL(string: "https://pivotal-london-dis-digest-test.s3.amazonaws.com/disruptions.json")!
        #endif

        
        let request = NSURLRequest(URL: url, cachePolicy: .ReloadIgnoringLocalCacheData, timeoutInterval: 10)
        
        let task = NSURLSession.sharedSession().dataTaskWithRequest(request) { data, response, error in
            if let data = data {
                var json = JSON(data: data)
                let disruptions = json[DisruptionsDataKeys.Root.rawValue].arrayValue.flatMap() { line in
                    return line[DisruptionsDataKeys.Line.rawValue].string
                }

                dispatch_async(dispatch_get_main_queue()) {
                    onSuccess(disruptions: disruptions)
                }
            } else {
                dispatch_async(dispatch_get_main_queue()) {
                    onError(error: "Couldn't retrieve data from server 💩")
                }
            }
        }
        task.resume()
    }
    
}
