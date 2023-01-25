//
//  DataPost.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/23.
//

import Foundation
import Combine

class DataPost: ObservableObject {
    var didChange = PassthroughSubject<DataPost, Never>()
    var formCompleted = false {
        didSet {
            didChange.send(self)
        }
    }
    
    func checkDetails(userId: Int, productId: Int, title: String, link: String, image: String, price: Int) {
        
        let body: [String: Any] = ["userId": userId, "productId": productId, "title": title, "link": link, "image": image, "price": price]
        
        let jsonData = try? JSONSerialization.data(withJSONObject: body)
          
        let url = URL(string: "https://433c-183-98-186-73.jp.ngrok.io/items/storages")!
        var request = URLRequest(url: url)
        request.httpMethod = "POST"
//        request.setValue("\(String(describing: jsonData?.count))", forHTTPHeaderField: "Content-Length")
        request.setValue("application/json", forHTTPHeaderField: "Content-Type")
        request.httpBody = jsonData
        
        let task = URLSession.shared.dataTask(with: request) { data, response, error in
            print("-----> data: \(data)")
            print("-----> error: \(error)")
            
            guard let data = data, error == nil else {
                print(error?.localizedDescription ?? "No data")
                return
            }

            let responseJSON = try? JSONSerialization.jsonObject(with: data, options: [])
            print("-----1> responseJSON: \(responseJSON)")
            if let responseJSON = responseJSON as? [String: Any] {
                print("-----2> responseJSON: \(responseJSON)")
            }
        }
        
        task.resume()
    }
}
