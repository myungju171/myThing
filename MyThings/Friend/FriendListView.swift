//
//  FriendListView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/15.
//

import SwiftUI

struct FriendListView: View {
  var body: some View {
    List {
      Section(
        header: Text("생일인 친구")
      ) {
        NavigationLink {
          BirthdayListView()
        } label: {
          Text("생일인 친구를 확인해봐요!")
        }
      }
      ForEach((0...10), id: \.self) { index in
        NavigationLink {
          MyWishListView()
        }
      label: {
        FriendCell()
      }
      }
    }
  }
}

struct FriendListView_Previews: PreviewProvider {
  static var previews: some View {
    FriendListView()
  }
}
