import Foundation
import Alamofire
import SwiftyJSON

public class DisruptionsService {
    
    enum DisruptionsDataKeys : String {
        case Root = "disruptions"
        case Line = "line"
    }
    
    func getDisruptions(onSuccess: (disruptions: [String]) -> Void){
        var url = ""

        #if TEST
            url = "http://localhost:8080/disruptions.json"
        #else
            url = "https://pivotal-london-dis-digest-test.s3.amazonaws.com/disruptions.json"
        #endif
        
        Alamofire.request(.GET, url).validate().responseJSON { response in
            switch response.result {
            case .Success:
                if let value = response.result.value {
                    let json = JSON(value)[DisruptionsDataKeys.Root.rawValue]
                    
                    var disruptions = [String]()
                    for(_, disruptionData):(String, JSON) in json {
                        disruptions.append(disruptionData[DisruptionsDataKeys.Line.rawValue].string!)
                    }
                    
                    onSuccess(disruptions: disruptions)
                }
            case .Failure(let error):
                print(error)
                
                onSuccess(disruptions: [])
            }
        }
    }
}
