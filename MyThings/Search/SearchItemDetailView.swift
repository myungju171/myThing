//
//  MyWishDetail.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/15.
//

import SwiftUI

struct SearchItemDetailView: View {
  var model: SearchItem
  @EnvironmentObject var viewModel: MyWishListViewModel
  @ObservedObject var searchItemDetailViewModel = SearchItemDetailViewModel()
//  @State var manager = Network()
  @State private var showing = false
  func decimalWon(value: Int) -> String {
    let numberFormatter = NumberFormatter()
    numberFormatter.numberStyle = .decimal
    let result = numberFormatter.string(from: NSNumber(value: value))! + "원"
    
    return result
  }
  var body : some View {
    ScrollView(Axis.Set.vertical, showsIndicators: true) {
      VStack(alignment: .leading, spacing: 10) {
        AsyncImage(url: URL(string: model.image), content: { image in
          image.resizable()
        }, placeholder: {
        })
        .frame(height: 500)
        Text(model.title.replacingOccurrences(of: "<b>", with: "").replacingOccurrences(of: "</b>", with: ""))
          .padding(EdgeInsets(top: 0, leading: 15, bottom: 0, trailing: 0))
          .font(.system(size: 20, weight: .bold))
        HStack(spacing:0) {
          Text(model.lprice.decimalWon())
        }
        .font(.system(size: 20, weight: .bold))
        .padding(EdgeInsets(top: 0, leading: 15, bottom: 0, trailing: 0))
        Spacer()
        HStack(alignment: .center) {
          Link(destination: URL(string: model.link)!) {
            HStack {
              Text("사이트로 이동하기")
            } .foregroundColor(.white)
          }
          .frame(width: 150, height: 50)
          .background(.blue)
          .cornerRadius(10)
          Button {
            self.showing.toggle()
          } label: {
            Text("위시리스트에 담기")
          }
          .alert(isPresented: $showing) {
            let defaultButton = Alert.Button.default(Text("담기")) {  self.searchItemDetailViewModel.registerProduct(userId: 1, productId: Int(model.productId)!, title: model.title, link: model.link, image: model.image, price: Int( model.lprice)!)
            }
            let cancelButton = Alert.Button.cancel(Text("취소"))
            return Alert(title: Text("위시리스트에 담으시겠어요?"), message: Text(""), primaryButton: defaultButton, secondaryButton: cancelButton)
          }
          .frame(width: 80, height: 50)
          .background(.blue)
          .cornerRadius(10)
        }
        .padding()
      }
    }
  }
}
