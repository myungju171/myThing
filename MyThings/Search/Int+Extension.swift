//
//  Int+Extension.swift
//  MyThings
//
//  Created by 고명주 on 2023/02/04.
//

import Foundation

extension String {
  func decimalWon() -> String {
          let numberFormatter = NumberFormatter()
          numberFormatter.numberStyle = .decimal
          let result = numberFormatter.string(from: NSNumber(value: Int(self)!))! + "원"
          
          return result
      }
}
