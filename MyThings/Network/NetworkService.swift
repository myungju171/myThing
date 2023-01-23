//
//  NetworkService.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/21.
//

import Foundation
import Combine

final class NetworkService {
  let session: URLSession
  
  init(configuration: URLSessionConfiguration) {
    session = URLSession(configuration: configuration)
  }
  func load<T>(_ resource: Resource<T>) -> AnyPublisher<T, Error> {
    guard let request = resource.urlRequest else {
      return .fail(NetworkError.invalidRequest)
    }
    return session
      .dataTaskPublisher(for: request)
      .tryMap { result -> Data in
        guard let response = result.response as? HTTPURLResponse,
              (200..<300).contains(response.statusCode)
        else {
          let response = result.response as? HTTPURLResponse
          let statusCode = response?.statusCode ?? -1
          throw NetworkError.responseError(statusCode: statusCode)
        }
        return result.data
      }
      .decode(type: T.self, decoder: JSONDecoder())
      .eraseToAnyPublisher()
  }
}
