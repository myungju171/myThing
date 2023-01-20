//
//  MyWishDetail.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/15.
//

import SwiftUI

struct MyWishDetailView: View {
  var index : Int
  
  var body : some View {
    //       Color.yellow.edgesIgnoringSafeArea(.all)
    ScrollView(Axis.Set.vertical, showsIndicators: true) {
      VStack(alignment: .leading) {
        Image(systemName: "heart.fill")
          .resizable()
          .aspectRatio(contentMode: .fit)
        Text("[디즈니 공식] '몬스터 듀오' 등받이 의자&방수 돗자리 세트_캠프 피크닉 페스티벌")
          .padding()
          .font(.system(size: 20, weight: .bold))
        HStack(spacing:0) {
          Text("42,000")
          Text("원")
        }
        .font(.system(size: 20, weight: .bold))
        .padding()
        Text("이거 내가 진짜 갖고 싶었던 건데 힝힝 근데 너무 비싸고 또 내가 사긴 좀 그럼 그래서 친구놈들이 사주면 정말 좋겠따 벗 아임 낫 속물~")
          .padding()
          .overlay(
            RoundedRectangle(cornerRadius: 10)
              .stroke(Color.gray, lineWidth: 1)
          )
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
          Button {
            print("shoshop")
          } label: {
            Text("쇼핑몰로 이동하기")
              .foregroundColor(.white)
          }
          .frame(width: 150, height: 50)
          .background(.blue)
          .cornerRadius(10)
          Button {
            print("shoshop")
          } label: {
            Text("선물 예약하기")
              .foregroundColor(.white)
          }
          .frame(width: 120, height: 50)
          .background(.blue)
          .cornerRadius(10)
        }
        .padding()
      }
//      .frame(minWidth: .infinity)
    }
    //     .navigationBarTitle("\(index) View",displayMode: .inline)
    //     .navigationBarBackButtonHidden(true)
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

struct MyWishDetail_Previews: PreviewProvider {
  static var previews: some View {
    MyWishDetailView(index: 5)
  }
}
