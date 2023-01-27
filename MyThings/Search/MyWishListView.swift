//
//  FirstView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/15.
//

import SwiftUI
import Combine

struct MyWishListView: View {
  @ObservedObject var viewModel = MyWishListViewModel(network: NetworkService(configuration: .default))
  var body: some View {
    List(viewModel.items, id: \.title) { item in
      NavigationLink {
        WishListDetailView(viewModel: WishListDetailViewModel(network: NetworkService(configuration: .default), itemId: item.itemId, userId: 1), itemId: item.itemId, userId: 1)
      } label: {
        HStack {
          AsyncImage(url: URL(string: item.image), content: { image in
            image.resizable()
          }, placeholder: {Color.gray})
          .aspectRatio(contentMode: .fit)
          .frame(width: 100, height: 100)
          VStack(alignment: .leading, spacing: 10) {
            Text(item.title)
              .fontWeight(.bold)
            HStack(spacing: 0) {
              Text(String(item.price))
              Text("원")
            }
            Image(systemName: item.interestedItem ? "heart.fill" : "heart")
              .resizable()
              .aspectRatio(contentMode: .fit)
              .frame(width: 30)
              .foregroundColor(item.interestedItem ? .red : .gray)
              .padding()
          }
          .padding()
        }
      }
    }
    //            .navigationTitle("뉴스 둘러보기")
  }
}

struct MyWishListView_Previews: PreviewProvider {
  static var previews: some View {
    MyWishListView()
  }
}

struct MyWishCell: View {
  var body: some View {
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
