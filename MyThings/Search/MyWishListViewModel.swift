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
  private var disposeBag = Set<AnyCancellable>()
  let network: NetworkService
  init(network: NetworkService) {
    self.network = network
  }
  func getWishList(userId: Int, start: String, size: String) {
    let resource: Resource<UserWishListModel> = Resource(
      base: Endpoint.baseURL,
      path: "/items/users/\(userId)",
      params: ["start": "\(start)", "size": "\(size)"],
      header: ["Content-Type": "application/json"]
    )
    network.load(resource)
      .map { $0.data }
      .print()
      .replaceError(with: [])
      .receive(on: DispatchQueue.main)
      .assign(to: \.items, on: self)
      .store(in: &disposeBag)
  }
}
