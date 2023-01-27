//
//  WishListDetailView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/23.
//

import SwiftUI

struct WishListDetailView: View {
  var itemId: Int
  var userId: Int
  @ObservedObject var viewModel: WishListDetailViewModel
  @State var manager = Network()
  init(viewModel: WishListDetailViewModel, itemId: Int, userId: Int) {
    self.itemId = itemId
    self.userId = userId
    self.viewModel = viewModel
  }
  
  var body : some View {
    //       Color.yellow.edgesIgnoringSafeArea(.all)
    ScrollView(Axis.Set.vertical, showsIndicators: true) {
      VStack(alignment: .leading, spacing: 10) {
        AsyncImage(url: URL(string: viewModel.item.first!.image), content: { image in
          image.resizable()
            .aspectRatio(contentMode: .fit)
        }, placeholder: {
        })
        Text(viewModel.item.first!.title)
          .padding(EdgeInsets(top: 0, leading: 15, bottom: 0, trailing: 0))
          .font(.system(size: 20, weight: .bold))
        HStack(spacing:0) {
          Text(String(viewModel.item.first!.price))
          Text("원")
        }
        .font(.system(size: 20, weight: .bold))
        .padding(EdgeInsets(top: 0, leading: 15, bottom: 0, trailing: 0))
        VStack(alignment: .center) {
          Text(viewModel.item.first!.memo ?? "상품에 대한 한 줄 메모를 남겨보세요.")
            .padding()
            .overlay(
              RoundedRectangle(cornerRadius: 10)
                .stroke(Color.gray, lineWidth: 1)
            )
        }
        .padding()
        Spacer()
        HStack {
          Button {
            self.manager.changeInterestStatus(userId: 1, itemId: viewModel.item.first!.itemId)
            print("cli>>> \(viewModel.item.first!.interestedItem)")
          } label: {
            Image(systemName: viewModel.item.first!.interestedItem ? "heart.fill" : "heart")
              .resizable()
              .aspectRatio(contentMode: .fit)
              .frame(width: 30)
              .foregroundColor(viewModel.item.first!.interestedItem ? .red : .gray)
              .padding()
          }
          Link(destination: URL(string: viewModel.item.first!.link)!) {
            HStack {
              Text("사이트로 이동하기")
            } .foregroundColor(.white)
          }
          .frame(width: 150, height: 50)
          .background(.blue)
          .cornerRadius(10)
          Button {
            viewModel.getWishListDetail(itemId: 1, userId: 1)
            if viewModel.item.first!.itemStatus != "BOUGHT" {
              self.manager.changeItemStatus(userId: 1, itemId: viewModel.item.first!.itemId, itemStatus: "BOUGHT")
            } else if viewModel.item.first!.itemStatus != "RESERVE" {
              self.manager.changeItemStatus(userId: 1, itemId: viewModel.item.first!.itemId, itemStatus: "RECEIVED")
            }
          } label: {
            Text("상품 구매하기")
              .foregroundColor(.white)
          }
          .frame(width: 80, height: 50)
          .background(viewModel.item.first!.itemStatus == "BOUGHT" ? .gray : .blue)
          .cornerRadius(10)
        }
        .padding()
      }
      //      .navigationBarHidden(true)
    }
    //     .toolbar {
    //       ToolbarItem(placement: .navigationBarLeading) {
    //         Button(action: { print("") }) {
    //           Image(systemName: "chevron.left")
    //             .foregroundColor(.white)
    //           Text("Back")
    //         }
    //       }
    //     }
  }
}
