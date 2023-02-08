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
  func changeItemStatus(userId: Int, itemId: Int, itemStatus: String) {
    let body: [String: Any] = ["userId": userId, "itemId": itemId, "itemStatus": itemStatus]
    let jsonData = try? JSONSerialization.data(withJSONObject: body)
    let url = URL(string: Endpoint.baseURL + "/items/statuses")!
    var request = URLRequest(url: url)
    request.httpMethod = "PATCH"
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
    request.httpBody = jsonData
    let task = URLSession.shared.dataTask(with: request) { data, response, error in
      guard let data = data, error == nil else {
        return
      }
      let responseJSON = try? JSONSerialization.jsonObject(with: data, options: [])
      if let responseJSON = responseJSON as? [String: Any] {
      }
    }
    task.resume()
  }
  func changeInterestStatus(userId: Int, itemId: Int) {
    let body: [String: Any] = ["userId": userId, "itemId": itemId]
    let jsonData = try? JSONSerialization.data(withJSONObject: body)
    let url = URL(string: Endpoint.baseURL + "/items/interests")!
    var request = URLRequest(url: url)
    request.httpMethod = "PATCH"
    request.setValue("application/json", forHTTPHeaderField: "Content-Type")
    request.httpBody = jsonData
    let task = URLSession.shared.dataTask(with: request) { data, response, error in
      guard let data = data, error == nil else {
        print(error?.localizedDescription ?? "No data")
        return
      }
      let responseJSON = try? JSONSerialization.jsonObject(with: data, options: [])
      if let responseJSON = responseJSON as? [String: Any] {
      }
    }
    task.resume()
  }
}
