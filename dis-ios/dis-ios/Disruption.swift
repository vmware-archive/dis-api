import SwiftyJSON

public struct Disruption {
    
    let line: Line
    let status: String?
    let startTime: String?
    let endTime: String?
    
    init?(json: JSON) {
        guard let line = Line(json: json["line"]) else {
            return nil
        }

        self.line = line
        self.status = json["status"].string
        self.startTime = json["startTime"].string
        self.endTime = json["endTime"].string
    }

}
