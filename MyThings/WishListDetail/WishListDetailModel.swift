//
//  WishListDetailModel.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/23.
//

import Foundation

struct WishListDetailModel: Codable {
  var itemId: Int
  var title: String
  var price: Int
  var link: String
  var image: String
  var memo: String?
  var interestedItem: Bool
  var secretItem: Bool
  var itemStatus: String
}
