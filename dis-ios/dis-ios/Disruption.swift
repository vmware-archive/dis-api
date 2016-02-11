import SwiftyJSON

public struct Disruption {
    
    let lineName: String
    let status: String?
    let startTime: String?
    let endTime: String?
    
    init?(json: JSON) {
        guard let lineName = json["line"].string else {
            return nil
        }

        self.lineName = lineName
        self.status = json["status"].string
        self.startTime = json["startTime"].string
        self.endTime = json["endTime"].string
    }

}
