//
//  Publisher+Utils.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/21.
//

import Foundation
import Combine

extension Publisher {
  static func empty() -> AnyPublisher<Output, Failure> {
    return Empty().eraseToAnyPublisher()
  }
  static func just(_ output: Output) -> AnyPublisher<Output, Failure> {
    return Just(output)
      .catch { _ in AnyPublisher<Output, Failure>.empty() }
      .eraseToAnyPublisher()
  }
  static func fail(_ error: Failure) -> AnyPublisher<Output, Failure> {
    return Fail(error: error).eraseToAnyPublisher()
  }
}
