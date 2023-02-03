//
//  MyPageMainView.swift
//  MyThings
//
//  Created by 고명주 on 2023/02/02.
//

import SwiftUI

struct MyPageMainView: View {
  var body: some View {
    VStack(alignment: .leading, spacing: 20) {
      HStack(alignment: .top, spacing: 30) {
        Image(systemName: "heart.fill")
          .resizable()
          .frame(width: 100, height: 100)
        VStack(alignment: .leading) {
          Text("jelllly")
            .font(.title)
          Text("birrrttthday")
            .font(.title2)
          Text("wlwrmrno rweridfsfsdfsdfdsf")
            .lineLimit(1)
            .font(.title2)
        }
        Spacer()
      }
      .padding(50)
        NavigationLink {
          MyPageEditView()
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
  }
}
struct MyPageEditView_Previews: PreviewProvider {
  static var previews: some View {
    MyPageMainView()
  }
}
