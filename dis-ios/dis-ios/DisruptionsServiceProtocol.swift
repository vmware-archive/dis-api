import Foundation

public protocol DisruptionsServiceProtocol {
    func getDisruptions(onSuccess: (disruptions: [String]) -> Void, onError: (error: String) -> Void)
}