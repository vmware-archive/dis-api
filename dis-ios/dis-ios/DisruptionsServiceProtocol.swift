import Foundation

public protocol DisruptionsServiceProtocol {
    func getDisruptions(onSuccess: (disruptions: [Disruption]) -> Void, onError: (error: String) -> Void)
}