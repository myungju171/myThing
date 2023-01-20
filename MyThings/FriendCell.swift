//
//  FriendCell.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/19.
//

import SwiftUI

struct FriendCell: View {
  var body: some View {
    HStack(alignment: .top, spacing: 20) {
      Image(systemName: "person.fill")
        .resizable()
        .aspectRatio(contentMode: .fit)
        .frame(width: 80, height: 80)
      VStack(alignment: .leading) {
        Text("젤리 고")
        Text("1998-01-17")
        Text("나는 속물이야 얘들아 고맙게 받을게")
      }
      Image(systemName: "circle.fill")
        .resizable()
        .aspectRatio(contentMode: .fit)
        .frame(width: 20, height: 20)
    }
    .frame(minWidth: 300, minHeight: 100)
    .padding()
    .background(Color.gray)

  }
}

struct FriendCell_Previews: PreviewProvider {
  static var previews: some View {
    FriendCell()
  }
}
