//
//  BirthdayListView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/20.
//

import SwiftUI

struct BirthdayListView: View {
  var body: some View {
    List {
      ForEach((0...10), id: \.self) { index in
        NavigationLink {
          FriendListView()
        }
      label: {
        FriendCell()
          .frame(width: .infinity, height: 50)
      }
      }
    }
  }
}

struct BirthdayListView_Previews: PreviewProvider {
  static var previews: some View {
    BirthdayListView()
  }
}
