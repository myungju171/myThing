//
//  ContentView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/15.
//

import SwiftUI
import CoreData

struct MainTabView: View {
  @State private var selection = 0
  var handler: Binding<Int> { Binding(
    get: { self.selection },
    set: {
      if $0 == 0 {
        MyWishListViewModel(network: NetworkService(configuration: .default)).getWishList(userId: 1, start: "1", size: "10")
      }
    }
  )}
  var body: some View {
    NavigationView{
      TabView(selection: handler) {
        MyWishListView()
          .tabItem {
            Image(systemName: "arrow.right.circle.fill")
            Text("내 Wish")
              .navigationTitle("jelly")
          }.tag(0)
        FriendListView()
          .tabItem {
            Image(systemName: "arrow.right.circle.fill")
            Text("친구 Wish")
          }.tag(1)
        ContentoneView()
          .tabItem {
            Image(systemName: "arrow.right.circle.fill")
            Text("상품 검색")
          }.tag(2)
        ChattingView()
          .tabItem {
            Image(systemName: "arrow.right.circle.fill")
            Text("채팅")
          }.tag(3)
        MyPageView()
          .tabItem {
            Image(systemName: "arrow.right.circle.fill")
            Text("마이페이지")
          }.tag(4)
      }
      .navigationTitle(selection == 0 ? "jelly" : "navi")
      .navigationBarItems(trailing:
                            Button(action: {
        print("Edit button pressed...")
      }) {
        Text("Edit")
      }
      )
    }
  }
}

struct ContentView_Previews: PreviewProvider {
  static var previews: some View {
    MainTabView().environment(\.managedObjectContext, PersistenceController.preview.container.viewContext)
  }
}
