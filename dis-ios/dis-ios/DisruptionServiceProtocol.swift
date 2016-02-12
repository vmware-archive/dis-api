import Foundation

public enum Result<T> {
    
//    case NoConnection
    case Success(T)
    case HTTPError(message: String)
    
}

public protocol DisruptionServiceProtocol {

    func getDisruptions(completion: (result: Result<[Disruption]>) -> Void)

}