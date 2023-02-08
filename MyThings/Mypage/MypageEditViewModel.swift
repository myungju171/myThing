//
//  MyPageEditViewModel.swift
//  MyThings
//
//  Created by 고명주 on 2023/02/05.
//

import Foundation
import Combine
import Alamofire
import UIKit

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
  func editMyInfo(image: UIImage, userId: Int, name: String, infoMessage: String, birthDay: String, completionHandler: @escaping () -> Void) {
    let url = Endpoint.baseURL + "/users/profiles"
    let header: HTTPHeaders = ["Content-Type": "multipart/form-data"]
    AF.upload(multipartFormData: { multipartFormData in
      multipartFormData.append(Data(String(userId).utf8), withName: "userId")
      multipartFormData.append(Data(name.utf8), withName: "name")
      multipartFormData.append(Data(infoMessage.utf8), withName: "infoMessage")
      multipartFormData.append(Data(birthDay.utf8), withName: "birthDay")
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
