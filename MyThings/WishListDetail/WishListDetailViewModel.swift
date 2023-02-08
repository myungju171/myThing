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
  @Published var item: WishListDetailModel = WishListDetailModel(itemId: 1, title: "", price: 0, link: "", image: "", interestedItem: false, secretItem: false, itemStatus: "")
  private var disposeBag = Set<AnyCancellable>()
  var network: NetworkService
  init(network: NetworkService) {
    self.network = network
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
      .map { $0 }
      .replaceError(with: WishListDetailModel(itemId: 1, title: "", price: 0, link: "", image: "", interestedItem: false, secretItem: false, itemStatus: ""))
      .receive(on: DispatchQueue.main)
      .assign(to: \.item, on: self)
      .store(in: &disposeBag)
  }
}
