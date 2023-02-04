//
//  ContentView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/15.
//

import SwiftUI
import CoreData

struct MainTabView: View {
  @EnvironmentObject var viewmodel: MyWishListViewModel
  enum TabItem: String {
    case firstItem = "내 위시리스트"
    case secondItem = "친구 목록"
    case thirdItem = "상품 검색"
    case fourthTab = "채팅"
    case fifthTab = "마이페이지"
  }
  @State private var selection = 0
  var handler: Binding<Int> { Binding(
    get: { self.selection },
    set: {
      if $0 == 0 {
        print("Reset here!!")
        viewmodel.getWishList(userId: 1, start: "1", size: "10")
      }
      self.selection = $0
    }
  )}
  var body: some View {
    TabView(selection: handler) {
      MyWishListView()
        .tabItem {
          Image(systemName: "arrow.right.circle.fill")
          Text("내 Wish")
        }.tag(0)
      FriendListView()
        .tabItem {
          Image(systemName: "arrow.right.circle.fill")
          Text("친구 Wish")
        }.tag(1)
      SearchView()
        .tabItem {
          Image(systemName: "arrow.right.circle.fill")
          Text("상품 검색")
        }.tag(2)
      ChattingView()
        .tabItem {
          Image(systemName: "arrow.right.circle.fill")
          Text("채팅")
        }.tag(3)
      MyPageMainView()
        .tabItem {
          Image(systemName: "arrow.right.circle.fill")
          Text("마이페이지")
        }.tag(4)
    }
    //      .navigationTitle(selection.rawValue)
    .navigationBarBackButtonHidden()
    //    .navigationBarItems(selection.rawValue == "firstItem" ? Button(action: {
    //    }) { Text("Edit") })
  }
}

struct ContentView_Previews: PreviewProvider {
  static var previews: some View {
    MainTabView().environment(\.managedObjectContext, PersistenceController.preview.container.viewContext)
  }
}
