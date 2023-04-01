//
//  LoginView.swift
//  MyThings
//
//  Created by 고명주 on 2023/01/14.
//

import SwiftUI
import Combine

struct LoginView: View {
  @State var name: String = ""
  @State var birthDatestr: String = ""
  @State var phoneNumber: String = ""
  @State var validateNumber: String = ""
  @State private var birthDate = Date()
  var dateFormatter: DateFormatter {
    let formatter = DateFormatter()
    formatter.dateStyle = .full
    return formatter
  }
  var body: some View {
    VStack(alignment: .center, spacing: 30) {
      Spacer()
      Text("MyThing과 함께 위시리스트를 공유해봐요!")
        .fontWeight(.bold)
        .font(.title)
      Spacer()
      TextField("이름을 입력해주세요.", text: $name)
        .padding()
        .background(Color(uiColor: .secondarySystemBackground))
        .cornerRadius(10)
      HStack {
        Text("생일을 선택해주세요!")
          .foregroundColor(Color(uiColor: .systemGray2))
          .background(Color(uiColor: .secondarySystemBackground))
          .padding()
        DatePicker(selection: $birthDate, in: ...Date(), displayedComponents: .date) {
        }.padding()
      }
      .background(Color(uiColor: .secondarySystemBackground))
      .cornerRadius(10)
      HStack {
        TextField("핸드폰 번호를 입력해주세요.", text: $phoneNumber)
          .onReceive(Just(phoneNumber)) { location in
          }
          .padding()
          .background(Color(uiColor: .secondarySystemBackground))
          .cornerRadius(10)
        Button {
          print("bubub")
        } label: {
          Text("인증번호 받기")
            .foregroundColor(.white)
            .padding()
        }
        .background(Color(.systemBlue))
        .cornerRadius(10)
      }
      HStack {
        TextField("인증번호를 입력해주세요.", text: $validateNumber)
          .padding()
          .background(Color(uiColor: .secondarySystemBackground))
          .cornerRadius(10)
        Button {
        } label: {
          Text("재전송")
            .foregroundColor(.white)
            .padding()
        }
        .frame(minWidth: 120)
        .background(Color(.systemBlue))
        .cornerRadius(10)
      }
      Spacer()
      NavigationLink {
        MainTabView()
      } label: {
        Text("회원가입하기")
          .foregroundColor(.white)
          .padding(20)
      }
      .frame(minWidth: 350)
      .background(Color(.systemBlue))
      .cornerRadius(10)
    }
    .padding(15)
    .navigationBarHidden(true)
  }
}

struct LoginView_Previews: PreviewProvider {
  static var previews: some View {
    LoginView()
  }
}
