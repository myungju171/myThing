//
//  ContentView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/15.
//

import SwiftUI
import CoreData

struct MainTabView: View {
  var body: some View {
    NavigationView{
      TabView {
        MyWishListView()
          .tabItem {
            Image(systemName: "arrow.right.circle.fill")
            Text("내 Wish")
          }
        FriendListView()
          .tabItem {
            Image(systemName: "arrow.right.circle.fill")
            Text("친구 Wish")
          }
        ChattingView()
          .tabItem {
            Image(systemName: "arrow.right.circle.fill")
            Text("채팅")
          }
        MyPageView()
          .tabItem {
            Image(systemName: "arrow.right.circle.fill")
            Text("마이페이지")
          }
      }
      .navigationTitle(" Jelly님의 위시리스트")
    }
  }
}

struct ContentView_Previews: PreviewProvider {
  static var previews: some View {
    MainTabView().environment(\.managedObjectContext, PersistenceController.preview.container.viewContext)
  }
}