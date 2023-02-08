//
//  SearchItemDetailViewModel.swift
//  MyThings
//
//  Created by 고명주 on 2023/02/08.
//

import Foundation

final class SearchItemDetailViewModel: ObservableObject {
  func registerProduct(userId: Int, productId: Int, title: String, link: String, image: String, price: Int) {
    let body: [String: Any] = ["userId": userId, "productId": productId, "title": title, "link": link, "image": image, "price": price]
    let jsonData = try? JSONSerialization.data(withJSONObject: body)
    let url = URL(string: Endpoint.baseURL + "/items/storages")!
    var request = URLRequest(url: url)
    request.httpMethod = "POST"
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
}
