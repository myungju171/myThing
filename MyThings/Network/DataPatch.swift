//
//  Network.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/24.
//

import Foundation
import Combine

class Network: ObservableObject {
  var didChange = PassthroughSubject<Network, Never>()
  var formCompleted = false {
    didSet {
      didChange.send(self)
    }
  }
  func checkDetails(userId: Int, productId: Int, title: String, link: String, image: String, price: Int) {
    let body: [String: Any] = ["userId": userId, "productId": productId, "title": title, "link": link, "image": image, "price": price]
    let jsonData = try? JSONSerialization.data(withJSONObject: body)
    let url = URL(string: Endpoint.baseURL + "/items/storages")!
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
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
