//
//  FirstView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/15.
//

import SwiftUI

struct MyWishListView: View {
  var body: some View {
    List {
      ForEach((0...10), id: \.self) { index in
        NavigationLink {
          MyWishDetailView(index: 5)
        } label: {
          HStack {
            Image(systemName: "heart.fill")
              .resizable()
              .aspectRatio(contentMode: .fill)
              .frame(width: 80, height: 80)
            VStack(alignment: .leading, spacing: 10) {
              Text("짱구 손전등 신상")
                .fontWeight(.bold)
              HStack(spacing: 0) {
                Text("34,000")
                Text("원")
              }
              Image(systemName: "heart")
            }
            .padding()
          }
        }
      }
    }
  }
}

struct MyWishListView_Previews: PreviewProvider {
  static var previews: some View {
    MyWishListView()
  }
}
