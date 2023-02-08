//
//  Network.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/24.
//

import Foundation
import Combine
import SwiftUI
import Alamofire

class Network: ObservableObject {
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
  func editMyInfo(image: UIImage, userId: Int, name: String, infoMessage: String, birthDay: String, completionHandler: @escaping () -> Void) {
    let url = "http://ec2-13-125-113-82.ap-northeast-2.compute.amazonaws.com:8000/users/profiles"
    let header: HTTPHeaders = ["Content-Type": "multipart/form-data"]
    AF.upload(multipartFormData: { multipartFormData in
      multipartFormData.append(Data(String(userId).utf8), withName: "userId")
      multipartFormData.append(Data(name.utf8), withName: "name")
      multipartFormData.append(Data(infoMessage.utf8), withName: "infoMessage")
      multipartFormData.append(Data(birthDay.utf8), withName: "birthDay")
      // Date 처리
      //          multipartFormData.append(Data(model.time?.toString().utf8 ?? "".utf8), withName: "time")
      
      //          for image in images {
      //              // UIImage 처리
      //        UIImage(data: image.pngData()!)
      multipartFormData.append(image.jpegData(compressionQuality: 1) ?? Data(),
                               withName: "multipartFile",
                               fileName: "image.jpeg",
                               mimeType: "image/jpeg")
      //          }
      
      // 배열 처리
      //          let keywords =  try! JSONSerialization.data(withJSONObject: model.keywords, options: .prettyPrinted)
      //          multipartFormData.append(keywords, withName: "keywords")
      
    }, to: url, method: .post, headers: header)
    .responseData { response in
      print(response)
      guard let statusCode = response.response?.statusCode else { return }
      switch statusCode {
      case 200..<300:
        print("게시물 등록 성공")
        completionHandler()
      default:
        print("게시물 등록 실패")
      }
    }
  }
}
