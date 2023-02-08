//
//  FirstInitView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/20.
//

import SwiftUI

struct FirstInitView: View {
  var body: some View {
    NavigationView {
      VStack(spacing: 50) {
        Spacer()
        Image(systemName: "star")
          .resizable()
          .aspectRatio(contentMode: .fit)
          .frame(width: 100)
        VStack {
          Text("MyThing에서")
          Text("나만의 취향을")
          Text("찾아보고 공유해요")
        }
        .font(.title)
        NavigationLink {
          LoginView()
        } label: {
          Button {
          } label: {
            Text("시작하기")
              .foregroundColor(.white)
              .padding(20)
          }
          .frame(width: 300)
          .background(Color(.systemBlue))
          .cornerRadius(10)
        }
        Spacer()
      }
      .navigationBarHidden(true)
    }
  }
}

struct FirstInitView_Previews: PreviewProvider {
  static var previews: some View {
    FirstInitView()
  }
}
