//
//  SearchModel.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/21.
//

import Foundation

struct TotalInfo: Codable, Hashable {
  var lastBuildDate : String
  var total : Int
  var start : Int
  var display : Int
  var items : [SearchItem]
}
struct SearchItem: Codable, Hashable {
  var title : String
  var link: String
  var image: String
  var lprice: String
  var hprice: String
  var mallName: String
  var productId: String
  var productType: String
  var brand: String
  var maker: String
  var category1: String
  var category2: String
  var category3: String
  var category4: String
}


