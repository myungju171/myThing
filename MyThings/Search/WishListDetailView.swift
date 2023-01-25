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
//  @State var manager = DataPost()
  init(viewModel: WishListDetailViewModel, itemId: Int, userId: Int) {
    self.itemId = itemId
    self.userId = userId
    self.viewModel = viewModel
//    self.manager = manager
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
            print("fire~~더사고싶어")
          } label: {
            Image(systemName: "heart")
              .resizable()
              .aspectRatio(contentMode: .fit)
              .frame(width: 30)
              .foregroundColor(.gray)
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
            //            self.manager.checkDetails(userId: 1, productId: Int(model.productId)!, title: model.title, link: model.link, image: model.image, price: Int( model.lprice)!)
          } label: {
            Text("상품 구매하기")
              .foregroundColor(.white)
          }
          .frame(width: 80, height: 50)
          .background(.blue)
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
