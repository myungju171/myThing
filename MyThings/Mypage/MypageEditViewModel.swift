//
//  MyPageEditViewModel.swift
//  MyThings
//
//  Created by 고명주 on 2023/02/05.
//

import Foundation
import Combine

final class MypageEditViewModel: ObservableObject {
  @Published var item: MyPageModel?
  var subscriptions = Set<AnyCancellable>()
  var network: NetworkService
  var userId: Int?
  init(network: NetworkService, userId: Int) {
    self.network = network
    self.userId = userId
    self.search(userId: userId)
  }
  func search(userId: Int) {
    let resource: Resource<MyPageModel?> = Resource(
      base: Endpoint.baseURL,
      path: "/users/\(userId)",
      params: [:],
      header: ["Content-Type": "application/json"]
    )
    return network.load(resource)
      .print()
      .map { $0 }
      .replaceError(with: MyPageModel(userId: 0, name: "", phone: "", birthDay: ""))
      .receive(on: DispatchQueue.main)
      .assign(to: \.item, on: self)
      .store(in: &subscriptions)
  }
}
