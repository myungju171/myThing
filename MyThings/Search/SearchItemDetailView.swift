//
//  MyWishDetail.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/15.
//

import SwiftUI

struct SearchItemDetailView: View {
  var model: SearchItem
  @State var manager = Network()
  var body : some View {
    ScrollView(Axis.Set.vertical, showsIndicators: true) {
      VStack(alignment: .leading, spacing: 10) {
        AsyncImage(url: URL(string: model.image), content: { image in
          image.resizable()
            .aspectRatio(contentMode: .fit)
        }, placeholder: {
        })
        Text(model.title)
          .padding(EdgeInsets(top: 0, leading: 15, bottom: 0, trailing: 0))
          .font(.system(size: 20, weight: .bold))
        HStack(spacing:0) {
          Text(String(model.lprice))
          Text("원")
        }
        .font(.system(size: 20, weight: .bold))
        .padding(EdgeInsets(top: 0, leading: 15, bottom: 0, trailing: 0))
        VStack(alignment: .center) {
          Text("이거 내가 진짜 갖고 싶었던 건데 힝힝 근데 너무 비싸고 또 내가 사긴 좀 그럼 그래서 친구놈들이 사주면 정말 좋겠따 벗 아임 낫 속물~")
            .padding()
            .overlay(
              RoundedRectangle(cornerRadius: 10)
                .stroke(Color.gray, lineWidth: 1)
            )
        }
        .padding()
        Spacer()
        HStack {
          Link(destination: URL(string: model.link)!) {
            HStack {
              Text("사이트로 이동하기")
            } .foregroundColor(.white)
          }
          .frame(width: 150, height: 50)
          .background(.blue)
          .cornerRadius(10)
          Button {
            self.manager.checkDetails(userId: 1, productId: Int(model.productId)!, title: model.title, link: model.link, image: model.image, price: Int( model.lprice)!)
          } label: {
            Text("담기")
              .foregroundColor(.white)
          }
          .frame(width: 80, height: 50)
          .background(.blue)
          .cornerRadius(10)
        }
        .padding()
      }
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
