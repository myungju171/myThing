//
//  MyPageView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/15.
//

import SwiftUI

struct MyPageView: View {
  
  var body: some View {
    VStack {
      VStack(spacing: 20) {
        Image(systemName: "person")
          .resizable()
          .aspectRatio(contentMode: .fit)
          .frame(width: 80)
          .cornerRadius(40)
        Text("jelly")
          .font(.title)
        Text("내 한줄 소개 좋아좋아좋아 선물 좋아")
      }
    }
    .navigationBarItems(trailing: tra)
  }
}

struct MyPageView_Previews: PreviewProvider {
  static var previews: some View {
    MyPageView()
  }
}
