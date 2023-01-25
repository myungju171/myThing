//
//  WishListDetailViewModel.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/24.
//

import Foundation
import Combine
import SwiftUI

final class WishListDetailViewModel: ObservableObject {
  @Published var item: [WishListDetailModel] = []
  //  var subscriptions = Set<AnyCancellable>()
  private var disposeBag = Set<AnyCancellable>()
  var itemId: Int = 1
  var userId: Int = 1
  
  let network: NetworkService
  
  init(network: NetworkService, itemId: Int, userId: Int) {
    self.network = network
    self.getWishListDetail(itemId: itemId, userId: userId)
      .print()
      .map { [$0] }
    //      .print()
      .replaceError(with: [])
      .receive(on: DispatchQueue.main)
      .assign(to: \.item, on: self)
      .store(in: &disposeBag)
  }
  
  func getWishListDetail(itemId: Int, userId: Int) -> AnyPublisher<WishListDetailModel, Error> {
    let resource: Resource<WishListDetailModel> = Resource(
      base: "https://433c-183-98-186-73.jp.ngrok.io",
      path: "/items/\(itemId)/users/\(userId)",
      params: [:],
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
