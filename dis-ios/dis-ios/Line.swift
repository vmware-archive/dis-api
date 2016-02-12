import SwiftyJSON
import UIColor_Hex_Swift

struct Line {
    let name: String
    let backgroundColor: UIColor
    let foregroundColor: UIColor
    
    init?(json: JSON) {
        guard let name = json["name"].string where !name.isEmpty else {
            return nil
        }
        self.name = name
        self.foregroundColor = UIColor(rgba: json["foregroundColor"].stringValue, defaultColor: UIColor.blackColor())
        self.backgroundColor = UIColor(rgba: json["backgroundColor"].stringValue, defaultColor: UIColor.whiteColor())
    }

}