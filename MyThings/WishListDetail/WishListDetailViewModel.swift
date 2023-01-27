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
  private var disposeBag = Set<AnyCancellable>()
  var itemId: Int?
  var userId: Int?
  let network: NetworkService
  init(network: NetworkService, itemId: Int, userId: Int) {
    self.network = network
    self.getWishListDetail(itemId: itemId, userId: userId)
  }
  func getWishListDetail(itemId: Int, userId: Int) {
    let resource: Resource<WishListDetailModel> = Resource(
      base: Endpoint.baseURL,
      path: "/items/\(itemId)/users/\(userId)",
      params: [:],
      header: ["Content-Type": "application/json"]
    )
    return network.load(resource)
      .print()
      .map { [$0] }
      .replaceError(with: [])
      .receive(on: DispatchQueue.main)
      .assign(to: \.item, on: self)
      .store(in: &disposeBag)
  }
}
