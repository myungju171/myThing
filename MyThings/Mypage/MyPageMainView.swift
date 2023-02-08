//
//  MyPageMainView.swift
//  MyThings
//
//  Created by 고명주 on 2023/02/02.
//

import SwiftUI

struct MyPageMainView: View {
  @ObservedObject var viewModel = MypageEditViewModel(network: NetworkService(configuration: .default), userId: 1)
  var body: some View {
    VStack(alignment: .leading, spacing: 20) {
      HStack(alignment: .top, spacing: 30) {
        AsyncImage(url: URL(string: viewModel.item?.image ?? ""), content: { image in
          image.resizable()
        }, placeholder: {Color.gray})
        .cornerRadius(100)
        .frame(width: 100, height: 100)
        VStack(alignment: .leading) {
          Text(viewModel.item?.name ?? "")
            .font(.title)
          Text(viewModel.item?.birthDay ?? "")
            .font(.title2)
          Text(viewModel.item?.infoMessage ?? "")
            .lineLimit(1)
            .font(.title2)
        }
        Spacer()
      }
      .padding(50)
      NavigationLink {
        MyPageEditView(model: viewModel.item)
      } label: {
        Text("내 정보 수정하기")
          .font(.title)
          .foregroundColor(.black)
        Spacer()
        Image(systemName: "chevron.right")
          .padding(30)
      }.padding(30)
      Spacer()
    }
    .onAppear {
      print("onappear")
      viewModel.search(userId: 1)
    }
  }
}
//struct MyPageEditView_Previews: PreviewProvider {
//  static var previews: some View {
//    MyPageMainView()
//  }
//}
