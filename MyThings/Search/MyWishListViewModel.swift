//
//  MyWishListViewModel.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/23.
//

import Foundation
import Combine

final class MyWishListViewModel: ObservableObject {
  @Published var items: [UserItem] = []
  //  var subscriptions = Set<AnyCancellable>()
  private var disposeBag = Set<AnyCancellable>()
  
  let network: NetworkService
  init(network: NetworkService) {
    self.network = network
    self.getWishList(userId: 1, start: "1", size: "10")
      .map { $0.data }
      .print()
      .replaceError(with: [])
      .receive(on: DispatchQueue.main)
      .assign(to: \.items, on: self)
      .store(in: &disposeBag)
  }
  
  func getWishList(userId: Int, start: String, size: String) -> AnyPublisher<UserWishListModel, Error> {
    let resource: Resource<UserWishListModel> = Resource(
      base: "https://433c-183-98-186-73.jp.ngrok.io",
      path: "/items/users/\(userId)",
      params: ["start": "\(start)", "size": "\(size)"],
      header: ["Content-Type": "application/json"]
    )
    return network.load(resource)
    //      .map { $0.items }
    //          .print("nettttotototo >>> \(items)")
    //          .replaceError(with: [])
    //          .receive(on: RunLoop.main)
    //          .assign(to: \.items, on: self)
    //          .store(in: &subscriptions)
    //      .map { $0 }
    //      .mapError { $0 as Error }
    //      .decode(type: [TotalInfo].self, decoder: JSONDecoder())
    //      .map { searchTerm.isEmpty ? $0 : $0.filter { $0.title.contains(searchTerm) } }
    //      .eraseToAnyPublisher()
    //  }
  }
}
