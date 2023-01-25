//
//  MyWish.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/21.
//

import Foundation

struct UserWishListModel: Codable {
  var data: [UserItem]
  var user: User
  var pageInfo: PageInfo
}

struct UserItem: Codable {
  var itemId : Int
  var title : String
  var price : Int
  var image : String
  var interestedItem : Bool
  var secretItem : Bool
  var itemStatus : String
  var createdAt : String
  var lastModifiedAt : String
}

struct User: Codable {
  var userId: Int
  var name: String
  var image: String
}

struct PageInfo: Codable {
  var page: Int
  var size: Int
  var totalElements: Int
  var totalPages: Int
}
