//
//  NetworkError.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/21.
//

import Foundation

enum NetworkError: Error {
  case invalidRequest
  case invalidResponse
  case responseError(statusCode: Int)
  case jsonDecodingError(error: Error)
}
