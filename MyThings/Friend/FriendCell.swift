//
//  FriendCell.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/19.
//

import SwiftUI

struct FriendCell: View {
  var body: some View {
    HStack(alignment: .center, spacing: 10) {
      Image(systemName: "person.fill")
        .resizable()
        .aspectRatio(contentMode: .fit)
        .frame(width: 50, height: 50)
      VStack(alignment: .leading, spacing: 3) {
        HStack {
          Text("젤리 고")
          Text("1월 17일")
            .foregroundColor(.gray)
        }
        Text("나는 속물이야 얘들아 고맙게 받을게")
        Text("3개")
          .foregroundColor(.gray)
      }
    }
  }
}

struct FriendCell_Previews: PreviewProvider {
  static var previews: some View {
    FriendCell()
  }
}
