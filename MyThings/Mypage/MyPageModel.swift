//
//  MyPageModel.swift
//  MyThings
//
//  Created by 고명주 on 2023/02/05.
//

import Foundation

struct MyPageModel: Codable {
  var userId: Int
  var name: String
  var phone: String
  var birthDay: String
  var infoMessage: String?
  var avatarId: Int?
  var image: String?
}
